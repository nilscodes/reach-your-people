export function formatISODateToCustomString(isoDateString: string) {
  const date = new Date(isoDateString);
  const month = date.toLocaleString('en-US', { month: 'long' });
  const year = date.getFullYear();
  return `${month}, ${year}`;
}