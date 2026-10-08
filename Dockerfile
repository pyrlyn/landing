# syntax=docker/dockerfile:1
# Root build of the storefront served by nginx. See "Run with Docker" in README.md.
#   docker build -t pyrlyn-landing .
#   docker run -d --name pyrlyn-landing -p 8080:80 pyrlyn-landing     # http://localhost:8080/

ARG NODE_VERSION=22
ARG NGINX_VERSION=1.30-alpine

FROM node:${NODE_VERSION}-bookworm-slim AS build
# git: @pyrlyn/brand is a GitHub dependency; the lockfile pins it over SSH, fetch it over HTTPS instead.
RUN apt-get update \
 && apt-get install -y --no-install-recommends git ca-certificates \
 && rm -rf /var/lib/apt/lists/* \
 && git config --global url."https://github.com/".insteadOf "ssh://git@github.com/"
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY . .
# Served at the domain root by default; override for another host or a sub-path.
ARG SITE_URL=https://pyrlyn.dev
ARG SITE_BASE=/
ENV SITE_URL=${SITE_URL} SITE_BASE=${SITE_BASE}
RUN npm run build && test -f dist/index.html && test -f dist/404.html

FROM nginx:${NGINX_VERSION}
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
