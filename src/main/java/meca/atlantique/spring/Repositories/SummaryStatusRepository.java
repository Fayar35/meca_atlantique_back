package meca.atlantique.spring.Repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import meca.atlantique.spring.Data.SummaryStatus;

public interface SummaryStatusRepository extends JpaRepository<SummaryStatus, Long> {
    Optional<SummaryStatus> findByMachineIpAndDate(String machineIp, LocalDate date);
}
