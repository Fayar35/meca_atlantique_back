package meca.atlantique.spring.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import meca.atlantique.spring.Data.Machine;

public interface MachineRepository extends JpaRepository<Machine, String> {
    Optional<Machine> findByIp(String ip);

    @Modifying
    Optional<Machine> deleteByIp(String ip);
}
