import { Verification } from '@vibrantnet/core';
import axios from 'axios';

const getToken = async (): Promise<{ access_token: string }> => {
  const tokenUrl = `${process.env.VIBRANT_AUTH_RYP_URL}/oauth2/token`;
  const clientId = `${process.env.VIBRANT_AUTH_RYP_CLIENT_ID}`;
  const clientSecret = `${process.env.VIBRANT_AUTH_RYP_CLIENT_SECRET}`;

  const response = await axios.post(tokenUrl, 'grant_type=client_credentials&scope=api', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': 'Basic ' + Buffer.from(`${clientId}:${clientSecret}`).toString('base64'),
    },
  });

  return response.data;
};

export class VibrantIntegrationApi {
  constructor(private readonly baseUrl: string) {
    axios.defaults.withCredentials = true;
    this.baseUrl = baseUrl;
  }

  async getVerificationsForDiscordUser(discordUserId: string): Promise<Verification[]> {
    const token = await getToken();
    return (await axios.get(`${this.baseUrl}/externalaccounts/discord/${discordUserId}/verifications`, {
      headers: {
        Authorization: `Bearer ${token.access_token}`,
      },
    })).data;
  }
}
