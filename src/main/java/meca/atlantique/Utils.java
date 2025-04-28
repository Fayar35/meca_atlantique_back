package meca.atlantique;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    } 
}
