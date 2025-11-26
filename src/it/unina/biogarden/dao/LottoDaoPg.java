package it.unina.biogarden.dao;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;

import it.unina.biogarden.model.Lotto;

	class LottoDaoPg implements LottoDao{
	
	@Override
	public List<Lotto> findByProprietario(String emailProprietario)throws Exception{
		
		String sql="""
				SELECT
					l.id_lotto,
					l.posizione,
					l.superficie,
					l.fk_proprietario
				FROM Lotto l
				WHERE l.fk_proprietario=?
				ORDER BY id_lotto
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, emailProprietario);
			
			try(ResultSet rs=ps.executeQuery()){
				
				List<Lotto> out=new ArrayList<>();
				
				while(rs.next()) {
					out.add(new Lotto(
							rs.getInt("id_lotto"),
							rs.getString("posizione"),
							rs.getBigDecimal("superficie"),
							rs.getString("fk_proprietario")));

				}
				
				return out;
			}
		}
	}
	
	
	@Override
	public Lotto findById(int id_lotto)throws Exception{
		
		String sql="""
				SELECT
					l.id_lotto,
					l.posizione,
					l.superficie,
					l.fk_proprietario
				FROM Lotto l
				WHERE l.id_lotto=?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_lotto);
			
			try(ResultSet rs=ps.executeQuery()){
				
				if(rs.next()) {
					return new Lotto(
							rs.getInt("id_lotto"),
							rs.getString("posizione"),
							rs.getBigDecimal("superficie"),
							rs.getString("fk_proprietario"));
				}
				
				return null;
			}
		}
	}
	
	public List<Lotto> findAll()throws Exception{
		String sql="""
				SELECT 
					l.id_lotto,
					l.posizione,
					l.superficie,
					l.fk_proprietario
				FROM Lotto l
				ORDER BY l.id_lotto
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql)){
			
			List<Lotto> out=new ArrayList<>();
			
			while(rs.next()) {
				out.add(new Lotto(rs.getInt("id_lotto"),
									rs.getString("posizione"),
									rs.getBigDecimal("superficie"),
									rs.getString("fk_proprietario")));
			}
			return out;
		}
		
	}

}




