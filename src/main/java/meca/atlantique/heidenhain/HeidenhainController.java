package meca.atlantique.heidenhain;

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
@RequestMapping("/heidenhain")
@CrossOrigin(origins = "http://192.168.0.23:5173") // autorise react
@AllArgsConstructor
public class HeidenhainController {
    
    @Autowired
    private final HeidenhainMachineService heidenhainMachineService;

    @PostMapping("/createMachine")
    ResponseEntity<?> createHeidenhainMachine(@RequestParam String ip, @RequestParam String name, @RequestParam("port") Optional<Short> portOptional) {
        try {
            if (heidenhainMachineService.has(ip)) {
                return ResponseEntity.badRequest().body("La machine avec l'ip : " + ip + " existe déjà");
            }
            
            short port = portOptional.orElse(heidenhainMachineService.DEFAULT_PORT);
            HeidenhainMachine machine = new HeidenhainMachine(ip, port, name);
            heidenhainMachineService.add(machine);
            
            return ResponseEntity.ok(machine);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récuperation de la machine heidenhain " + ip + " : " + e.getMessage());
        }
    }

    @GetMapping("/getAllMachine")
    ResponseEntity<?> getAllMachine() {
        try {
            return ResponseEntity.ok(heidenhainMachineService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récuperation des machines heidenhain " + e.getMessage());
        }
    }

    @GetMapping("/getAlarmes")
    public ResponseEntity<?> getAlarmes(@RequestParam String ip) {
        try {
            if (!heidenhainMachineService.has(ip)) {
                return ResponseEntity.badRequest().body("La machine avec l'ip : " + ip + " n'existe pas dans la base de donnée");
            }
            
            HeidenhainMachine machine = heidenhainMachineService.getByIp(ip);
            
            return ResponseEntity.ok(heidenhainMachineService.getAlarmeMessages(machine));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récuperation des messages d'alarme de la machine heidenhain " + ip + " : " + e.getMessage());
        }
    }
}
