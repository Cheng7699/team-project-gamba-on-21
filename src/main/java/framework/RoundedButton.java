package framework;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private final int radius; // how round the corners are

    public RoundedButton(String text, int radius) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(new Color(255, 196, 0));
        setBackground(new Color(168, 83, 83));
        this.radius = radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // anti-alias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        if (radius == 100) {
            g2.setColor(getBackground());
            g2.fillOval(0, 0, getWidth(), getHeight());
        }
        else{
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        }

        // paint text
        super.paintComponent(g2);
        g2.dispose();
    }


}
