#!/bin/bash

echo "🚀 Iniciando Astra Cinemas..."
echo ""

# Verifica se as dependências do frontend estão instaladas
if [ ! -d "frontend/node_modules" ]; then
    echo "📦 Instalando dependências do frontend..."
    cd frontend
    npm install
    cd ..
    echo "✅ Dependências instaladas!"
    echo ""
fi

# Inicia o backend em background
echo "🔧 Iniciando backend Spring Boot..."
./mvnw spring-boot:run &
BACKEND_PID=$!

# Aguarda o backend iniciar
sleep 10

# Inicia o frontend
echo "🎨 Iniciando frontend React..."
cd frontend
npm start &
FRONTEND_PID=$!

echo ""
echo "✅ Aplicação iniciada!"
echo "📱 Frontend: http://localhost:3000"
echo "🔌 Backend: http://localhost:8080"
echo "💾 H2 Console: http://localhost:8080/h2-console"
echo ""
echo "Para parar a aplicação, pressione Ctrl+C"

# Função para limpar processos ao sair
cleanup() {
    echo ""
    echo "🛑 Parando aplicação..."
    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null
    exit 0
}

trap cleanup SIGINT SIGTERM

# Mantém o script rodando
wait
