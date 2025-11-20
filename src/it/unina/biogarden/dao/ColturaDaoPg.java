package it.unina.biogarden.dao;

import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.StatoColtura;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*Il PreparedStatement ha bisogno dell'SQL al momento della creazione per attivare la precompilazione sul DB.

Lo Statement è solo un contenitore generico che non ha bisogno di conoscere l'SQL fino al momento in cui non gli dici di eseguirlo.*/

	class ColturaDaoPg implements ColturaDao{
	
	@Override
	public List<Coltura> findByProgetto(int id_progetto)throws Exception{
		
		String sql="""
				SELECT c.id_coltura, 
					   c.fk_tipo_coltura, 
					   c.quantitaPrevista, 
					   c.dataSemina, 
					   c.stato::text, 
					   c.fk_progetto
				FROM Coltura c
				WHERE c.fk_progetto = ?
				ORDER BY c.id_coltura
				""";
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection();
			//la query viene passata prima al db (nella riga sottostante), perchè deve essere precompilata (questo per via del fatto che vi è un preparedStatement)
			PreparedStatement ps = conn.prepareStatement(sql)){
			
			//qui la query viene modificata mettendo l'id_progetto al posto del primo segnaposto(?)
			ps.setInt(1, id_progetto);
			
			//qui proviamo ad eseguire la query finita e precompilata
			try(ResultSet rs = ps.executeQuery()){
				List<Coltura> out = new ArrayList<>();
				while(rs.next()) {
					StatoColtura stato=StatoColtura.valueOf(rs.getString("stato").toUpperCase());
					
					out.add(new Coltura(
							rs.getInt("id_coltura"),
							rs.getBigDecimal("quantitaPrevista"),
							rs.getObject("dataSemina", LocalDate.class),
							stato,
							rs.getInt("fk_progetto"),
							rs.getString("fk_tipo_coltura")
							));
				}
				
				return out;
			}
			
		}
		
	}
	
	@Override
	public Coltura findById(int id_coltura)throws Exception{
		String sql="""
				SELECT
					c.id_coltura,
					c.quantitaPrevista,
					c.dataSemina,
					c.stato::text,
					c.fk_progetto,
					c.fk_tipo_coltura
				FROM Coltura c
				WHERE c.id_coltura=?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setInt(1, id_coltura);
			
			try(ResultSet rs=ps.executeQuery()){
				if(rs.next()) {
					String strStato=rs.getString("stato").toUpperCase();
					StatoColtura stato=StatoColtura.valueOf(strStato);
					
					return new Coltura(
							rs.getInt("id_coltura"),
							rs.getBigDecimal("quantitaPrevista"),
							rs.getObject("dataSemina", LocalDate.class),
							stato,
							rs.getInt("fk_progetto"),
							rs.getString("fk_tipo_coltura"));
				}
				
				return null;
			}
		}
	}
	
	@Override
	public int create(Coltura c)throws Exception{
		String sql="""
				INSERT INTO Coltura(quantitaPrevista, dataSemina, stato, fk_progetto, fk_tipo_coltura)
				VALUES
				(?, ?, ?::StatoColtura, ?, ?)
				RETURNING id_coltura
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setBigDecimal(1, c.getQuantitaPrevista());
			ps.setObject(2, c.getDataSemina());
			ps.setString(3, c.getStato().toString());
			ps.setInt(4, c.getFk_progetto());
			ps.setString(5, c.getFk_tipo_coltura());
			
			try(ResultSet rs=ps.executeQuery()){
				rs.next();
				return rs.getInt("id_coltura");
			}
		}
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;			
		}
	}
	
	@Override
	public int update(Coltura c)throws Exception{
		String sql="""
				UPDATE Coltura
				SET quantitaPrevista = ?,
					dataSemina = ?,
					stato = ?::StatoColtura,
					fk_progetto = ?,
					fk_tipo_coltura = ?
				WHERE id_coltura = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setBigDecimal(1, c.getQuantitaPrevista());
			ps.setObject(2, c.getDataSemina());
			ps.setString(3, c.getStato().toString());
			ps.setInt(4, c.getFk_progetto());
			ps.setString(5, c.getFk_tipo_coltura());
			ps.setInt(6, c.getId_coltura());
			
			return ps.executeUpdate();
			
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
			
	}
	
	@Override
	public int delete(int id_coltura)throws Exception{
		String sql="""
				DELETE FROM Coltura
				WHERE id_coltura = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_coltura);
			
			return ps.executeUpdate();
			
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
		
	}

}
