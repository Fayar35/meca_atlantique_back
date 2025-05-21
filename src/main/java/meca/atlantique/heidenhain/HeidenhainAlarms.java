package meca.atlantique.heidenhain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class HeidenhainAlarms {
    
    @Entity
    @NoArgsConstructor
    public static class Alarm {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long id;
        public String message;
        public LocalDateTime time;

        public Alarm(String message, LocalDateTime time) {
            this.message = message;
            this.time = time;
        }
    }

    public HeidenhainAlarms() {
        this.alarms = new ArrayList<>();
        this.lastMessages = new ArrayList<>();
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Alarm> alarms;

    @ElementCollection
    private List<String> lastMessages;
    
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public List<String> getAlarms() {
        if (this.alarms == null) {
            this.alarms = new ArrayList<>();
        }
        return this.alarms.stream().map(a -> "" + a.time.toString() + " : " + a.message).collect(Collectors.toList());
    }

    public void addAlarms(List<String> messages) {
        if (this.lastMessages == null) {
            this.lastMessages = new ArrayList<>();
        }

        messages.forEach(m -> {
            if (!lastMessages.contains(m)) {
                this.alarms.add(new Alarm(m, LocalDateTime.now()));
            }
        });

        //fait une copie
        this.lastMessages = messages.stream().collect(Collectors.toList());
    }
}
