FROM node:alpine AS build

WORKDIR /app
COPY . .
WORKDIR /app/shared
RUN npm install
RUN npm run build
WORKDIR /app/telegram
RUN npm install
RUN npm run build

FROM node:alpine AS run
WORKDIR /app
COPY --from=build ./app/telegram/dist ./dist
COPY --from=build ./app/telegram/package* ./
RUN npm install --omit=dev && chown -R node:node dist
USER node
CMD ["node", "./dist"]