package meca.atlantique.heidenhain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import meca.atlantique.spring.Data.Machine;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Table(name = "heidenhainMachine")
public class HeidenhainMachine extends Machine {
    public HeidenhainMachine(String ip, short port, String name) {
        this.setIp(ip);
        this.setPort(port);
        this.setName(name);
    }
}
