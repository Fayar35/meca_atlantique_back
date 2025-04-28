package meca.atlantique.heidenhain;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import meca.atlantique.Utils;
import meca.atlantique.spring.Data.MachineState;
import meca.atlantique.spring.Data.MachineStatus;

@Service
@AllArgsConstructor
public class HeidenhainMachineService {
    final short DEFAULT_PORT = 19000;

    @Autowired
    private final HeidenhainMachineRepository repository;

    @Transactional
    public List<HeidenhainMachine> getAll() {
        return repository.findAll();
    }

    @Transactional
    public HeidenhainMachine getByIp(String ip) {
        return repository.findByIp(ip).orElse(null);
    }

    @Transactional
    public HeidenhainMachine add(HeidenhainMachine machine) {
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

    public List<MachineStatus> updateHeidenhainMachineStatus() {
        List<HeidenhainMachine> machines = repository.findAll();
        List<MachineStatus> machinesStatus = new ArrayList<>();
        machines.forEach(machine -> {
            short prgStatus = HeidenhainApi.getPyStatus(machine.getIp());

            MachineState state;
            // https://pylsv2.readthedocs.io/en/master/protocol.html#machine-state
            switch (prgStatus) {
                case -1: {
                    // erreur de récupération de l'état
                    state = MachineState.OFFLINE;
                    break;
                }
                case 0: {
                    // Started
                    state = MachineState.RUNNING;
                    break;
                }
                case 1: {
                    // Stopped
                    state = MachineState.STOPPED;
                    break;
                }
                case 2: {
                    // Finished
                    state = MachineState.HOLD;
                    break;
                }
                case 3: {
                    // Cancelled
                    state = MachineState.STOPPED;
                    break;
                }
                case 4: {
                    // Interrupted
                    state = MachineState.STOPPED;
                    break;
                }
                case 5: {
                    // Error
                    state = MachineState.STOPPED;
                    break;
                }
                case 6: {
                    // Error Cleared
                    state = MachineState.HOLD;
                    break;
                }
                case 7: {
                    // Idle
                    state = MachineState.HOLD;
                    break;
                }
                case 8: {
                    // Undefined
                    state = MachineState.UNKNOWN;
                    break;
                }
                default: {
                    System.out.println(Utils.getTime() + " Heidenhain status inconnu : " + prgStatus);
                    state = MachineState.STOPPED;
                }
            }

            String programName = HeidenhainApi.getPyPrgName(machine.getIp());

            MachineStatus status = new MachineStatus();
            status.setMachine(machine);
            status.setState(state);
            status.setProgramName(programName.replace("\u0000", ""));

            machinesStatus.add(status);
        });
        return machinesStatus;
    }
}
