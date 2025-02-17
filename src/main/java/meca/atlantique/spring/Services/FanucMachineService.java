package meca.atlantique.spring.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.FanucMachine;
import meca.atlantique.spring.Repositories.FanucMachineRepository;

@Service
@Transactional
@AllArgsConstructor
public class FanucMachineService {
    @Autowired
    private final FanucMachineRepository repository;

    public List<FanucMachine> getAll() {
        return repository.findAll();
    }

    public FanucMachine getByIp(String ip) {
        return repository.findByIp(ip).orElse(null);
    }

    public FanucMachine add(FanucMachine machine) {
        return repository.save(machine);
    }

    public boolean has(String ip) {
        return repository.existsById(ip);
    }

    public void removeByIp(String ip) {
        repository.deleteByIp(ip);
    }
}
