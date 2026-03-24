package it.unina.biogarden.gui;

import it.unina.biogarden.model.ReportLotto;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class GraficoReportPanel extends JPanel {
    private List<ReportLotto> dati;

    public GraficoReportPanel(List<ReportLotto> dati) {
        this.dati = dati;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dati == null || dati.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 50;
        int labelPadding = 60;
        int width = getWidth();
        int height = getHeight();

        // 1. Calcolo del range per le Ordinate (N)
        double maxN = 0;
        for (ReportLotto r : dati) {
            if (r.getQuantita_max().doubleValue() > maxN) maxN = r.getQuantita_max().doubleValue();
        }
        maxN = maxN == 0 ? 10 : maxN * 1.2; // Padding superiore del 20%

        // 2. Disegno Assi
        g2.drawLine(padding + labelPadding, height - padding, padding + labelPadding, padding); // Asse N
        g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding); // Asse M

        // 3. Disegno Scala Ordinate (N)
        int numScale = 10;
        for (int i = 0; i <= numScale; i++) {
            int x0 = padding + labelPadding;
            int x1 = x0 - 5;
            int y = height - padding - (i * (height - padding * 2) / numScale);
            g2.drawLine(x0, y, x1, y);
            String label = String.format("%.1f", (maxN / numScale) * i);
            g2.drawString(label, x0 - 45, y + 5);
        }

        // 4. Disegno Barre (M dipendente da N per la proporzione altezza)
        int numColture = dati.size();
        int barWidth = (width - padding * 2 - labelPadding) / (numColture * 4); // Spazio per 3 barre + gap

        for (int i = 0; i < numColture; i++) {
            ReportLotto r = dati.get(i);
            int xBase = padding + labelPadding + (i * (width - padding * 2 - labelPadding) / numColture) + 20;

            // Altezze calcolate in base a N
            int hMin = (int) (r.getQuantita_min().doubleValue() * (height - padding * 2) / maxN);
            int hAvg = (int) (r.getQuantita_med().doubleValue() * (height - padding * 2) / maxN);
            int hMax = (int) (r.getQuantita_max().doubleValue() * (height - padding * 2) / maxN);

            // Disegno Barre: Min (Giallo), Media (Verde), Max (Arancione)
            disegnaBarra(g2, xBase, height - padding - hMin, barWidth, hMin, new Color(255, 213, 79), "Min");
            disegnaBarra(g2, xBase + barWidth, height - padding - hAvg, barWidth, hAvg, new Color(102, 187, 106), "Media");
            disegnaBarra(g2, xBase + barWidth * 2, height - padding - hMax, barWidth, hMax, new Color(255, 112, 67), "Max");

            // Label Coltura (M)
            g2.setColor(Color.BLACK);
            g2.drawString(r.getTipo_coltura(), xBase, height - padding + 20);
        }
    }

    private void disegnaBarra(Graphics2D g2, int x, int y, int w, int h, Color c, String tip) {
        g2.setColor(c);
        g2.fillRect(x, y, w, h);
        g2.setColor(c.darker());
        g2.drawRect(x, y, w, h);
    }
}