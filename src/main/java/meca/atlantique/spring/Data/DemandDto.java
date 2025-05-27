package meca.atlantique.spring.Data;

import java.util.Date;

import lombok.Data;

@Data
public class DemandDto {
    private Long id;
    private Date dateCreated;
    private String machineName;
    private String interventionType;
    private Date datePlanned;
    private String description;
    private String status;
    private String priority;
}
