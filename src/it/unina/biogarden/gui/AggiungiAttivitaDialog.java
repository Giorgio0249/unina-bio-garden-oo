package it.unina.biogarden.gui;

import it.unina.biogarden.model.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class AggiungiAttivitaDialog extends JDialog {
    private JComboBox<TipoAttivita> comboTipo;
    private JComboBox<Coltura> comboColtura;
    private JComboBox<Coltivatore> comboColtivatore;
    private JFormattedTextField txtData;
    private JTextField txtQuantita; // AGGIUNTO
    
    private Attivita attivitaCreata;
    private boolean confermato = false;

    public AggiungiAttivitaDialog(JDialog parent, List<Coltura> colture, List<Coltivatore> coltivatori, boolean isModifica) {
        super(parent, "Aggiungi Nuova Attività", true);
        
        setSize(500, 500); 
        setLayout(new GridBagLayout());
        setLocationRelativeTo(parent);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Tipo Attività
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Tipo Attività:"), gbc);
        
        List<TipoAttivita> tipiDisponibili = new ArrayList<>();
        for(TipoAttivita t : TipoAttivita.values()){
            if(isModifica && (t == TipoAttivita.SEMINA || t == TipoAttivita.TRAPIANTO)) continue;
            tipiDisponibili.add(t);
        }
        
        comboTipo = new JComboBox<>(tipiDisponibili.toArray(new TipoAttivita[0]));
        gbc.gridx = 1;
        add(comboTipo, gbc);

        // 2. Selezione Coltura
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Coltura di riferimento:"), gbc);
        comboColtura = new JComboBox<>(colture.toArray(new Coltura[0]));
        comboColtura.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Coltura) {
                    Coltura c = (Coltura) value;
                    value = "ID: " + c.getId_coltura() + " - " + c.getFk_tipo_coltura();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        gbc.gridx = 1;
        add(comboColtura, gbc);

        // 3. Data Pianificata
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Data (AAAA-MM-GG):"), gbc);
        try {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            dateMask.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(dateMask);
        } catch (Exception e) {
            txtData = new JFormattedTextField();
        }
        gbc.gridx = 1;
        add(txtData, gbc);

        // 4. Assegnazione Coltivatore
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Assegna a:"), gbc);
        comboColtivatore = new JComboBox<>(coltivatori.toArray(new Coltivatore[0]));
        comboColtivatore.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Coltivatore) {
                    Coltivatore c = (Coltivatore) value;
                    value = c.getNome() + " (" + c.getEmail() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        gbc.gridx = 1;
        add(comboColtivatore, gbc);

        // 5. Quantità Prevista 
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Quantità Prevista (kg):"), gbc);
        txtQuantita = new JTextField();
        txtQuantita.setEnabled(false); // Disabilitato di default
        gbc.gridx = 1;
        add(txtQuantita, gbc);

        // Listener per abilitare/disabilitare quantità 
        comboTipo.addActionListener(e -> {
            boolean isRaccolta = comboTipo.getSelectedItem() == TipoAttivita.RACCOLTA;
            txtQuantita.setEnabled(isRaccolta);
            if (!isRaccolta) txtQuantita.setText("");
        });

        // --- BOTTONI ---
        JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("Aggiungi");
        JButton btnAnnulla = new JButton("Annulla");
        pnlBottoni.add(btnAnnulla);
        pnlBottoni.add(btnSalva);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(pnlBottoni, gbc);

        btnAnnulla.addActionListener(e -> dispose());

        btnSalva.addActionListener(e -> {
            String dataStr = txtData.getText();
            TipoAttivita tipoSel = (TipoAttivita) comboTipo.getSelectedItem();

            if (dataStr.contains("_")) {
                JOptionPane.showMessageDialog(this, "Inserire una data valida!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // VALIDAZIONE QUANTITÀ
            String descFinal = "Aggiunta da gestione progetto";
            if (tipoSel == TipoAttivita.RACCOLTA) {
                String qStr = txtQuantita.getText().trim();
                if (qStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Per la RACCOLTA la quantità prevista è obbligatoria!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    double val = Double.parseDouble(qStr.replace(",", "."));
                    if (val <= 0) {
                        JOptionPane.showMessageDialog(this, "La quantità deve essere maggiore di zero!", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    descFinal += " | Quantità prevista: " + qStr + " kg";
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "La quantità deve essere un numero valido!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                Coltura selColtura = (Coltura) comboColtura.getSelectedItem();
                Coltivatore selColtivatore = (Coltivatore) comboColtivatore.getSelectedItem();

                attivitaCreata = new Attivita(
                    0, 
                    tipoSel, 
                    StatoAttivita.PIANIFICATA,
                    LocalDate.parse(dataStr), 
                    null, 
                    descFinal, // Salviamo la quantità nella descrizione
                    selColtura.getId_coltura(), 
                    selColtivatore.getEmail()
                );

                confermato = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isConfermato() { return confermato; }
    public Attivita getAttivitaCreata() { return attivitaCreata; }
}