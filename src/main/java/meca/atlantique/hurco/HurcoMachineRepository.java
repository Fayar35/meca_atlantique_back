package meca.atlantique.hurco;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface HurcoMachineRepository extends JpaRepository<HurcoMachine, String>{
    Optional<HurcoMachine> findByIp(String ip);

    @Modifying
    Optional<HurcoMachine> deleteByIp(String ip);
}
