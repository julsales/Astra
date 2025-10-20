Feature: Selecionar Tipo de Ingresso

  Scenario: Selecionar ingresso do tipo inteira
    Given um cliente com ID 1
    And uma compra pendente
    When o cliente adiciona um ingresso do tipo "INTEIRA"
    Then o ingresso é adicionado com o tipo "INTEIRA"

  Scenario: Selecionar ingresso do tipo meia-entrada
    Given um cliente com ID 1
    And uma compra pendente
    When o cliente adiciona um ingresso do tipo "MEIA"
    Then o ingresso é adicionado com o tipo "MEIA"

  Scenario: Selecionar ingresso do tipo promocional
    Given um cliente com ID 1
    And uma compra pendente
    When o cliente adiciona um ingresso do tipo "PROMOCAO"
    Then o ingresso é adicionado com o tipo "PROMOCAO"

  Scenario: Alterar tipo de ingresso após confirmação da compra
    Given um cliente com ID 1
    And uma compra confirmada com ingresso do tipo "INTEIRA"
    When o cliente tenta adicionar outro ingresso
    Then o sistema não permite adicionar ingressos após confirmação
