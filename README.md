# Skill Sync Backend

Local setup for testing:

1. Run `chmod +x start-local.sh stop-local.sh`
2. Run `./start-local.sh`
3. In a new terminal, run `cd ../skill-sync-frontend && npm install && npm run dev`
4. Open `http://localhost:8761` for Eureka, `http://localhost:8080` for the gateway, and `http://localhost:5173` for the React UI

Notes:

- The startup script builds all services with `-DskipTests` and then starts the full Docker Compose stack.
- MySQL and RabbitMQ are included in `docker-compose.yml`, so no separate local database setup is required.
- Service Swagger UIs are available on ports `8081` through `8088` at `/swagger-ui.html`.
- The frontend defaults to `/proxy`, which Vite forwards to `http://localhost:8080` so the UI can call the gateway without browser CORS issues during local development.
