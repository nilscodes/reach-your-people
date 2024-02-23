"use client";

import {
  Button,
  Container,
  Divider,
  FormControl,
  FormLabel,
  Input,
  Select,
  Stack,
  Text,
  useToast,
} from '@chakra-ui/react';
import axios from 'axios';
import { useState } from "react";

// Get the verification URL from the environment
const verificationUrl = process.env.NEXT_PUBLIC_VERIFICATION_URL;

export default function Test() {
  const [policyId, setPolicyId] = useState('');
  const [referenceId, setReferenceId] = useState('');
  const [serviceName, setServiceName] = useState('');
  const toast = useToast();
  // Array to store dropdown choices
  const options = ["Discord", "Google", "Twitter", "LinkedIn", "Steam", "Apple", "GitHub", "Twitch"];

  const handleVerification = async () => {
    try {
      const response = await axios.get(`${verificationUrl}/cip66/${policyId}/${serviceName}/${referenceId}`);
      const isValid = await response.data;

      if (isValid) {
        // Display success toast
        toast({
          title: "Verified",
          status: "success",
          duration: 5000,
          isClosable: true,
          position: "top",
          variant: "solid",
        });
      } else {
        // Display error toast
        toast({
          title: "Verification Failed",
          status: "error",
          duration: 5000,
          isClosable: true,
          position: "top",
          variant: "solid",
        });
      }
    } catch (error) {
      console.error("Verification error:", error);
    }
  };

  return (
    <Container py={{ base: '4', md: '8' }}>
      <Stack spacing="5">
        <Text textStyle="lg" fontWeight="medium">
        Verify if a user is permitted to publish
        </Text>
        <Divider />
        <Stack spacing="5">
          <FormControl id="policyId">
            <FormLabel>Policy ID to test</FormLabel>
            <Input
              placeholder="d390811dc0e73d62d66eec7616a01d7951c286219e19e789c0d87d3e"
              value={policyId}
              onChange={(e) => setPolicyId(e.target.value)}
            />
          </FormControl>

          <FormControl id="serviceName">
            <FormLabel>Choose a Service</FormLabel>
            <Select
              placeholder="Select option"
              value={serviceName}
              onChange={(e) => setServiceName(e.target.value)}
            >
              {options.map((option) => (
                <option value={option.toLowerCase()} key={option}>
                  {option}
                </option>
              ))}
            </Select>
          </FormControl>

          <FormControl id="referenceId">
            <FormLabel>Service Identifier to verify</FormLabel>
            <Input
              placeholder="..."
              value={referenceId}
              onChange={(e) => setReferenceId(e.target.value)}
            />
          </FormControl>

          <Button onClick={handleVerification} alignSelf="end">Test</Button>
        </Stack>
      </Stack>
    </Container>
  );
};

