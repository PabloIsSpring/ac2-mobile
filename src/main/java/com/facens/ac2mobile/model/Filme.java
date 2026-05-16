package com.facens.ac2mobile.model;

public class Filme {

    private String id;
    private String nome;
    private String tipo;
    private String genero;
    private int anoLancamento;
    private int notaPessoal;
    private boolean jaAssistiu;

    public Filme(String id, String nome, String tipo, String genero, int anoLancamento, int notaPessoal, boolean jaAssistiu) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.genero = genero;
        this.anoLancamento = anoLancamento;
        this.notaPessoal = notaPessoal;
        this.jaAssistiu = jaAssistiu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(int anoLançamento) {
        this.anoLancamento = anoLançamento;
    }

    public int getNotaPessoal() {
        return notaPessoal;
    }

    public void setNotaPessoal(int notaPessoal) {
        this.notaPessoal = notaPessoal;
    }

    public boolean isJaAssistiu() {
        return jaAssistiu;
    }

    public void setJaAssistiu(boolean jaAssistiu) {
        this.jaAssistiu = jaAssistiu;
    }
}
