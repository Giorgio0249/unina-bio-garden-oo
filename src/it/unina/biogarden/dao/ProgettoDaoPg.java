package it.unina.biogarden.dao;

import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Stagione;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

 class ProgettoDaoPg implements ProgettoDao{
	
	@Override
	public List<ProgettoStagionale> findAll() throws Exception{
		String sql="""
				SELECT 
					p.id_progetto,
					p.nome,
					p.stagione::text,
					p.anno,
					p.dataInizio,
					p.dataFine,
					p.fk_lotto,
					p.descrizione
				FROM ProgettoStagionale p
				ORDER BY p.dataInizio DESC
				""";
		
		//blocco try-with-resources
		try(Connection c = ConnectionFactory.getInstance().getConnection(); 
			Statement st = c.createStatement();
			//la query viene passata al db nella riga sottostante, quindi al momento dell'esecuzione (questo perchè vi è un semplice Statement)
			ResultSet rs = st.executeQuery(sql)){
			
			List<ProgettoStagionale> out = new ArrayList<>();
			while(rs.next()) {
				
				//1) leggo la stringa dall'enum del DB
				String stagStr = rs.getString("stagione").toUpperCase();
				//2) la converto nell'enum java corrispondente
				Stagione stagione = Stagione.valueOf(stagStr); 
				
				out.add(new ProgettoStagionale(
						rs.getInt("id_progetto"),
						rs.getString("nome"),
						stagione,
						rs.getInt("anno"),
						rs.getObject("dataInizio", LocalDate.class),
						rs.getObject("dataFine", LocalDate.class),
						rs.getInt("fk_lotto"),
						rs.getString("descrizione")
						));
			}
			return out;
		}
		
	}
	
	
	@Override
	public ProgettoStagionale findById(int id_progetto) throws Exception{
		String sql="""
				SELECT 
					p.id_progetto,
					p.nome,
					p.stagione::text,
					p.anno,
					p.dataInizio,
					p.dataFine,
					p.fk_lotto,
					p.descrizione
				FROM ProgettoStagionale p
				WHERE p.id_progetto=?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_progetto);
			
			try(ResultSet rs=ps.executeQuery()){
				if(rs.next()) {
					String stagStr=rs.getString("stagione").toUpperCase();
					Stagione stagione=Stagione.valueOf(stagStr);
					
					return new ProgettoStagionale(
							rs.getInt("id_progetto"),
							rs.getString("nome"),
							stagione,
							rs.getInt("anno"),
							rs.getObject("dataInizio", LocalDate.class),
							rs.getObject("dataFine", LocalDate.class),
							rs.getInt("fk_lotto"),
							rs.getString("descrizione")
						);
				}
				return null;
						
			}
				
		}
	}
	
	@Override
	public List<ProgettoStagionale> findAllByProprietario(String emailProprietario)throws Exception{
		
		String sql="""
				SELECT 
					p.id_progetto,
					p.nome,
					p.stagione::text,
					p.anno,
					p.dataInizio,
					p.dataFine,
					p.fk_lotto,
					p.descrizione
				FROM ProgettoStagionale p
				JOIN Lotto l ON p.fk_lotto = l.id_lotto
				WHERE l.fk_proprietario = ?
				ORDER BY p.dataInizio DESC
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, emailProprietario);
			
			try(ResultSet rs=ps.executeQuery()){
				
				List<ProgettoStagionale> out=new ArrayList<>();
				
				while(rs.next()) {
					String stagStr=rs.getString("stagione").toUpperCase();
					Stagione stagione=Stagione.valueOf(stagStr);
					
					out.add(new ProgettoStagionale(
							rs.getInt("id_progetto"),
							rs.getString("nome"),
							stagione,
							rs.getInt("anno"),
							rs.getObject("dataInizio", LocalDate.class),
							rs.getObject("dataFine", LocalDate.class),
							rs.getInt("fk_lotto"),
							rs.getString("descrizione")));
				}
				
				return out;
			}
		}
	}
	
	@Override
	public List<ProgettoStagionale> findByLotto(int id_lotto)throws Exception{
		
		String sql="""
				SELECT
					p.id_progetto,
					p.nome,
					p.stagione::text,
					p.anno,
					p.dataInizio,
					p.dataFine,
					p.fk_lotto,
					p.descrizione
				FROM ProgettoStagionale p
				WHERE p.fk_lotto = ?
				ORDER BY p.dataInizio DESC
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_lotto);
			
			try(ResultSet rs=ps.executeQuery()){
				
				List<ProgettoStagionale> out=new ArrayList<>();
				
				while(rs.next()) {
					
					String stagStr=rs.getString("stagione").toUpperCase();
					Stagione stagione=Stagione.valueOf(stagStr);
					
					out.add(new ProgettoStagionale(
							rs.getInt("id_progetto"),
							rs.getString("nome"),
							stagione,
							rs.getInt("anno"),
							rs.getObject("dataInizio", LocalDate.class),
							rs.getObject("dataFine", LocalDate.class),
							rs.getInt("fk_lotto"),
							rs.getString("descrizione")));							
					
				}
				
				return out;
			}
		}
	}
	
	@Override
	public int create(ProgettoStagionale p)throws Exception{
		String sql="""
				INSERT INTO ProgettoStagionale(nome, stagione, anno, dataInizio, dataFine, fk_lotto, descrizione)
				VALUES(?, ?::stagione, ?, ?, ?, ?, ?)
				RETURNING id_progetto
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, p.getNome());
			ps.setString(2, p.getStagione().toString());
			ps.setInt(3, p.getAnno());
			ps.setObject(4, p.getDataInizio());
			ps.setObject(5, p.getDataFine());
			ps.setInt(6, p.getFk_lotto());
			ps.setString(7, p.getDescrizione());
			
			try(ResultSet rs=ps.executeQuery()){
				
				rs.next();
				return rs.getInt("id_progetto");
			}
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}
	
	@Override
	public int update(ProgettoStagionale p)throws Exception{
		String sql="""
				UPDATE ProgettoStagionale
				SET nome = ?,
					stagione = ?::Stagione,
					anno = ?,
					dataInizio = ?,
					dataFine = ?,
					fk_lotto = ?,
					descrizione = ?
				WHERE id_progetto = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, p.getNome());
			ps.setString(2, p.getStagione().toString());
			ps.setInt(3, p.getAnno());
			ps.setObject(4, p.getDataInizio());
			ps.setObject(5, p.getDataFine());
			ps.setInt(6, p.getFk_lotto());
			ps.setString(7, p.getDescrizione());
			ps.setInt(8, p.getId_progetto());
			
			return ps.executeUpdate();
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}
	
	@Override
	public int delete(int id_progetto)throws Exception{
		String sql="""
				DELETE FROM ProgettoStagionale
				WHERE id_progetto = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setInt(1, id_progetto);
			
			return ps.executeUpdate();
			
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}

}
