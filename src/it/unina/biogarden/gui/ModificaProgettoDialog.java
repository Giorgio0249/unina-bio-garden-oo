package it.unina.biogarden.gui;

import it.unina.biogarden.controller.DashboardController;
import it.unina.biogarden.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

public class ModificaProgettoDialog extends JDialog {
    private JTextField txtNome;
    private JFormattedTextField txtDataFine;
    private JTextArea txtDescrizione;

    private JTable tblAttivita;
    private DefaultTableModel modelAttivita;

    private ProgettoStagionale progettoOriginale;
    private ProgettoStagionale progettoModificato;
    private DashboardController controller;
    private boolean confermato = false;

    public ModificaProgettoDialog(Frame parent, ProgettoStagionale progetto, 
                                 List<Attivita> attivitaEsistenti, 
                                 List<Coltura> coltureProgetto, 
                                 List<Coltivatore> tuttiIColtivatori,
                                 DashboardController controller) {
        super(parent, "Gestione Progetto: " + progetto.getNome(), true);
        this.progettoOriginale = progetto;
        this.controller = controller;

        setSize(850, 650); 
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(parent);

        //1. PANNELLO DATI PROGETTO
        JPanel pnlDatiProgetto = new JPanel(new GridBagLayout());
        pnlDatiProgetto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Dati Generali Progetto"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlDatiProgetto.add(new JLabel("Nome:"), gbc);
        txtNome = new JTextField(progetto.getNome(), 20);
        gbc.gridx = 1; pnlDatiProgetto.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlDatiProgetto.add(new JLabel("Data Fine (AAAA-MM-GG):"), gbc);
        txtDataFine = creaDataField();
        if (progetto.getDataFine() != null) txtDataFine.setText(progetto.getDataFine().toString());
        gbc.gridx = 1; pnlDatiProgetto.add(txtDataFine, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlDatiProgetto.add(new JLabel("Descrizione:"), gbc);
        txtDescrizione = new JTextArea(progetto.getDescrizione(), 3, 20);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescrizione);
        gbc.gridx = 1; pnlDatiProgetto.add(scrollDesc, gbc);

        add(pnlDatiProgetto, BorderLayout.NORTH);

        //2. PANNELLO CENTRALE (TABELLA + BOTTONI AZIONE)
        JPanel pnlCentro = new JPanel(new BorderLayout(10, 10));
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Colonne con ID (nascosto) per la gestione dell'eliminazione
        String[] colonne = {"ID", "Tipo", "Coltura", "Stato", "Data Pianificata", "Coltivatore"};
        modelAttivita = new DefaultTableModel(colonne, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Attivita a : attivitaEsistenti) {
            modelAttivita.addRow(new Object[]{
                a.getId_attivita(),
                a.getTipoAttivita(), 
                a.getNomeTipoColtura() + " (ID: " + a.getFk_coltura() + ")", 
                a.getStato(), 
                a.getDataPianificata(), 
                a.getFk_coltivatore()
            });
        }
        
        tblAttivita = new JTable(modelAttivita);
        
        tblAttivita.getColumnModel().getColumn(3).setCellRenderer(new AttivitaStatoRenderer());
        
        // Nascondiamo la colonna ID
        tblAttivita.getColumnModel().getColumn(0).setMinWidth(0);
        tblAttivita.getColumnModel().getColumn(0).setMaxWidth(0);
        tblAttivita.getColumnModel().getColumn(0).setWidth(0);
        
        pnlCentro.add(new JScrollPane(tblAttivita), BorderLayout.CENTER);

        // Pannello per i bottoni sotto la tabella
        JPanel pnlAzioniAttivita = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnApriAggiunta = new JButton("+ Aggiungi Nuova Attività");
        btnApriAggiunta.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JButton btnEliminaAtt = new JButton("Elimina Attività Selezionata");
        btnEliminaAtt.setForeground(Color.RED);
        
        pnlAzioniAttivita.add(btnApriAggiunta);
        pnlAzioniAttivita.add(btnEliminaAtt);
        pnlCentro.add(pnlAzioniAttivita, BorderLayout.SOUTH);

        add(pnlCentro, BorderLayout.CENTER);

        //3. BOTTONI SALVATAGGIO 
        JPanel pnlSud = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSud.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton btnSalva = new JButton("Salva Modifiche Progetto");
        JButton btnChiudi = new JButton("Chiudi");
        pnlSud.add(btnChiudi); 
        pnlSud.add(btnSalva);
        add(pnlSud, BorderLayout.SOUTH);

        //LOGICA EVENTI
        
        btnApriAggiunta.addActionListener(e -> {
            // MODIFICATO: Passiamo true per indicare che siamo in fase di modifica
            AggiungiAttivitaDialog aggiungiDialog = new AggiungiAttivitaDialog(this, coltureProgetto, tuttiIColtivatori, true);
            aggiungiDialog.setVisible(true);

            if (aggiungiDialog.isConfermato()) {
                Attivita nuova = aggiungiDialog.getAttivitaCreata();
                
                int idGenerato = controller.aggiungiAttivitaDiretta(nuova);
                
                if (idGenerato != -1) {
                    String nomeColtura = "ID: " + nuova.getFk_coltura();
                    for (Coltura c : coltureProgetto) {
                        if (c.getId_coltura() == nuova.getFk_coltura()) {
                            nomeColtura = c.getFk_tipo_coltura() + " (ID: " + nuova.getFk_coltura() + ")";
                            break;
                        }
                    }

                    modelAttivita.addRow(new Object[]{
                        idGenerato, 
                        nuova.getTipoAttivita(), 
                        nomeColtura, 
                        nuova.getStato(), 
                        nuova.getDataPianificata(), 
                        nuova.getFk_coltivatore()
                    });
                }
            }
        });

        btnEliminaAtt.addActionListener(e -> {
            int row = tblAttivita.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un'attività dalla tabella!");
                return;
            }

            // MODIFICATO: Controllo blocco eliminazione SEMINA/TRAPIANTO
            TipoAttivita tipo = (TipoAttivita) modelAttivita.getValueAt(row, 1);
            if (tipo == TipoAttivita.SEMINA || tipo == TipoAttivita.TRAPIANTO) {
                JOptionPane.showMessageDialog(this, 
                    "Non è possibile eliminare l'attività iniziale di " + tipo + " per garantire la coerenza del progetto.", 
                    "Azione negata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idAtt = (int) modelAttivita.getValueAt(row, 0);
            StatoAttivita stato = (StatoAttivita) modelAttivita.getValueAt(row, 3);

            if (stato != StatoAttivita.PIANIFICATA) {
                JOptionPane.showMessageDialog(this, 
                    "Non è possibile eliminare un'attività in stato: " + stato, 
                    "Azione non consentita", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int conf = JOptionPane.showConfirmDialog(this, 
                "Eliminare definitivamente l'attività selezionata?", 
                "Conferma", JOptionPane.YES_NO_OPTION);
                
            if (conf == JOptionPane.YES_OPTION) {
                controller.eliminaAttivitaDiretta(idAtt);
                modelAttivita.removeRow(row);
            }
        });

        btnSalva.addActionListener(e -> {
            if (txtNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Il nome progetto è obbligatorio!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dFine = null;
            String dataFineStr = txtDataFine.getText();
            if (!dataFineStr.contains("_")) {
                try {
                    dFine = LocalDate.parse(dataFineStr);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Data fine non valida!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            progettoModificato = new ProgettoStagionale(
                progettoOriginale.getId_progetto(), 
                txtNome.getText().trim(), 
                progettoOriginale.getStagione(),
                progettoOriginale.getAnno(), 
                progettoOriginale.getDataInizio(), 
                dFine,
                progettoOriginale.getFk_lotto(), 
                txtDescrizione.getText().trim()
            );

            confermato = true;
            dispose();
        });

        btnChiudi.addActionListener(e -> dispose());
    }

    private JFormattedTextField creaDataField() {
        try {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            dateMask.setPlaceholderCharacter('_');
            return new JFormattedTextField(dateMask);
        } catch (ParseException e) { return new JFormattedTextField(); }
    }

    public boolean isConfermato() { return confermato; }
    public ProgettoStagionale getProgettoModificato() { return progettoModificato; }
}