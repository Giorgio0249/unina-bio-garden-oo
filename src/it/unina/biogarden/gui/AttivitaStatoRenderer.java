package it.unina.biogarden.gui;

import it.unina.biogarden.model.StatoAttivita;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AttivitaStatoRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Se il valore è uno StatoAttivita, cambiamo il colore
        if (value instanceof StatoAttivita) {
            StatoAttivita stato = (StatoAttivita) value;
            switch (stato) {
                case IN_CORSO:
                    c.setBackground(new Color(200, 230, 255)); // Blu chiaro
                    c.setForeground(Color.BLACK);
                    break;
                case COMPLETATA:
                    c.setBackground(new Color(200, 255, 200)); // Verde chiaro
                    c.setForeground(Color.BLACK);
                    break;
                case ANNULLATA:
                    c.setBackground(Color.LIGHT_GRAY);
                    c.setForeground(Color.DARK_GRAY);
                    break;
                default:
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                    break;
            }
        } else {
            // Ripristina i colori standard per le altre celle se necessario
            if (!isSelected) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
        }

        // Mantiene l'evidenziazione della riga se selezionata
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }

        return c;
    }
}