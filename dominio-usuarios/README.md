# Domínio de Usuários

## Descrição
Bounded Context responsável por gerenciar **identidade e acesso** dos usuários do sistema cinema.

## Agregados

### Usuario (Raiz do Agregado)
- **Entidades:** `Usuario`, `Cliente`, `Funcionario`
- **Value Objects:** `Cargo`, `TipoUsuario`
- **Repositórios:** `UsuarioRepositorio`, `ClienteRepositorio`, `FuncionarioRepositorio`
- **Services:** `ClienteService`

## Responsabilidades

### O que PERTENCE a este domínio:
✅ Cadastro e autenticação de usuários
✅ Gerenciamento de clientes (cadastro, atualização)
✅ Gerenciamento de funcionários e seus cargos
✅ Controle de permissões (GERENTE, ATENDENTE)

### O que NÃO pertence a este domínio:
❌ Compras de ingressos (domínio-vendas)
❌ Validação de ingressos (domínio-vendas)
❌ Gerenciamento de sessões (domínio-sessoes)

## Regras de Negócio
- **RN11:** Apenas funcionários com cargo GERENTE podem executar operações administrativas
- Funcionários podem ter diferentes cargos (GERENTE, ATENDENTE)
- Clientes podem ou não ter conta no sistema

## Dependências
- `dominio-compartilhado` (Value Objects compartilhados)

## Estrutura de Pacotes
```
com.astra.cinema.dominio.usuario/
├── Usuario.java (Entidade base)
├── Cliente.java (Especialização de Usuario)
├── Funcionario.java (Especialização de Usuario)
├── Cargo.java (Enum)
├── TipoUsuario.java (Enum)
├── UsuarioId.java → movido para dominio-compartilhado
├── UsuarioRepositorio.java (Interface)
├── ClienteRepositorio.java (Interface)
├── FuncionarioRepositorio.java (Interface)
└── ClienteService.java (Domain Service)
```

## Linguagem Ubíqua
- **Cliente:** Usuário que compra ingressos e produtos
- **Funcionário:** Usuário que opera o sistema (GERENTE ou ATENDENTE)
- **Gerente:** Funcionário com permissões administrativas completas
- **Atendente:** Funcionário com permissões limitadas de operação
