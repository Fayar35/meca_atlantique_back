package meca.atlantique.spring.Controllers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import meca.atlantique.fanuc.FanucApi.ODBST_15;
import meca.atlantique.spring.Data.Machine;

@RestController
@AllArgsConstructor
public class MainController {

    private final FanucController fanucController;

    @GetMapping("/getAllMachine")
    List<Machine> getAllMachine() {
        List<Machine> ret = fanucController.getAllMachine();
        return ret;
    }

    @GetMapping("/error")
    void error() {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error");
    }

    @GetMapping("/test")
    List<String> test() {
        return Arrays.asList(ODBST_15.class.getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}
