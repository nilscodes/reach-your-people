if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import { Client, Collection, GatewayIntentBits, Partials } from 'discord.js';
import pino from 'pino'
import amqplib from 'amqplib'

const client = new Client({
  intents: [GatewayIntentBits.DirectMessages],
});

const logger = pino();

client.on('ready', () => {
    logger.info(`RYP bot is running. Logged in as ${client.user?.tag} (${client.user?.id})`);
});

client.login(process.env.TOKEN);

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
      name: 'discord',
      consume: async (client: Client, msg: MessageDto) => {
        const user = await client.users.fetch(msg.referenceId);
        if (user) {
          try {
            await user.send(`**${msg.announcement.title}**\n\n${msg.announcement.content}${msg.announcement.link ? `\n\n<${msg.announcement.link}>` : ''}`);
          } catch (e: any) {
            logger.error({ msg: `Error sending message to user with ID ${msg.referenceId}`, error: e });
          }
        } else {
          logger.error({ msg: `User with ID ${msg.referenceId} not found` });
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