package meca.atlantique.spring.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Repositories.MachineStatusRepository;

@Service
@AllArgsConstructor
public class MachineStatusService {
    private final MachineStatusRepository machineStatusRepository;

    public void saveMachineStatus(MachineStatus status) {
        machineStatusRepository.save(status);
    }

    public List<MachineStatus> getMachineHistory(String machineIp) {
        return machineStatusRepository.findByMachineIpOrderByTimestampDesc(machineIp);
    }
}
