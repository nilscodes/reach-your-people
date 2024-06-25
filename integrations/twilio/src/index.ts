if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import express from 'express';
import twilio, { Twilio } from 'twilio';
import pino from 'pino';
import amqplib from 'amqplib'

const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const verificationServiceId = process.env.TWILIO_VERIFY_SERVICE_SID || '';
const fromNumber = process.env.TWILIO_FROM_NUMBER || '';
const client = twilio(accountSid, authToken);
const logger = pino({ level: process.env.LOG_LEVEL || 'info' });

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());

// POST endpoint to start the verification process
app.post('/startVerification', async (req, res) => {
  const { phoneNumber, channel } = req.body;
  logger.info(`Starting verification for ${phoneNumber} via ${channel}`);

  try {
    const verification = await client.verify.v2.services(verificationServiceId)
      .verifications
      .create({ to: phoneNumber, channel: channel });
    logger.debug(JSON.stringify(verification));
    res.status(204).end();
  } catch (err) {
    logger.error(JSON.stringify(err));
    res.status(500).send('Failed to start verification');
  }
});

app.post('/checkVerificationStatus', async (req, res) => {
  const { phoneNumber, code } = req.body;
  logger.info(`Checking status for code phone number ${phoneNumber} and ${code}`);

  try {
    const verificationCheck = await client.verify.v2.services(verificationServiceId)
      .verificationChecks
      .create({ to: phoneNumber, code: code })

    logger.debug(JSON.stringify(verificationCheck));
    if (verificationCheck.status === 'approved') {
      res.status(200).send(verificationCheck.status);
    } else {
      res.status(400).send(verificationCheck.status);
    }
  } catch (err) {
    logger.error(JSON.stringify(err));
    res.status(500).send('Failed to check verification status');
  }
});

app.listen(port, () => {
  logger.info(`Server running at http://localhost:${port}`);
});

type BasicAnnouncementDto = {
  id: string;
  author: number;
  title: string;
  content: string;
  link: string;
  externalLink?: string;
}

type BasicProjectDto = {
  id: number;
  name: string;
  url: string;
  logo: string;
}

type MessageDto = {
  referenceId: string; // Phone number
  announcement: BasicAnnouncementDto;
  metadata?: string;
  project: BasicProjectDto;
}

type AnnouncementTextMessages = {
  [key: string]: {
    [lang: string]: string;
  };
};

type StatisticsDto = {
  delivered?: number;
  failures?: number;
  views?: number;
}

type StatisticsUpdateDto = {
  announcementId: string;
  statistics: StatisticsDto;
}

const announcementTextMessages: AnnouncementTextMessages = {
  'newAnnouncement': {
    'en': 'A new announcement has been posted by "{0}", a project you are following. Read more at {1}'
  }
}

type EventKey = keyof typeof announcementTextMessages;
type LangKey = keyof typeof announcementTextMessages[EventKey];

const getTextForEvent = (lang: LangKey, event: EventKey) => {
  return announcementTextMessages[event][lang];
}

const sendStatistics = async (channel: amqplib.Channel, queueName: string, statisticsUpdate: StatisticsUpdateDto) => {
  await channel.assertQueue(queueName);
  channel.sendToQueue(queueName, Buffer.from(JSON.stringify(statisticsUpdate)));
};

const connectToAmqp = async () => {
  const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
  const rabbitMqPort = +(process.env.RABBITMQ_PORT || 5672);
  try {
    const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}:${rabbitMqPort}`);
    const queue = {
      name: 'sms',
      consume: async (queueChannel: amqplib.Channel, client: Twilio, msg: MessageDto) => {
        logger.debug({ msg: `Received message with ID ${msg.announcement} for user with phone number ${msg.referenceId}` });
        const shortenedProjectName = msg.project.name.length > 20 ? msg.project.name.substring(0, 20) + '…' : msg.project.name;
        const announcementTextMessage = getTextForEvent('en', 'newAnnouncement');
        const announcementText = announcementTextMessage.replace('{0}', shortenedProjectName).replace('{1}', msg.announcement.link);
        try {
            const message = await client.messages
                .create({
                    body: announcementText,
                    from: fromNumber,
                    to: msg.referenceId,
                });
            logger.debug({ msg: `Message sent to user`, message: message });
            sendStatistics(queueChannel, 'statistics-sms', {
              announcementId: msg.announcement.id,
              statistics: {
                delivered: 1,
              },
            });
            return
        } catch (e: any) {
            logger.error({ msg: `Error sending message to user with phone number ${msg.referenceId}`, error: e });
        }
        sendStatistics(queueChannel, 'statistics-sms', {
          announcementId: msg.announcement.id,
          statistics: {
            failures: 1,
          },
        });
      },
    };
    if (queue.name && queue.consume) {

      const queueChannel = await conn.createChannel();
      await queueChannel.assertQueue(queue.name);
      // Set prefetch to 5 to avoid overloading the bot
      queueChannel.prefetch(5);
      queueChannel.consume(queue.name, (msg) => {
        if (msg !== null) {
          queue.consume(queueChannel, client, JSON.parse(msg.content.toString()));
          queueChannel.ack(msg);
        } else {
          logger.error({ msg: `Consumer for queue ${queue.name} cancelled by server` });
        }
      });
    }
    conn.on('close', () => {
      logger.error({ msg: 'Connection to AMQP server was closed. Reconnecting in 10 seconds...' });
      setTimeout(connectToAmqp, 10000);
    });
    conn.on('error', (e: any) => {
      logger.error({ msg: 'Error during AMQP connection', error: e });
    });
  } catch (_) {
    logger.error({ msg: 'Connection to AMQP server could not be established. Reconnecting in 10 seconds...' });
    setTimeout(connectToAmqp, 10000);
  }
};
connectToAmqp();

