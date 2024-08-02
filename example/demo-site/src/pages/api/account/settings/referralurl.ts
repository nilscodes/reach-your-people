import { getServerSession } from "next-auth";
import { getNextAuthOptions } from "../../auth/[...nextauth]";
import { coreSubscriptionApi } from '@/lib/core-subscription-api';
import type { NextApiRequest, NextApiResponse } from 'next'
import { coreRedirectApi } from "@/lib/core-redirect-api";
import { isPremiumAccount } from "@/lib/premium";
import { ShortenedUrl } from "@/lib/ryp-redirect-api";
import { Profanity, ProfanityOptions } from '@2toad/profanity';

const options = new ProfanityOptions();
options.wholeWord = false;
const profanity = new Profanity(options);
 
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const session = await getServerSession(
    req,
    res,
    getNextAuthOptions(req, res)
  );
  
  if (session?.userId) {
    const accountId = session.userId;
    if (req.method === 'POST') {
      const account = (await coreSubscriptionApi.getAccountById(accountId)).data;
      const accountSettings = (await coreSubscriptionApi.getSettingsForAccount(accountId)).data;
      const isPremium = isPremiumAccount(account);
      const updatePremiumShortcode = req.body.shortcode;
      if (accountSettings.REFERRAL_URL && (!updatePremiumShortcode || !isPremium)) {
        // No need to generate a new standard referral URL if it already exists
        return res.status(200).json({ referralUrl: accountSettings.REFERRAL_URL });
      }
      let referralUrl = process.env.NEXT_PUBLIC_API_URL + '/?ref=' + accountId;
      try {
        const shortcode = isPremium && updatePremiumShortcode ? updatePremiumShortcode : null;
        if (profanity.exists(shortcode) || /^[A-Za-z0-9]{4,10}$/i.test(shortcode) === false) {
          res.status(400).json({ message: `Bad Request - shortcode ${shortcode} includes profanity or invalid characters.` });
          return;
        }
        let shortUrl: ShortenedUrl | null = null;
        if (isPremium && accountSettings.REFERRAL_URL_PREMIUM && shortcode) {
          const currentShortcode = accountSettings.REFERRAL_URL_PREMIUM.split('/').pop();
          if (currentShortcode) {
            try {
              const currentShorturl = (await coreRedirectApi.getUrlByShortcode(currentShortcode)).data;
              if (currentShorturl) {
                try {
                  shortUrl = (await coreRedirectApi.updateUrlById(currentShorturl.id!, {
                    shortcode,
                  })).data;
                } catch (error) {
                  res.status(409).json({ message: 'Conflict' });
                  return
                }
              }
            } catch (error) {
              console.error('Error getting current short URL:', error);
              // Continue with creating a new short URL if the existing one cannot be found
            }
          }
        }

        // If no short URL is available by now, we have to create it
        if (!shortUrl) { 
          shortUrl = (await coreRedirectApi.createShortUrl({
            shortcode,
            url: '/?ref=' + accountId,
            type: 'RYP',
            status: 'ACTIVE',
          })).data;
        }
        referralUrl = `${process.env.RYP_SHORT_URL}/${shortUrl.shortcode}`;
      }
      catch (error) {
        console.log(error);
        // TODO: log to Prometheus, since we want to know if this is failing, but we can just use the full URL in the meantime
      }
      if (isPremium && updatePremiumShortcode) {
        await coreSubscriptionApi.updateAccountSetting(accountId, 'REFERRAL_URL_PREMIUM', { name: 'REFERRAL_URL_PREMIUM', value: referralUrl });
      } else {
        await coreSubscriptionApi.updateAccountSetting(accountId, 'REFERRAL_URL', { name: 'REFERRAL_URL', value: referralUrl });
      }
      res.status(200).json({ referralUrl });
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
}