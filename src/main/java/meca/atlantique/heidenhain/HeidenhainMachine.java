package meca.atlantique.heidenhain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        this.setAlarms(new HeidenhainAlarms());
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alarms_id")
    @JsonIgnore
    private HeidenhainAlarms alarms;
    
    public List<String> getAlarms() {
        if (this.alarms == null) {
            this.alarms = new HeidenhainAlarms();
        }
        return this.alarms.getAlarms();
    }

    public void addAlarms(List<String> messages) {
        if (this.alarms == null) {
            this.alarms = new HeidenhainAlarms();
        }
        this.alarms.addAlarms(messages);
    }
}
