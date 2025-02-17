package meca.atlantique.spring.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import meca.atlantique.spring.Data.FanucMachine;
import meca.atlantique.spring.Services.FanucMachineService;

@RestController
@RequestMapping("/fanuc")
@CrossOrigin(origins = "http://localhost:5173") // autorise react
@AllArgsConstructor
public class FanucController {
    
    @Autowired
    private final FanucMachineService fanucMachineService;

    @GetMapping("/getMachine")
    ResponseEntity<?> getFanucMachine(@RequestParam String ip, @RequestParam("port") Optional<Short> portOptional) {
        try {
            if (fanucMachineService.has(ip)) {
                return ResponseEntity.ok(fanucMachineService.getByIp(ip));
            }
            
            short port = portOptional.orElse(fanucMachineService.DEFAULT_PORT);
            FanucMachine machine = fanucMachineService.collectFanucMachine(ip, port);
            fanucMachineService.add(machine);
            
            return ResponseEntity.ok(machine);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récuperation de la machine fanuc " + ip + " : " + e.getMessage());
        }
    }

    @GetMapping("/getAllMachine")
    ResponseEntity<?> getAllMachine() {
        try {
            return ResponseEntity.ok(fanucMachineService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation des machines fanuc " + e.getMessage());
        }
    }

    @GetMapping("/removeMachine")
    ResponseEntity<?> removeFanucMachine(@RequestParam String ip) {
        try {
            fanucMachineService.removeByIp(ip);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression de la machine fanuc " + ip + " : " + e.getMessage());
        }
    }
}
