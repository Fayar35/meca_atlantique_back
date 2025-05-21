package meca.atlantique.heidenhain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeidenhainApi {
    private static final String FILE_PATH = "./script/heidenhain.py";

    public static short getPyStatus(String ip) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", FILE_PATH, "get_status", ip);
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
            ProcessBuilder pb = new ProcessBuilder("python", FILE_PATH, "get_prg_name", ip);
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

    public static List<String> getAlarms(String ip) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", FILE_PATH, "get_alarms", ip);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            List<String> ret = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                String regex = "ALARM : .*";
                Matcher matcher = Pattern.compile(regex).matcher(line);

                if (matcher.matches()) {
                    ret.add(line.substring(8));
                } else {
                    //si la ligne ne commence pas par "ALARM : " c'est qu'il y a eu une erreur
                    return new ArrayList<>();
                }

                line = reader.readLine();
            }

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
