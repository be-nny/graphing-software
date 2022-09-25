package calculator.line;

import javax.swing.*;
import java.awt.*;

public class MyTextField extends JTextField {

    private int width;
    private int height;
    public Color colour;

    /**
     * @param c Colour
     * @param width Width of the Text Field component
     * @param height Height of the Text Field component
     * */
    public MyTextField(int width, int height, Color c){
        //setting the width, height and colour params
        this.width = width;
        this.height = height;
        this.colour = c;

        //setting up the JPanel info
        setPreferredSize(new Dimension(width, height));
        setAutoscrolls(true);
        setOpaque(true);
        setForeground(Color.WHITE);
        setBackground(null);

        revalidate();
    }

    @Override
    public void paintComponent(Graphics g){
        //drawing a curved border around the text field
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(this.colour);
        g2d.drawRoundRect(0,0, this.width, this.height, height/2, height/2);
        revalidate();
    }
}
