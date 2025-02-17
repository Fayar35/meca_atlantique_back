package meca.atlantique.spring.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import meca.atlantique.spring.Data.Machine;

public interface MachineRepository extends JpaRepository<Machine, String> {
}
