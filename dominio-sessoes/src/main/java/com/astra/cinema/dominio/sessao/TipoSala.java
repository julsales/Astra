package com.astra.cinema.dominio.sessao;

/**
 * Enum que define os tipos de sala disponíveis no cinema.
 */
public enum TipoSala {
    PADRAO("Padrão"),
    VIP("VIP"),
    IMAX("IMAX"),
    TRES_D("3D"),
    QUATRO_DX("4DX"),
    DOLBY_ATMOS("Dolby Atmos");

    private final String descricao;

    TipoSala(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
