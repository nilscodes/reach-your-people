import NodeCache from 'node-cache';

export const nonceCache = new NodeCache({ stdTTL: 600 });
