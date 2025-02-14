package meca.atlantique.spring.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Machine {
    @Id
    private String ip;

    private short port;
    private String name;
}
