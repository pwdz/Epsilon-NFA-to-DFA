package Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FileHandler {
    /** Read a file line by line(Encoding: UNICODE).
     * @param path path of the file
     * @return lines of the file
     */
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
    /** writes to a file line by line.
     * @param path path of the file
     * @param lines lines to be written to the file.
     */
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
