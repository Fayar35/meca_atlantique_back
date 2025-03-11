package meca.atlantique.spring.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Repositories.MachineRepository;

@Service
@Transactional
@AllArgsConstructor
public class MachineService {
    @Autowired
    private final MachineRepository repository;

    public List<Machine> getAll() {
        return repository.findAll();
    }

    public Machine getByIp(String ip) {
        return repository.findByIp(ip).orElse(null);
    }

    public Machine add(Machine machine) {
        return repository.save(machine);
    }

    public boolean has(String ip) {
        return repository.existsById(ip);
    }

    public void removeByIp(String ip) {
        repository.deleteByIp(ip);
    }
}
