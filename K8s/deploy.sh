#!/bin/bash

echo "🚀 Desplegando Sistema de Notificaciones en Kubernetes..."

# Verificar que kubectl esté disponible
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl no está instalado. Por favor, instala kubectl primero."
    exit 1
fi

# Verificar que el cluster esté funcionando
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ No se puede conectar al cluster de Kubernetes. Verifica que esté funcionando."
    exit 1
fi

echo "✅ Cluster de Kubernetes conectado"

# Crear namespace
echo "📦 Creando namespace..."
kubectl apply -f namespace.yaml

# Crear ConfigMap
echo "⚙️  Creando ConfigMap..."
kubectl apply -f configmap.yaml

# Crear Secret (necesitas configurar tus credenciales de AWS)
echo "🔐 Creando Secret..."
echo "⚠️  IMPORTANTE: Edita secret.yaml con tus credenciales reales de AWS antes de continuar"
read -p "¿Has configurado las credenciales de AWS en secret.yaml? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Por favor, configura las credenciales de AWS en secret.yaml primero"
    exit 1
fi

kubectl apply -f secret.yaml

# Desplegar MariaDB
echo "🗄️  Desplegando MariaDB..."
kubectl apply -f mariadb-deployment-corrected.yaml

# Esperar a que MariaDB esté listo
echo "⏳ Esperando a que MariaDB esté listo..."
kubectl wait --for=condition=ready pod -l app=mariadb -n notification-system --timeout=300s

# Desplegar Eureka Server
echo "🌐 Desplegando Eureka Server..."
kubectl apply -f eureka-deployment-corrected.yaml

# Esperar a que Eureka esté listo
echo "⏳ Esperando a que Eureka Server esté listo..."
kubectl wait --for=condition=ready pod -l app=eureka-server -n notification-system --timeout=300s

# Desplegar Producer
echo "🔌 Desplegando Notification Producer..."
kubectl apply -f producer-deployment-corrected.yaml

# Desplegar Dispatcher
echo "📨 Desplegando Notification Dispatcher..."
kubectl apply -f dispatcher-deployment-corrected.yaml

# Desplegar Ingress
echo "🌍 Desplegando Ingress..."
kubectl apply -f ingress-corrected.yaml

# Esperar a que todos los pods estén listos
echo "⏳ Esperando a que todos los servicios estén listos..."
kubectl wait --for=condition=ready pod -l app=notification-producer -n notification-system --timeout=300s
kubectl wait --for=condition=ready pod -l app=notification-dispatcher -n notification-system --timeout=300s

echo "✅ Despliegue completado!"
echo ""
echo "📊 Estado de los pods:"
kubectl get pods -n notification-system

echo ""
echo "🌐 Servicios disponibles:"
kubectl get services -n notification-system

echo ""
echo "🔗 URLs de acceso:"
echo "Eureka Server: http://localhost:8761"
echo "Producer API: http://localhost:8081"
echo "Dispatcher API: http://localhost:8082"
echo ""
echo "📝 Para ver logs: kubectl logs -f <pod-name> -n notification-system"
echo "📝 Para acceder a un pod: kubectl exec -it <pod-name> -n notification-system -- /bin/bash" 