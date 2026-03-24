package it.unina.biogarden.gui;

import it.unina.biogarden.model.ReportLotto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportLottoDialog extends JDialog {
    
    public ReportLottoDialog(Frame parent, String nomeLotto, List<ReportLotto> dati) {
        super(parent, "Report Analitico: " + nomeLotto, true);
        setSize(1000, 700);
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(parent);

        // Header
        JLabel lblTitolo = new JLabel("Statistiche di Raccolta - " + nomeLotto, JLabel.CENTER);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitolo, BorderLayout.NORTH);

        //Il Grafico
        GraficoReportPanel pnlGrafico = new GraficoReportPanel(dati);
        pnlGrafico.setBorder(BorderFactory.createTitledBorder("Confronto Rese (Kg)"));
        add(pnlGrafico, BorderLayout.CENTER);

        //Tabella Dati
        String[] colonne = {"Coltura", "Minimo", "Media", "Massimo", "N. Raccolte"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0);
        for (ReportLotto r : dati) {
            model.addRow(new Object[]{
                r.getTipo_coltura(), r.getQuantita_min() + " kg", 
                r.getQuantita_med() + " kg", r.getQuantita_max() + " kg", r.getNum_racolte()
            });
        }
        JTable tabella = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabella);
        scroll.setPreferredSize(new Dimension(0, 150));
        add(scroll, BorderLayout.SOUTH);
    }
}