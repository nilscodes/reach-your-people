if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import express from 'express';
import twilio, { Twilio } from 'twilio';
import pino from 'pino';
import amqplib from 'amqplib'
import axios from 'axios';

const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const verificationServiceId = process.env.TWILIO_VERIFY_SERVICE_SID || '';
const RYP_SHORT_URL = process.env.RYP_SHORT_URL?.replace(/\/$/, '') || 'https://go.ryp.io';
const RYP_BASE_URL = process.env.RYP_BASE_URL?.replace(/\/$/, '') || 'https://ryp.io';
const REDIRECT_SERVICE_URL = process.env.REDIRECT_SERVICE_URL?.replace(/\/$/, '') || 'http://core-redirect:8074';
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
  link?: string;
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

const redirectAxios = axios.create({
  baseURL: REDIRECT_SERVICE_URL,
  timeout: 1000,
});

const connectToAmqp = async () => {
  const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
  const rabbitMqPort = +(process.env.RABBITMQ_PORT || 5672);
  try {
    const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}:${rabbitMqPort}`);
    const queue = {
      name: 'sms',
      consume: async (client: Twilio, msg: MessageDto) => {
        logger.debug({ msg: `Received message with ID ${msg.announcement} for user with phone number ${msg.referenceId}` });
        const shortenedProjectName = msg.project.name.length > 20 ? msg.project.name.substring(0, 20) + '…' : msg.project.name;
        const rypShortLink = await createShortUrl(msg);
        const announcementTextMessage = getTextForEvent('en', 'newAnnouncement');
        const announcementText = announcementTextMessage.replace('{0}', shortenedProjectName).replace('{1}', rypShortLink);
        try {
            const message = client.messages
                .create({
                    body: announcementText,
                    from: '+18775222797',
                    to: msg.referenceId,
                });
            logger.debug({ msg: `Message sent to user`, message: message });
        } catch (e: any) {
            logger.error({ msg: `Error sending message to user with phone number ${msg.referenceId}`, error: e });
        }
      },
    };
    if (queue.name && queue.consume) {

      const queueChannel = await conn.createChannel();
      await queueChannel.assertQueue(queue.name);
      // Set prefetch to 5 to avoid overloading the bot
      queueChannel.prefetch(5);
      queueChannel.consume(queue.name, (msg) => {
        if (msg !== null) {
          queue.consume(client, JSON.parse(msg.content.toString()));
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

async function createShortUrl(msg: MessageDto) {
  const rypLink = `announcements/${msg.announcement.id}`;
  try {
    const shortenedUrl = (await redirectAxios.post('/urls', {
      url: rypLink,
      type: 'RYP',
      status: 'ACTIVE',
      projectId: msg.project.id,
    })).data;
    return `${RYP_SHORT_URL}/${shortenedUrl.shortcode}`;
  } catch (e: any) {
    logger.error({ msg: `Error creating short URL for announcement ${msg.announcement.id}`, error: e });
    return `${RYP_BASE_URL}/${rypLink}`; // Return the long URL if the short URL could not be created
  }
}
