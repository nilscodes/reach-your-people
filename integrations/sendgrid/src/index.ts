if (process.env.NODE_ENV !== 'production') {
  require('dotenv').config();
}
import pino from 'pino';
import amqplib from 'amqplib'
import sgMail from '@sendgrid/mail';
import fs from 'fs';
import path from 'path';
import { htmlizeMarkdown, stripMarkdown, initializeTranslations, MessageDto, StatisticsUpdateDto, t, augmentAnnouncementIfRequired, AnnouncementType } from '@vibrantnet/integration-shared';
import mjml2html from 'mjml'

const apiKey = process.env.SENDGRID_API_KEY!;
const fromEmail = process.env.SENDGRID_FROM || '';
sgMail.setApiKey(apiKey);
const logger = pino({ level: process.env.LOG_LEVEL || 'info' });

const isDev = process.env.NODE_ENV !== 'production';
const templatePath = path.join(__dirname, isDev ? '../templates/single-announcement.mjml' : 'templates/single-announcement.mjml');
const singleAnnouncementTemplate = fs.readFileSync(templatePath, 'utf8');

const sendStatistics = async (channel: amqplib.Channel, queueName: string, statisticsUpdate: StatisticsUpdateDto) => {
  await channel.assertQueue(queueName);
  channel.sendToQueue(queueName, Buffer.from(JSON.stringify(statisticsUpdate)));
};

const connectToAmqp = async () => {
  await initializeTranslations();
  const rabbitPw = process.env.RABBITMQ_PASSWORD as string;
  const rabbitMqPort = +(process.env.RABBITMQ_PORT || 5672);
  try {
    const conn = await amqplib.connect(`amqp://${process.env.RABBITMQ_USER}:${encodeURIComponent(rabbitPw)}@${process.env.RABBITMQ_HOST}:${rabbitMqPort}`);
    await setupQueue('email', conn);
    await setupQueue('google', conn);
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

async function setupQueue(queueName: string, conn: any) {
  const queue = {
    name: queueName,
    consume: async (queueChannel: amqplib.Channel, msg: MessageDto) => {
      const to = queueName === 'email' ? msg.referenceId : msg.referenceName;
      logger.debug({ msg: `Received message for announcement ID ${msg.announcement.id} for user with email ${to} on queue ${queueName}` });
      const finalAnnouncement = augmentAnnouncementIfRequired(msg.announcement, msg.language);
      const shortenedProjectName = msg.project.name.length > 20 ? msg.project.name.substring(0, 20) + '…' : msg.project.name;
      const subject = generateSubject(msg, shortenedProjectName);
      const ctaText = t('readMore', msg.language);
      try {
        const text = stripMarkdown(`# ${finalAnnouncement.title}\n\n${finalAnnouncement.content}`);
        const placeholders = {
          headerImageUrl: 'https://www.ryp.io/email_header_dark.png',
          title: finalAnnouncement.title,
          preview: text.substring(0, 100),
          announcementBody: await htmlizeMarkdown(`${finalAnnouncement.content}`),
          unsubscribeLink: `https://www.ryp.io/login/mail/unsubscribe?email=${to}`,
          preferencesLink: 'https://www.ryp.io/account',
          footerImageUrl: 'https://www.ryp.io/email_footer_dark.png',
          ctaText,
          ctaUrl: finalAnnouncement.link,
        } as any;

        let renderedTemplate = singleAnnouncementTemplate;
        Object.keys(placeholders).forEach(key => {
          const regex = new RegExp(`{{${key}}}`, 'g');
          renderedTemplate = renderedTemplate.replace(regex, placeholders[key]);
        });
        const { html, errors } = mjml2html(renderedTemplate);
        if (!errors.length) {
          const mail = {
            to,
            from: { name: 'Reach Your People (RYP)', email: fromEmail },
            subject,
            text,
            html,
          }
          const message = await sgMail.send(mail)
          logger.debug({ msg: `Message sent to user`, message: message });
          sendStatistics(queueChannel, 'statistics-email', {
            announcementId: msg.announcement.id,
            statistics: {
              delivered: 1,
            },
          });
          return;
        }
        logger.error({ msg: `Error rendering MJML template for announcement with ${msg.announcement.id}`, errors });
      } catch (e: any) {
        logger.error({ msg: `Error sending message to user with email ${to}`, error: e });
      }
      sendStatistics(queueChannel, 'statistics-email', {
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
    queueChannel.consume(queue.name, (msg: any) => {
      if (msg !== null) {
        queue.consume(queueChannel, JSON.parse(msg.content.toString()));
        queueChannel.ack(msg);
      } else {
        logger.error({ msg: `Consumer for queue ${queue.name} cancelled by server` });
      }
    });
  }
}

function generateSubject(msg: MessageDto, shortenedProjectName: string) {
  if (msg.announcement.type === AnnouncementType.STAKEPOOL_RETIREMENT) {
    return t('newEventSubject', msg.language, { title: t('retirement.title', msg.language, { ns: 'stakepool-cardano' }) });
  }
  return t('newAnnouncementSubject', msg.language, { projectName: shortenedProjectName });
}

