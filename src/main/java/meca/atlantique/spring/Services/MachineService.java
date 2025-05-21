package meca.atlantique.spring.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.fanuc.FanucMachine;
import meca.atlantique.fanuc.FanucMachineService;
import meca.atlantique.heidenhain.HeidenhainMachine;
import meca.atlantique.heidenhain.HeidenhainMachineService;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Data.MachineDto;
import meca.atlantique.spring.Repositories.MachineRepository;

@Service
@AllArgsConstructor
public class MachineService {

    
    @Autowired
    private final MachineRepository repository;
    private final FanucMachineService fanucMachineService;
    private final HeidenhainMachineService heidenhainMachineService;

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

    //@Transactional
    public void save(Machine machine) {
        if (machine != null) {
            repository.save(machine);
        }
    }

    public List<String> getAlarmeMessages(Machine machine) {
        if (machine instanceof FanucMachine) {
            return fanucMachineService.getAlarmeMessages((FanucMachine) machine);
        } else if (machine instanceof HeidenhainMachine) {
            return heidenhainMachineService.getAlarmeMessages((HeidenhainMachine) machine);
        }

        return null;
    }
}
