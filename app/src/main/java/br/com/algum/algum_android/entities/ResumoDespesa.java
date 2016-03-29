package br.com.algum.algum_android.entities;

/**
 * Created by sn1007071 on 09/03/2016.
 */
public class ResumoDespesa {
    private String nome;
    private float valorRealizado;
    private float valorPlanejado;

    public ResumoDespesa(String nome, float valorRealizado, float valorPlanejado) {
        this.nome = nome;
        this.valorRealizado = valorRealizado;
        this.valorPlanejado = valorPlanejado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getValorRealizado() {
        return valorRealizado;
    }

    public void setValorRealizado(float valorRealizado) {
        this.valorRealizado = valorRealizado;
    }

    public float getValorPlanejado() {
        return valorPlanejado;
    }

    public void setValorPlanejado(float valorPlanejado) {
        this.valorPlanejado = valorPlanejado;
    }
}
