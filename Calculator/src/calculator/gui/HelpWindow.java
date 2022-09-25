package calculator.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HelpWindow {
    public static JFrame window;
    private static int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static int width = 600;
    private static int height = 350;

    //image
    private static String backgroundImagePath = "src/assets/images/background.png";
    private static ImageIcon backgroundImage = new ImageIcon(String.valueOf(new File(backgroundImagePath)));
    private static ImageIcon scaledBackgroundImage = new ImageIcon(backgroundImage.getImage().getScaledInstance(backgroundImage.getIconWidth() * 2, backgroundImage.getIconHeight() * 2, Image.SCALE_SMOOTH));

    //text elements
    private static GraphicsEnvironment ge;
    private static JLabel titleText = new JLabel();
    private static JLabel bodyText = new JLabel();
    private static JLabel subTitleText = new JLabel();


    //fonts
    private static Font titleFont;
    private static Font textFont;
    //trying to create custom fonts
    static {
        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/fonts/ModernSans-Light.otf"));
            textFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/fonts/ModernSans-Light.otf"));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HelpWindow(){
        //creating the window
        window = new JFrame();
        window.setResizable(false);
        window.setLocation(screen_width/2 - width/2, screen_height/2 - height/2);
        window.setPreferredSize(new Dimension(width, height));
        window.setBackground(Color.BLUE);
        window.setVisible(true);
        window.pack();

        setFonts();

        //adding the main panel to the window
        window.add(mainPanel());

    }

    private void setFonts(){
        //creating graphics environment to add in custom fonts
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(titleFont);

    }

    private JPanel mainPanel(){
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        titleText.setText("How To Use");
        titleText.setFont(titleFont.deriveFont(30f));
        titleText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        bodyText.setText("<html>" +
                "<ul>" +
                "<li>Click the 'add' button to create a new line and right clicking the line's box will bring up the save menu</li>" +
                "<li>Click and Drag on the white graph to move around</li>" +
                "<li>Zooming in and out is done by scrolling</li>" +
                "<li>To open a saved line, click the 'open' button and find the line file</li>" +
                "<li>The menu at the top also has the same tools that are accessible in the gui</li>" +
                "</ul>" +
                "<br></br>" +
                "<br></br>" +
                "<p>When writing the equations in, the can only be in the form y = x....</p>" +
                "</html>");
        bodyText.setFont(textFont.deriveFont(16f));
        bodyText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        subTitleText = new JLabel();
        subTitleText.setText("<html>Welcome to my Graphing Software</html>");
        subTitleText.setFont(textFont.deriveFont(20f));
        subTitleText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //adding the text to the panel
        pane.add(titleText, BorderLayout.NORTH);
        pane.add(subTitleText, BorderLayout.PAGE_START);
        pane.add(bodyText, BorderLayout.PAGE_START);

        return pane;
    }
}
