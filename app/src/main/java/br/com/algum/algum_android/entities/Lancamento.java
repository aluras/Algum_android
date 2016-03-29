package br.com.algum.algum_android.entities;

import java.util.Date;

/**
 * Created by sn1007071 on 10/03/2016.
 */
public class Lancamento {
    private Date data;
    private String conta;
    private String grupo;
    private String detalhe;
    private float valor;

    public Lancamento(Date data, String grupo, float valor) {
        this.data = data;
        this.grupo = grupo;
        this.valor = valor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}
