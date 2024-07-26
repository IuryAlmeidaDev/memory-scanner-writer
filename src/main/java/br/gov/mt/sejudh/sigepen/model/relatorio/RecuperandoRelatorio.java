package br.gov.mt.sejudh.sigepen.model.relatorio;

import java.sql.Blob;
import java.util.Date;

public class RecuperandoRelatorio {
    private String nomeCela;
    private Long matricula;
    private String nomeRecuperando;
    private String nomeSocial;
    private String nomeMae;
    private Date dataNascimento;
    private String emMovimento;
    private Blob foto;

    public String getNomeCela() {
        return nomeCela;
    }

    public void setNomeCela(String nomeCela) {
        this.nomeCela = nomeCela;
    }

    public Long getMatricula() {
        return matricula;
    }

    public void setMatricula(Long matricula) {
        this.matricula = matricula;
    }

    public String getNomeRecuperando() {
        return nomeRecuperando;
    }

    public void setNomeRecuperando(String nomeRecuperando) {
        this.nomeRecuperando = nomeRecuperando;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmMovimento() {
        return emMovimento;
    }

    public void setEmMovimento(String emMovimento) {
        this.emMovimento = emMovimento;
    }

    public Blob getFoto() {
        return foto;
    }

    public void setFoto(Blob foto) {
        this.foto = foto;
    }
}
