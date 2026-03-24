package it.unina.biogarden.gui;

import it.unina.biogarden.controller.ColtivatoreController;
import it.unina.biogarden.controller.LoginController;
import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.StatoAttivita;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ColtivatoreFrame extends JFrame {
    private JTable tblAttive, tblStorico;
    private DefaultTableModel modelAttive, modelStorico;
    private ColtivatoreController controller;

    private static final Color PRIMARY_COLOR = new Color(45, 110, 60);
    private static final Color ACCENT_COLOR = new Color(200, 230, 201);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    public ColtivatoreFrame(ColtivatoreController controller, String nomeColtivatore) {
        super("UninaBioGarden - Area Operativa");
        this.controller = controller;
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                LoginController.logout(ColtivatoreFrame.this);
            }
        });

        setSize(1100, 700);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(PRIMARY_COLOR);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel lblTitle = new JLabel("<html><font color='white' size='5'><b>BioGarden</b> Operatore</font></html>");
        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlUser.setOpaque(false);
        
        JLabel lblUser = new JLabel("Coltivatore: " + nomeColtivatore + "  ");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(FONT_BOLD);

        JButton btnNotifiche = new JButton("CENTRO NOTIFICHE");
        btnNotifiche.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNotifiche.setBackground(new Color(0, 120, 215)); // Blu per distinguersi
        btnNotifiche.setForeground(Color.WHITE);
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

        // --- TABBED PANE STILIZZATO ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.addTab("  Lavoro da Svolgere  ", creaPannelloOperativo());
        tabs.addTab("  Storico Attività  ", creaPannelloStorico());
        add(tabs, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
    }

    private JPanel creaPannelloOperativo() {
        JPanel mainPnl = new JPanel(new BorderLayout(15, 15));
        mainPnl.setBackground(Color.WHITE);
        mainPnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblInfo = new JLabel("Attività Pianificate e in Corso");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPnl.add(lblInfo, BorderLayout.NORTH);

        String[] colonne = {"ID", "Tipo", "Coltura", "Stato", "Scadenza", "Descrizione"};
        modelAttive = new DefaultTableModel(colonne, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblAttive = new JTable(modelAttive);
        stileTabella(tblAttive);
        
        // Nascondo ID
        tblAttive.getColumnModel().getColumn(0).setMinWidth(0);
        tblAttive.getColumnModel().getColumn(0).setMaxWidth(0);
        
        // Ottimizzazione colonne
        tblAttive.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblAttive.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblAttive.getColumnModel().getColumn(3).setCellRenderer(new AttivitaStatoRenderer());
        tblAttive.getColumnModel().getColumn(5).setPreferredWidth(350);
        
        mainPnl.add(new JScrollPane(tblAttive), BorderLayout.CENTER);

        // --- BOTTONI AZIONE ---
        JPanel pnlAzioni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlAzioni.setOpaque(false);
        
        JButton btnInizia = creaBottoneModerno("Inizia Lavoro", new Color(0, 102, 204));
        JButton btnCompleta = creaBottoneModerno("Segna Completata", PRIMARY_COLOR);
        JButton btnAnnulla = creaBottoneModerno("Annulla", new Color(180, 180, 180));
        
        btnInizia.addActionListener(e -> gestisciCambioStato(StatoAttivita.IN_CORSO));
        btnCompleta.addActionListener(e -> gestisciCambioStato(StatoAttivita.COMPLETATA));
        btnAnnulla.addActionListener(e -> gestisciCambioStato(StatoAttivita.ANNULLATA));

        pnlAzioni.add(btnAnnulla);
        pnlAzioni.add(btnInizia);
        pnlAzioni.add(btnCompleta);
        mainPnl.add(pnlAzioni, BorderLayout.SOUTH);
        
        return mainPnl;
    }

    private JPanel creaPannelloStorico() {
        JPanel mainPnl = new JPanel(new BorderLayout(15, 15));
        mainPnl.setBackground(Color.WHITE);
        mainPnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblInfo = new JLabel("Archivio Lavori Completati");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPnl.add(lblInfo, BorderLayout.NORTH);

        String[] colonne = {"Tipo", "Coltura", "Stato", "Data Esecuzione"};
        modelStorico = new DefaultTableModel(colonne, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStorico = new JTable(modelStorico);
        stileTabella(tblStorico);
        tblStorico.getColumnModel().getColumn(2).setCellRenderer(new AttivitaStatoRenderer());
        
        mainPnl.add(new JScrollPane(tblStorico), BorderLayout.CENTER);
        return mainPnl;
    }

    //METODI DI STILE PRIVATI
    private void stileTabella(JTable table) {
        table.setFont(FONT_PLAIN);
        table.setRowHeight(32);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 235, 235));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
    }

    private JButton creaBottoneModerno(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(colore);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void gestisciCambioStato(StatoAttivita nuovo) {
        int row = tblAttive.getSelectedRow();
        if (row != -1) {
            int modelRow = tblAttive.convertRowIndexToModel(row);
            int id = (int) tblAttive.getModel().getValueAt(modelRow, 0);
            controller.cambiaStato(id, nuovo);
            tblAttive.clearSelection();
        } else {
            mostraErrore("Seleziona un'attività dalla tabella!");
        }
    }

    public void popolaTabelle(List<Attivita> attive, List<Attivita> storico) {
        modelAttive.setRowCount(0);
        for (Attivita a : attive) {
            modelAttive.addRow(new Object[]{
                a.getId_attivita(), a.getTipoAttivita(),
                a.getNomeTipoColtura() + " (ID: " + a.getFk_coltura() + ")",
                a.getStato(), a.getDataPianificata(), a.getDescrizione()
            });
        }
        
        modelStorico.setRowCount(0);
        for (Attivita a : storico) {
            modelStorico.addRow(new Object[]{
                a.getTipoAttivita(),
                a.getNomeTipoColtura() + " (ID: " + a.getFk_coltura() + ")",
                a.getStato(), a.getDataEffettiva() != null ? a.getDataEffettiva() : "---"
            });
        }
    }

    public void mostraErrore(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Attenzione", JOptionPane.ERROR_MESSAGE);
    }
}