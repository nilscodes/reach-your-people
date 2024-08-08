if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import { pino } from 'pino'
import amqplib from 'amqplib'
import { stripMarkdown } from "./markdownTools";
import TelegramBot from 'node-telegram-bot-api';

const token = process.env.TELEGRAM_BOT_TOKEN as string;
const bot = new TelegramBot(token);

const logger = pino({ level: process.env.LOG_LEVEL || 'info' });

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
  referenceId: string; // Unused
  announcement: BasicAnnouncementDto;
  metadata?: string; // Base64 encoded subscription for Push API
  project: BasicProjectDto;
}

type StatisticsDto = {
  delivered?: number;
  failures?: number;
  views?: number;
}

type StatisticsUpdateDto = {
  announcementId: string;
  statistics: StatisticsDto;
}

const sendStatistics = async (channel: amqplib.Channel, queueName: string, statisticsUpdate: StatisticsUpdateDto) => {
  await channel.assertQueue(queueName);
  channel.sendToQueue(queueName, Buffer.from(JSON.stringify(statisticsUpdate)));
};

const connectToAmqp = async () => {
  const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
  try {
    const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}`);
    const queue = {
      name: 'telegram',
      consume: async (queueChannel: amqplib.Channel, msg: MessageDto) => {
        try {
          const attachLink = msg.announcement.externalLink ? `\n\n[Read more](${msg.announcement.externalLink})` : '';
          const body = stripMarkdown(`# ${msg.announcement.title}\n\n${msg.announcement.content}${attachLink}\n\n[${msg.announcement.link}](${msg.announcement.link})`);
          const telegramMessage = await bot.sendMessage(msg.referenceId, body, { parse_mode: 'MarkdownV2' });
          logger.debug({ msg: 'Telegram message sent successfully:', telegramMessage });
          sendStatistics(queueChannel, 'statistics-telegram', {
            announcementId: msg.announcement.id,
            statistics: {
              delivered: 1,
            },
          });
          return;
        } catch (e: any) {
          logger.error({ msg: `Error sending telegram message for user with ID ${msg.referenceId}`, error: e });
        }
        sendStatistics(queueChannel, 'statistics-telegram', {
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
      // Set prefetch to 5 to avoid too many messages being sent at once to the telegram endpoint
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
