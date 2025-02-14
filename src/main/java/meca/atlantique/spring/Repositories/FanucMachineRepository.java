package meca.atlantique.spring.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import meca.atlantique.spring.Data.FanucMachine;

@Repository
public interface FanucMachineRepository extends JpaRepository<FanucMachine, String> {
    FanucMachine findByIp(String ip);
}
