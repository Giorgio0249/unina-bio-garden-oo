package it.unina.biogarden.dao;

import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Stagione;
import it.unina.biogarden.model.TipoColtura;
import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.TipoAttivita;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

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
	
	
	@Override
	public boolean existsProgettoPerProprietario(int id_progetto, String email_prop)throws Exception{
		String sql="""
				SELECT 1
				FROM ProgettoStagionale p
				JOIN Lotto l ON p.fk_lotto = l.id_lotto
				WHERE p.id_progetto = ?
				AND l.fk_proprietario = ?
				LIMIT 1
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_progetto);
			ps.setString(2, email_prop);
			
			try(ResultSet rs=ps.executeQuery()){
				return rs.next();
			}
		}
	}
	
	@Override
	public void insertProgettoCompleto(ProgettoStagionale p, List<TipoColtura> tipiColture, List<Attivita> listaAttivita) throws Exception {
	    Connection conn = ConnectionFactory.getInstance().getConnection();
	    
	    try {
	        conn.setAutoCommit(false); 

	        // 1. Inserimento ProgettoStagionale con i nuovi campi
	        int idProgetto;
	        String sqlProgetto = """
	                INSERT INTO ProgettoStagionale (nome, stagione, anno, dataInizio, dataFine, fk_lotto, descrizione)
	                VALUES (?, ?::Stagione, ?, ?, ?, ?, ?) 
	                RETURNING id_progetto
	                """;
	        
	        try (PreparedStatement psP = conn.prepareStatement(sqlProgetto)) {
	            psP.setString(1, p.getNome());
	            psP.setString(2, p.getStagione().toString());
	            psP.setInt(3, p.getAnno());
	            psP.setObject(4, p.getDataInizio());
	            
	            // Gestione DATA FINE (Opzionale)
	            psP.setObject(5, p.getDataFine()); // Se è null, setObject gestisce il NULL SQL
	            
	            psP.setInt(6, p.getFk_lotto());
	            
	            // Gestione DESCRIZIONE (Opzionale)
	            psP.setString(7, p.getDescrizione());
	            
	            try (ResultSet rs = psP.executeQuery()) {
	                if (rs.next()) {
	                    idProgetto = rs.getInt(1);
	                } else {
	                    throw new SQLException("Errore creazione progetto: ID non restituito.");
	                }
	            }
	        }

	        // 2. Inserimento Colture (Resto del codice invariato...)
	        Map<String, Integer> tipoToIdColtura = new HashMap<>();
	        String sqlColtura = """
	                INSERT INTO Coltura(quantitaPrevista, dataSemina, stato, fk_progetto, fk_tipo_coltura) 
	                VALUES (?, ?, ?::StatoColtura, ?, ?) 
	                RETURNING id_coltura
	                """;

	        try (PreparedStatement psC = conn.prepareStatement(sqlColtura)) {
	            for (TipoColtura tc : tipiColture) {
	                psC.setBigDecimal(1, BigDecimal.ZERO); 
	                psC.setObject(2, null); 
	                psC.setString(3, "AVVIATA"); 
	                psC.setInt(4, idProgetto);
	                psC.setString(5, tc.getNome()); 
	                
	                try (ResultSet rs = psC.executeQuery()) {
	                    if (rs.next()) {
	                        tipoToIdColtura.put(tc.getNome(), rs.getInt("id_coltura"));
	                    }
	                }
	            }
	        }

	        // 3. Inserimento Attività (Fase A e B invariate...)
	        String sqlAttivita = """
	                INSERT INTO Attivita (tipoAttivita, stato, dataPianificata, dataEffettiva, descrizione, fk_coltura, fk_coltivatore)
	                VALUES (?::TipoAttivita, ?::StatoAttivita, ?, ?, ?, ?, ?)
	                """;
	        
	        try (PreparedStatement psA = conn.prepareStatement(sqlAttivita)) {
	            for (Attivita att : listaAttivita) {
	                if (att.getTipoAttivita().toString().equals("SEMINA")) {
	                    configuraParametriAttivita(psA, att, tipoToIdColtura);
	                    psA.addBatch();
	                }
	            }
	            psA.executeBatch(); 

	            for (Attivita att : listaAttivita) {
	                if (!att.getTipoAttivita().toString().equals("SEMINA")) {
	                    configuraParametriAttivita(psA, att, tipoToIdColtura);
	                    psA.addBatch();
	                }
	            }
	            psA.executeBatch(); 
	        }

	        conn.commit(); 
	        
	    } catch (SQLException e) {
	        if (conn != null) conn.rollback(); 
	        throw e; 
	    } finally {
	        if (conn != null) {
	            conn.setAutoCommit(true);
	            conn.close();
	        }
	    }
	}
	
	private void configuraParametriAttivita(PreparedStatement psA, Attivita att, Map<String, Integer> tipoToIdColtura) throws SQLException {
	    // Recupero ID Coltura dalla descrizione
	    String desc = att.getDescrizione();
	    String nomeColtura = desc.substring(desc.indexOf(":") + 2).split(" ")[0].trim();
	    
	    Integer idColturaVal = tipoToIdColtura.get(nomeColtura);
	    if (idColturaVal == null) {
	        idColturaVal = tipoToIdColtura.values().iterator().next();
	    }

	    psA.setString(1, att.getTipoAttivita().toString());
	    psA.setString(2, "PIANIFICATA"); 
	    psA.setObject(3, att.getDataPianificata());
	    psA.setObject(4, null); 
	    psA.setString(5, att.getDescrizione());
	    psA.setInt(6, idColturaVal);
	    psA.setString(7, att.getFk_coltivatore());
	}

}
