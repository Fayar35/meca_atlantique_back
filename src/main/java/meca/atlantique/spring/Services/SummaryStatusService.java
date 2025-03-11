package meca.atlantique.spring.Services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.MachineState;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Data.SummaryStatus;
import meca.atlantique.spring.Repositories.MachineRepository;
import meca.atlantique.spring.Repositories.SummaryStatusRepository;

@Service
@AllArgsConstructor
@Transactional
public class SummaryStatusService {
    private final SummaryStatusRepository summaryStatusRepository;
    private final MachineRepository machineRepository;

    private final MachineStatusService machineStatusService;

    public void save(SummaryStatus summary) {
        summaryStatusRepository.save(summary);
    }

    public void deleteSummaryStatus(SummaryStatus status) {
        summaryStatusRepository.deleteById(status.getId());
    }

    public Optional<SummaryStatus> getSummaryStatus(String ip, LocalDate date) {
        return summaryStatusRepository.findByMachineIpAndDate(ip, date);
    }

    public SummaryStatus createSummaryStatus(String machineIp, LocalDate date) {
        Map<String, Long> prgNameUsage = new HashMap<>();
        List<MachineStatus> list =  machineStatusService.getHistoryForDate(machineIp, date);
        Long totalUsage = 0L;
        Long totalMeasuredTime = 0L;

        for (int i = 0; i < list.size() - 1; i++) {
            MachineStatus status = list.get(i);
            if (status.getState() == MachineState.OFFLINE) {
                continue;
            }

            Long duration = Duration.between(status.getTimestamp(), list.get(i+1).getTimestamp()).toMillis();
            totalMeasuredTime += duration;

            if (status.getState() == MachineState.RUNNING) {
                totalUsage  += duration;

                if (!prgNameUsage.containsKey(status.getProgramName())) {
                    prgNameUsage.put(status.getProgramName(), 0L);
                }

                prgNameUsage.put(status.getProgramName(), prgNameUsage.get(status.getProgramName()) + duration);
            }
        }

        SummaryStatus summary = new SummaryStatus();
        summary.setMachine(machineRepository.findById(machineIp).orElse(null));
        summary.setDate(date);
        summary.setPrgNameUsage(prgNameUsage);
        summary.setTotalMeasuredTimeMs(totalMeasuredTime);
        summary.setTotalUsageMs(totalUsage);

        return summary;
    }
}
