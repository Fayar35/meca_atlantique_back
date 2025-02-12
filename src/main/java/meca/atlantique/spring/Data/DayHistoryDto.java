package meca.atlantique.spring.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

@Data
public class DayHistoryDto {
    String ip;
    Date day = Date.valueOf(LocalDate.now());
    Map<Date, Integer> history;
}
