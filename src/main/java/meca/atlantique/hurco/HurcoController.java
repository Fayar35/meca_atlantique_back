package meca.atlantique.hurco;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/hurco")
@CrossOrigin(origins = "http://192.168.0.23:5173") // autorise react
@AllArgsConstructor
public class HurcoController {

    @Autowired
    private final HurcoMachineService hurcoMachineService;

    @PostMapping("/createMachine")
    ResponseEntity<?> createHurcoMachine(@RequestParam String ip, @RequestParam String name, @RequestParam("port") Optional<Short> portOptional) {
        try {
            if (hurcoMachineService.has(ip)) {
                return ResponseEntity.badRequest().body("La machine avec l'ip : " + ip + " existe déjà");
            }

            short port = portOptional.orElse(hurcoMachineService.DEFAULT_PORT);
            HurcoMachine machine = new HurcoMachine(ip, port, name);
            hurcoMachineService.add(machine);

            return ResponseEntity.ok(machine);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récuperation de la machine hurco " + ip + " : " + e.getMessage());
        }
    }
}
