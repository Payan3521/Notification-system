#!/bin/bash

echo "🚀 Building and deploying notification system..."

# ✅ Build JARs
echo "�� Building JARs..."
cd microservice-notification-producer
mvn clean package -DskipTests
cd ../microservice-notification-dispatcher
mvn clean package -DskipTests
cd ..

# ✅ Build Docker images
echo "🐳 Building Docker images..."
docker build -t notification-producer:latest ./microservice-notification-producer
docker build -t notification-dispatcher:latest ./microservice-notification-dispatcher

# ✅ Deploy to Kubernetes
echo "☸️ Deploying to Kubernetes..."
kubectl apply -f k8s/

# ✅ Wait for deployment
echo "⏳ Waiting for deployment..."
kubectl wait --for=condition=available --timeout=300s deployment/notification-producer -n notification-system
kubectl wait --for=condition=available --timeout=300s deployment/notification-dispatcher -n notification-system

echo "✅ Deployment completed!"
echo "�� Check status with: kubectl get pods -n notification-system"