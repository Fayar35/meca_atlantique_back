package meca.atlantique;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import meca.atlantique.heidenhain.HeidenhainApi;
import meca.atlantique.heidenhain.HeidenhainMachine;
import meca.atlantique.heidenhain.HeidenhainMachineService;
import meca.atlantique.spring.Data.MachineState;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Services.MachineStatusService;

@Component
@AllArgsConstructor
public class MachineStatusScheduler {
    private final MachineStatusService machineStatusService;
    private final HeidenhainMachineService heidenhainService;

    /*
     * enregistre l'état des machines actuel, 
     * garde en mémoire seulement l'état courant et lorsque l'état change.
     * l'état est considéré 'offline' lorsque le dernier état enregistré date d'il y a plus de 2 minutes 
     * (début d'une journée ou mise en pause du système)
     */
    @Async
    @Scheduled(cron = "0 * 4-23 * * *")
    public void updateMachineStatus() {
        machineStatusService.updateMachineStatus().forEach((status) -> {
            List<MachineStatus> list = machineStatusService.getHistoryForDate(status.getMachine().getIp(), LocalDate.now());
            if (list.size() >= 2) {
                MachineStatus lastElement = list.get(list.size()-1);
                MachineStatus beforeLastElement = list.get(list.size()-2);
                LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);
                
                // supprime le dernier état s'il est plus recent que 2 minutes,
                // et est le même que l'avant dernier
                // (si l'avant dernier est différent ça veut dire que le dernier notifie un changement d'état) 
                if (lastElement.getTimestamp().isAfter(threeMinutesAgo)) {
                    if (beforeLastElement.getState() == lastElement.getState()) {
                        list.remove(lastElement);
                        machineStatusService.deleteMachineStatus(lastElement);
                    }
                } else {
                    // remplace le dernier état sauvegardé en offline
                    lastElement.setState(MachineState.OFFLINE);
                    machineStatusService.saveMachineStatus(lastElement);
                }
            }
            
            list.add(status);
            machineStatusService.saveMachineStatus(status);
        });
    }

    // s'éxecute tout les jours à 8H 
    // pour supprimer les machines status vieux de plus de 14 jours
    @Scheduled(cron="0 0 8 * * ?")
    public void deleteOldHistory() {
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);
        machineStatusService.deleteBefore(fourteenDaysAgo);
    }

    @Async
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkAlarmsHeidenhain() {
        List<HeidenhainMachine> machines = heidenhainService.getAll();
        machines.forEach(m -> {
            m.addAlarms(HeidenhainApi.getAlarms(m.getIp()));
        });
    }
}
