package it.unina.biogarden.dao;

import java.util.*;
import java.sql.*;
import java.time.LocalDate;

import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.model.TipoAttivita;
import it.unina.biogarden.model.StatoAttivita;

class ColtivatoreDaoPg implements ColtivatoreDao{

	
	@Override
	public Coltivatore Authenticate(String email, String password) throws Exception {
	    String sql = """
	    		SELECT email, 
	    				nome,
	    				cognome,
	    				dataN
	    		FROM Coltivatore 
	    		WHERE email = ? AND password = ?
	    		""";
	    
	    try (Connection conn = ConnectionFactory.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setString(1, email);
	        ps.setString(2, password);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return new Coltivatore(
	                    rs.getString("email"),
	                    rs.getString("nome"),
	                    rs.getString("cognome"),
	                    rs.getObject("dataN", LocalDate.class)
	                );
	            }
	        }
	    }
	    return null; 
	}

	@Override
	public Coltivatore findByEmail(String email)throws Exception{
		String sql="""
				SELECT
					c.email,
					c.nome,
					c.cognome,
					c.dataN
				FROM Coltivatore c
				WHERE c.email = ?
				""";
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
				
				ps.setString(1, email);
				
				try(ResultSet rs=ps.executeQuery()){
					if(rs.next()) {
						return new Coltivatore(rs.getString("email"),
												rs.getString("nome"),
												rs.getString("cognome"),
												rs.getObject("dataN", LocalDate.class));
					}
					return null;
				}
		}
	}
	
	@Override
	public List<Coltivatore> findAll() throws Exception {
	    List<Coltivatore> lista = new ArrayList<>();
	    String sql ="""
	    		SELECT email, nome, cognome, dataN
	    		from Coltivatore
	    		""";
	    
	    try (Connection conn = ConnectionFactory.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	            lista.add(new Coltivatore(
	                rs.getString("email"),
	                rs.getString("nome"),
	                rs.getString("cognome"),
	                rs.getObject("dataN", LocalDate.class)
	            ));
	        }
	    }
	    return lista;
	}
	
	@Override
	public List<Coltivatore> findColtivatoriPerProprietario(String emailProprietario) throws Exception {
	    String sql = """
	        SELECT DISTINCT c.* 
	        FROM Coltivatore c
	        JOIN Attivita a ON c.email = a.fk_coltivatore
	        JOIN Coltura col ON a.fk_coltura = col.id_coltura
	        JOIN ProgettoStagionale p ON col.fk_progetto = p.id_progetto
	        JOIN Lotto l ON p.fk_lotto = l.id_lotto
	        WHERE l.fk_proprietario = ?
	    """;
	    
	    List<Coltivatore> lista = new ArrayList<>();
	    try (Connection conn = ConnectionFactory.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setString(1, emailProprietario);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                lista.add(new Coltivatore(
	                    rs.getString("email"),
	                    rs.getString("nome"),
	                    rs.getString("cognome"),
	                    rs.getObject("dataN", LocalDate.class)
	                ));
	            }
	        }
	    }
	    return lista;
	}
	
}
