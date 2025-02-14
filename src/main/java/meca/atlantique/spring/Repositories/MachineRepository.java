package meca.atlantique.spring.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import meca.atlantique.spring.Data.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, String> {
}
