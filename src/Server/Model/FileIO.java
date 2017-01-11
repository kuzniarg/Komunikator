package Server.Model;

import java.io.*;
import java.util.ArrayList;

public class FileIO {
    public static String loadFile(File file) {
        String text = "";
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader bufferReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                text += line + "\n";
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static void saveFile(String fileName, ArrayList<String> data) {
        try {
            PrintWriter out = new PrintWriter(fileName);
            for (int i = 0; i < data.size(); i++) {
                out.println(data.get(i));
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
