package it.unina.biogarden.dao;

import it.unina.biogarden.model.ProgettoStagionale;
import java.util.List;


public interface ProgettoDao {
	
	public List<ProgettoStagionale> findAll()throws Exception;
	
	public ProgettoStagionale findById(int id_progetto)throws Exception;
	
	public List<ProgettoStagionale> findAllByProprietario(String emailProprietario)throws Exception;
	
	public List<ProgettoStagionale> findByLotto(int id_lotto)throws Exception;
	
	public int create(ProgettoStagionale p)throws Exception;
	
	public int update(ProgettoStagionale p)throws Exception;
	
	public int delete(int id_progetto)throws Exception;

}
