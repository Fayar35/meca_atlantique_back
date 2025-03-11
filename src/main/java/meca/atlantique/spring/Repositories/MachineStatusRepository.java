package meca.atlantique.spring.Repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import meca.atlantique.spring.Data.MachineStatus;

public interface MachineStatusRepository extends JpaRepository<MachineStatus, Long> {
    List<MachineStatus> findByMachineIpOrderByTimestampDesc(String machineIp);

    @Transactional
    void deleteByTimestampBefore(LocalDateTime timestamp);
    
    @Query("SELECT ms FROM MachineStatus ms WHERE ms.machine.ip = :machineIp AND DATE(ms.timestamp) = :date ORDER BY ms.timestamp")
    List<MachineStatus> findByMachineIpAndDate(@Param("machineIp") String machineIp, @Param("date") LocalDate date);
}
