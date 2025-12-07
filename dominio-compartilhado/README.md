# Domínio Compartilhado (Shared Kernel)

## Descrição
Este módulo contém os **conceitos compartilhados** entre todos os bounded contexts do sistema. Representa o núcleo comum que todos os domínios precisam para se comunicar.

## Padrão DDD: Shared Kernel
O Shared Kernel é um padrão do Domain-Driven Design que define elementos compartilhados entre múltiplos contextos delimitados (Bounded Contexts).

### ⚠️ Princípios Importantes
- **APENAS** Value Objects e validações genéricas devem estar aqui
- **NÃO** deve conter entidades de negócio específicas
- **NÃO** deve conter lógica de regras de negócio
- Mudanças aqui afetam TODOS os domínios

## Estrutura

### `/comum`
Contém Value Objects e utilitários compartilhados:

#### Value Objects (Identificadores)
- `AssentoId` - Identificador único de assento
- `ClienteId` - Identificador único de cliente
- `CompraId` - Identificador único de compra
- `FilmeId` - Identificador único de filme
- `FuncionarioId` - Identificador único de funcionário
- `IngressoId` - Identificador único de ingresso
- `PagamentoId` - Identificador único de pagamento
- `ProdutoId` - Identificador único de produto
- `ProgramacaoId` - Identificador único de programação
- `RemarcacaoId` - Identificador único de remarcação
- `SessaoId` - Identificador único de sessão
- `UsuarioId` - Identificador único de usuário
- `ValidacaoIngressoId` - Identificador único de validação
- `VendaId` - Identificador único de venda

#### Utilitários
- `ValidacaoDominio` - Métodos estáticos para validações comuns

## Dependências
- **Nenhuma dependência de outros domínios**
- Apenas bibliotecas de infraestrutura básicas (commons-validator, commons-lang3)

## Uso Correto
✅ **CORRETO:** Adicionar novos Value Objects que são usados por múltiplos domínios
✅ **CORRETO:** Adicionar validações genéricas reutilizáveis

❌ **ERRADO:** Adicionar entidades de negócio (Compra, Sessao, Produto, etc)
❌ **ERRADO:** Adicionar services ou repositórios
❌ **ERRADO:** Adicionar lógica de regras de negócio específicas
