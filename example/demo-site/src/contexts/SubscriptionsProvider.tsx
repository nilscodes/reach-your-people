import { Subscription } from '@/lib/types/Subscription';
import { createContext, useContext, useState } from 'react';

export const SubscriptionsContext = createContext({
  subscriptions: [] as Subscription[],
  setSubscriptions: (_: Subscription[]) => {},
});

export default function SubscriptionsProvider(props: any) {
  const [subscriptions, setData] = useState<Subscription[]>([]);

  const setSubscriptions = (newSubscriptions: Subscription[]) => {
    setData(newSubscriptions);
  };

  return (
    <SubscriptionsContext.Provider value={{ subscriptions, setSubscriptions }}>
      {props.children}
    </SubscriptionsContext.Provider>
  );
}

export function useSubscriptions() {
  return useContext(SubscriptionsContext);
}