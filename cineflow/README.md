# CineFlow – Base Operacional Compartilhada

Este diretório concentra os artefatos de dados entregues por outras pessoas do time para que possamos continuar o desenvolvimento do **CineFlow**, o painel operacional do cinema fictício. Os números servem apenas como referência: use-os para validar fluxos, testar consultas e alinhar nomenclaturas com o restante da squad.

## Visão geral dos arquivos

| Arquivo | Objetivo | Quando atualizar |
| --- | --- | --- |
| `cineflow.sql` | Cria o esquema mínimo (filmes, salas, sessões, ingressos, vendas da bomboniere e indicadores diários) + carga de exemplo do dia 13/01. | Sempre que o modelo de dados mudar ou quando precisar de um novo "snapshot" consistente para testes locais. |
| `relatorio_cineflow_diario.csv` | Detalha cada sessão do dia (ocupação, itens de bomboniere, canal dominante etc.). É o relatório granular que abastece dashboards. | Ao fim de cada dia. Gere a partir das tabelas `sessoes`, `ingressos` e `bomboniere_vendas`. |
| `resumo_cineflow_hoje.csv` | Consolida os principais KPIs (ingressos, receitas, taxa média). É a base para notificações rápidas. | Logo após montar o relatório diário, agregando os mesmos dados. |

## Como seguir com o desenvolvimento

1. **Bank de dados (`cineflow.sql`)**
   - Execute o script em um PostgreSQL local: `psql -h localhost -U postgres -f cineflow.sql`.
   - Para novos requisitos (ex.: fidelidade ou cupons), crie tabelas/colunas extras no mesmo arquivo e deixe comentários `-- TODO` explicando o racional.
   - Sempre que inserir dados fictícios, mantenha datas ISO (`YYYY-MM-DD`) e distribua canais (`APP`, `BILHETERIA`, `TOTEM`) para testar filtros.

2. **Relatório diário (`relatorio_cineflow_diario.csv`)**
   - Estrutura fixa recomendada:
     ```text
     data,sessao_id,filme,sala,horario,capacidade,ingressos_confirmados,ingressos_cancelados,taxa_ocupacao_percentual,bomboniere_itens,bomboniere_receita,canal_top_1,observacoes
     ```
   - Alimente cada linha com o `sessao_id` e os agregados vindos das tabelas base. Se ainda não houver automação, gerar via planilha ou query manual é aceitável.
   - Campo `observacoes` é livre: registre hipóteses para que o time de produto acompanhe (ex.: "fila curta" / "push convertido").

3. **Resumo do dia (`resumo_cineflow_hoje.csv`)**
   - Estrutura sugerida:
     ```text
     data,indicador,valor,meta,tendencia,comentario
     ```
   - Use o mesmo `data` do relatório diário. `tendencia` aceita textos curtos (`alta`, `estavel`, `queda`).
   - Lembre-se: valores podem ser aproximados. Informe, no comentário, se a fonte foi parcial (ex.: "bomboniere até 21h").

## Fluxo recomendado

1. **Resetar o banco** com `cineflow.sql` sempre que precisar de um ambiente limpo.
2. **Rodar consultas** para obter métricas do dia:
   - Ocupação por sessão: `SELECT s.id, COUNT(*) ...`
   - Receita: use a view `vw_resumo_financeiro` criada pelo script para somar bilheteria + bomboniere de forma rápida.
3. **Exportar CSVs**:
   - `relatorio_cineflow_diario.csv`: uma linha por sessão.
   - `resumo_cineflow_hoje.csv`: 3 a 6 linhas com indicadores-chave.
4. **Compartilhar**: suba os três arquivos no repositório ou anexe ao card da sprint para manter o histórico.

## Boas práticas para os próximos passos

- **Versione mudanças estruturais**: inclua migrações incrementais (por exemplo, `ALTER TABLE ...`) em vez de editar o dump inteiro quando o banco já estiver em uso.
- **Automatize exportações**: scripts simples em Python/SQL podem gerar os dois CSVs diretamente do banco; documente-os neste diretório quando surgirem.
- **Validação rápida**: antes de entregar, confira se o total de ingressos do resumo bate com a soma do relatório diário. Erros de arredondamento são aceitáveis, mas registre-os em `comentario`.
- **Próximos incrementos sugeridos**:
  - Adicionar coluna de `campanha_id` em `ingressos` para cruzar marketing x vendas.
  - Criar indicador de `ticket médio da bomboniere` no resumo.
  - Registrar cancelamentos com motivo para ajudar o time de experiência.

Com esses passos, qualquer pessoa nova no projeto consegue entender o contexto rapidamente e evoluir os três arquivos sem depender de alinhamentos adicionais.