# Camada de Aplicação

## Descrição
Camada responsável por **orquestrar casos de uso** (Use Cases) que coordenam múltiplos agregados e domínios.

## Padrão DDD: Application Layer
A camada de aplicação não contém lógica de negócio, apenas:
- Coordena chamadas entre diferentes agregados
- Gerencia transações
- Converte entre DTOs e entidades de domínio
- Implementa casos de uso específicos da aplicação

## Estrutura

### Use Cases (Casos de Uso)
Organizados por contexto de negócio:

```
com.astra.cinema.aplicacao/
├── bomboniere/
│   ├── AdicionarProdutoUseCase.java
│   ├── AjusteEstoqueUseCase.java
│   ├── EntradaEstoqueUseCase.java
│   ├── ModificarProdutoUseCase.java
│   ├── RemoverProdutoUseCase.java
│   └── VenderProdutoUseCase.java
├── compra/
│   ├── CancelarCompraUseCase.java
│   ├── ConfirmarCompraUseCase.java
│   └── IniciarCompraUseCase.java
├── filme/
│   ├── AdicionarFilmeUseCase.java
│   ├── AlterarFilmeUseCase.java
│   └── RemoverFilmeUseCase.java
├── funcionario/
│   ├── ConsultarHistoricoFuncionarioUseCase.java
│   ├── RemarcarIngressoFuncionarioUseCase.java
│   └── ValidarIngressoFuncionarioUseCase.java
├── ingresso/
│   ├── RemarcarIngressoUseCase.java
│   └── ValidarIngressoUseCase.java
├── pagamento/
│   └── AutorizarPagamentoUseCase.java
├── programacao/
│   └── CriarProgramacaoUseCase.java
├── sessao/
│   ├── CriarSessaoUseCase.java
│   ├── ModificarSessaoUseCase.java
│   ├── RemarcarIngressosSessaoUseCase.java
│   └── RemoverSessaoUseCase.java
└── usuario/
    ├── AutenticarUsuarioUseCase.java
    ├── GerenciarCinemaUseCase.java
    ├── UsuarioDTO.java
    └── funcionario/
        └── GerenciarFuncionariosUseCase.java
```

## Princípios

### ✅ O que PERTENCE à camada de aplicação:
- Use Cases que orquestram múltiplos agregados
- Coordenação de transações
- Conversão entre DTOs e entidades
- Casos de uso específicos da interface do usuário

### ❌ O que NÃO pertence à camada de aplicação:
- Lógica de negócio (vai para Domain Services)
- Validações de regras de negócio (vai para Entidades/Services de domínio)
- Acesso direto a infraestrutura (banco, APIs externas)
- Application Services que apenas delegam (foram REMOVIDOS)

## Diferença: Use Case vs Domain Service

### Domain Service (camada de domínio)
```java
// Contém LÓGICA DE NEGÓCIO
public class CompraService {
    public void cancelarCompra(CompraId id) {
        var compra = repo.obter(id);
        compra.cancelar(); // <- Regra de negócio

        if (compra.getPagamentoId() != null) {
            var pagamento = pagRepo.obter(...);
            pagamento.cancelar(); // <- Regra de negócio
        }
    }
}
```

### Use Case (camada de aplicação)
```java
// ORQUESTRA múltiplos serviços
public class CancelarCompraENotificarUseCase {
    public void executar(CompraId id) {
        compraService.cancelar(id);      // <- Usa domínio
        notificacaoService.enviar(...);  // <- Usa infra
        auditoria.registrar(...);        // <- Usa infra
    }
}
```

## Dependências
- `dominio-compartilhado`
- `dominio-usuarios`
- `dominio-sessoes`
- `dominio-vendas`
- `dominio-bomboniere`

## Refatoração Recente ⭐
Foram **removidos** os Application Services duplicados que apenas delegavam para Use Cases:
- ❌ `FilmeService` (aplicação) - substituído por `FilmeService` (domínio)
- ❌ `SessaoService` (aplicação) - substituído por `SessaoService` (domínio)
- ❌ `CompraService` (aplicação) - substituído por `CompraService` (domínio)
- ❌ `ProdutoService` (aplicação) - substituído por `ProdutoService` (domínio)
- ❌ `ProgramacaoService` (aplicação) - substituído por `ProgramacaoService` (domínio)

**Razão:** Eliminar duplicação desnecessária. Domain Services contêm lógica de negócio e são suficientes.
