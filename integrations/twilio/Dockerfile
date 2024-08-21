FROM node:alpine AS build

WORKDIR /app
COPY . .
RUN npm install && \
    npm run --workspace=@vibrantnet/integration-shared build && \
    npm run --workspace=@vibrantnet/ryp-integration-twilio build

FROM node:alpine AS run
WORKDIR /app
COPY --from=build ./app/twilio/dist ./dist
COPY --from=build ./app/twilio/package* ./
COPY --from=build ./app/shared ./node_modules/@vibrantnet/integration-shared
RUN npm install --omit=dev && chown -R node:node dist
USER node
CMD ["node", "./dist"]