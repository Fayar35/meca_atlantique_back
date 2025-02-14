package meca.atlantique.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.ShortByReference;

import lombok.AllArgsConstructor;
import meca.atlantique.fanuc.FanucApi;
import meca.atlantique.fanuc.FanucApi.ODBST_15;
import meca.atlantique.fanuc.FanucApi.ODBST_OTHER;
import meca.atlantique.fanuc.FanucApi.ODBSYS;
import meca.atlantique.spring.Data.EnumSeries;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Data.ODBSTDto;
import meca.atlantique.spring.Mapper.ODBSTMapper;
import meca.atlantique.spring.Service.MachineService;

@RestController
@AllArgsConstructor
public class MainController {
    static short DEFAULT_PORT = (short) 8193;
    @Autowired
    private final MachineService machineService;

    @GetMapping("/getFanucMachine")
    Machine getMachine(@RequestParam String ip, @RequestParam("port") Optional<Short> portOptional) {
        if (machineService.has(ip)) {
            return machineService.getByIp(ip);
        }

        short port = portOptional.orElse(DEFAULT_PORT);
        
        ShortByReference handle = new ShortByReference();
        short error_code = FanucApi.INSTANCE.cnc_allclibhndl3(ip, port, new NativeLong(10), handle);
        if (error_code != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot connect to " + ip + ":" + port + ", error code : " + error_code);
        }
        
        ODBSYS info_system = new ODBSYS();
        error_code = FanucApi.INSTANCE.cnc_sysinfo(handle.getValue(), info_system);

        String name = "Series " + 
            new String(info_system.cnc_type) + 
            ((info_system.addinfo & 0x70) != 0 ? "i" : "") + "-" +
            new String(info_system.mt_type) + " (" +
            new String(info_system.series) + "-" +
            new String(info_system.version) + ")";

        EnumSeries serie = Machine.getEnumSeriesFromSysInfos(info_system.cnc_type, info_system.addinfo);

        Machine machine = new Machine(ip, port, name, serie.toString(), handle.getValue());

        machineService.add(machine);
        
        return machine;
    }

    @GetMapping("/getAllMachine")
    List<Machine> getAllMachine() {
        return machineService.getAll();
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

    @GetMapping("/getIps")
    List<String> getIps() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File("machines_info.json"));
            ArrayList<String> ret = new ArrayList<String>();
            
            JsonNode fanuc = jsonNode.get("fanuc");
            fanuc.forEach((JsonNode node) -> {
                ret.add(node.asText());
            });

            JsonNode heidenhain = jsonNode.get("heidenhain");
            heidenhain.forEach((JsonNode node) -> {
                ret.add(node.asText());
            });

            return ret;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "cannot read json file");
        }
    }

    @GetMapping("/getFanucStatus")
    ODBSTDto getFanucStatus(@RequestParam String ip, @RequestParam("port") Optional<Short> portOptional) {
        short port = portOptional.orElse(DEFAULT_PORT);

        Machine machine;
        if (machineService.has(ip)) {
            machine = machineService.getByIp(ip);
        } else {
            machine = getMachine(ip, Optional.of(port));
        }

        if (machine.getSerie() == EnumSeries.SERIE_15.toString() || machine.getSerie() == EnumSeries.SERIE_15i.toString()) {
            ODBST_15 stats = new ODBST_15();
            FanucApi.INSTANCE.cnc_statinfo(machine.getHandle(), stats);
            return ODBSTMapper.INSTANCE.ODBST_15ToODBSTDto(stats);
        } else {
            ODBST_OTHER stats = new ODBST_OTHER();
            FanucApi.INSTANCE.cnc_statinfo(machine.getHandle(), stats);
            return ODBSTMapper.INSTANCE.ODBST_OTHERToODBSTDto(stats);
        }
    }
}
