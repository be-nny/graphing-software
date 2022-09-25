package calculator.gui;

import calculator.line.Line;
import calculator.software.Graff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.concurrent.locks.ReentrantLock;

public class RenderPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    //dimension data
    public static int width = (int) (Window.frame.getWidth() * Window.main_pane_ratio);
    public static int height = Window.frame.getHeight();

    //coordinate data
    public static int x_origin;
    public static int y_origin;
    public static float start_x;
    public static float end_x;
    public static double scale_factor = 0.01f;
    public static double scale_amount = 0.0005f;

    //grid data
    public static float grid_scale = 0.01f;
    public static float min_grid_scale = 0.005f;
    public static float max_grid_spacing = 0.02f;

    //mouse data
    private static double mouse_coord_x = 0;
    private static double mouse_coord_y = 0;
    private static int start_click_x = 0;
    private static int start_click_y = 0;
    private static int mouse_x;
    private static int mouse_y;
    private static DecimalFormat df = new DecimalFormat("#.####");

    //colours
    private final static Color backGroundColour = new Color(227, 227, 227);
    private final static Color gridColour = new Color(190, 190, 190);

    //threading
    private static ReentrantLock lock = new ReentrantLock();

    public RenderPanel(){
        setPreferredSize(new Dimension(width , height));
        setBackground(backGroundColour);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        x_origin = width /2;
        y_origin = height /2;
        start_x = -(width/2);
        end_x = (width/2);

    }

    //overrides the render graph paint method
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        renderLines(g);
        renderCoordinates(g);
        revalidate();
    }

    private void renderCoordinates(Graphics g){
        //renders the users mouse coordinates
        Graphics2D g2d = (Graphics2D) g;
        String text = "(" + df.format(mouse_coord_x) + ", " + df.format(mouse_coord_y) + ")";
        g2d.drawString(text, mouse_x, mouse_y);
    }

    private void renderGrid(Graphics g) {
        synchronized (lock){
            //locks the thread lock so the x and y axis can be drawnss
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(1));
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x_origin - 3, y_origin - 3, 6, 6);

            g2d.setColor(gridColour);
            for(int i = (int) -(Math.abs(x_origin) / grid_scale) / 2; i < (Math.abs(width + x_origin))/ grid_scale; i ++){
                int x = (int)((x_origin - i) / grid_scale) + x_origin;
                g2d.drawLine(x,0, x, RenderPanel.height);
            }

            for(int i = (int) -(Math.abs(y_origin) / grid_scale) / 2; i < 2 * (Math.abs(height + y_origin)) / grid_scale; i ++){
                int y = (int)((y_origin - i) / grid_scale) + y_origin;
                g2d.drawLine(0,y, RenderPanel.width, y);
            }

            //axis lines
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x_origin,0, x_origin, RenderPanel.height);
            g2d.drawLine(0, y_origin, RenderPanel.width, y_origin);
            revalidate();

        }
    }

    private void renderLines(Graphics g) {
        /*
        * the thread lock is locked so there are no concurrent errors
        * each line is then updated with its new coordinates
        * this line is then drawn
        * once done, the lock is unlocked allowing all the visual's to be in sync with each other
        * */
        synchronized (lock){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (Line line : Graff.getAllLines()) {
                try {
                    //simplify coordinates to remove noise
                    int[] x = line.getX_coords();
                    int[] y = line.getY_coords();
                    int length = x.length;

                    //drawing the line
                    g2d.setColor(line.getLineColour());
                    g2d.drawPolyline(x, y, length);
                    revalidate();

                } catch (NullPointerException n) { }
            }
            renderGrid(g);
        }
    }

    private void updateLines() {
        /*
        * New thread created so the rest of the program can run freely
        * all of the coordinates are then generated fro each individual line
        * each line has ist own thread so the coordinates can be updated!
        * pog
        * */

        ArrayList<Thread> threads = new ArrayList<Thread>();
        Thread thread = new Thread(() -> {
            synchronized (lock){
                lock.lock();
                try{
                    for (Line line: Graff.getAllLines()){
                        threads.add(new Thread(() -> line.renderUpdate()));
                    }
                    //iterating through all the threads, stating them and then waiting for it to die to stop concurrency erros
                    for(Thread t: threads){
                        t.start();
                        t.join(100);
//                        t.interrupt();
//                        if(t.isInterrupted()){
//                            continue;
//                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        });
        thread.start();
    }

    private void updateMouseCoords(int x , int y){
        mouse_x = x;
        mouse_y = y;

        mouse_coord_x = (mouse_x - x_origin) * scale_factor;
        mouse_coord_y = (mouse_y - y_origin) * -scale_factor;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        start_click_x = e.getX();
        start_click_y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        updateLines();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = (start_click_x - e.getX()) * -1;
        int y = (start_click_y - e.getY()) * -1;

        x_origin += x;
        y_origin += y;

        start_x -= x;
        end_x = start_x + width;

        start_click_x = e.getX();
        start_click_y = e.getY();

        System.out.println(start_x);
        System.out.println(end_x);

        //updating the lines
        updateLines();
        updateMouseCoords(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMouseCoords(e.getX(), e.getY());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(!e.isShiftDown()){
            scale_factor = scale_factor + (e.getWheelRotation() * scale_amount);
            updateLines();
            if(scale_factor < 0.001){
                scale_factor = 0.001;
                grid_scale = max_grid_spacing;
            }
        }

        grid_scale += (e.getWheelRotation() * 0.001f);
        System.out.println(grid_scale);
        if(grid_scale > max_grid_spacing){
            grid_scale = min_grid_scale;
        } else if(grid_scale < min_grid_scale){
            grid_scale = max_grid_spacing;
        }

        updateMouseCoords(e.getX(), e.getY());
    }
}