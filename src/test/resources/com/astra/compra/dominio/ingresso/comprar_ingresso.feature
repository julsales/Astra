Feature: Comprar Ingresso

  Scenario: Compra iniciada com ingresso inteiro
    Given um cliente com ID 1
    When o cliente inicia uma compra
    And adiciona um ingresso "inteiro" para a sessão 1 no assento "A1"
    Then a compra é criada com status "PENDENTE"
    And a compra possui 1 ingresso

  Scenario: Compra iniciada com ingresso meia-entrada
    Given um cliente com ID 1
    When o cliente inicia uma compra
    And adiciona um ingresso "meia" para a sessão 1 no assento "B2"
    Then a compra é criada com status "PENDENTE"
    And o ingresso tem tipo "MEIA"

  Scenario: Compra iniciada com ingresso promocional
    Given um cliente com ID 1
    When o cliente inicia uma compra
    And adiciona um ingresso "promocional" para a sessão 1 no assento "C3"
    Then a compra é criada com status "PENDENTE"
    And o ingresso tem tipo "PROMOCAO"

  Scenario: Adicionar múltiplos ingressos à compra
    Given um cliente com ID 1
    When o cliente inicia uma compra
    And adiciona um ingresso "inteiro" para a sessão 1 no assento "A1"
    And adiciona um ingresso "meia" para a sessão 1 no assento "A2"
    Then a compra possui 2 ingressos
