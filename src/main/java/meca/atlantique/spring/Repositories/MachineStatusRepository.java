package meca.atlantique.spring.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import meca.atlantique.spring.Data.MachineStatus;

public interface MachineStatusRepository extends JpaRepository<MachineStatus, Long> {
    List<MachineStatus> findByMachineIpOrderByTimestampDesc(String machineIp);
}
