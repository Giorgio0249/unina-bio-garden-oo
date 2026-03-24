package it.unina.biogarden.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {
	
	//variabile Singleton
	private static final ConnectionFactory INSTANCE=new ConnectionFactory();
	
	private String url;
	private String user;
	private String password;
	
	private ConnectionFactory() {
		//apro stream in input col file db.properties(strumenti di accesso al db)
		try(InputStream in= getClass().getClassLoader().getResourceAsStream("db.properties")){
			Properties props=new Properties();
			if(in == null) {
				throw new IllegalStateException("File db.properties non trovato.");
			}
			props.load(in);
			url = props.getProperty("db.url");
			user = props.getProperty("db.user");
			password = props.getProperty("db.password");
			
			//carico in memoria il codice del driver di PostgreSQL
			Class.forName("org.postgresql.Driver");
		}
		catch(Exception e) {
		throw new RuntimeException("Errore inizializzando la connessione al DB: "+e.getMessage(), e);
		}
	}
	
	public static ConnectionFactory getInstance() {
		return INSTANCE;
	}
	
	public Connection getConnection() throws Exception{
		//il DriverMenager identifica il driver corretto da utilizzare in base all'url passato in input, il quale stabilisce la connessione al db
		return DriverManager.getConnection(url, user, password);
	}
	

}
