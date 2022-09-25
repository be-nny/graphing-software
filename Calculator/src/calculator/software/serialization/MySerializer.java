package calculator.software.serialization;

import calculator.line.Line;

import java.io.*;

public class MySerializer {
    public static String filePath = "Calculator/src/saves/";

    public static void serialiseObject(String name, Line line) throws IOException {
        //setting file name and pat
        String fileName = name.replace(" ", "_");
        String path = filePath + fileName + ".graff";

        //creating the output streams so the object can be serialized
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        //writing the object
        outputStream.writeObject(line);
        outputStream.close();
        fileOutputStream.close();
    }

    public static Object deserialiseObejct(String name) throws IOException, ClassNotFoundException {
        //getting the path with the name param
        String path = filePath + name + ".graff";

        //getting the input streams so the object can be read properly
        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream outputStream = new ObjectInputStream(fileInputStream);
        Line line = (Line) outputStream.readObject();
        fileInputStream.close();
        outputStream.close();

        return line;
    }
}