# Plano de Implementação – Jornada do Cliente Astra

_Data: 16/11/2025_

## 1. Objetivo
Entregar uma experiência completa para o cliente no fluxo “COMPRAR INGRESSO”, cobrindo:

1. **Filmes em Cartaz** – seleção de filme e sessão.
2. **Escolha de Assentos** – mapa visual com estados disponível/selecionado/ocupado.
3. **Bomboniere** – upsell com produtos reais do endpoint `/api/produtos`.
4. **Checkout + Pagamento** – resumo, métodos de pagamento e confirmação.
5. **Confirmação/Sucesso** – recibo com instruções.
6. **Meus Ingressos** – histórico com QR Code e status por ingresso.

## 2. Alinhamento com Backend
| Domínio | Endpoint | Shape/Observações |
| --- | --- | --- |
| Filmes | `GET /api/filmes/em-cartaz` | `{ id, titulo, sinopse, classificacaoEtaria, duracao, status }` |
| Sessões | `GET /api/sessoes/filme/{filmeId}` | Retorna `SessaoDetalhadaDTO` (capacidade, assentosDisponiveis, sala, horario, status). |
| Sessão detalhada | `GET /api/sessoes/{id}` | Mesmo DTO; será usado para enriquecer checkout/assentos. |
| Produtos | `GET /api/produtos` | `{ id, nome, preco, estoque }`. |
| Clientes | `GET /api/clientes` | `{ id, nome, email }` usado para hidratar painéis/logins. |

> ⚠️ **Gaps identificados:** não existe endpoint publicado para `/api/sessoes/{id}/assentos` ou `/api/compras`. Precisamos neutralizar isso na jornada do cliente até que `CompraController` seja reativado.

## 3. Estratégia para Lacunas
1. **Mapa de Assentos**
   - Buscar `GET /api/sessoes/{id}` ao entrar em Assentos.
   - O DTO expõe `capacidade`. Enquanto o endpoint granular não existe, gerar dinamicamente um grid (ex: 10 colunas × `capacidade/10` linhas) e persistir ocupações localmente (estado + `sessionStorage`).
   - Quando o backend expuser o mapa real (`mapaAssentosDisponiveis`), bastará substituir a fonte do array `assentos`.

2. **Compra & Meus Ingressos**
   - Persistir as compras confirmadas no `localStorage` por usuário (`meus-ingressos-${usuario.id}`).
   - Cada compra salva conterá: `codigo`, `data`, `filme`, `sessao`, `assentos`, `produtos`, `total`, `qrCodeBase64`.
   - Endpoint futuro (`/api/compras` + `/api/clientes/{id}/ingressos`) poderá sobrescrever o provider local sem alterar o componente.

## 4. Arquitetura de Componentes
```
ClientePainel
├── Header global (status da jornada, logout)
├── Etapas
│   ├── FilmesPage
│   ├── AssentosPage
│   ├── BombonierePage
│   ├── CheckoutPage
│   ├── SucessoPage
│   └── MeusIngressosPage (nova)
└── Barra lateral/resumo (opcional pós-UX)
```

### Estados Globais do Painel
| Estado | Tipo | Fonte | Consumidores |
| --- | --- | --- | --- |
| `etapa` | `'filmes' … 'meusIngressos'` | Local | Todos |
| `usuario` | objeto | Prop do login | Header, MeusIngressos |
| `filmeSelecionado`, `sessaoSelecionada` | objetos | Filmes → Assentos/Checkout | Assentos, Checkout |
| `carrinho` | `{ ingressos, produtos, totalIngressos, filme, sessao }` | Assentos/Bomboniere | Checkout/Sucesso |
| `historicoIngressos` | array persistido | LocalStorage | MeusIngressos |

## 5. Páginas & Comportamentos

### FilmesPage
- Grid com cartões dos filmes.
- Sessões agrupadas com chips (data/hora, sala, vagas).
- Ações:
  - `onSelecionarSessao(sessao, filme)` → avança para Assentos.
  - CTA “Meus Ingressos” no topo abre nova página.
- Estados adicionais: busca/filtro (opcional), skeleton loaders.

### AssentosPage
- Título com filme + sessão.
- Grid de assentos 10×10 (ou proporcional) com classes CSS por estado.
- Paginação mobile-friendly.
- CTA “Confirmar assentos” habilita Bomboniere.
- Fallback: se `fetch` falhar, exibir mensagem e usar layout default.

### BombonierePage
- Cards responsivos para combos/produtos.
- Controle de quantidade inline.
- Resumo lateral mantendo subtotal ingressos + produtos.
- CTA “Pular” e “Finalizar compra” → Checkout.

### CheckoutPage
- Resumo completo (filme, sessão, assentos, produtos).
- Botões de método de pagamento com estado selecionado.
- Ao confirmar:
  1. Gera payload da compra fictícia.
  2. Simula `POST /api/compras` (quando habilitado) ou resolve localmente.
  3. Persistir no `historicoIngressos` e enviar para Sucesso.

### SucessoPage
- Hero com confirmação e instruções.
- Mostra QR code (gerado via `qrcode` npm lib) e permite baixar/abrir.
- Botões: “Ver Meus Ingressos” e “Voltar ao início”.

### MeusIngressosPage (Novo)
- Lista cards agrupados por data/filme.
- Cada ingresso:
  - Status (Confirmado / Utilizado / Cancelado – random placeholder até backend enviar status real).
  - Sessão, assentos, valor e QR.
  - Botão “Mostrar QR” abre modal com imagem SVG/Canvas.
- Fonte de dados: hook `useMeusIngressos(usuario)` (localStorage + fallback).

## 6. Serviços & Utilitários
- `services/api.js` – wrapper para `fetch` com tratamento básico.
- `hooks/useFetch` – estados padrão (loading, error, data).
- `hooks/useMeusIngressos` – CRUD local + ponte com backend futuro.
- `utils/assentos.js` – gerar grade mock (`generateSeatMap(capacidade)`), marcar ocupados etc.
- `utils/qr.js` – gerar base64 usando `qrcode`.

## 7. UI/UX Diretrizes
- Paleta baseada nos anexos: gradientes roxo/azul, cartões com glassmorphism leve.
- Tipografia Inter 600/400.
- Header fixo mostrando progresso (Filmes → Assentos → Bomboniere → Pagamento).
- Ações principais sempre visíveis em mobile (botões full-width).
- Estados vazios/erros com mensagens amistosas.

## 8. Backlog Técnico
1. ✅ Mapear endpoints & gaps.
2. 🔄 Refatorar `ClientePainel` para incluir etapa `meusIngressos` e provider de histórico.
3. 🔄 Atualizar `Assentos` para usar `generateSeatMap` + fallback quando endpoint real chegar.
4. 🔄 Incluir `MeusIngressosPage` com QR code. Dependências: `npm i qrcode`.
5. 🔄 Ajustar estilos em `PagesStyles.css` para suportar nova página + responsividade.
6. 🔄 Fluxo de persistência em `localStorage` (hook + integrações nas etapas Checkout/Sucesso).
7. 🔄 Rodar `npm test`/`npm run build` no módulo React e `mvn -pl apresentacao-frontend package` no final.

## 9. Riscos & Mitigações
| Risco | Impacto | Mitigação |
| --- | --- | --- |
| Endpoint real de assentos ausente | Assentos podem não refletir disponibilidade real | Gerar mapa sintético + camada de abstração para trocar facilmente. |
| `CompraController` desabilitado | Não há confirmação real no backend | Persistir no cliente e preparar service para ligar no backend quando liberado. |
| UX mobile | Fluxo extenso pode ficar pesado | Testar breakpoints 320–768px, usar colunas empilhadas. |

## 10. Próximos Passos
1. Implementar hook `useClienteJourney` centralizando estado (sessão, carrinho, histórico).
2. Atualizar componentes existentes conforme o plano.
3. Criar novos componentes (`MeusIngressos`, `QrModal`).
4. Revisar estilos e animações.
5. Rodar build/testes + smoke test Docker.
