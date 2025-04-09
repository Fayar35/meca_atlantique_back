package meca.atlantique.spring.Data;

import lombok.Data;

@Data
public class MachineDto {
    private String ip;
    private short port;
    private String name;
}