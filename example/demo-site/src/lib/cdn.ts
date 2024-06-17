const CDN_BASE_URL = process.env.NEXT_PUBLIC_CDN_BASE_URL?.replace(/\/$/, '');

export function makeCdnUrl(path: string) {
    return `${CDN_BASE_URL}/${path}`;
}