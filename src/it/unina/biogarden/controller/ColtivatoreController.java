package it.unina.biogarden.controller;

import it.unina.biogarden.gui.ColtivatoreFrame;
import it.unina.biogarden.gui.VisualizzaNotificheDialog;
import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.model.Notifica;
import it.unina.biogarden.model.StatoAttivita;
import it.unina.biogarden.model.TipoAttivita;
import it.unina.biogarden.service.AttivitaService;
import it.unina.biogarden.service.NotificaService;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class ColtivatoreController {
    private ColtivatoreFrame view;
    private Coltivatore coltivatoreLoggato;
    private AttivitaService attivitaService;
    private NotificaService notificaService;

    public ColtivatoreController(Coltivatore c) {
        this.coltivatoreLoggato = c;
        this.attivitaService = new AttivitaService();
        this.notificaService= new NotificaService();
        this.view = new ColtivatoreFrame(this, c.getNome());
        this.view.setVisible(true);
        aggiornaDati();
    }

    public void aggiornaDati() {
        try {
            List<Attivita> tutte = attivitaService.findByColtivatore(coltivatoreLoggato.getEmail());
            
            List<Attivita> attive = tutte.stream()
                .filter(a -> a.getStato() == StatoAttivita.PIANIFICATA || a.getStato() == StatoAttivita.IN_CORSO)
                .collect(Collectors.toList());
                
            List<Attivita> storico = tutte.stream()
                .filter(a -> a.getStato() == StatoAttivita.COMPLETATA || a.getStato() == StatoAttivita.ANNULLATA)
                .collect(Collectors.toList());

            view.popolaTabelle(attive, storico);
        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento dati: " + e.getMessage());
        }
    }

    public void cambiaStato(int idAttivita, StatoAttivita nuovoStato) {
        try {
            // Se si tenta di iniziare (IN_CORSO) o finire (COMPLETATA) un'attività che NON è una semina
            Attivita attCorrente = attivitaService.findById(idAttivita);
            if (attCorrente.getTipoAttivita() != TipoAttivita.SEMINA && 
               (nuovoStato == StatoAttivita.IN_CORSO || nuovoStato == StatoAttivita.COMPLETATA)) {
                
                // Verifichiamo se esiste una semina completata per questa coltura
                List<Attivita> attivitaColtura = attivitaService.findByColtura(attCorrente.getFk_coltura());
                boolean seminaOk = attivitaColtura.stream()
                    .anyMatch(a -> a.getTipoAttivita() == TipoAttivita.SEMINA && a.getStato() == StatoAttivita.COMPLETATA);
                
                if (!seminaOk) {
                    view.mostraErrore("Operazione negata: devi prima completare l'attività di SEMINA per questa coltura!");
                    return;
                }
            }

            if (nuovoStato == StatoAttivita.COMPLETATA) {
                if (attCorrente.getTipoAttivita() == TipoAttivita.RACCOLTA) {
                    String input = JOptionPane.showInputDialog(view, 
                        "Inserisci la quantità effettiva raccolta in kg:", 
                        "Completamento Raccolta", JOptionPane.QUESTION_MESSAGE);
                    
                    if (input == null) return; 

                    try {
                        String pulita = input.trim().replace(",", ".");
                        java.math.BigDecimal q = new java.math.BigDecimal(pulita);
                        
                        if (q.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                            view.mostraErrore("La quantità deve essere positiva!");
                            return;
                        }
                        
                        attivitaService.completaAttivitaConRaccolta(coltivatoreLoggato, idAttivita, q);
                    } catch (NumberFormatException e) {
                        view.mostraErrore("Formato numero non valido!");
                        return;
                    } catch (Exception e) {
                        view.mostraErrore(e.getMessage());
                        return;
                    }
                } else {
                    attivitaService.updateStatoPerColtivatore(this.coltivatoreLoggato, idAttivita, nuovoStato);
                }
            } else {
                attivitaService.updateStatoPerColtivatore(this.coltivatoreLoggato, idAttivita, nuovoStato);
            }
            
            aggiornaDati(); 
        } catch (Exception e) {
            view.mostraErrore(e.getMessage());
        }
    }
    
    public void apriCentroNotifiche() {
        try {
            // Recupera le notifiche tramite il service 
            List<Notifica> mieNotifiche = notificaService.findByColtivatore(coltivatoreLoggato.getEmail());
            
            // Passiamo 'null' come quarto parametro perché il coltivatore non ha un DashboardController
            VisualizzaNotificheDialog dialog = new VisualizzaNotificheDialog(view, "Centro Notifiche", mieNotifiche, null);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            // Se non ci sono notifiche, mostriamo comunque il dialog vuoto
            new VisualizzaNotificheDialog(view, "Centro Notifiche", new java.util.ArrayList<>(), null).setVisible(true);
        }
    }

    public Coltivatore getColtivatoreLoggato() { return coltivatoreLoggato; }
}