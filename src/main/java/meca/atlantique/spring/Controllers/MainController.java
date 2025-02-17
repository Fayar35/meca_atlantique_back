package meca.atlantique.spring.Controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Data.MachineStatus;
import meca.atlantique.spring.Services.FanucMachineService;
import meca.atlantique.spring.Services.MachineStatusService;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // autorise react
public class MainController {
    private final FanucMachineService fanucMachineService;
    private final MachineStatusService machineStatusService;

    @GetMapping("/getAllMachine")
    ResponseEntity<?> getAllMachine() {
        try {
            List<Machine> ret = fanucMachineService.getAll().stream().map(m -> (Machine) m).collect(Collectors.toList());
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation des machines " + e.getMessage());
        }
    }

    @GetMapping("/getMachineHistory")
    ResponseEntity<?> getMachineHistory(
            @RequestParam String ip, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(machineStatusService.getHistoryForDate(ip, date));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation de l'historique " + e.getMessage());
        }
    }

    @GetMapping("/testerror")
    ResponseEntity<?> error() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lol");
    }

    @GetMapping("/test")
    MachineStatus test() {
        MachineStatus history = new MachineStatus();
        return history;
    }
}
