package it.unina.biogarden.dao;

import java.util.List;

import it.unina.biogarden.model.Lotto;

public interface LottoDao {

	public List<Lotto> findByProprietario(String emailProprietario)throws Exception;
	
	public Lotto findById(int id_lotto)throws Exception;
	
	public int create(Lotto l)throws Exception;
	
	public void delete(int id_lotto)throws Exception;
	
}
