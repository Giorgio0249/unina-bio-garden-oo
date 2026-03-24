package it.unina.biogarden.gui;

import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.ProgettoStagionale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class VisualizzaAttivitaDialog extends JDialog {

    private static final Color COLOR_PRIMARY = new Color(45, 110, 60); 
    private static final Color COLOR_ACCENT = new Color(200, 230, 201);
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_TEXT_LIGHT = new Color(120, 120, 120);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    public VisualizzaAttivitaDialog(Frame parent, ProgettoStagionale progetto, List<Attivita> attivita) {
        super(parent, "Scheda Completa Progetto | UninaBioGarden", true);
        
        //SETUP DIALOG 
        setSize(1200, 750); 
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE); 
        
        // header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(COLOR_PRIMARY);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Titolo (usiamo HTML per unire Titolo e Sottotitolo)
        JLabel lblTitle = new JLabel("<html><b><font color='white' size='6'>" + progetto.getNome() + "</font></b><br>" +
                                      "<font color='#C8E6C9'><i>Dettagli Operativi e Attività</i></font></html>");
        
        JLabel lblIcon = new JLabel("ID: " + progetto.getId_progetto());
        lblIcon.setFont(FONT_SUBTITLE);
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(lblIcon, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        //PANNELLO RIEPILOGO DATI
        JPanel pnlRiepilogo = new JPanel(new GridBagLayout());
        pnlRiepilogo.setBackground(COLOR_ACCENT);
        pnlRiepilogo.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 30); 
        gbc.anchor = GridBagConstraints.WEST;

        // Metodo di supporto per creare le label dati formattate
        // Riga 1: Stagione/Anno
        gbc.gridx = 0; gbc.gridy = 0;
        pnlRiepilogo.add(creaLabelDato("Stagione", (progetto.getStagione() != null ? progetto.getStagione().toString() : "N/D")), gbc);
        
        gbc.gridx = 1;
        pnlRiepilogo.add(creaLabelDato("Anno", String.valueOf(progetto.getAnno())), gbc);

        // Riga 2: Date
        gbc.gridx = 0; gbc.gridy = 1;
        pnlRiepilogo.add(creaLabelDato("Data Inizio", progetto.getDataInizio().toString()), gbc);
        
        gbc.gridx = 1;
        String dataF = (progetto.getDataFine() != null) ? progetto.getDataFine().toString() : "<i>Progetto In Corso</i>";
        pnlRiepilogo.add(creaLabelDato("Data Fine", dataF), gbc);

        // Riga 3: Descrizione
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        String desc = (progetto.getDescrizione() != null && !progetto.getDescrizione().isEmpty()) 
                      ? progetto.getDescrizione() : "Nessuna descrizione inserita.";
        
        JLabel lblDescTitle = new JLabel("Descrizione");
        lblDescTitle.setFont(FONT_SUBTITLE);
        lblDescTitle.setForeground(COLOR_TEXT_DARK);
        
        JTextArea txtDesc = new JTextArea(desc, 3, 20); // TextArea per gestire testo lungo
        txtDesc.setFont(FONT_PLAIN);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false); txtDesc.setBackground(COLOR_ACCENT);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel pnlDescBlock = new JPanel(new BorderLayout(5, 5));
        pnlDescBlock.setBackground(COLOR_ACCENT);
        pnlDescBlock.add(lblDescTitle, BorderLayout.NORTH);
        pnlDescBlock.add(new JScrollPane(txtDesc, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        pnlRiepilogo.add(pnlDescBlock, gbc);

        // Pannello che unisce Riepilogo e Tabella
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 15));
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlCenter.add(pnlRiepilogo, BorderLayout.NORTH);

        // TABELLA ATTIVITÀ
        String[] colonne = {"Tipo", "Coltura", "Stato", "Data Pianificata", "Data Effettiva", "Coltivatore", "Descrizione"};
        
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Attivita a : attivita) {
            Object dataEff = (a.getDataEffettiva() != null) ? a.getDataEffettiva() : "---";
            String infoColtura = a.getNomeTipoColtura() + " (ID: " + a.getFk_coltura() + ")";
            Object[] riga = { a.getTipoAttivita(), infoColtura, a.getStato(), a.getDataPianificata(), dataEff, a.getFk_coltivatore(), a.getDescrizione() };
            model.addRow(riga);
        }

        JTable tabella = new JTable(model);
        tabella.setFont(FONT_PLAIN); tabella.setRowHeight(28); 
        tabella.setSelectionBackground(COLOR_ACCENT); tabella.setSelectionForeground(COLOR_TEXT_DARK);
        tabella.setShowVerticalLines(false); tabella.setGridColor(new Color(230, 230, 230));

        // STILIZZAZIONE HEADER TABELLA
        JTableHeader header = tabella.getTableHeader();
        header.setFont(FONT_SUBTITLE); header.setBackground(Color.WHITE); header.setForeground(COLOR_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARY)); 

        // Renderer colori
        tabella.getColumnModel().getColumn(2).setCellRenderer(new AttivitaStatoRenderer());
        
        tabella.getColumnModel().getColumn(0).setPreferredWidth(100); // Tipo
        tabella.getColumnModel().getColumn(1).setPreferredWidth(180); // Coltura
        tabella.getColumnModel().getColumn(2).setPreferredWidth(120); // Stato
        tabella.getColumnModel().getColumn(6).setPreferredWidth(350); // Descrizione
        
        pnlCenter.add(new JScrollPane(tabella), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        //PANNELLO INFERIORE
        JPanel pnlSud = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSud.setBackground(Color.WHITE);
        pnlSud.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 25));
        
        JButton btnChiudi = new JButton("Chiudi Scheda Progetto");
        btnChiudi.setFont(FONT_SUBTITLE); btnChiudi.setForeground(Color.WHITE);
        btnChiudi.setBackground(COLOR_PRIMARY); // Bottone solido
        btnChiudi.setFocusPainted(false); btnChiudi.setBorderPainted(false);
        btnChiudi.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btnChiudi.addActionListener(e -> dispose());
        
        pnlSud.add(btnChiudi);
        add(pnlSud, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
    }

    // Metodo di supporto per creare un blocco JLabel "Titolo Dato -> Valore" usando HTML
    private JLabel creaLabelDato(String titolo, String valore) {
        JLabel label = new JLabel("<html><b><font color='#444444'>" + titolo + ": </font></b>" +
                                   "<font color='#000000' size='4'>  " + valore + "</font></html>");
        label.setFont(FONT_PLAIN);
        return label;
    }
}