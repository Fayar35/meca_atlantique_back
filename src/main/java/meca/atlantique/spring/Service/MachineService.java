package meca.atlantique.spring.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Repositories.MachineRepository;

@Service
@AllArgsConstructor
public class MachineService {
    @Autowired
    private final MachineRepository repository;

    public List<Machine> getAll() {
        return repository.findAll();
    }

    public Machine getByIp(String ip) {
        if (repository.existsById(ip)) return null;
        return repository.findById(ip).get();
    }

    public Machine add(Machine machine) {
        return repository.save(machine);
    }

    public boolean has(String ip) {
        return repository.existsById(ip);
    }
}
