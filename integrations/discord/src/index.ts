if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import { Client, EmbedBuilder, GatewayIntentBits } from 'discord.js';
import pino from 'pino'
import amqplib from 'amqplib'

const client = new Client({
  intents: [GatewayIntentBits.DirectMessages],
});

const logger = pino({ level: process.env.LOG_LEVEL || 'info' });

const RYP_BASE_URL = process.env.RYP_BASE_URL?.replace(/\/$/, '') || 'https://ryp.io';

client.on('ready', () => {
    logger.info(`RYP bot is running. Logged in as ${client.user?.tag} (${client.user?.id})`);
});

client.login(process.env.TOKEN);

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
  referenceId: string; // Discord snowflake for the user ID we want to DM
  announcement: BasicAnnouncementDto;
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

type AnnouncementTextMessages = {
  [key: string]: {
    [lang: string]: string;
  };
};

const announcementTextMessages: AnnouncementTextMessages = {
  'author': {
    'en': '{0} via RYP.io'
  },
  'link': {
    'en': 'Link to announcement'
  }
}

const sendStatistics = async (channel: amqplib.Channel, queueName: string, statisticsUpdate: StatisticsUpdateDto) => {
  await channel.assertQueue(queueName);
  channel.sendToQueue(queueName, Buffer.from(JSON.stringify(statisticsUpdate)));
};

const connectToAmqp = async () => {
  const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
  try {
    const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}`) as any; // The Connection class has some weird typescript conflict that seems unresolvable at this time with these versions
    const queue = {
      name: 'discord',
      consume: async (queueChannel: amqplib.Channel, client: Client, msg: MessageDto) => {
        try {
          const user = await client.users.fetch(msg.referenceId);
          if (user) {
            try {
              // Make a nice Discord embed
              const baseEmbed = new EmbedBuilder()
                .setColor('#FF145F')
                .setTitle(msg.announcement.title)
                .setAuthor({
                  name: announcementTextMessages['author']['en'].replace('{0}', msg.project.name),
                  iconURL: `${RYP_BASE_URL}/logo192.png`,
                  url: `${RYP_BASE_URL}/projects/${msg.project.id}`,
                })
                .setDescription(msg.announcement.content)
                .setTimestamp()
                .setFields([{
                  name: announcementTextMessages['link']['en'],
                  value: msg.announcement.link,
                }])
                await user.send({ embeds: [baseEmbed] });
                sendStatistics(queueChannel, 'statistics-discord', {
                  announcementId: msg.announcement.id,
                  statistics: {
                    delivered: 1,
                  },
                });
                return
            } catch (e: any) {
              logger.error({ msg: `Error sending message to user with ID ${msg.referenceId}`, error: e });
            }
          }
        } catch(e: any) {
          logger.error({ msg: `User with ID ${msg.referenceId} not found` , error: e });
        }
        sendStatistics(queueChannel, 'statistics-discord', {
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
      queueChannel.consume(queue.name, async (msg: any) => {
        if (msg !== null) {
          queue.consume(queueChannel, client, JSON.parse(msg.content.toString()))
            .finally(() => {;
              queueChannel.ack(msg);
            });
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