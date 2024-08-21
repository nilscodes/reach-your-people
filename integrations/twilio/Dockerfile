FROM node:alpine AS build

WORKDIR /app
COPY . .
WORKDIR /app/shared
RUN npm install
RUN npm run build
WORKDIR /app/twilio
RUN npm install
RUN npm run build

FROM node:alpine AS run
WORKDIR /app
COPY --from=build ./app/twilio/dist ./dist
COPY --from=build ./app/twilio/package* ./
RUN npm install --omit=dev && chown -R node:node dist
USER node
CMD ["node", "./dist"]