package meca.atlantique.spring.Data;

import java.time.LocalDate;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class SummaryStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "machine_ip", nullable = false)
    private Machine machine;

    @ElementCollection
    @CollectionTable(name = "prg_name_usage", joinColumns = @JoinColumn(name = "summary_status_id"))
    @MapKeyColumn(name = "program_name")
    @Column(name = "usage_duration")
    private Map<String, Long> prgNameUsage;

    private LocalDate date;
    private Long totalUsageMs;
    private Long totalMeasuredTimeMs;
}
