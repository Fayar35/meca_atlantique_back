package meca.atlantique.spring.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import meca.atlantique.spring.Data.Demand;

public interface DemandRepository extends JpaRepository<Demand, Long> {
    
}
