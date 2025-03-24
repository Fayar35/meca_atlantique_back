package meca.atlantique.heidenhain;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HeidenhainApi {
    private static final String FILE_PATH = "./src/main/java/meca/atlantique/heidenhain/heidenhain.py";

    public static short getPyStatus(String ip) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", FILE_PATH, "get_status", ip);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            if (line == null) {
                return -1;
            }
            
            // si le script python retourne une erreur, il y a une deuxième ligne
            if (reader.readLine() != null) {
                return -1;
            }
            
            short ret = Short.parseShort(line);
            p.waitFor();

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getPyPrgName(String ip) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", FILE_PATH, "get_prg_name", ip);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ret = reader.readLine();
            if (ret != null) {
                ret = ret.trim();
            } else {
                ret = "";
            }

            // si le script python retourne une erreur, il y a une deuxième ligne
            if (reader.readLine() != null) {
                return "";
            }
            p.waitFor();

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
