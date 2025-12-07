#!/bin/bash

# Script de diagnóstico para entender por que os ingressos desaparecem

echo "🔍 DIAGNÓSTICO DO PROBLEMA DE INGRESSOS"
echo "========================================"
echo ""

echo "1️⃣ Verificando containers Docker..."
docker-compose ps
echo ""

echo "2️⃣ Verificando volumes do PostgreSQL..."
docker volume ls | grep postgres
echo ""

echo "3️⃣ Verificando dados no banco de dados..."
echo ""
echo "📊 Total de compras:"
docker exec astra-postgres psql -U astra -d astra -c "SELECT COUNT(*) as total FROM compra;"
echo ""

echo "📊 Total de ingressos por status:"
docker exec astra-postgres psql -U astra -d astra -t -c "SELECT status, COUNT(*) FROM ingresso GROUP BY status;"
echo ""

echo "📊 Ingressos com suas compras:"
docker exec astra-postgres psql -U astra -d astra -c "
SELECT 
    i.id as ingresso_id,
    i.status as ingresso_status,
    i.compra_id,
    c.status as compra_status,
    c.criado_em
FROM ingresso i
LEFT JOIN compra c ON i.compra_id = c.id
ORDER BY i.id;
"
echo ""

echo "4️⃣ Verificando a constraint CASCADE:"
docker exec astra-postgres psql -U astra -d astra -c "
SELECT 
    tc.constraint_name, 
    tc.table_name, 
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    rc.delete_rule
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
JOIN information_schema.referential_constraints AS rc
  ON rc.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY' 
  AND tc.table_name='ingresso'
  AND kcu.column_name='compra_id';
"
echo ""

echo "✅ Diagnóstico completo!"
echo ""
echo "⚠️  ATENÇÃO: Se você vir 'CASCADE' no delete_rule acima,"
echo "    significa que deletar uma compra vai deletar TODOS os ingressos!"
