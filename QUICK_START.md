# 🚀 Quick Start Guide

## One-Command Startup

Simply run this **single command** to start everything:

```bash
./start-all.sh
```

This script will automatically:
1. Stop any running instances
2. Start the ELK stack (Elasticsearch, Logstash, Kibana)
3. Wait for services to be ready
4. Start your Spring Boot application

## What You Get

After running the script (wait ~60 seconds for everything to start):

- **Application**: http://localhost:8081
- **Kibana (Logs)**: http://localhost:5601
- **Elasticsearch**: http://localhost:9200

## Viewing Logs in Kibana

### First Time Setup:
1. Open http://localhost:5601
2. Click **Management** → **Stack Management** → **Index Patterns**
3. Click **Create index pattern**
4. Enter: `spring-boot-logs-*`
5. Click **Next step**
6. Select `@timestamp` as time field
7. Click **Create index pattern**

### View Logs:
1. Click **Discover** in left sidebar
2. All your application logs appear in real-time!

## Using the Application

1. Browse products: http://localhost:8081
2. Add items to cart
3. Go to checkout
4. **IMPORTANT**: Refresh the page (F5) to see pre-filled form
5. The form will show:
   - Name: "John Doe"
   - Address: "123 Main St, Seattle, WA 98101"
6. Click "Place Order"
7. Address will be saved and displayed!

## Stopping Everything

Run this **single command**:

```bash
./stop-all.sh
```

This stops both Spring Boot and the ELK stack.

## Troubleshooting

### Port 8081 already in use?
Run the stop script first:
```bash
./stop-all.sh
./start-all.sh
```

### Logs not appearing in Kibana?
Wait 2-3 minutes after starting, then refresh Kibana.

### Need to rebuild after code changes?
```bash
./stop-all.sh
./gradlew build
./start-all.sh
```

That's it! 🎉
