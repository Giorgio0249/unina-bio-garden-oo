package it.unina.biogarden.service;

import it.unina.biogarden.dao.ColtivatoreDao;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.model.Coltivatore;

 class ColtivatoreService {//specificatore default perchè deve essere chiamato solo all'interno del package non da altri
	
	private final ColtivatoreDao coltivatoreDao;
	
	public ColtivatoreService(ColtivatoreDao coltivatoreDao) {
		this.coltivatoreDao=coltivatoreDao;
	}
	
	public ColtivatoreService() {
		this(DaoFactory.createColtivatoreDao());
	}
	
	public Coltivatore findByEmail(String email)throws Exception{
		Coltivatore coltivatore=coltivatoreDao.findByEmail(email);
		
		if(coltivatore==null) {
			throw new IllegalArgumentException("Nessun coltivatore trovato associato all'email inserita.");
		}
		return coltivatore;
	}

}
