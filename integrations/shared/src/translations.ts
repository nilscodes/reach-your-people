import i18next, { InitOptions, TFunction } from 'i18next';
import Backend from 'i18next-fs-backend';

let isInitialized = false;

export async function initializeTranslations(options: InitOptions = {}): Promise<TFunction> {
  if (isInitialized) {
    return Promise.resolve(i18next.t);
  }

  await i18next.use(Backend).init({
    debug: false,
    fallbackLng: 'en',
    preload: ['en', 'de'], // Ensure we do not have to use await to load languages ad-hoc via changeLanguage
    backend: {
      loadPath: `${__dirname}/locales/{{lng}}/{{ns}}.json`, // Load from fixed path
    },
    ns: ['common', 'stakepool-cardano', 'governance-cardano'],
    defaultNS: 'common',
    ...options,
  });

  isInitialized = true;
  return i18next.t;
}

export function t(key: string, lng: string = 'en', options?: any): string {
  return i18next.t(key, { ...options, lng }) as string;
}
