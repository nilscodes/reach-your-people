FROM node:alpine AS build

WORKDIR /app
COPY . .
RUN npm install
RUN npm run build

FROM node:alpine AS run
WORKDIR /app
COPY --from=build ./app/dist ./dist
COPY package* ./
RUN npm install --only=prod && chown -R node:node dist && mv dist/templates .
USER node
CMD ["node", "./dist"]