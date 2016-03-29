package br.com.algum.algum_android.entities;

/**
 * Created by sn1007071 on 09/03/2016.
 */
public class Grupo {
    private String nome;
    private int cor;

    public Grupo(String nome, int cor) {
        this.nome = nome;
        this.cor = cor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}
