import webpush from 'web-push';

const vapidKeys = webpush.generateVAPIDKeys();

const vapidKeyOutput = `These are your VAPID keys to use with Push API. Make sure to NEVER expose your private key to anyone. The public key will be used by the client to subscribe to push notifications.

VAPID_PUBLIC_KEY=${vapidKeys.publicKey}
VAPID_PRIVATE_KEY=${vapidKeys.privateKey}
VAPID_SUBJECT=https://ryp.io

Both keys and a subject (either a mailto: email link or a https URL) are required as environment variables to set up the RYP webpush integration.`

console.log(vapidKeyOutput);