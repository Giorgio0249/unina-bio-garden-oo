package it.unina.biogarden.gui;

import it.unina.biogarden.controller.DashboardController;
import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.model.Notifica;
import it.unina.biogarden.model.TipoNotifica;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class VisualizzaNotificheDialog extends JDialog {
    private DashboardController controller;
    private JPanel pnlLista; 
    private JScrollPane scroll;

    public VisualizzaNotificheDialog(Frame parent, String titolo, List<Notifica> lista, DashboardController controller) {
        super(parent, titolo, true);
        this.controller = controller;
        setSize(750, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        //headerr
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(45, 110, 60));
        pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitoloHeader = new JLabel("Centro Notifiche");
        lblTitoloHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitoloHeader.setForeground(Color.WHITE);

        JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBottoni.setOpaque(false);

        JButton btnAuto = creaBottoneAzione("GENERA AUTOMATICHE", Color.DARK_GRAY);
        JButton btnTutti = creaBottoneAzione("+ MESSAGGIO A TUTTI", new Color(0, 102, 204));
        JButton btnSingolo = creaBottoneAzione("+ MESSAGGIO SINGOLO", new Color(255, 193, 7));
        btnSingolo.setForeground(Color.BLACK);

        btnAuto.addActionListener(e -> {
            controller.generaNotificheAutomatiche(); 
            refreshLista(controller.getListaNotificheAggiornata());
        });

        btnTutti.addActionListener(e -> mostraFormInvio(null));
        btnSingolo.addActionListener(e -> {
            List<Coltivatore> associati = controller.getColtivatoriAssociati();
            
            if (associati.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non ci sono coltivatori associati ai tuoi lotti.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            //ComboBox con i coltivatori
            JComboBox<Coltivatore> comboColt = new JComboBox<>(new DefaultComboBoxModel<>(associati.toArray(new Coltivatore[0])));
            
            // Renderer per mostrare Nome Cognome (Email) nella combo
            comboColt.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof Coltivatore c) {
                        value = c.getNome() + " " + c.getCognome() + " (" + c.getEmail() + ")";
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });

            int res = JOptionPane.showConfirmDialog(this, comboColt, "Seleziona Destinatario", JOptionPane.OK_CANCEL_OPTION);
            
            if (res == JOptionPane.OK_OPTION) {
                Coltivatore scelto = (Coltivatore) comboColt.getSelectedItem();
                if (scelto != null) {
                    mostraFormInvio(scelto.getEmail());
                }
            }
        });

        pnlBottoni.add(btnAuto);
        pnlBottoni.add(btnTutti);
        pnlBottoni.add(btnSingolo);

        if (this.controller == null) {
            pnlBottoni.setVisible(false);
            lblTitoloHeader.setText("I Tuoi Avvisi");
        }

        pnlHeader.add(lblTitoloHeader, BorderLayout.WEST);
        pnlHeader.add(pnlBottoni, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        //LISTA NOTIFICHE
        pnlLista = new JPanel();
        pnlLista.setLayout(new BoxLayout(pnlLista, BoxLayout.Y_AXIS));
        pnlLista.setBackground(new Color(245, 245, 245));

        scroll = new JScrollPane(pnlLista);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshLista(lista); 

        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dispose());
        JPanel pnlSud = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSud.add(btnChiudi);
        add(pnlSud, BorderLayout.SOUTH);
    }

    public void refreshLista(List<Notifica> nuovaLista) {
        pnlLista.removeAll();
        if (nuovaLista == null || nuovaLista.isEmpty()) {
            JLabel lblVuoto = new JLabel("Nessuna notifica presente nello storico.");
            lblVuoto.setBorder(new EmptyBorder(30, 0, 0, 0));
            lblVuoto.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlLista.add(lblVuoto);
        } else {
            for (Notifica n : nuovaLista) {
                pnlLista.add(creaCardNotifica(n));
                pnlLista.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        pnlLista.revalidate();
        pnlLista.repaint();
    }

    private JButton creaBottoneAzione(String testo, Color bg) {
        JButton b = new JButton(testo);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void mostraFormInvio(String emailDestinatario) {
        String subTitle = (emailDestinatario == null) ? "A tutti i tuoi coltivatori" : "A: " + emailDestinatario;
        
        JPanel pnlForm = new JPanel(new BorderLayout(10, 10));
        pnlForm.setPreferredSize(new Dimension(400, 200));

        JTextField txtTitolo = new JTextField();
        txtTitolo.setBorder(BorderFactory.createTitledBorder("Titolo Notifica"));

        JTextArea txtMessaggio = new JTextArea();
        txtMessaggio.setLineWrap(true);
        txtMessaggio.setWrapStyleWord(true);
        JScrollPane scrollMsg = new JScrollPane(txtMessaggio);
        scrollMsg.setBorder(BorderFactory.createTitledBorder("Messaggio (" + subTitle + ")"));

        pnlForm.add(txtTitolo, BorderLayout.NORTH);
        pnlForm.add(scrollMsg, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, pnlForm, 
                "Invia Nuova Notifica", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String t = txtTitolo.getText().trim();
            String m = txtMessaggio.getText().trim();

            if (t.isEmpty() || m.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Campi obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Invio e refresh immediato
            controller.inviaNotificaManuale(t, m, emailDestinatario);
            refreshLista(controller.getListaNotificheAggiornata());
        }
    }

    private JPanel creaCardNotifica(Notifica n) {
        JPanel card = new JPanel(new BorderLayout(15, 5)); // Aumentato gap
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        Color accent = switch (n.getTipo()) {
            case ATTIVITA_IN_RITARDO -> new Color(210, 50, 50);
            case ATTIVITA_IMMINENTE -> new Color(255, 160, 0);
            case ANOMALIA -> new Color(0, 120, 215);
        };

        //Info Sinistra (Titolo e Messaggio)
        JPanel pnlInfo = new JPanel(new GridLayout(0, 1, 0, 5));
        pnlInfo.setOpaque(false);
        
        JLabel lblTitolo = new JLabel(n.getTitolo());
        lblTitolo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitolo.setForeground(accent);

        JLabel lblMessaggio = new JLabel("<html>" + n.getMessaggio() + "</html>");
        lblMessaggio.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        String dest = (n.getDestinatario_coltivatore_email() == null) ? "TUTTI" : n.getDestinatario_coltivatore_email();
        JLabel lblDettagli = new JLabel("Data: " + n.getData_creazione().format(Notifica.formatter) + " | Dest: " + dest);
        lblDettagli.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblDettagli.setForeground(Color.GRAY);

        pnlInfo.add(lblTitolo);
        pnlInfo.add(lblMessaggio);
        pnlInfo.add(lblDettagli);
        card.add(pnlInfo, BorderLayout.CENTER);

        //TASTO ELIMINA (Solo per Proprietario, Coltivatore visualizza solo)
        if (this.controller != null) {
            JButton btnElimina = new JButton("Elimina");
            btnElimina.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnElimina.setForeground(new Color(180, 0, 0));
            btnElimina.setBackground(new Color(255, 235, 235));
            btnElimina.setFocusPainted(false);
            btnElimina.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnElimina.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Eliminare questa notifica?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.eliminaNotifica(n.getId());
                    refreshLista(controller.getListaNotificheAggiornata());
                }
            });
            
            JPanel pnlEst = new JPanel(new GridBagLayout());
            pnlEst.setOpaque(false);
            pnlEst.add(btnElimina);
            card.add(pnlEst, BorderLayout.EAST);
        }

        return card;
    }
}