package it.unina.biogarden;

import java.util.*;

import it.unina.biogarden.dao.*;

import it.unina.biogarden.model.*;
import it.unina.biogarden.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import it.unina.biogarden.gui.*;
import javax.swing.*;


public class Main {

	public static void main(String[] args) {
		
		/*try {
			ProgettoDao dao=new ProgettoDaoPg();
			List<ProgettoStagionale> progetti= dao.findAll();
			System.out.println("Progetti trovati: "+progetti.size());
			for(ProgettoStagionale p: progetti) {
				System.out.println(p);
			}
			
			int idProgetto=progetti.get(0).getId_progetto();
			System.out.println("vedo le colture che hanno come id_progetto: "+idProgetto);
			
			ColturaDao c=new ColturaDaoPg();
			c.findByProgetto(idProgetto).forEach(System.out::println);
			int id_coltura=c.findByProgetto(idProgetto).get(0).getId_coltura();
			
			AttivitaDao a=new AttivitaDaoPg();
			System.out.println("cerco le attività sulla coltura "+id_coltura);
			a.findByColtura(id_coltura).forEach(System.out::println);
			
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
		int idColtura=601;
		String emailColtivatore="colt1@bio.it";
		
		Attivita a=new Attivita(
				0,
				TipoAttivita.SCERBATURA,
				StatoAttivita.PIANIFICATA,
				LocalDate.now().plusDays(2),
				null,
				"irrigazione programmata",
				idColtura,
				emailColtivatore);
		
				AttivitaDao aDao=new AttivitaDaoPg();
				
				int newId=aDao.create(a);
				System.out.println("Nuova attività inserita con id: "+newId);
				
				
		}
		catch(Exception e) {
			e.printStackTrace();}
		*/
		
		/*try {
			RaccoltaDao r=new RaccoltaDaoPg();
			r.findByColtura(600).forEach(System.out::println);
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			ProgettoDao p=new ProgettoDaoPg();
			int id=501;
			System.out.println("stampo il progetto con id="+id+": \n");
			System.out.println(p.findById(id));
			System.out.println("stampo la descrizione:\n"+p.findById(id).getDescrizione());
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			ColturaDao c=new ColturaDaoPg();
			int id=601;
			System.out.println("trovo la coltura con id="+id);
			System.out.println(c.findById(id));
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		
		/*try {
			AttivitaDao a=new AttivitaDaoPg();
			a.updateStato(7011, StatoAttivita.IN_CORSO);
			
		}
		catch(Exception e) {
			e.getMessage();
		}*/
		
		
		/*
		try(Scanner scanner=new Scanner(System.in)) {
			ProprietarioDao p=new ProprietarioDaoPg();
			
			
			System.out.print("inserire email: ");
			String email=scanner.nextLine();
			System.out.print("inserire password: ");
			String pass=scanner.nextLine();		
			
			Proprietario p1=p.authenticate(email, pass);
			
			if(p1!=null)
				System.out.println("benvenuto: "+p1.getNome()+" "+p1.getCognome());
			else
				System.out.println("Nessun proprietario trovato con le credenziali inserite.");
				
			
			
			if(p.findByEmail(email)==null)
				System.out.println("nessun proprietario associato all'email indicata.");
			else
			System.out.println("il proprietario con l'email indicata è:\n"+p.findByEmail(email));
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		
		
		/*try {
			LottoDao l=new LottoDaoPg();
			
			String emailP="owner2@biogarden.it";
			
			System.out.println("mostro tutti i lotti associati all'email: "+emailP);
			
			l.findByProprietario(emailP).forEach(System.out::println);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			LottoDao l=new LottoDaoPg();
			
			int id=5;
			
			System.out.println(l.findById(id));
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			ProgettoDao p=new ProgettoDaoPg();
			
			List<ProgettoStagionale> result=p.findByLotto(1);
			
			if(result.isEmpty()) {
				System.out.println("nessun progetto associato al lotto indicato.");
			}
			
			else
			result.forEach(System.out::println);
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*try {
			Lotto l=new Lotto(0, "Area Nord", BigDecimal.valueOf(170.5), "owner2@biogarden.it");
			LottoDao ld=new LottoDaoPg();
			int id=ld.create(l);
			System.out.println("Ho inserito una riga nella tabella Lotto con id: "+id);
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*
		try {
			int id=6;
			LottoDao l=new LottoDaoPg();
			l.delete(id);
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		/*
		try {
			RaccoltaDao rd=new RaccoltaDaoPg();
			rd.delete(1);
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
		
		//progetti 15 e 501 sono del primo priprietario e insistono rispettivamente su lotto 3 e 1
		
		
		/*try {
			AttivitaService a=new AttivitaService();
			Attivita aa=new Attivita(
					9,
					TipoAttivita.SEMINA,
					StatoAttivita.PIANIFICATA,
					LocalDate.of(2025, 12, 30),
					null,
					"irrigazione di carote.",
					3,
					"colt1@bio.it");
			//ProprietarioDao p=new ProprietarioDaoPg();
			//Proprietario pp=p.authenticate("owner2@biogarden.it", "Password2");
			
			//a.createAttivitaPerProprietario(pp, aa);
			//a.updateStatoAttivitaPerProprietario(pp, 29, StatoAttivita.COMPLETATA);
			//a.deleteAttivitaPerProprietario(pp, 7010);
			/*
			RaccoltaService r=new RaccoltaService();
			Raccolta rr=new Raccolta(0, LocalDate.of(2028, 9, 19), BigDecimal.valueOf(-44), 5);
			r.createRaccoltaPerProprietario(pp, rr);*/
			
			//ProgettoService ppp=new ProgettoService();
			//ppp.findAllByProprietario("owner1@biogarden.it").forEach(System.out::println);
			
			
			
			/*ProprietarioService ps=new ProprietarioService();
			Proprietario prop=ps.authenticate("owner1@biogarden.it", "Password1");

			NotificaService ns=new NotificaService();
			
			//ns.generaAutomaticheDaView(prop);
			
			RaccoltaService rs=new RaccoltaService();
			//rs.deleteRaccoltaPerProprietario(prop, 14);
			
			ProgettoService pss=new ProgettoService();
			ProgettoStagionale prog=new ProgettoStagionale(1111, "aujajws", Stagione.INVERNO, 2026, 
															LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 25), 4, null);
			
			//a.updateStatoAttivitaPerProprietario(prop, 9, StatoAttivita.COMPLETATA);
			ns.generaAutomaticheDaView(prop);
			
			ReportService r=new ReportService();
			r.getReportPerLotto(prop, 3).forEach(System.out::println);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}*/
			
		//INIZIO TEST GUI
		
		SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });

	}

}
