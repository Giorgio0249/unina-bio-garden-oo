package it.unina.biogarden.gui;

import it.unina.biogarden.controller.DashboardController;
import it.unina.biogarden.controller.LoginController;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.TipoColtura;
import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.model.ProgettoStagionale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class DashboardFrame extends JFrame {
    private DashboardController controller;
    private JTable tblLotti, tblProgetti;
    private DefaultTableModel modelLotti, modelProgetti;
    private JComboBox<Lotto> comboLotti;
    private JList<TipoColtura> listColture;
    private DefaultListModel<TipoColtura> modelListColture;
    private JTextField txtNomeProgetto;
    private JFormattedTextField txtDataInizio, txtDataFine; 
    private JTextArea txtDescrizione;

    private static final Color PRIMARY_COLOR = new Color(45, 110, 60);
    private static final Color SIDEBAR_COLOR = new Color(245, 250, 245);
    private static final Color ACCENT_COLOR = new Color(200, 230, 201);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    public DashboardFrame(DashboardController controller) {
        super("UninaBioGarden - Gestione Proprietario");
        this.controller = controller;
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                LoginController.logout(DashboardFrame.this);
            }
        });

        setSize(1200, 800);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(PRIMARY_COLOR);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel lblTitle = new JLabel("<html><font color='white' size='5'><b>BioGarden</b> Dashboard</font></html>");
        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0)); 
        pnlUser.setOpaque(false);
        
        JLabel lblUser = new JLabel("Bentornato, " + controller.getProprietarioLoggato().getNome() + "  ");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(FONT_BOLD);
        
        JButton btnNotifiche = new JButton("CENTRO NOTIFICHE");
        btnNotifiche.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNotifiche.setBackground(new Color(255, 193, 7)); 
        btnNotifiche.setForeground(Color.BLACK);
        btnNotifiche.setFocusPainted(false);
        btnNotifiche.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNotifiche.addActionListener(e -> controller.apriCentroNotifiche());
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> LoginController.logout(this));
        
        pnlUser.add(lblUser);
        pnlUser.add(btnNotifiche); 
        pnlUser.add(btnLogout);
        
        pnlTop.add(lblTitle, BorderLayout.WEST);
        pnlTop.add(pnlUser, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_BOLD);
        tabbedPane.addTab(" Esplora Lotti e Progetti ", creaPannelloVisualizzazione());
        tabbedPane.addTab(" Crea Nuovo Progetto ", creaPannelloCreazioneProgetto());

        add(tabbedPane, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel creaPannelloVisualizzazione() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        //SIDEBAR SINISTRA (Selezione Lotto) 
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel lblLotti = new JLabel(" I Tuoi Lotti");
        lblLotti.setFont(FONT_BOLD);
        lblLotti.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        sidebar.add(lblLotti, BorderLayout.NORTH);

        String[] colLotti = {"ID", "Posizione", "Mq"};
        modelLotti = new DefaultTableModel(colLotti, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLotti = new JTable(modelLotti);
        stileTabella(tblLotti);
        tblLotti.getColumnModel().getColumn(0).setMinWidth(0);
        tblLotti.getColumnModel().getColumn(0).setMaxWidth(0);
        
        sidebar.add(new JScrollPane(tblLotti), BorderLayout.CENTER);

        //AREA CENTRALE (Progetti del Lotto selezionato)
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 15));
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblProgetti = new JLabel("Progetti Attivi nel Lotto Selezionato");
        lblProgetti.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlCenter.add(lblProgetti, BorderLayout.NORTH);

        String[] colProgetti = {"ID", "Nome Progetto", "Data Inizio", "Data Fine"};
        modelProgetti = new DefaultTableModel(colProgetti, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProgetti = new JTable(modelProgetti);
        stileTabella(tblProgetti);
        tblProgetti.getColumnModel().getColumn(0).setMinWidth(0);
        tblProgetti.getColumnModel().getColumn(0).setMaxWidth(0);
        
        pnlCenter.add(new JScrollPane(tblProgetti), BorderLayout.CENTER);

        //PANNELLO AZIONI (BOTTONI)
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlActions.setOpaque(false);
        
        JButton btnDettagli = creaBottoneModerno("Vedi Dettagli", PRIMARY_COLOR);
        JButton btnModifica = creaBottoneModerno("Modifica", Color.DARK_GRAY);
        JButton btnReport = creaBottoneModerno("Report Progetto", new Color(0, 102, 204));
        JButton btnElimina = creaBottoneModerno("Elimina", new Color(200, 50, 50));

        pnlActions.add(btnDettagli);
        pnlActions.add(btnModifica);
        pnlActions.add(btnReport);
        pnlActions.add(btnElimina);
        pnlCenter.add(pnlActions, BorderLayout.SOUTH);

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(pnlCenter, BorderLayout.CENTER);

        // --- EVENTI ---
        tblLotti.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblLotti.getSelectedRow();
                if (row != -1) controller.lottoSelezionato(row);
            }
        });

        btnDettagli.addActionListener(e -> {
            int row = tblProgetti.getSelectedRow();
            if (row != -1) {
                int id = (int)tblProgetti.getValueAt(row, 0);
                controller.visualizzaDettagliProgetto(id, tblProgetti.getValueAt(row, 1).toString());
            } else mostraErrore("Seleziona un progetto!");
        });

        btnModifica.addActionListener(e -> {
            int row = tblProgetti.getSelectedRow();
            if (row != -1) controller.apriModificaProgetto((int)tblProgetti.getValueAt(row, 0));
            else mostraErrore("Seleziona un progetto!");
        });

        btnElimina.addActionListener(e -> {
            int row = tblProgetti.getSelectedRow();
            if (row != -1) {
                int id = (int)tblProgetti.getValueAt(row, 0);
                int conf = JOptionPane.showConfirmDialog(this, "Eliminare definitivamente il progetto?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) controller.eliminaProgetto(id);
            }
        });
        
        btnReport.addActionListener(e -> {
            int row = tblProgetti.getSelectedRow();
            if (row != -1) {
                int id = (int) tblProgetti.getValueAt(row, 0);  
                controller.visualizzaReportLotto(id, tblProgetti.getValueAt(row, 1).toString());
            } else mostraErrore("Seleziona un progetto per le statistiche!");
        });

        return mainPanel;
    }

    private JPanel creaPannelloCreazioneProgetto() {
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Color.WHITE);
        
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; card.add(new JLabel("Seleziona Lotto di Destinazione:"), gbc);
        comboLotti = new JComboBox<>(); gbc.gridx = 1; card.add(comboLotti, gbc);

        gbc.gridx = 0; gbc.gridy = 1; card.add(new JLabel("Nome del Progetto:"), gbc);
        txtNomeProgetto = new JTextField(20); gbc.gridx = 1; card.add(txtNomeProgetto, gbc);

        gbc.gridx = 0; gbc.gridy = 2; card.add(new JLabel("Data Inizio (AAAA-MM-GG):"), gbc);
        try {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            dateMask.setPlaceholderCharacter('_'); 
            txtDataInizio = new JFormattedTextField(dateMask);
        } catch (ParseException e) { txtDataInizio = new JFormattedTextField(); }
        gbc.gridx = 1; card.add(txtDataInizio, gbc);

        gbc.gridx = 0; gbc.gridy = 3; card.add(new JLabel("Data Fine (Opzionale):"), gbc);
        try {
            MaskFormatter dateMask2 = new MaskFormatter("####-##-##");
            dateMask2.setPlaceholderCharacter('_'); 
            txtDataFine = new JFormattedTextField(dateMask2);
        } catch (ParseException e) { txtDataFine = new JFormattedTextField(); }
        gbc.gridx = 1; card.add(txtDataFine, gbc);

        gbc.gridx = 0; gbc.gridy = 4; card.add(new JLabel("Note / Descrizione:"), gbc);
        txtDescrizione = new JTextArea(3, 20); txtDescrizione.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescrizione); gbc.gridx = 1; card.add(scrollDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 5; card.add(new JLabel("Scegli le Colture:"), gbc);
        modelListColture = new DefaultListModel<>();
        listColture = new JList<>(modelListColture);
        JScrollPane scrollColture = new JScrollPane(listColture);
        scrollColture.setPreferredSize(new Dimension(250, 80)); gbc.gridx = 1; card.add(scrollColture, gbc);

        JButton btnSalva = creaBottoneModerno("Continua alla Definizione Attività", PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.ipady = 15;
        card.add(btnSalva, gbc);

        btnSalva.addActionListener(e -> {
            String nome = txtNomeProgetto.getText().trim();
            if (txtDataInizio.getText().contains("_") || nome.isEmpty() || comboLotti.getSelectedItem() == null) {
                mostraErrore("Compila tutti i campi obbligatori!"); return;
            }
            controller.avviaDefinizioneAttivita(nome, txtDataInizio.getText(), 
                txtDataFine.getText().contains("_") ? null : txtDataFine.getText(), 
                txtDescrizione.getText().trim(), (Lotto) comboLotti.getSelectedItem(), listColture.getSelectedValuesList());
        });

        mainContainer.add(card);
        return mainContainer;
    }

    private void stileTabella(JTable table) {
        table.setFont(FONT_PLAIN);
        table.setRowHeight(30);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));
    }

    private JButton creaBottoneModerno(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(colore);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void popolaComponentiCreazione(List<Lotto> lotti, List<TipoColtura> colture, List<Coltivatore> coltivatori) {
        comboLotti.removeAllItems();
        for (Lotto l : lotti) comboLotti.addItem(l);
        modelListColture.clear();
        for (TipoColtura tc : colture) modelListColture.addElement(tc);
    }

    public void aggiornaTabellaLotti(List<Lotto> lotti) {
        modelLotti.setRowCount(0);
        for (Lotto l : lotti) {
            modelLotti.addRow(new Object[]{ l.getId_lotto(), l.getPosizione(), l.getSuperficie() });
        }
    }

    public void aggiornaTabellaProgetti(List<ProgettoStagionale> progetti) {
        modelProgetti.setRowCount(0);
        for (ProgettoStagionale p : progetti) {
            modelProgetti.addRow(new Object[]{p.getId_progetto(), p.getNome(), p.getDataInizio(), p.getDataFine() });
        }
    }
    
    public JTable getTblLotti() { return tblLotti; }

    public void mostraErrore(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Attenzione", JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostraMessaggio(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Operazione Riuscita", JOptionPane.INFORMATION_MESSAGE);
    }
}