package meca.atlantique.spring.Data;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Table(name = "offlineMachine")
public class OfflineMachine extends Machine {
    public OfflineMachine(String name) {
        this.setIp(UUID.randomUUID().toString());
        this.setPort((short) 0);
        this.setName(name);
    }
}
