package calculator.gui;

import calculator.line.Line;
import calculator.software.Graff;
import calculator.software.serialization.MySerializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class Window implements WindowListener, ActionListener {

    public static JFrame frame;
    public static int width;
    public static int height;

    //panel ratios
    public static float side_pane_ratio = 0.2f;
    public static float main_pane_ratio = 1 - side_pane_ratio;

    //colours
    public static Color sidePanelColour = new Color(28,28,28);
    public static Color mainPanelColour = new Color(255,255,255);

    //panels
    public static JPanel mainPane;
    public static JPanel sidePane;
    public static JPanel equPane;
    public static RenderPanel renderPanel;

    //buttons
    private static JButton addBtn;
    private static JButton removeBtn;
    private static JButton openBtn;

    //images logos
    private static final String smallIconLogo = "src/assets/logos/SMALL_LOGO.png";

    //saved files
    private static String[] lineSaveNames;

    public Window() throws IOException {
        super();

        frame = new JFrame();
        frame.setTitle("Graff | Graph Software");
        width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(width, height));
        frame.setVisible(true);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(smallIconLogo));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sidePane = sidePanel();

        SwingUtilities.invokeLater(() -> {
            frame.add(sidePane, BorderLayout.LINE_START);
            //render panel
            renderPanel = new RenderPanel();
            mainPane = renderPanel;
            frame.add(mainPane, BorderLayout.CENTER);
        });

        frame.setJMenuBar(menuBar());

        try {
            Graff.mainThread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lineSaveNames = Graff.getFiles();

        frame.revalidate();
        new Line();
    }

    private JPanel sidePanel(){
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());

        pane.setPreferredSize(new Dimension((int) (frame.getWidth() * side_pane_ratio), frame.getHeight()));
        pane.setBackground(sidePanelColour);

        equPane = equationPane();
        pane.add(footerPanel(), BorderLayout.PAGE_END);
        pane.add(equPane, BorderLayout.NORTH);

        return pane;
    }

    private JMenuBar menuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenu exitMenu = new JMenu("Exit");

        //file menu
        JMenuItem add = new JMenuItem("add");
        add.setActionCommand("add");
        add.addActionListener(this);

        JMenuItem removeAll = new JMenuItem("remove all");
        removeAll.setActionCommand("removeALL");
        removeAll.addActionListener(this);

        JMenuItem open = new JMenuItem("open");
        open.setActionCommand("open");
        open.addActionListener(this);

        fileMenu.addSeparator();
        fileMenu.add(add);
        fileMenu.add(removeAll);
        fileMenu.addSeparator();
        fileMenu.add(open);
        fileMenu.addSeparator();

        //help menu
        JMenuItem help = new JMenuItem("how to use");
        help.setActionCommand("help");
        help.addActionListener(this);

        helpMenu.add(help);

        //exit menu
        JMenuItem exit = new JMenuItem("exit");
        exit.setActionCommand("exit");
        exit.addActionListener(this);
        exitMenu.add(exit);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(exitMenu);

        return menuBar;
    }

    private static JPanel equationPane(){
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setBackground(sidePanelColour);
        mainPane.setPreferredSize(new Dimension((int) (frame.getWidth() * side_pane_ratio), frame.getHeight()-100));
        mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        pane.setBackground(sidePanelColour);
        pane.add(Box.createHorizontalGlue());

        JScrollPane scrollPane = new JScrollPane(pane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.setWheelScrollingEnabled(true);

        mainPane.add(pane);

        return mainPane;

    }

    private JPanel footerPanel(){
        JPanel pane = new JPanel(new FlowLayout());
        addBtn = new JButton("add");
        addBtn.setActionCommand("add");
        addBtn.addActionListener(this);

        removeBtn = new JButton("remove ALL");
        removeBtn.setActionCommand("removeALL");
        removeBtn.addActionListener(this);

        openBtn = new JButton("open");
        openBtn.setActionCommand("open");
        openBtn.addActionListener(this);

        pane.add(addBtn, BorderFactory.createEmptyBorder(0,5,0,5));
        pane.add(removeBtn, BorderFactory.createEmptyBorder(0,5,0,5));
        pane.add(openBtn, BorderFactory.createEmptyBorder(0,5,0,5));

        pane.setVisible(true);

        return pane;
    }

    //adds the line gui to the sidepanel
    public static void addLineGUI(JPanel pane){
        SwingUtilities.invokeLater(() -> {
            equPane.add(pane);
            equPane.add(Box.createRigidArea(new Dimension(sidePane.getWidth(), 10)));
            equPane.revalidate();
            mainPane.revalidate();
        });
    }

    //removes the line gui from the sidepanel
    public static void removeLineGUI(){
        equPane.removeAll();
        equPane.revalidate();
        equPane.repaint();

        for(Line line: Graff.getAllLines()){
            JPanel pane = line.pane;
            equPane.add(pane);
            equPane.add(Box.createRigidArea(new Dimension(sidePane.getWidth(), 10)));
        }

        equPane.revalidate();
        equPane.repaint();
    }

    //window listeners
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        Graff.isRunning = false;

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {
        Graff.mainThread.resume();

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        Graff.mainThread.suspend();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //manages the events for button presses
        if(e.getActionCommand().equals("add")){
            System.out.println("ADD BUTTON PRESSED");
            new Line();
        } if(e.getActionCommand().equals("removeALL")){
            SwingUtilities.invokeLater(() -> {
                //remove all the lines and call the 'garbage collector' to remove all instances
                synchronized (Graff.getAllLines()){
                    for(Line line: Graff.allLines){
                        line.kill();
                    }

                    Graff.allLines.clear();
                    System.gc();
                    equPane.removeAll();
                    equPane.revalidate();
                    equPane.repaint();
                }
            });

        } if(e.getActionCommand().equals("open")){
            try{
                Object jOptionPane = JOptionPane.showInputDialog(frame, "Open File...", "Menu",JOptionPane.PLAIN_MESSAGE, null, lineSaveNames, lineSaveNames[0]);
                String selection = jOptionPane.toString();
                System.out.println(selection);

                try {
                    Line line = (Line) MySerializer.deserialiseObejct(selection);
                    new Line().setUpLine(line.getEquation(), line.getRpn(), line.getX_coords(), line.getY_coords());
                } catch (Exception ioException) {
                    JOptionPane.showMessageDialog(frame, "Error Occurred :(", "File Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (ArrayIndexOutOfBoundsException n){
                JOptionPane.showMessageDialog(frame, "You have no lines saved!");
            }

        } if(e.getActionCommand().equals("exit")){
            int op = JOptionPane.showConfirmDialog(Window.frame,
                    "Are you sure you want to exit?",
                    "Exit?",
                    JOptionPane.YES_NO_OPTION);

            if(op == 0){
                System.exit(0);
                System.gc();
            }
        } if(e.getActionCommand().equals("help")){
            new HelpWindow();
        }
    }
}