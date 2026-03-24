package it.unina.biogarden.gui;

import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.TipoAttivita;
import it.unina.biogarden.model.StatoAttivita;
import it.unina.biogarden.model.TipoColtura;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class AttivitaDialog extends JDialog {
    private JComboBox<String> comboTipoAttivita;
    private JComboBox<TipoColtura> comboColturaTarget; 
    private JList<Coltivatore> listColtivatori;
    private JTextField txtQuantita; 
    private JFormattedTextField txtDataAttivita; 
    private DefaultListModel<String> modelAttivitaAggiunte;
    private JList<String> listAttivitaAggiunte;
    
    private List<Attivita> listaAttivitaOggetti = new ArrayList<>();
    private boolean confermato = false;
    private LocalDate dataInizioProgetto;

    public AttivitaDialog(Frame parent, List<Coltivatore> coltivatori, List<TipoColtura> coltureScelte, LocalDate dataInizioProgetto) {
        super(parent, "Definisci Attività", true);
        this.dataInizioProgetto = dataInizioProgetto; 
        
        setSize(600, 750); 
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        //PANNELLO INPUT 
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Nuova Attività"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Selezione Coltura
        gbc.gridx = 0; gbc.gridy = 0;
        pnlInput.add(new JLabel("Coltura di riferimento:"), gbc);
        
        DefaultComboBoxModel<TipoColtura> modelColture = new DefaultComboBoxModel<>();
        for (TipoColtura tc : coltureScelte) modelColture.addElement(tc);
        comboColturaTarget = new JComboBox<>(modelColture);
        comboColturaTarget.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof TipoColtura) value = ((TipoColtura) value).getNome(); 
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        gbc.gridy = 1;
        pnlInput.add(comboColturaTarget, gbc);

        // 2. Tipo Attività
        gbc.gridy = 2;
        pnlInput.add(new JLabel("Tipo Attività:"), gbc);
        String[] tipi = {"SEMINA", "IRRIGAZIONE", "CONCIMAZIONE", "TRATTAMENTO", "POTATURA", "SCERBATURA", "TRAPIANTO", "RACCOLTA", "ALTRO"};
        comboTipoAttivita = new JComboBox<>(tipi);
        gbc.gridy = 3;
        pnlInput.add(comboTipoAttivita, gbc);

        // 3. Data Attività
        gbc.gridy = 4;
        pnlInput.add(new JLabel("Data Attività (AAAA-MM-GG):"), gbc);
        try {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            dateMask.setPlaceholderCharacter('_');
            txtDataAttivita = new JFormattedTextField(dateMask);
            txtDataAttivita.setText(dataInizioProgetto.toString()); 
        } catch (Exception e) {
            txtDataAttivita = new JFormattedTextField();
        }
        gbc.gridy = 5;
        pnlInput.add(txtDataAttivita, gbc);

        // 4. Coltivatori
        gbc.gridy = 6;
        pnlInput.add(new JLabel("Seleziona Coltivatori:"), gbc);
        DefaultListModel<Coltivatore> modelColt = new DefaultListModel<>();
        for (Coltivatore c : coltivatori) modelColt.addElement(c);
        listColtivatori = new JList<>(modelColt);
        listColtivatori.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Coltivatore) {
                    Coltivatore c = (Coltivatore) value;
                    value = c.getNome() + " " + c.getCognome() + " (" + c.getEmail() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        listColtivatori.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollColt = new JScrollPane(listColtivatori);
        scrollColt.setPreferredSize(new Dimension(0, 80));
        gbc.gridy = 7;
        pnlInput.add(new JScrollPane(scrollColt), gbc);

        // 5. Quantità
        gbc.gridy = 8;
        pnlInput.add(new JLabel("Quantità Prevista (kg) - Solo per RACCOLTA:"), gbc);
        txtQuantita = new JTextField();
        txtQuantita.setEnabled(false);
        gbc.gridy = 9;
        pnlInput.add(txtQuantita, gbc);

        comboTipoAttivita.addActionListener(e -> {
            txtQuantita.setEnabled(comboTipoAttivita.getSelectedItem().equals("RACCOLTA"));
            if (!txtQuantita.isEnabled()) txtQuantita.setText("");
        });

        JButton btnAggiungi = new JButton("Aggiungi Attività ↓");
        gbc.gridy = 10;
        pnlInput.add(btnAggiungi, gbc);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Attività da Salvare"));
        modelAttivitaAggiunte = new DefaultListModel<>();
        listAttivitaAggiunte = new JList<>(modelAttivitaAggiunte);
        pnlCenter.add(new JScrollPane(listAttivitaAggiunte), BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("Conferma Tutto");
        JButton btnAnnulla = new JButton("Annulla");
        pnlSouth.add(btnAnnulla); pnlSouth.add(btnSalva);

        add(pnlInput, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);

        //LOGICA DI AGGIUNTA
        btnAggiungi.addActionListener(e -> {
            TipoColtura colturaSelezionata = (TipoColtura) comboColturaTarget.getSelectedItem();
            String tipoStr = (String) comboTipoAttivita.getSelectedItem();
            List<Coltivatore> scelti = listColtivatori.getSelectedValuesList();
            String dataAttStr = txtDataAttivita.getText();

            if (scelti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleziona almeno un coltivatore!");
                return;
            }

            if (dataAttStr.contains("_")) {
                JOptionPane.showMessageDialog(this, "Data attività non valida!");
                return;
            }
            
            String qStr = txtQuantita.getText().trim();
            if (tipoStr.equals("RACCOLTA")) {
                if (qStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Per la RACCOLTA la quantità prevista è obbligatoria!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    double val = Double.parseDouble(qStr.replace(",", "."));
                    if (val <= 0) {
                        JOptionPane.showMessageDialog(this, "Inserire una quantità maggiore di zero!", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "La quantità prevista deve essere un numero valido!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                LocalDate dataScelta = LocalDate.parse(dataAttStr);

                if (dataScelta.isBefore(dataInizioProgetto)) {
                    JOptionPane.showMessageDialog(this, "L'attività non può avvenire prima del progetto (" + dataInizioProgetto + ")");
                    return;
                }

                TipoAttivita tipoEnum = TipoAttivita.valueOf(tipoStr);

                StringBuilder nomiColtivatori = new StringBuilder();
                for (int i = 0; i < scelti.size(); i++) {
                    nomiColtivatori.append(scelti.get(i).getNome()).append(" ").append(scelti.get(i).getCognome());
                    if (i < scelti.size() - 1) {
                        nomiColtivatori.append(", ");
                    }
                }

                for (Coltivatore c : scelti) {
                    String desc = "Per: " + colturaSelezionata.getNome();
                    if(tipoStr.equals("RACCOLTA")) {
                        desc += " | Quantità prevista: " + qStr + " kg";
                    }

                    Attivita nuovaAtt = new Attivita(
                        0,                          
                        tipoEnum,                   
                        StatoAttivita.PIANIFICATA,  
                        dataScelta,  
                        null,                       
                        desc, 
                        0, 
                        c.getEmail()                
                    );
                    
                    // Fondamentale per il controllo nel Controller
                    nuovaAtt.setNomeTipoColtura(colturaSelezionata.getNome());
                    
                    listaAttivitaOggetti.add(nuovaAtt);
                }

                String riepilogo = tipoStr + " [" + dataScelta + "] su " + colturaSelezionata.getNome();
                if(tipoStr.equals("RACCOLTA")) {
                    riepilogo += " (Previsti: " + qStr + " kg)";
                }
                riepilogo += " - Incaricati: " + nomiColtivatori.toString();
                
                modelAttivitaAggiunte.addElement(riepilogo);

                txtQuantita.setText("");
                listColtivatori.clearSelection();
                txtDataAttivita.setText(dataInizioProgetto.toString()); 

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage());
            }
        });
        
        btnAnnulla.addActionListener(e -> { confermato = false; dispose(); });
        btnSalva.addActionListener(e -> {
            if (modelAttivitaAggiunte.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aggiungi almeno un'attività!");
                return;
            }
            confermato = true;
            dispose();
        });
    }

    public boolean isConfermato() { return confermato; }
    public List<Attivita> getAttivitaInserite() { return listaAttivitaOggetti; }
}