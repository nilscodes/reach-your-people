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
                    .create({to: phoneNumber, channel: channel });
        logger.debug(JSON.stringify(verification));
        res.status(204).end();
    } catch(err) {
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
            .create({to: phoneNumber, code: code})

        logger.debug(JSON.stringify(verificationCheck));
        if (verificationCheck.status === 'approved') {
            res.status(200).send(verificationCheck.status);
        } else {
            res.status(400).send(verificationCheck.status);
        }
    } catch(err) {
        logger.error(JSON.stringify(err));
        res.status(500).send('Failed to check verification status');
    }
});

app.listen(port, () => {
    logger.info(`Server running at http://localhost:${port}`);
});

type BasicAnnouncementDto = {
    author: number;
    title: string;
    content: string;
    link?: string;
  }
  
  type MessageDto = {
    referenceId: string; // Discord snowflake for the user ID we want to DM
    announcement: BasicAnnouncementDto;
  }

const connectToAmqp = async () => {
    const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
    try {
      const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}`);
      const queue = {
        name: 'sms',
        consume: async (client: Twilio, msg: MessageDto) => {
          logger.debug({ msg: `Received message with ID ${msg.announcement} for user with phone number ${msg.referenceId}` });
          try {
              const message = client.messages
                  .create({
                      body: 'This is the ship that made the Kessel Run in fourteen parsecs?',
                      from: '+18775222797',
                      to: msg.referenceId,
                  });
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