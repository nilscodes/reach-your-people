import nextTranslate from 'next-translate-plugin';

const CDN_BASE_URL = process.env.NEXT_PUBLIC_CDN_BASE_URL?.replace(/\/$/, '').replace('https://', '');

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  webpack: function (config, options) {
    config.experiments = {
      asyncWebAssembly: true,
      layers: true,
    };
    return config;
  },
  experimental: {
    esmExternals: false,
  },
  images: {
    domains: [CDN_BASE_URL],
  },
};

export default nextTranslate(nextConfig);
