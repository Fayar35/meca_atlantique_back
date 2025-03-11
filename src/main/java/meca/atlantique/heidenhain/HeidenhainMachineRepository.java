package meca.atlantique.heidenhain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface HeidenhainMachineRepository extends JpaRepository<HeidenhainMachine, String> {
    Optional<HeidenhainMachine> findByIp(String ip);

    @Modifying
    Optional<HeidenhainMachine> deleteByIp(String ip);
}
