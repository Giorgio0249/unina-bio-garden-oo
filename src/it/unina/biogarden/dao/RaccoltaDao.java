package it.unina.biogarden.dao;

import java.util.List;

import it.unina.biogarden.model.Raccolta;

public interface RaccoltaDao {
	
	public List<Raccolta> findByColtura(int id_coltura) throws Exception;
	
	public Raccolta findById(int id_raccolta)throws Exception;
	
	public int create(Raccolta r)throws Exception;
	
	//nel nostro modello è un evento puntuale(registrato). Per coerenza non è previsto l'aggiornamento di una raccolta.
	
	public int delete(int id_raccolta)throws Exception;

}
