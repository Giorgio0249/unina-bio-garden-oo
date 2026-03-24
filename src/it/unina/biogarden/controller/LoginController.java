package it.unina.biogarden.controller;

import it.unina.biogarden.gui.LoginFrame;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.service.ProprietarioService;
import it.unina.biogarden.service.ColtivatoreService;
import javax.swing.*;

public class LoginController {
    private LoginFrame view;
    private ProprietarioService proprietarioService;
    private ColtivatoreService coltivatoreService;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.proprietarioService = new ProprietarioService(); 
        this.coltivatoreService = new ColtivatoreService();
    }

    public void gestisciLogin() {
        String email = view.getUsername(); 
        String password = view.getPassword();

        try {
            if (view.isProprietarioSelected()) {
                Proprietario p = proprietarioService.authenticate(email, password);
                view.dispose();
                new DashboardController(p);
            } else {
                Coltivatore c = coltivatoreService.authenticate(email, password);
                view.dispose();
                new ColtivatoreController(c); 
            }
        } catch (IllegalArgumentException e) {
            view.mostraMessaggioErrore(e.getMessage());
        } catch (Exception e) {
            view.mostraMessaggioErrore("Errore di sistema: " + e.getMessage());
        }
    }

    // METODO DI LOGOUT UNIVERSALE
    public static void logout(JFrame currentFrame) {
        int scelta = JOptionPane.showConfirmDialog(
            currentFrame, 
            "Vuoi effettuare il logout e tornare alla schermata di accesso?", 
            "Conferma Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (scelta == JOptionPane.YES_OPTION) {
            currentFrame.dispose();
            new LoginFrame(); // Riapre la finestra di login
        }
    }
}