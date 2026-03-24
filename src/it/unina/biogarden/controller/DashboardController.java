package it.unina.biogarden.controller;

import it.unina.biogarden.gui.DashboardFrame;
import it.unina.biogarden.gui.AttivitaDialog;
import it.unina.biogarden.gui.VisualizzaAttivitaDialog;
import it.unina.biogarden.gui.VisualizzaNotificheDialog;
import it.unina.biogarden.gui.ModificaProgettoDialog;
import it.unina.biogarden.gui.ReportLottoDialog;
import it.unina.biogarden.model.*;
import it.unina.biogarden.service.LottoService;
import it.unina.biogarden.service.ProgettoService;
import it.unina.biogarden.service.TipoColturaService;
import it.unina.biogarden.service.ColturaService;
import it.unina.biogarden.service.ColtivatoreService;
import it.unina.biogarden.service.AttivitaService;
import it.unina.biogarden.service.ReportService;
import it.unina.biogarden.service.NotificaService;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DashboardController {
    private DashboardFrame view;
    private Proprietario proprietarioLoggato;
    
    private LottoService lottoService;
    private ProgettoService progettoService;
    private ColtivatoreService coltivatoreService;
    private ColturaService colturaService;
    private TipoColturaService tipoColturaService;
    private AttivitaService attivitaService;
    private ReportService reportService;
    private NotificaService notificaService;
    
    private List<Lotto> lottiCorrenti;    
    private List<Coltivatore> elencoColtivatori;
    private List<TipoColtura> catalogoColture;

    public DashboardController(Proprietario p) {
        this.proprietarioLoggato = p;
        this.lottoService = new LottoService();
        this.progettoService = new ProgettoService();
        this.tipoColturaService = new TipoColturaService();
        this.coltivatoreService = new ColtivatoreService();
        this.attivitaService= new AttivitaService();
        this.colturaService= new ColturaService();
        this.reportService= new ReportService();
        this.notificaService= new NotificaService();
        
        this.view = new DashboardFrame(this);
        caricaLotti();
        preparaDatiCreazione();
    }
    
    public void caricaLotti() {
        try {
            this.lottiCorrenti = lottoService.findByProprietario(proprietarioLoggato.getEmail());
            view.aggiornaTabellaLotti(lottiCorrenti);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento lotti: " + formattaMessaggioErrore(e));
        }
    }

    public void lottoSelezionato(int rowIndex) {
        if (rowIndex < 0) return;
        try {
            Lotto selezionato = lottiCorrenti.get(rowIndex);
            List<ProgettoStagionale> progetti = progettoService.findByLotto(selezionato.getId_lotto());
            view.aggiornaTabellaProgetti(progetti);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento progetti del lotto: " + formattaMessaggioErrore(e));
        }
    }

    public void preparaDatiCreazione() {
        try {
            this.catalogoColture = tipoColturaService.getCatalogoColture();
            this.elencoColtivatori = coltivatoreService.findAll();
            view.popolaComponentiCreazione(lottiCorrenti, catalogoColture, elencoColtivatori);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento cataloghi: " + formattaMessaggioErrore(e));
        }
    }
    
    public void avviaDefinizioneAttivita(String nome, String dataInizioStr, String dataFineStr, String descrizione, Lotto lotto, List<TipoColtura> colture) {
        try {
            LocalDate dataInizio = LocalDate.parse(dataInizioStr);
            LocalDate dataFine = (dataFineStr != null) ? LocalDate.parse(dataFineStr) : null;
            
            AttivitaDialog dialog = new AttivitaDialog(view, elencoColtivatori, colture, dataInizio);
            dialog.setVisible(true);
            
            if (dialog.isConfermato()) {
                List<Attivita> listaAttivita = dialog.getAttivitaInserite(); 

                // --- CONTROLLO SEMINA OBBLIGATORIA ---
                for (TipoColtura tc : colture) {
                    boolean haSemina = listaAttivita.stream()
                        .anyMatch(a -> a.getTipoAttivita() == TipoAttivita.SEMINA && 
                                       a.getNomeTipoColtura().equalsIgnoreCase(tc.getNome()));
                    
                    if (!haSemina) {
                        view.mostraErrore("Impossibile salvare: non hai pianificato la SEMINA per " + tc.getNome());
                        return; 
                    }
                }
                
                
                ProgettoStagionale nuovoProgetto = new ProgettoStagionale(
                    0, 
                    nome, 
                    null, 
                    dataInizio.getYear(), 
                    dataInizio, 
                    dataFine, 
                    lotto.getId_lotto(), 
                    descrizione
                );
                
                salvaProgettoCompleto(nuovoProgetto, colture, listaAttivita);
            }
        } catch (DateTimeParseException e) {
            view.mostraErrore("Formato data non valido! Usa AAAA-MM-GG");
        } catch (Exception e) {
            view.mostraErrore("Errore: " + formattaMessaggioErrore(e));
        }
    }
    
    public void salvaProgettoCompleto(ProgettoStagionale p, List<TipoColtura> colture, List<Attivita> listaAttivita) {
        try {
            progettoService.creaProgettoConDatiCompleti(proprietarioLoggato, p, colture, listaAttivita);
            view.mostraMessaggio("Progetto '" + p.getNome() + "' e attività salvati correttamente!");
            caricaLotti(); 
        } catch (Exception e) {
            view.mostraErrore("Errore nel salvataggio: " + formattaMessaggioErrore(e));
        }
    }
    
    public void eliminaProgetto(int idProgetto) {
        try {
            progettoService.deleteProgettoPerProprietario(proprietarioLoggato, idProgetto);
            view.mostraMessaggio("Progetto eliminato con successo.");
            int selectedLottoRow = view.getTblLotti().getSelectedRow();
            if (selectedLottoRow != -1) {
                lottoSelezionato(selectedLottoRow);
            }
        } catch (Exception e) {
            view.mostraErrore("Impossibile eliminare il progetto: " + formattaMessaggioErrore(e));
        }
    }
    
    public void apriModificaProgetto(int idProgetto) {
        try {
            ProgettoStagionale progetto = progettoService.findById(idProgetto);
            List<Attivita> attivitaEsistenti = attivitaService.findByProgetto(idProgetto);
            List<Coltura> coltureProgetto = colturaService.findByProgetto(idProgetto);
            List<Coltivatore> tuttiIColtivatori = coltivatoreService.findAll();
            
            ModificaProgettoDialog dialog = new ModificaProgettoDialog(
                view, 
                progetto, 
                attivitaEsistenti, 
                coltureProgetto, 
                tuttiIColtivatori,
                this
            );
            dialog.setVisible(true);
            
            if (dialog.isConfermato()) {
                ProgettoStagionale modificato = dialog.getProgettoModificato();
                progettoService.updateProgettoPerProprietario(proprietarioLoggato, modificato);
                view.mostraMessaggio("Progetto '" + modificato.getNome() + "' e relative attività aggiornati!");
                int selectedLottoRow = view.getTblLotti().getSelectedRow();
                if (selectedLottoRow != -1) {
                    lottoSelezionato(selectedLottoRow);
                }
            }
        } catch (Exception e) {
            view.mostraErrore("Errore durante la modifica: " + formattaMessaggioErrore(e));
        }
    }

    public int aggiungiAttivitaDiretta(Attivita nuova) {
        try {
            // 1. Recuperiamo la coltura per sapere a quale progetto appartiene
            Coltura c = colturaService.findById(nuova.getFk_coltura());
            
            // 2. Recuperiamo il progetto usando il fk_progetto della coltura
            ProgettoStagionale p = progettoService.findById(c.getFk_progetto());

            // 3. Validazione date
            if (nuova.getDataPianificata().isBefore(p.getDataInizio())) {
                view.mostraErrore("Data non valida: il progetto '" + p.getNome() + 
                                  "' inizia il " + p.getDataInizio());
                return -1;
            }

            if (p.getDataFine() != null && nuova.getDataPianificata().isAfter(p.getDataFine())) {
                view.mostraErrore("Data non valida: il progetto '" + p.getNome() + 
                                  "' termina il " + p.getDataFine());
                return -1;
            }

            // 4. Se i controlli passano, salviamo
            return attivitaService.createAttivitaPerProprietario(this.proprietarioLoggato, nuova);
            
        } catch (Exception e) {
            view.mostraErrore(formattaMessaggioErrore(e));
            return -1;
        }
    }
    
    public void eliminaAttivitaDiretta(int idAttivita) {
        try {
            attivitaService.deleteAttivitaPerProprietario(this.proprietarioLoggato ,idAttivita);
        } catch (Exception e) {
            view.mostraErrore("Errore durante l'eliminazione: " + formattaMessaggioErrore(e));
        }
    }
    
    public void visualizzaDettagliProgetto(int idProgetto, String nomeProgettoIgnorato) {
        try {
            ProgettoStagionale progetto = progettoService.findById(idProgetto);
            
            List<Attivita> lista = attivitaService.findByProgetto(idProgetto);
            
            VisualizzaAttivitaDialog dialog = new VisualizzaAttivitaDialog(view, progetto, lista);
            dialog.setVisible(true);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento dettagli: " + formattaMessaggioErrore(e));
        }
    }
    
    public void visualizzaReportLotto(int idProgetto, String nomeProgetto) {
        try {
            List<ReportLotto> dati = reportService.getReportPerLotto(proprietarioLoggato, idProgetto);
            
            if (dati.isEmpty()) {
                view.mostraErrore("Nessun dato di raccolta disponibile per questo progetto.");
                return;
            }

            ReportLottoDialog dialog = new ReportLottoDialog(view, nomeProgetto, dati);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            view.mostraErrore("Errore generazione report: " + formattaMessaggioErrore(e));
        }
    }
    
    public void apriCentroNotifiche() {
        try {
            List<Notifica> storico = notificaService.findByProprietario(proprietarioLoggato);
            
            VisualizzaNotificheDialog dialog = new VisualizzaNotificheDialog(view, "Gestione Notifiche", storico, this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            // Se non ci sono notifiche, apriamo comunque il dialog vuoto per permettere l'invio
            new VisualizzaNotificheDialog(view, "Gestione Notifiche", new ArrayList<>(), this).setVisible(true);
        }
    }

    // NUOVO METODO: Richiamato dal bottone "Scansiona Ritardi" nel Dialog
    public void generaNotificheAutomatiche() {
        try {
            notificaService.generaAutomaticheDaView(proprietarioLoggato);
            view.mostraMessaggio("Scansione completata: notifiche generate per ritardi e scadenze.");
        } catch (Exception e) {
            view.mostraErrore("Errore durante la generazione: " + e.getMessage());
        }
    }
    
    public void inviaNotificaManuale(String titolo, String messaggio, String emailDestinatario) {
        try {
            if (emailDestinatario == null) {
                // Caso 1: Invio a tutti i coinvolti nei miei progetti
                notificaService.createNotificaPerTutti(
                    proprietarioLoggato, titolo, messaggio, TipoNotifica.ANOMALIA, null, null);
            } else {
                // Caso 2: Invio a coltivatore specifico
                notificaService.createNotificaPerColtivatore(
                    proprietarioLoggato, emailDestinatario, titolo, messaggio, TipoNotifica.ANOMALIA, null, null);
            }
            view.mostraMessaggio("Notifica inviata correttamente.");
            
        	} catch (Exception e) {
            view.mostraErrore("Impossibile inviare: " + e.getMessage());
        }
    }
    
    public List<Notifica> getListaNotificheAggiornata() {
        try {
            return notificaService.findByProprietario(proprietarioLoggato);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public List<Coltivatore> getColtivatoriAssociati() {
        try {
            return coltivatoreService.findColtivatoriPerProprietario(proprietarioLoggato.getEmail());
        } catch (Exception e) {
            view.mostraErrore("Errore nel recupero dei coltivatori: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void eliminaNotifica(int idNotifica) {
        try {
            notificaService.deleteNotificaPerProprietario(proprietarioLoggato, idNotifica);
            // Non serve mostrare un messaggio per ogni eliminazione, il refresh della lista basta
        } catch (Exception e) {
            view.mostraErrore("Impossibile eliminare la notifica: " + e.getMessage());
        }
    }
    
    // METODO DI UTILITÀ PER PULIZIA MESSAGGI TRIGGER
    private String formattaMessaggioErrore(Throwable e) {
        if (e == null) 
        	return "Errore sconosciuto";
        
        String msg = e.getMessage();

        //Se l'errore è di tipo SQL, cerchiamo l'eccezione successiva (quella vera)
        if (e instanceof SQLException) {
            SQLException sqlEx = (SQLException) e;
            if (sqlEx.getNextException() != null) {
                msg = sqlEx.getNextException().getMessage();
            }
        }

        if (msg == null) return "Errore di database generico";

        // 1. Caso Trigger (RAISE EXCEPTION)
        if (msg.contains("ERRORE:")) {
            try {
                return msg.split("ERRORE:")[1].split("Dove:")[0].trim();
            } catch (Exception ex) { return msg; }
        }

        // 2. Caso Vincolo Unique Attività 
        if (msg.contains("unq_attivita")) {
            return "Attenzione: esiste già un'attività identica (stessa coltura, tipo e data) per questo coltivatore.";
        }
        
        // 3. Caso Vincolo Unique Raccolta
        if (msg.contains("unq_raccolta")) {
            return "Operazione negata: è già presente una pesata per questa coltura in questa data.";
        }

        // 4. Messaggio generico per valori duplicati
        if (msg.contains("duplicate key")) {
            return "I dati inseriti violano un vincolo di unicità (dato già presente).";
        }

        return msg;
    }
    public Proprietario getProprietarioLoggato() {
        return proprietarioLoggato;
    }
}