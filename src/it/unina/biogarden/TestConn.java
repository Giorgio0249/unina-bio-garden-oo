package it.unina.biogarden;

import it.unina.biogarden.dao.ConnectionFactory;
import java.sql.Connection;

public class TestConn {
	
	public static void main(String[] args) {
		
		try (Connection c=ConnectionFactory.getInstance().getConnection()){
			System.out.println("CONNESSIONE RIUSCITA!!!");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public ProgettoStagionale findById(int id_progetto) throws Exception{
		ProgettoStagionale progetto=progettoDao.findById(id_progetto);
		if(progetto==null) {
			throw new IllegalArgumentException("Nessun progetto con id ("+id_progetto+") trovato.");
		}
		else
			return progetto;
	}
	
	

}
