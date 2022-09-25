package calculator.line;

import calculator.software.serialization.MySerializer;
import calculator.gui.Window;
import calculator.math.rpn.MyMathCompiler;
import calculator.software.CustonException;
import calculator.software.Graff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

public class Line implements ActionListener, MouseListener, Serializable {

    protected transient Thread thread;
    private transient boolean isRunning;

    public transient static int width = Window.sidePane.getWidth() - 20;
    public transient static int height = 50;

    private transient JTextField textBox;
    public transient JPanel pane;
    private transient JButton removeBtn;
    private transient Color lineColour;
    private transient Color panelColour = new Color(38,38,38);

    //regex pog
    private transient static Pattern DecimalPattern = Pattern.compile("(\\p{Digit}+[.]\\p{Digit}+)|(\\p{Digit}+)");

    //display text
    private transient String errorText = "Enter Equation!";
    private String equation = "";

    //coordinates
    private int[] x_coords;
    private int[] y_coords;
    private ArrayList<String> rpn;

    //constructor
    public Line(){
        //setting up the lines variables
        System.out.println("created line gui");
        this.textBox = new JTextField();
        this.isRunning = true;
        this.lineColour = generateColour();
        createPanel();

        start();
    }

    //starting function
    private void start(){
        //line thread
        this.thread = new Thread(() -> {
            //creating the new line
            System.out.println("Running on Thread: " + this.thread.getName());

            Window.addLineGUI(this.pane);
            Graff.queueLine(this);

            while (this.isRunning){
                try {
                    update();
                    thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.thread.start();
    }

    //kills the LINE THREAD
    /**
     * Kills the instance of this line and its Thread
     * */
    public void kill(){
        this.isRunning = false;
        System.gc();
    }

    //creates the panel for the line gui
    private void createPanel(){
        //configure the pane
        this.pane = new JPanel();
        this.pane.setLayout(new FlowLayout());
        this.pane.setSize(new Dimension(width, height));
        this.pane.setMaximumSize(new Dimension(width, height));
        this.pane.setBackground(this.panelColour);
        this.pane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        this.pane.setToolTipText(this.errorText);
        this.pane.addMouseListener(this);

        //text box
        this.textBox = new MyTextField(this.pane.getWidth()-100, 25, this.lineColour);

        //remove button
        this.removeBtn = new JButton("Del");
        this.removeBtn.setActionCommand("remove");
        this.removeBtn.addActionListener(this);
        this.removeBtn.setDefaultCapable(true);

        //add the components
        this.pane.add(this.textBox);
        this.pane.add(this.removeBtn);
    }

    /**
     * Recalculates the coordinates
     * @apiNote Generates new coordinates with the current RPN.
     * */
    public void renderUpdate(){
        //this generates a new coordinate set
        if(!this.equation.equals("") || !this.equation.equals(null)){
            try {
                try{
                    createLineCoordinates();
                    this.errorText = "Valid Equation!";
                    System.out.println("updated!");
                } catch (NullPointerException n){}
            } catch (CustonException e) {
                this.errorText = e.getMessage();
                this.rpn.clear();
            }
        }
    }

    //this gets called every tick on the LINES thread
    /**
     * Responsible for the tick update of the thread for the line
     * @apiNote All threads are independent of all instances of a line clas
     * */
    private void update() {
        if(!this.textBox.getText().equals("")){
            if(!this.equation.equals(this.textBox.getText())){
                this.equation = this.textBox.getText();
                System.out.println(this.equation);

                //try getting the rpn
                try{
                    createLineRpn();
                    this.errorText = "Valid Equation!";
                    this.textBox.setForeground(Color.WHITE);

                    //try getting the coordinates
                    try{
                        createLineCoordinates();
                        this.errorText = "Valid Equation!";
                        this.textBox.setForeground(Color.WHITE);

                    } catch (CustonException e){
                        this.textBox.setForeground(Color.RED);
                        this.errorText = e.getMessage();
                        this.rpn.clear();

                    }
                } catch (CustonException e){
                    this.textBox.setForeground(Color.RED);
                    this.errorText = e.getMessage();
                }
            }

        } else{
            this.errorText = "Enter Equation!";
            this.textBox.setForeground(Color.WHITE);

        }
        this.pane.setToolTipText(this.errorText);
        this.pane.revalidate();
    }

    /**
     * @return int[] containing the x coordinates
     * */
    public int[] getX_coords(){
        return this.x_coords;
    }

    /**
     * @return int[] containing the y coordinates
     * */
    public int[] getY_coords(){
        return this.y_coords;
    }

    /**
     * @return Color of the line
     * */
    public Color getLineColour(){
        return this.lineColour;
    }

    /**
     * @return String text of Equation
     * */
    public String getEquation(){
        return this.equation;
    }

    /**
     * @return String text of Equation
     * */
    public ArrayList<String> getRpn(){
        return this.rpn;
    }

    /**
     * Generates the Colour for the line
     * */
    private Color generateColour(){
        Color color = new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256), 100);
        return color;
    }

    /**
     * This generates the RPN of the current equation to the Line Class
     * @throws CustonException 'Bracket Error' for incorrect equation input
     * */
    private void createLineRpn() throws CustonException {
        this.rpn = new MyMathCompiler(this.equation).getRPN();
        System.out.println(this.rpn);
    }

    /**
     * This is used for opening a line and setting its paramters read from the file
     *
     * @param x the X Coordinates for the line
     * @param y the Y Coordinates for the line
     * @param e the equation for the line
     * @param rpn the RPN for the line
     * @apiNote Can only be called if an instance has already been created for this class
     * */
    public void setUpLine(String e, ArrayList<String> rpn, int[] x, int[] y){
        this.equation = e;
        this.rpn = rpn;
        this.x_coords = x;
        this.y_coords = y;
        this.textBox.setText(e);
    }

    /**
     * Generates the coordinate data to be plotted
     * @throws CustonException 'Missing Expression' or 'Invalid Mathematical Input'
     * @apiNote These Coordinates are that of the canvas and not of the graphical context
     * */
    private void createLineCoordinates() throws CustonException {
        int[] offset = {Window.renderPanel.x_origin, Window.renderPanel.y_origin};
        int[][] coords;

        coords = new MyMathCompiler(this.equation).getCoordinates(Window.renderPanel.start_x, offset, Window.renderPanel.scale_factor, this.rpn);
        this.x_coords = coords[0];
        this.y_coords = coords[1];
    }

    //actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("remove")){
            synchronized (Graff.getAllLines()){
                int op = JOptionPane.showConfirmDialog(Window.frame,
                        "Delete Line?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION);

                if(op == 0){
                    synchronized (Graff.getAllLines()){
                        Graff.dequeueLine(this);
                        kill();
                        Window.removeLineGUI();
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        //saving the lines with a JOptionpane to get the user input
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem saveItem = new JMenuItem("Save...");
        saveItem.addActionListener(e1 -> {
            String name = JOptionPane.showInputDialog("Enter Line Name: ");
            try {
                MySerializer.serialiseObject(name, this);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        popupMenu.add(saveItem);
        popupMenu.addSeparator();

        //delete JMenu item for pop up
        JMenuItem deleteItem = new JMenuItem("Delete...");
        deleteItem.setActionCommand("remove");
        deleteItem.addActionListener(this);

        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
        popupMenu.show(e.getComponent(), x, y);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}