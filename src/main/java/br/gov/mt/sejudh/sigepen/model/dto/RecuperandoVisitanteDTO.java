package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.model.Visitante;

import java.util.ArrayList;
import java.util.List;

public class RecuperandoVisitanteDTO {

    private Long id;
    private String nomeRecuperando;
    private String nomeSocialRecuperando;
    private String nomeEstrutura;
    private List<Visitante> entradaSaidas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeRecuperando() {
        return nomeRecuperando;
    }

    public void setNomeRecuperando(String nomeRecuperando) {
        this.nomeRecuperando = nomeRecuperando;
    }

    public String getNomeSocialRecuperando() {
        return nomeSocialRecuperando;
    }

    public void setNomeSocialRecuperando(String nomeSocialRecuperando) {
        this.nomeSocialRecuperando = nomeSocialRecuperando;
    }

    public String getNomeEstrutura() {
        return nomeEstrutura;
    }

    public void setNomeEstrutura(String nomeEstrutura) {
        this.nomeEstrutura = nomeEstrutura;
    }

    public List<Visitante> getEntradaSaidas() {
        return entradaSaidas;
    }

    public void setEntradaSaidas(List<Visitante> entradaSaidas) {
        this.entradaSaidas = entradaSaidas;
    }

}
