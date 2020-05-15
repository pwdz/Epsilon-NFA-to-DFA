package Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FileHandler {
    public static List<String> readLines(String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"Unicode"));
            List<String>lines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void writeLines(String path,List<String>lines) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(path));
            for (String line:lines) {
                pw.println(line);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
