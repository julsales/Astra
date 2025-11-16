-- Baseline da infraestrutura Astra alinhada ao SGB
CREATE TABLE IF NOT EXISTS filme (
    id                      SERIAL PRIMARY KEY,
    titulo                  VARCHAR(255) NOT NULL,
    sinopse                 TEXT,
    classificacao_etaria    VARCHAR(10),
    duracao                 INTEGER      NOT NULL,
    status                  VARCHAR(30)  NOT NULL,
    criado_em               TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sessao (
    id              SERIAL PRIMARY KEY,
    filme_id        INTEGER       NOT NULL REFERENCES filme(id),
    horario         TIMESTAMP WITH TIME ZONE NOT NULL,
    status          VARCHAR(30)   NOT NULL,
    capacidade      INTEGER       NOT NULL,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sessao_assento (
    sessao_id       INTEGER       NOT NULL REFERENCES sessao(id) ON DELETE CASCADE,
    assento_id      VARCHAR(10)   NOT NULL,
    disponivel      BOOLEAN       NOT NULL DEFAULT TRUE,
    PRIMARY KEY (sessao_id, assento_id)
);

CREATE TABLE IF NOT EXISTS cliente (
    id              SERIAL PRIMARY KEY,
    nome            VARCHAR(200) NOT NULL,
    email           VARCHAR(200) UNIQUE NOT NULL,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS compra (
    id              SERIAL PRIMARY KEY,
    cliente_id      INTEGER NOT NULL REFERENCES cliente(id),
    status          VARCHAR(30) NOT NULL,
    pagamento_id    INTEGER,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ingresso (
    id              SERIAL PRIMARY KEY,
    compra_id       INTEGER NOT NULL REFERENCES compra(id) ON DELETE CASCADE,
    sessao_id       INTEGER NOT NULL REFERENCES sessao(id),
    assento         VARCHAR(10) NOT NULL,
    tipo            VARCHAR(20) NOT NULL,
    status          VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS pagamento (
    id              SERIAL PRIMARY KEY,
    status          VARCHAR(20) NOT NULL,
    valor           DOUBLE PRECISION NOT NULL,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS produto (
    id              SERIAL PRIMARY KEY,
    nome            VARCHAR(120) NOT NULL,
    preco           DOUBLE PRECISION NOT NULL,
    estoque         INTEGER NOT NULL,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS venda (
    id              SERIAL PRIMARY KEY,
    produto_id      INTEGER NOT NULL REFERENCES produto(id),
    quantidade      INTEGER NOT NULL,
    pagamento_id    INTEGER REFERENCES pagamento(id),
    status          VARCHAR(20) NOT NULL,
    criado_em       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS funcionario (
    id              SERIAL PRIMARY KEY,
    nome            VARCHAR(200) NOT NULL,
    cargo           VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario (
    id              SERIAL PRIMARY KEY,
    email           VARCHAR(200) UNIQUE NOT NULL,
    senha           VARCHAR(200) NOT NULL,
    nome            VARCHAR(200) NOT NULL,
    tipo            VARCHAR(50) NOT NULL
);
