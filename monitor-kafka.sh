#!/bin/bash

# Kafka Message Monitor Script
# This script helps you view Kafka messages for order processing

echo "========================================="
echo "  Kafka Message Monitoring Tool"
echo "========================================="
echo ""

# Check if Kafka is running
if ! docker ps | grep -q kafka; then
    echo "❌ Kafka is not running. Start it with:"
    echo "   docker-compose up -d kafka zookeeper"
    exit 1
fi

echo "✅ Kafka is running"
echo ""

# Function to list topics
list_topics() {
    echo "📋 Available Kafka Topics:"
    docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list 2>/dev/null
    echo ""
}

# Function to view messages from a topic
view_topic() {
    local topic=$1
    echo "📨 Messages from topic: $topic"
    echo "========================================"
    docker exec kafka kafka-console-consumer \
        --bootstrap-server localhost:9092 \
        --topic "$topic" \
        --from-beginning \
        --max-messages 50 \
        --timeout-ms 3000 \
        2>/dev/null || echo "No messages in this topic yet"
    echo ""
}

# Function to monitor topic in real-time
monitor_topic() {
    local topic=$1
    echo "🔄 Monitoring topic: $topic (Ctrl+C to stop)"
    echo "========================================"
    docker exec -it kafka kafka-console-consumer \
        --bootstrap-server localhost:9092 \
        --topic "$topic"
}

# Main menu
case "${1:-list}" in
    "list")
        list_topics
        ;;
    "view")
        if [ -z "$2" ]; then
            echo "Usage: $0 view <topic-name>"
            echo ""
            list_topics
            exit 1
        fi
        view_topic "$2"
        ;;
    "monitor")
        if [ -z "$2" ]; then
            echo "Usage: $0 monitor <topic-name>"
            echo ""
            list_topics
            exit 1
        fi
        monitor_topic "$2"
        ;;
    "order-placed")
        view_topic "order-placed-topic"
        ;;
    "all")
        list_topics
        echo "📨 Order Placed Topic:"
        view_topic "order-placed-topic"
        echo ""
        ;;
    *)
        echo "Usage: $0 [command] [topic-name]"
        echo ""
        echo "Commands:"
        echo "  list                    - List all Kafka topics"
        echo "  view <topic>           - View messages from a specific topic"
        echo "  monitor <topic>        - Monitor a topic in real-time"
        echo "  order-placed           - View order-placed-topic messages"
        echo "  all                    - View all topics and order messages"
        echo ""
        echo "Examples:"
        echo "  $0 list"
        echo "  $0 order-placed"
        echo "  $0 view order-placed-topic"
        echo "  $0 monitor order-placed-topic"
        ;;
esac
