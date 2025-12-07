package com.astra.cinema.dominio.eventos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PADRÃO OBSERVER
 * Publicador central de eventos do sistema (Subject).
 * Permite registro de observadores e notificação quando eventos ocorrem.
 */
public class PublicadorEventos {

    private static final PublicadorEventos INSTANCIA = new PublicadorEventos();

    private final Map<Class<?>, List<ObservadorEvento<?>>> observadores = new HashMap<>();

    private PublicadorEventos() {
        // Singleton
    }

    public static PublicadorEventos getInstancia() {
        return INSTANCIA;
    }

    /**
     * Registra um observador para um tipo específico de evento.
     */
    public <T> void registrar(ObservadorEvento<T> observador) {
        Class<T> tipoEvento = observador.getTipoEvento();
        observadores.computeIfAbsent(tipoEvento, k -> new ArrayList<>()).add(observador);
    }

    /**
     * Remove um observador.
     */
    public <T> void remover(ObservadorEvento<T> observador) {
        Class<T> tipoEvento = observador.getTipoEvento();
        List<ObservadorEvento<?>> lista = observadores.get(tipoEvento);
        if (lista != null) {
            lista.remove(observador);
        }
    }

    /**
     * Publica um evento, notificando todos os observadores registrados.
     */
    @SuppressWarnings("unchecked")
    public <T> void publicar(T evento) {
        if (evento == null) {
            return;
        }

        Class<?> tipoEvento = evento.getClass();
        List<ObservadorEvento<?>> lista = observadores.get(tipoEvento);

        if (lista != null) {
            for (ObservadorEvento<?> observador : lista) {
                try {
                    ((ObservadorEvento<T>) observador).atualizar(evento);
                } catch (Exception e) {
                    // Log do erro, mas continua notificando outros observadores
                    System.err.println("Erro ao notificar observador: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Limpa todos os observadores (útil para testes).
     */
    public void limpar() {
        observadores.clear();
    }
}
