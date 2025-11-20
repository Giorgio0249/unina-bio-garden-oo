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
	
	

}
