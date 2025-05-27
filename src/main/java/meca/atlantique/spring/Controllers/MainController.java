package meca.atlantique.spring.Controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Demand;
import meca.atlantique.spring.Data.DemandDto;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Data.MachineDto;
import meca.atlantique.spring.Data.OfflineMachine;
import meca.atlantique.spring.Data.SummaryStatus;
import meca.atlantique.spring.Mapper.DemandMapper;
import meca.atlantique.spring.Services.DemandService;
import meca.atlantique.spring.Services.MachineService;
import meca.atlantique.spring.Services.MachineStatusService;
import meca.atlantique.spring.Services.SummaryStatusService;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://192.168.0.23:5173") // autorise react
public class MainController {
    private final MachineStatusService machineStatusService;
    private final SummaryStatusService summaryStatusService;
    private final MachineService machineService;
    private final DemandService demandService;

    @GetMapping("/getAllMachine")
    ResponseEntity<?> getAllMachine() {
        try {
            List<Machine> ret = machineService.getAll();
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation des machines : " + e.getMessage());
        }
    }

    @GetMapping("/getAllOnlineMachine")
    ResponseEntity<?> getAllOnlineMachine() {
        try {
            List<Machine> ret = machineService.getAllOnline();
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation des machines connectées : " + e.getMessage());
        }
    }

    @PostMapping("/createOfflineMachine")
    ResponseEntity<?> createOfflineMachine(@RequestParam String name) {
        try {            
            OfflineMachine machine = new OfflineMachine(name);
            machineService.add(machine);
            
            return ResponseEntity.ok(machine);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de la machine hors ligne " + name + " : " + e.getMessage());
        }
    }
    

    @GetMapping("/getMachine")
    ResponseEntity<?> getMachine(@RequestParam String ip) {
        try {
            if (machineService.has(ip)) {
                return ResponseEntity.ok(machineService.getByIp(ip));
            }
            return ResponseEntity.badRequest().body("Aucune machine avec l'ip : " + ip + " n'a été trouvée");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la récuperation des machines : " + e.getMessage());
        }
    }

    @GetMapping("/getMachineHistory")
    ResponseEntity<?> getMachineHistory(
            @RequestParam String ip, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(machineStatusService.getHistoryForDate(ip, date));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation de l'historique : " + e.getMessage());
        }
    }

    private SummaryStatus getSummaryStatusByDate(String ip, LocalDate date) {
        Optional<SummaryStatus> sumOptional = summaryStatusService.getSummaryStatus(ip, date);
        SummaryStatus sum;
        if (!sumOptional.isPresent()) {
            sum = summaryStatusService.createSummaryStatus(ip, date);
            summaryStatusService.save(sum);
        } else if(date.isEqual(LocalDate.now()) && sumOptional.isPresent()) {
            // si aujourd'hui, refaire le résumé
            sum = summaryStatusService.createSummaryStatus(ip, date);
            sum.setId(sumOptional.get().getId());
            summaryStatusService.save(sum);
        } else {
            sum = sumOptional.get();
        }

        return sum;
    }

    @GetMapping("/getMachineSummary")
    ResponseEntity<?> getMachineSummary(
            @RequestParam String ip, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            SummaryStatus sum = getSummaryStatusByDate(ip, date);

            return ResponseEntity.ok(sum);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation du résumé : " + e.getMessage());
        }
    }

    @GetMapping("/getMachineSummaryWeek")
    ResponseEntity<?> getMachineSummaryWeek(
        @RequestParam String ip, 
        @RequestParam int weekNumber,
        @RequestParam int year) {
            try {
                WeekFields weekFields = WeekFields.of(Locale.FRANCE);
                
                LocalDate firstWeekDate = LocalDate.of(year, 1, 1)
                    .with(weekFields.weekOfYear(), weekNumber)
                    .with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));

                List<SummaryStatus> listSum = new ArrayList<>();
                
                for (int i = 0; i < 7; i++) {
                    LocalDate date = firstWeekDate.plusDays(i);
                    if (date.isAfter(LocalDate.now())) {
                        break;
                    } else {
                        listSum.add(getSummaryStatusByDate(ip, date));
                    }
                }
                
                return ResponseEntity.ok(listSum);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation du résumé pour la semaine : " + e.getMessage());
            }
    }

    @GetMapping("/getMachineSummaryMonth")
    ResponseEntity<?> getMachineSummaryMonth(
        @RequestParam String ip, 
        @RequestParam int monthNumber,
        @RequestParam int year) {
            try {
                YearMonth yearMonth = YearMonth.of(year, monthNumber);
                int daysInMonth = yearMonth.lengthOfMonth();

                List<SummaryStatus> listSum = new ArrayList<>();
                
                for (int i = 1; i <= daysInMonth; i++) {
                    LocalDate date = LocalDate.of(year, monthNumber, i);
                    if (date.isAfter(LocalDate.now())) {
                        break;
                    } else {
                        listSum.add(getSummaryStatusByDate(ip, date));
                    }
                }
                
                return ResponseEntity.ok(listSum);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation du résumé pour le mois : " + e.getMessage());
            }
    }

    @DeleteMapping("/deleteMachine")
    ResponseEntity<?> deleteMachine(@RequestParam String ip) {
        try {
            if (machineService.has(ip)) {
                machineService.removeByIp(ip);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("Aucune machine ne possède cette ip " + ip);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression de la machine : " + e.getMessage());
        }
    }

    @PostMapping("/updateMachine")
    ResponseEntity<?> updateMachine(@RequestBody MachineDto machine) {
        try {
            machineService.updateMachine(machine);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification de la machine : " + e.getMessage());
        }
    }

    @GetMapping("/getAlarmes")
    public ResponseEntity<?> getAlarmes(@RequestParam String ip) {
        try {
            if (!machineService.has(ip)) {
                return ResponseEntity.badRequest().body("La machine avec l'ip : " + ip + " n'existe pas dans la base de donnée");
            }
            
            Machine machine = machineService.getByIp(ip);
            
            return ResponseEntity.ok(machineService.getAlarmeMessages(machine));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récuperation des messages d'alarme de la machine heidenhain " + ip + " : " + e.getMessage());
        }
    }

    @PostMapping("/createDemand")
    public ResponseEntity<?> createDemand(@RequestBody DemandDto demandDto) {
        try {
            if (demandDto == null) {
                return ResponseEntity.badRequest().body("erreur lors de la création d'une demande, demandDto est null");
            }
            
            DemandMapper mapper = new DemandMapper();
            Demand demand = mapper.toEntity(demandDto);
            demandService.save(demand);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création d'une demande : " + e.getMessage());
        }
    }
    
    @GetMapping("/getAllDemands")
    public ResponseEntity<?> getAllDemands() {
        return ResponseEntity.ok(demandService.getAll());
    }

    @PostMapping("/updateDemand")
    public ResponseEntity<?> updateDemand(@RequestBody DemandDto demandDto) {
        try {
            if (demandDto == null) {
                return ResponseEntity.badRequest().body("erreur lors de l'update d'une demande, demandDto est null");
            }

            DemandMapper mapper = new DemandMapper();
            Demand demand = mapper.toEntity(demandDto);
            demandService.save(demand);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour d'une demande : " + e.getClass() + " : " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteDemand")
    public ResponseEntity<?> deleteDemand(@RequestParam Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("erreur lors de la suppression d'une demande, l'ID est null");
            }
            
            if (!demandService.has(id)) {
                return ResponseEntity.badRequest().body("erreur lors de la suppression d'une demande, aucune demande avec l'ID : " + id);
            }
            
            demandService.removeById(id);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la supression d'une demande : " + e.getMessage());
        }
    }
    
}
