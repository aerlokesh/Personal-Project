#!/bin/bash

echo "========================================="
echo "Stopping Personal Project & ELK Stack"
echo "========================================="
echo ""

# Navigate to project directory
cd "$(dirname "$0")"

# Step 1: Stop Spring Boot
echo "Step 1/2: Stopping Spring Boot application..."
pkill -f "gradle.*bootRun"
echo "✓ Spring Boot stopped"
echo ""

# Step 2: Stop ELK stack
echo "Step 2/2: Stopping ELK stack..."
docker compose down
echo "✓ ELK stack stopped"
echo ""

echo "========================================="
echo "✓ All services stopped successfully!"
echo "========================================="
echo ""
echo "To start again, run: ./start-all.sh"
echo ""
