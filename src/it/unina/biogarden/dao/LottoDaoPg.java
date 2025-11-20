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
	
	@Override
	public int create(Lotto l)throws Exception{
		String sql="""
				INSERT INTO Lotto(posizione, superficie, fk_proprietario)
				VALUES
				(?, ?, ?)
				RETURNING id_lotto
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, l.getPosizione());
			ps.setBigDecimal(2, l.getSuperficie());
			ps.setString(3, l.getFk_proprietario());
			
			try(ResultSet rs=ps.executeQuery()){
				rs.next();
				return rs.getInt("id_lotto");
			}
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;			
		}
		
	}
	
	
	@Override
	public void delete(int id_lotto)throws Exception{
		String sql="""
				DELETE FROM Lotto
				WHERE id_lotto = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setInt(1, id_lotto);
			
			int rimossi=ps.executeUpdate();
			
			if(rimossi==0) {
				System.out.println("Nessun lotto trovato con id: "+id_lotto);
			}
			else {
				System.out.format("Lotto (%d) rimosso con successo.\n", id_lotto);
			}

		}
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}

}




