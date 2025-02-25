package meca.atlantique.spring.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Repositories.MachineStatusRepository;

@Service
@AllArgsConstructor
@Transactional
public class MachineStatusService {
    private final MachineStatusRepository machineStatusRepository;

    public void saveMachineStatus(MachineStatus status) {
        machineStatusRepository.save(status);
    }

    public List<MachineStatus> getHistoryForDate(String machineIp, LocalDate date) {
        return machineStatusRepository.findByMachineIpAndDate(machineIp, date);
    }

    public List<MachineStatus> getMachineHistory(String machineIp) {
        return machineStatusRepository.findByMachineIpOrderByTimestampDesc(machineIp);
    }
}
