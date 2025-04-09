package meca.atlantique.spring.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.fanuc.FanucMachineService;
import meca.atlantique.heidenhain.HeidenhainMachineService;
import meca.atlantique.hurco.HurcoMachineService;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Repositories.MachineStatusRepository;

@Service
@AllArgsConstructor
public class MachineStatusService {
    private final MachineStatusRepository machineStatusRepository;
    private final FanucMachineService fanucMachineService;
    private final HeidenhainMachineService heidenhainMachineService;
    private final HurcoMachineService hurcoMachineService;

    @Transactional
    public void saveMachineStatus(MachineStatus status) {
        machineStatusRepository.save(status);
    }

    @Transactional
    public void deleteMachineStatus(MachineStatus status) {
        machineStatusRepository.deleteById(status.getId());
        machineStatusRepository.flush();
    }

    @Transactional
    public void deleteBefore(LocalDateTime timestamp) {
        machineStatusRepository.deleteByTimestampBefore(timestamp);
    }

    @Transactional
    public List<MachineStatus> getHistoryForDate(String machineIp, LocalDate date) {
        return machineStatusRepository.findByMachineIpAndDate(machineIp, date);
    }

    @Transactional
    public List<MachineStatus> getMachineHistory(String machineIp) {
        return machineStatusRepository.findByMachineIpOrderByTimestampDesc(machineIp);
    }

    public List<MachineStatus> updateMachineStatus() {
        List<MachineStatus> ret = fanucMachineService.updateFanucMachineStatus(); 
        ret.addAll(heidenhainMachineService.updateHeidenhainMachineStatus());
        ret.addAll(hurcoMachineService.updateHurcoMachineStatus());

        return ret;
    }
}
