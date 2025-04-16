package meca.atlantique.spring.Services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Data.SummaryStatus;
import meca.atlantique.spring.Repositories.MachineRepository;
import meca.atlantique.spring.Repositories.SummaryStatusRepository;

@Service
@AllArgsConstructor
public class SummaryStatusService {
    private final SummaryStatusRepository summaryStatusRepository;
    private final MachineRepository machineRepository;

    private final MachineStatusService machineStatusService;

    @Transactional
    public void save(SummaryStatus summary) {
        summaryStatusRepository.save(summary);
    }

    @Transactional
    public void deleteSummaryStatus(SummaryStatus status) {
        summaryStatusRepository.deleteById(status.getId());
    }

    @Transactional
    public Optional<SummaryStatus> getSummaryStatus(String ip, LocalDate date) {
        return summaryStatusRepository.findByMachineIpAndDate(ip, date);
    }

    public SummaryStatus createSummaryStatus(String machineIp, LocalDate date) {
        List<MachineStatus> list =  machineStatusService.getHistoryForDate(machineIp, date);
        Long runningDuration = 0L;
        Long stoppedDuration = 0L;
        Long holdDuration = 0L;
        Long unknownDuration = 0L;
        Long offlineDuration = 0L;

        for (int i = 0; i < list.size() - 1; i++) {
            MachineStatus status = list.get(i);
            Long duration = Duration.between(status.getTimestamp(), list.get(i+1).getTimestamp()).toMillis();

            switch (status.getState()) {
                case RUNNING: {
                    runningDuration  += duration;
                    break;
                }
                case STOPPED: {
                    stoppedDuration += duration;
                    break;
                }
                case HOLD: {
                    holdDuration += duration;
                    break;
                }
                case UNKNOWN: {
                    unknownDuration += duration;
                    break;
                }
                case OFFLINE: {
                    offlineDuration += duration;
                    break;
                }
            }
        }

        SummaryStatus summary = new SummaryStatus();
        summary.setMachine(machineRepository.findById(machineIp).orElse(null));
        summary.setDate(date);
        summary.setRunningMs(runningDuration);
        summary.setStoppedMs(stoppedDuration);
        summary.setHoldMs(holdDuration);
        summary.setUnknownMs(unknownDuration);
        summary.setOfflineMs(offlineDuration);

        return summary;
    }
}
