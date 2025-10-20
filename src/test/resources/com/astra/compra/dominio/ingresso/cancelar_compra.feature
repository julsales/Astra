Feature: Cancelar Compra

  Scenario: Cancelar compra pendente
    Given um cliente com ID 1
    And uma compra pendente com 2 ingressos
    When o cliente cancela a compra
    Then a compra tem status "CANCELADA"
    And todos os ingressos estão cancelados

  Scenario: Cancelar compra confirmada sem ingressos utilizados
    Given um cliente com ID 1
    And uma compra confirmada com 2 ingressos válidos
    When o cliente cancela a compra
    Then a compra tem status "CANCELADA"
    And todos os ingressos estão cancelados

  Scenario: Tentativa de cancelar compra com ingresso utilizado
    Given um cliente com ID 1
    And uma compra confirmada com um ingresso já utilizado
    When o cliente tenta cancelar a compra
    Then o sistema informa que não é possível cancelar
    And a compra continua com status "CONFIRMADA"

  Scenario: Tentativa de cancelar compra já cancelada
    Given um cliente com ID 1
    And uma compra já cancelada
    When o cliente tenta cancelar a compra novamente
    Then o sistema informa que a compra já está cancelada
