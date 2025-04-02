package meca.atlantique.fanuc;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/fanuc")
@CrossOrigin(origins = "http://192.168.0.23:5173") // autorise react
@AllArgsConstructor
public class FanucController {
    
    @Autowired
    private final FanucMachineService fanucMachineService;

    @PostMapping("/createMachine")
    ResponseEntity<?> createFanucMachine(@RequestParam String ip, @RequestParam String name, @RequestParam("port") Optional<Short> portOptional) {
        try {
            if (fanucMachineService.has(ip)) {
                return ResponseEntity.badRequest().body("La machine avec l'ip : " + ip + " existe déjà");
            }
            
            short port = portOptional.orElse(fanucMachineService.DEFAULT_PORT);
            FanucMachine machine = fanucMachineService.collectFanucMachine(ip, name, port);
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
}
