import { createContext, useContext } from 'react';
import { RypSiteApi } from '../lib/api';

export const ApiContext = createContext({} as RypSiteApi);

export default function ApiProvider(props: any) {
  const api = new RypSiteApi(`${process.env.NEXT_PUBLIC_API_URL!}/api`);

  return (
    <ApiContext.Provider value={api}>
      {props.children}
    </ApiContext.Provider>
  );
}

export function useApi() {
  return useContext(ApiContext);
}