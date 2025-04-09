package meca.atlantique.spring.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Data.MachineDto;
import meca.atlantique.spring.Repositories.MachineRepository;

@Service
@AllArgsConstructor
public class MachineService {
    @Autowired
    private final MachineRepository repository;

    @Transactional
    public List<Machine> getAll() {
        return repository.findAll();
    }

    @Transactional
    public Machine getByIp(String ip) {
        return repository.findByIp(ip).orElse(null);
    }

    @Transactional
    public Machine add(Machine machine) {
        return repository.save(machine);
    }

    @Transactional
    public boolean has(String ip) {
        return repository.existsById(ip);
    }

    @Transactional
    public void removeByIp(String ip) {
        repository.deleteByIp(ip);
    }

    public Machine updateMachine(MachineDto machineDto) {
        return repository.findById(machineDto.getIp())
            .map(machine -> {
                machine.setName(machineDto.getName());
                machine.setPort(machineDto.getPort());
                return repository.save(machine);
            })
            .orElseThrow(() -> new RuntimeException("Machine non trouvée avec IP : " + machineDto.getIp()));
    }
}
