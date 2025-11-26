package it.unina.biogarden.dao;

import java.util.List;

import it.unina.biogarden.model.Lotto;

public interface LottoDao {

	public List<Lotto> findByProprietario(String emailProprietario)throws Exception;
	
	public Lotto findById(int id_lotto)throws Exception;
	
	public List<Lotto> findAll()throws Exception;
	
}
