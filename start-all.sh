#!/bin/bash

echo "========================================="
echo "Starting Personal Project with ELK Stack"
echo "========================================="
echo ""

# Navigate to project directory
cd "$(dirname "$0")"

# Step 1: Stop any running instances
echo "Step 1/4: Stopping any running instances..."
pkill -f "gradle.*bootRun" 2>/dev/null
docker-compose down 2>/dev/null
echo "✓ Cleanup complete"
echo ""

# Step 2: Start ELK stack
echo "Step 2/4: Starting ELK stack (Elasticsearch, Logstash, Kibana)..."
docker-compose up -d
echo "✓ ELK stack started"
echo ""

# Step 3: Wait for services to be ready
echo "Step 3/4: Waiting for ELK services to be ready (60 seconds)..."
echo "  - Elasticsearch will be available at http://localhost:9200"
echo "  - Kibana will be available at http://localhost:5601"
for i in {1..60}; do
    echo -n "."
    sleep 1
done
echo ""
echo "✓ Services should be ready"
echo ""

# Step 4: Start Spring Boot application
echo "Step 4/4: Starting Spring Boot application..."
echo "  - Application will be available at http://localhost:8081"
echo "  - Logs will be sent to Logstash (port 5000)"
echo ""
./gradlew bootRun &

echo ""
echo "========================================="
echo "✓ All services started successfully!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Wait for Spring Boot to fully start (check terminal output)"
echo "2. Open application: http://localhost:8081"
echo "3. Open Kibana: http://localhost:5601"
echo "4. In Kibana (first time only):"
echo "   - Go to Management → Stack Management → Index Patterns"
echo "   - Create index pattern: spring-boot-logs-*"
echo "   - Select @timestamp as time field"
echo "   - Go to Discover to view logs!"
echo ""
echo "IMPORTANT: On checkout page, REFRESH browser (F5) to see pre-filled form!"
echo ""
echo "To stop all services, run: ./stop-all.sh"
echo ""
