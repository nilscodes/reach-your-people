if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import webpush from 'web-push';
import { pino } from 'pino'
import amqplib from 'amqplib'
import { stripMarkdown, initializeTranslations, StatisticsUpdateDto, MessageDto, augmentAnnouncementIfRequired } from '@vibrantnet/integration-shared';

const vapidDetails = {
  subject: process.env.VAPID_SUBJECT!,
  publicKey: process.env.VAPID_PUBLIC_KEY!,
  privateKey: process.env.VAPID_PRIVATE_KEY!
};

webpush.setVapidDetails(
  vapidDetails.subject,
  vapidDetails.publicKey,
  vapidDetails.privateKey
);

const logger = pino({ level: process.env.LOG_LEVEL || 'info' });

const sendStatistics = async (channel: amqplib.Channel, queueName: string, statisticsUpdate: StatisticsUpdateDto) => {
  await channel.assertQueue(queueName);
  channel.sendToQueue(queueName, Buffer.from(JSON.stringify(statisticsUpdate)));
};

const connectToAmqp = async () => {
  await initializeTranslations();
  const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
  try {
    const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}`);
    const queue = {
      name: 'pushapi',
      consume: async (queueChannel: amqplib.Channel, msg: MessageDto) => {
        if (msg.metadata !== undefined) {
          const subscription = JSON.parse(Buffer.from(msg.metadata, 'base64').toString());
          try {
            const finalAnnouncement = augmentAnnouncementIfRequired(msg.announcement, msg.language);
            const body = stripMarkdown(finalAnnouncement.content);
            const payload = JSON.stringify({
              title: finalAnnouncement.title,
              body,
              icon: '/logo192.png',
              url: finalAnnouncement.link,
            });
            const response = await webpush.sendNotification(subscription, payload);
            logger.debug({ msg: 'Notification sent successfully:', response });
            sendStatistics(queueChannel, 'statistics-pushapi', {
              announcementId: msg.announcement.id,
              statistics: {
                delivered: 1,
              },
            });
            return;
          } catch (e: any) {
            // TODO handle 410 error from Google API as subscription is no longer valid
            logger.error({ msg: `Error sending push API message to subscription for user with ID ${msg.referenceId}`, error: e });
          }
        } else {
          logger.error({ msg: `No metadata provided for message with announcement ID ${msg.announcement.id} for user with ID ${msg.referenceId}` })
        }
        sendStatistics(queueChannel, 'statistics-pushapi', {
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
      logger.info({ msg: `Consumer for queue ${queue.name} started` });
      // Set prefetch to 5 to avoid too many messages being sent at once to the push endpoint
      queueChannel.prefetch(5);
      queueChannel.consume(queue.name, (msg) => {
        if (msg !== null) {
          queue.consume(queueChannel, JSON.parse(msg.content.toString()));
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
