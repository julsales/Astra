package com.astra.cinema.dominio.eventos;

/**
 * PADRÃO OBSERVER
 * Interface para observadores que reagem a eventos do sistema.
 *
 * @param <T> Tipo do evento
 */
public interface ObservadorEvento<T> {

    /**
     * Método chamado quando um evento ocorre.
     *
     * @param evento Evento que ocorreu
     */
    void atualizar(T evento);

    /**
     * Retorna o tipo de evento que este observador escuta.
     */
    Class<T> getTipoEvento();
}
