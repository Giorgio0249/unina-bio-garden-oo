package it.unina.biogarden.model;

public class TipoColtura {
	
	private String nome;
	private int giorniMaturazione;
	
	public TipoColtura(String nome, int giorniMaturazione) {
		this.nome=nome;
		this.giorniMaturazione=giorniMaturazione;
	}
	
	//GETTERS
	public String getNome() {return nome;}
	public int getGiorniMaturazione() {return giorniMaturazione;}
	
	@Override
	public String toString() {
		return nome;
	}

}
