FROM node:14.4.0-alpine3.10 AS build-step
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . ./
RUN npm run build

FROM nginx as prod-stage
COPY --from=build-step /app/dist/streamingplatform /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
