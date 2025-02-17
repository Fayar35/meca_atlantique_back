package meca.atlantique;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Services.FanucMachineService;
import meca.atlantique.spring.Services.MachineStatusService;

@Component
@AllArgsConstructor
public class MachineStatusScheduler {
    private final FanucMachineService fanucMachineService;
    private final MachineStatusService machineStatusService;

    @Scheduled(fixedRate = 300_000) // 300000ms = 5 minutes
    public void updateMachineStatus() {
        fanucMachineService.updateFanucMachineStatus().forEach((status) -> machineStatusService.saveMachineStatus(status));
    }
}
