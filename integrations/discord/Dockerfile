FROM node:alpine AS build

WORKDIR /app
COPY . .
WORKDIR /app/shared
RUN npm install
RUN npm run build
WORKDIR /app/discord
RUN npm install
RUN npm run build

FROM node:alpine AS run
WORKDIR /app
COPY --from=build ./app/discord/dist ./dist
COPY --from=build ./app/discord/package* ./
RUN npm install --omit=dev && chown -R node:node dist
USER node
CMD ["node", "./dist"]