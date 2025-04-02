package meca.atlantique.hurco;

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
@Table(name = "hurcoMachine")
public class HurcoMachine extends Machine {
    
    public HurcoMachine(String ip, short port, String name) {
        this.setIp(ip);
        this.setPort(port);
        this.setName(name);
    }
}
