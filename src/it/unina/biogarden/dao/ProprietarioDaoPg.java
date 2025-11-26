package it.unina.biogarden.dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.sql.*;

import it.unina.biogarden.model.Proprietario;


 class ProprietarioDaoPg implements ProprietarioDao{
	
	@Override
	public Proprietario authenticate(String email, String password)throws Exception{
		
		String sql="""
				SELECT
					p.email,
					p.nome,
					p.cognome,
					p.dataN
				FROM Proprietario p
				WHERE p.email=? AND p.password=?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, email);
			ps.setString(2, password);
			
			try(ResultSet rs=ps.executeQuery()){
				if(rs.next()) {
					return new Proprietario(
							rs.getString("email"),
							rs.getString("nome"),
							rs.getString("cognome"),
							rs.getObject("dataN", LocalDate.class));
				}
				
				return null;
			}
		}
	}
	
	@Override
	public Proprietario findByEmail(String email)throws Exception{
		
		String sql="""
				SELECT 
					p.email, 
					p.nome, 
					p.cognome, 
					p.dataN
				FROM Proprietario p
				WHERE p.email=?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
				ps.setString(1, email);
				
				try(ResultSet rs=ps.executeQuery()){
					
					if(rs.next()) {
						return new Proprietario(
								rs.getString("email"),
								rs.getString("nome"),
								rs.getString("cognome"),
								rs.getObject("dataN", LocalDate.class)
								);
					}
					
					return null;
					
				}
		}
		
	}

}
