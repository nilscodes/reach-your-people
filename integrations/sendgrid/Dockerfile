FROM node:alpine AS build

WORKDIR /app
COPY . .
WORKDIR /app/shared
RUN npm install
RUN npm run build
WORKDIR /app/sendgrid
RUN npm install
RUN npm run build

FROM node:alpine AS run
WORKDIR /app
COPY --from=build ./app/sendgrid/dist ./dist
COPY --from=build ./app/sendgrid/package* ./
RUN npm install --omit=dev && chown -R node:node dist && mv dist/templates .
USER node
CMD ["node", "./dist"]