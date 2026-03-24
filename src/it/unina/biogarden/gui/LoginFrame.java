package it.unina.biogarden.gui;

import it.unina.biogarden.controller.LoginController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser = new JTextField(20);
    private JPasswordField txtPass = new JPasswordField(20);
    private JButton btnLogin;
    
    private JToggleButton btnRoleProp = new JToggleButton("PROPRIETARIO");
    private JToggleButton btnRoleColt = new JToggleButton("COLTIVATORE");
    private ButtonGroup gruppoRuolo = new ButtonGroup();

    private static final Color PRIMARY_COLOR = new Color(45, 110, 60);
    private static final Color BG_COLOR = new Color(240, 245, 240);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);

    public LoginFrame() {
        super("UninaBioGarden - Accesso"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new GridBagLayout());

        //1. CARD DI LOGIN 
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT); 
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 40, 40, 40)
        ));


        JLabel lblLogo = new JLabel("BioGarden");
        lblLogo.setFont(FONT_TITLE);
        lblLogo.setForeground(PRIMARY_COLOR);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSub = new JLabel("Gestione Orti Urbani");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSub.setBorder(new EmptyBorder(0, 0, 30, 0));

        // --- 3. SELETTORE RUOLO ---
        JLabel lblRuoloHeader = new JLabel("Seleziona Ruolo:");
        lblRuoloHeader.setFont(FONT_LABEL);
        lblRuoloHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlRuolo = new JPanel(new GridLayout(1, 2, 0, 0));

        pnlRuolo.setMaximumSize(new Dimension(320, 40)); 
        pnlRuolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        stileSwitchButton(btnRoleProp);
        stileSwitchButton(btnRoleColt);
        btnRoleProp.setSelected(true);

        gruppoRuolo.add(btnRoleProp);
        gruppoRuolo.add(btnRoleColt);
        pnlRuolo.add(btnRoleProp);
        pnlRuolo.add(btnRoleColt);

        // --- 4. CAMPI INPUT ---
        JLabel lblUser = new JLabel("Email Utente");
        lblUser.setFont(FONT_LABEL);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUser.setFont(FONT_INPUT);
        txtUser.setHorizontalAlignment(JTextField.LEFT);
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUser.setMaximumSize(new Dimension(320, 40));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(FONT_LABEL);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass.setFont(FONT_INPUT);
        txtPass.setHorizontalAlignment(JTextField.LEFT);
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPass.setMaximumSize(new Dimension(320, 40));
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));

        // 5. BOTTONE ACCEDi
        btnLogin = new JButton("ACCEDI");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setMaximumSize(new Dimension(320, 50));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT); 

        //ASSEMBLAGGIO
        card.add(lblLogo);
        card.add(lblSub);
        
        card.add(lblRuoloHeader);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(pnlRuolo);
        
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        card.add(lblUser);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(txtUser);
        
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        card.add(lblPass);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(txtPass);
        
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(btnLogin);

        add(card);

        LoginController controller = new LoginController(this);
        btnLogin.addActionListener(e -> controller.gestisciLogin());

        setLocationRelativeTo(null); 
        setVisible(true);
    }

    private void stileSwitchButton(JToggleButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(PRIMARY_COLOR);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.GRAY);
            }
        });
    }

    public String getUsername() { return txtUser.getText().trim(); }
    public String getPassword() { return new String(txtPass.getPassword()); }
    public boolean isProprietarioSelected() { return btnRoleProp.isSelected(); }
    
    public void mostraMessaggioErrore(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Errore di Autenticazione", JOptionPane.ERROR_MESSAGE);
    }
}