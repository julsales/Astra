# Plano de Alinhamento Astra ↔ SGB

## 1. Resumo Executivo
O objetivo é fazer com que o projeto **Astra** replique a organização arquitetural do **SGB (Sistema de Gestão Bibliotecária)** em termos de módulos Maven, convenções de pacotes, infraestrutura e processos de build/deploy. Concluímos o primeiro ciclo de trabalho: o código está redistribuído por módulo, os `pom.xml` foram alinhados, Postgres/Flyway/docker passaram a fazer parte do fluxo local e o frontend ganhou um módulo dedicado servido pelo backend via artefato Maven. Este documento captura o cenário original, registra o que já foi implementado e aponta os itens restantes para chegar a 100% da paridade.

## 2. Referência: Estrutura do SGB
| Camada / Módulo | Responsabilidade principal | Observações relevantes |
|-----------------|----------------------------|------------------------|
| `pai`           | Parent POM com Spring Boot 3.5.7, propriedades centrais, dependencyManagement (Vaadin, commons-validator, modelmapper, springdoc) e plugins (Surefire, depgraph). |
| `dominio-<contexto>` | Código de domínio puro por subdomínio (acervo, administração, análise, etc.) com invariantes usando `org.apache.commons.lang3.Validate`. | Um módulo por contexto garante baixo acoplamento. |
| `aplicacao`     | Serviços e casos de uso que orquestram os domínios. Depende apenas dos módulos de domínio. |
| `infraestrutura`| Integrações (JPA, Flyway, ModelMapper) com dependência de `aplicacao`. |
| `apresentacao-backend` | API REST Spring Boot + SpringDoc, ModelMapper, bancos H2/Postgres, depende de `aplicacao` e `infraestrutura`. |
| `apresentacao-frontend` | Módulo Maven com Angular; build acionado via `exec-maven-plugin` e _copy-resources_ para `resources/static`. |
| `apresentacao-vaadin`   | Cliente Vaadin separado (mesmo parent). |
| `docker-compose-sgb.yml`| Orquestra Postgres + imagem `sgb-apresentacao-frontend`. |

Outros padrões notáveis:
- Nome de pacote raiz `dev.sauloaraujo.sgb.<contexto>` por módulo.
- Banco Postgres gerido via Docker Compose e migrações Flyway.
- Contêinerização via `jib-maven-plugin`.
- Testes de aceitação com Cucumber nos domínios (via BOM centralizado).

## 3. Estado do Astra antes do alinhamento
| Área | Situação atual |
|------|----------------|
| Raiz (`pom.xml`) | Packaging `pom`, módulos declarados (`pai`, `dominio`, `aplicacao`, `infraestrutura`, `apresentacao-backend`), porém **todo** o código (domínio, aplicação, infraestrutura, adapters) está em `src/main/java/com/astra/cinema`. |
| `dominio / aplicacao / infraestrutura` | Módulos Maven vazios (somente `pom.xml` + `target/`). Não exportam artefatos; o backend Spring Boot não depende deles. |
| `apresentacao-backend` | Contém apenas `AstraApplication`. Nenhum controller/bean/repositório é empacotado aqui; durante o build o JAR resultante só leva a classe principal. |
| Frontend | Pasta `frontend/` React criada fora de qualquer módulo Maven; build é disparado a partir do backend (frontend-maven-plugin) e cópia para `target/classes/static`. |
| Infraestrutura | Não há `docker-compose`, `Flyway`, nem scripts DB versionados. Há arquivos H2 (`data/astradb.mv.db`). |
| Convenções de código | Pacote raiz `com.astra.cinema`. Validações manuais (`IllegalArgumentException`) ao invés de `Validate`. Controllers expõem DTOs `record` no mesmo arquivo. |

## 4. Estado do Astra após o alinhamento de 15/11/2025
| Área | Mudanças aplicadas |
|------|--------------------|
| Modularização | Código de domínio, aplicação, infraestrutura e apresentação foi distribuído nos respectivos módulos Maven. `apresentacao-backend` agora empacota apenas adapters/controllers e depende de `astra-aplicacao`, `astra-infraestrutura` e do novo módulo `astra-apresentacao-frontend`. |
| Parent POM | `pai/pom.xml` replica o dependencyManagement do SGB (Spring Boot 3.5.7, BOM do Cucumber, commons-validator, modelmapper, springdoc, Flyway) e adiciona plugins `maven-surefire` + `depgraph`. |
| Infraestrutura | Criados `docker-compose.yml` e `docker-compose-astra.yml` com Postgres 16 e serviço da aplicação, além de `infraestrutura/src/main/resources/db/migration/V1__create_core_tables.sql` gerenciado pelo Flyway. `application.properties` troca H2 por Postgres e ativa Flyway. |
| Frontend | Pasta React foi migrada para o módulo Maven `apresentacao-frontend`, que usa `exec-maven-plugin` + `maven-resources-plugin` como no SGB. O backend consome o artefato gerado para servir os assets estáticos. |
| Containerização | `apresentacao-backend` agora publica imagem Docker via `jib-maven-plugin` (imagem `astra-apresentacao-backend:${project.version}`) e consome as variáveis `DATABASE_*` fornecidas pelos arquivos Compose. |
| Build | `mvn -DskipTests package` executa os módulos em cadeia, incluindo o build do React (`npm install` + `npm run build`). |
| Qualidade do domínio | Criado `ValidacaoDominio` centralizando invariantes com `commons-lang3` e aplicado nas entidades críticas (`Compra`, `Ingresso`, `Programacao`, `Sessao`, `Pagamento`, `Cliente`, `Usuario`, `Funcionario`). |

> Resultado: os itens 1–4 do plano de ação foram concluídos; restam ações de qualidade (testes BDD adicionais e futura divisão em subdomínios) para atingir paridade total com o SGB.

## 5. Gaps Identificados
> A tabela mantém o histórico de lacunas mapeadas. As quatro primeiras já foram tratadas neste ciclo (✅); as demais seguem pendentes.

| Tema | Como está no SGB | Como está no Astra | Impacto |
|------|-----------------|---------------------|---------|
| Modularização ✅ | Cada camada/domínio em módulo próprio compilável. | Módulos ativos; código redistribuído e dependências encadeadas (`apresentacao-backend` consome `astra-aplicacao`, `astra-infraestrutura`, `astra-dominio`). | Gap fechado: artefatos reutilizáveis e JAR final completo. |
| Estrutura de pacotes ✅ | `dev.sauloaraujo.sgb.<modulo>.<contexto>` alinhado com módulos Maven. | Pacotes `com.astra.cinema.<camada>` agora residem nos módulos corretos; próximos passos envolvem subdividir por contexto. | Gap parcialmente fechado; resta granularidade por subdomínio. |
| Infra DB ✅ | Docker Compose Postgres + Flyway + JPA com Postgres driver. | Compose + Postgres 16 + Flyway adicionados, propriedades usam variáveis `DATABASE_*`. | Gap fechado: ambiente local replica SGB. |
| Frontend ✅ | Angular módulo Maven (`apresentacao-frontend`) com build integrado e artefato Docker via Jib. | React encapsulado em módulo Maven dedicado; build automatizado e assets empacotados. | Gap fechado mantendo React como tecnologia. |
| Contêinerização ✅ | `jib-maven-plugin` gera imagem `sgb-apresentacao-frontend`. | Backend publica imagem via Jib; compose referencia a imagem gerada. | Gap fechado para backend (frontend continua embutido). |
| Testes | Cucumber + JUnit com BOM central. | Suite BDD movida para o módulo `astra-dominio`, rodada via `RunCucumberTest`. Ainda precisamos expandir a cobertura e integrar fluxos adicionais. | Cobertura inicial já replica a abordagem do SGB e roda no build; adicionar novos cenários segue em aberto. |
| Documentação | `README` + `.cml` descrevendo módulos e histórias. | Documentos atualizados parcialmente (este relatório e README), porém mapa de histórias/CML ainda não refletem nova arquitetura. | Onboarding parcial; falta visão 100% alinhada ao SGB. |

## 6. Plano de Ação Proposto
### Etapa 1 – Preparação (curto prazo)
1. ✅ **Criar pasta `docs/`** e manter este relatório como fonte única da verdade.
2. ✅ **Ativar módulos existentes** movendo o código atual:
   - `com.astra.cinema.dominio` → `dominio/src/main/java/com/astra/cinema/dominio`.
   - `com.astra.cinema.aplicacao` → `aplicacao/src/main/java/com/astra/cinema/aplicacao`.
   - `com.astra.cinema.infraestrutura` → `infraestrutura/src/main/java/com/astra/cinema/infraestrutura`.
   - `com.astra.cinema.interface_adapters` → `apresentacao-backend/src/main/java/com/astra/cinema/interface_adapters`.
   - Ajustar `pom` para que `apresentacao-backend` dependa de `infraestrutura` (runtime) e `aplicacao`.
3. ✅ **Atualizar `parent`** (`pai/pom.xml`) para adicionar dependencyManagement similar ao SGB (modelmapper, commons-validator, springdoc, cucumber) e alinhar versão do Spring Boot (3.5.x).

### Etapa 2 – Infraestrutura & Build (médio prazo)
1. ✅ **Adicionar `docker-compose.yml`** replicando o serviço Postgres de `sgb-2025-01` e um serviço `astra` exposto na porta 8080.
2. ✅ **Introduzir Flyway**: criar `infraestrutura/src/main/resources/db/migration` e scripts iniciais; incluir dependências `flyway-core` e `postgresql` nos módulos adequados.
3. ✅ **Containerização**: aplicar `jib-maven-plugin` no backend, seguindo o exemplo do SGB.
4. ✅ **Frontend dedicado**: transformar `frontend/` em módulo Maven `apresentacao-frontend` ou integrá-lo ao `apresentacao-backend` com plugin e `copy-resources`, espelhando o POM do SGB (exec-maven-plugin + resources plugin). Ajustar `pom.xml` raiz para incluir o novo módulo.

### Etapa 3 – Qualidade e Conformidade (longo prazo)
1. ⏳ **Migrar validações** do domínio para `commons-validator`/`Validate` para uniformizar estilo.
2. ⏳ **Criar submódulos de domínio** se necessário (p. ex. `dominio-programacao`, `dominio-compra`, etc.) seguindo o padrão do SGB.
3. ✅ **Adicionar testes BDD** com Cucumber rodando no módulo `astra-dominio` (casos de compra, sessão, filme, bomboniere, pagamento, usuário) reaproveitando o BOM central.
4. ⏳ **Documentar** fluxos e histórias (pasta `entregáveis/mapa de histórias/` já existe; manter atualizado).

## 7. Próximos Passos Recomendados
1. Concluir a migração das validações restantes para `commons-lang3` (demais contextos, services e builders) e padronizar mensagens de erro entre domínios.
2. Elaborar backlog para quebrar `dominio` em submódulos (compra, sessão, programação, catálogo) seguindo a mesma elástica do SGB.
3. Ampliar a suíte BDD já integrada ao `astra-dominio` cobrindo fluxos adicionais (ex.: relatórios, autenticação) e garantindo traço de requisitos.
4. Expandir documentação funcional (mapa de histórias, C4, README) com os novos fluxos e dependências.
5. Tratar os warnings do build do React (arquivos com BOM) para limpar a saída do pipeline.

## 8. Métricas de Conclusão
- ✅ Cada módulo Maven produz um artefato `.jar` com código real.
- ✅ `apresentacao-backend` depende de `astra-infraestrutura`, `astra-aplicacao` e do pacote de frontend.
- ✅ Builds geram imagem Docker via Jib e funcionam com `docker-compose up` apontando para Postgres.
- ✅ Documentação técnica mínima atualizada (`README`, `docs/sgb-alignment-report.md`).
- ✅ Testes automatizados com Cucumber rodando no `astra-dominio` (alinhados aos cenários do SGB).

> **Status:** Ciclo 1 concluído (estrutura + infraestrutura + build). Ciclo 2 focará em BDD, subdomínios e documentação funcional.
