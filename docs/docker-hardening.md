# Docker and release hardening (Sonar S6505, S6471)

## How it works
- `npm ci --ignore-scripts` in the Dockerfile and `release.yml`: dependency install/postinstall
  scripts do not run. The project's own `prebuild` still runs via `npm run build`.
- The runtime image is `nginxinc/nginx-unprivileged` and runs as uid 101 (`USER 101`), not root.
  nginx listens on 8080 inside the container (`docker/nginx.conf`).

## Enable
On by default in the image and the release workflow.

## Disable
Drop `--ignore-scripts`, or switch back to `nginx:<version>` with `listen 80` and `EXPOSE 80`.

## Test
`docker build -t pyrlyn-landing . && docker run --rm -p 127.0.0.1:8080:8080 pyrlyn-landing`, then
`curl -I http://127.0.0.1:8080/` returns 200; `docker exec <id> id -u` prints 101.

## Limitations
- Container port changed 80 -> 8080: any `docker run -p X:80` must become `-p X:8080`.
- A dependency that needs a postinstall step (e.g. a native build) would break; none does today.
