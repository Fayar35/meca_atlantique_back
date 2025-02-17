package meca.atlantique.spring.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import meca.atlantique.spring.Data.FanucMachine;

@Repository
public interface FanucMachineRepository extends JpaRepository<FanucMachine, String> {
    Optional<FanucMachine> findByIp(String ip);

    @Modifying
    Optional<FanucMachine> deleteByIp(String ip);
}
