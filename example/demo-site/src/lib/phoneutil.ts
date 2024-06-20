export function formatPhoneNumber(countryCode: string, phoneNumber: string) {
  // Simplified formatting, replace with a more robust solution as needed
  const formattedNumber = phoneNumber.replace(/\D/g, '').substring(0, 10);
  // Style with the US format
  const usNumber = `${countryCode} (${formattedNumber.substring(0, 3)}) ${formattedNumber.substring(3, 6)} ${formattedNumber.substring(6)}`
  return usNumber;
}