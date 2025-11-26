package it.unina.biogarden.dao;

import java.sql.*;
import java.time.LocalDate;

import it.unina.biogarden.model.Coltivatore;

class ColtivatoreDaoPg implements ColtivatoreDao{

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
	
}
