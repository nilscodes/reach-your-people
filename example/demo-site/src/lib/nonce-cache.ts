import { createClient } from 'redis';

const REDIS_URL = process.env.REDIS_URL;
const REDIS_PASSWORD = process.env.REDIS_PASSWORD;

const redisClient = createClient({
  url: REDIS_URL,
  password: REDIS_PASSWORD,
});

redisClient.on('error', (err) => console.log('Redis Client Error', err));

(async () => {
  await redisClient.connect();
})();

export { redisClient };
