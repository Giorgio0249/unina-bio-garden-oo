package it.unina.biogarden.dao;

import it.unina.biogarden.model.TipoColtura;
import java.util.List;

public interface TipoColturaDao {
	
	List<TipoColtura> findAll() throws Exception;

}
