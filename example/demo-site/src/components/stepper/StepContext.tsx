import { createContext, useContext, ReactNode, useState } from 'react';
import useStep from './useStep'; // Import the useStep hook

type StepContextType = {
  currentStep: number;
  setStep: (step: number) => void;
  goToNextStep: () => void;
  goToPrevStep: () => void;
  canGoToNextStep: boolean;
  canGoToPrevStep: boolean;
  reset: () => void;
  metadata: Record<string, unknown>;
  setMetadata: (metadata: Record<string, unknown>) => void;
};

const StepContext = createContext<StepContextType | undefined>(undefined);

const useStepContext = () => {
  const context = useContext(StepContext);
  if (!context) {
    throw new Error('useStepContext must be used within a StepProvider');
  }
  return context;
};

type StepProviderProps = {
  children: ReactNode;
  initialStep: number;
  maxStep: number;
  metadata?: Record<string, unknown>;
};

const StepProvider = ({ children, initialStep, metadata: metadataProp, maxStep }: StepProviderProps) => {
  const [currentStep, helpers] = useStep({ initialStep, maxStep });
  const [metadata, setMetadata] = useState(metadataProp ?? {});

  return (
    <StepContext.Provider value={{ currentStep, metadata, setMetadata, ...helpers }}>
      {children}
    </StepContext.Provider>
  );
};

export { StepProvider, useStepContext };
