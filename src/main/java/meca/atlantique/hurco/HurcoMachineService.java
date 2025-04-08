package meca.atlantique.hurco;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.MachineState;
import meca.atlantique.spring.Data.MachineStatus;

@Service
@AllArgsConstructor
public class HurcoMachineService {
    public final short DEFAULT_PORT = 5000;
    
    @Autowired
    private final HurcoMachineRepository repository;

    @Transactional
    public List<HurcoMachine> getAll() {
        return repository.findAll();
    }

    @Transactional
    public HurcoMachine add(HurcoMachine machine) {
        return repository.save(machine);
    }

    @Transactional
    public boolean has(String ip) {
        return repository.existsById(ip);
    }

    public List<MachineStatus> updateHurcoMachineStatus() {
        List<HurcoMachine> machines = repository.findAll();
        List<MachineStatus> machinesStatus = new ArrayList<>();
        machines.forEach(machine -> {
            String prgStatus = MTConnectApi.getPrgStatus(machine.getIp(), machine.getPort());

            MachineState state;
            switch(prgStatus) {
                case "ACTIVE": {
                    state = MachineState.RUNNING;
                    break;
                }
                case "PROGRAM_COMPLETED": {
                    state = MachineState.HOLD;
                    break;
                }
                case "READY": {
                    state = MachineState.HOLD;
                    break;
                }
                case "PROGRAM_STOPPED": {
                    state = MachineState.STOPPED;
                    break;
                }
                default: {
                    System.out.println("Hurco status inconnu : " + prgStatus);
                    state = MachineState.STOPPED;
                }
            }

            String programName = MTConnectApi.getPrgName(machine.getIp(), machine.getPort());

            MachineStatus status = new MachineStatus();
            status.setMachine(machine);
            status.setState(state);
            status.setProgramName(programName);

            machinesStatus.add(status);
        });

        return machinesStatus;
    }
}
