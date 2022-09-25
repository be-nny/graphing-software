package calculator.software;

import calculator.line.Line;
import calculator.gui.Window;
import calculator.software.serialization.MySerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

public class Graff extends Window {

    public static Thread mainThread;
    public static boolean isRunning;
    private static long tick_speed = 5L;

    private static File[] savedLines;

    public static CopyOnWriteArrayList<Line> allLines = new CopyOnWriteArrayList<Line>();

    public static void main(String[] args) {
        //starting the programs thread
        mainThread = new Thread(() -> {
            try {
                isRunning = true;
                //instantiating it's self
                new Graff();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mainThread.start();
    }

    //running on main thread
    public Graff() throws InterruptedException, IOException {
        super();
        while(isRunning){
            //update the paint panel every tick
            mainPane.repaint();
            mainThread.sleep(tick_speed);
        }
    }

    /**
     * This can be called to update the file names in the saves folder.
     * @return String[] of all the file names
     * @apiNote Usually called at the beginning.
     * */
    public static String[] getFiles(){
        //creating new file list to store all the saved lines
        File folder = new File(MySerializer.filePath);
        File[] files = folder.listFiles();
        ArrayList<File> allFiles = new ArrayList<File>();
        ArrayList<String> saveNames = new ArrayList<String>();

        if(files != null){
            //for all the files, get the names, replace the extension and add it to the respective arrays
            for(File file: files){
                String name = file.getName().replaceAll("(.graff)*", "");
                saveNames.add(name);
                allFiles.add(file.getAbsoluteFile());
            }
            savedLines = allFiles.stream().toArray(File[]::new);
            String[] names = saveNames.stream().toArray(String[] :: new);
            //returning just the names of the line saves with out any extensions. just plain text
            return names;
        }
        return null;
    }

    /**
     * @param line Queues the line given
     * */
    public static synchronized void queueLine(Line line){
        allLines.add(line);
    }

    /**
     * @param line Removes the line given
     * */
    public static synchronized void dequeueLine(Line line){
        allLines.remove(line);
    }

    /**
     * @return ArrayList of all the lines in use
     * */
    public static synchronized CopyOnWriteArrayList<Line> getAllLines(){
        return Graff.allLines;
    }
}
