package meca.atlantique.spring.Controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.ShortByReference;

import lombok.AllArgsConstructor;
import meca.atlantique.fanuc.FanucApi;
import meca.atlantique.fanuc.FanucApi.ODBST_15;
import meca.atlantique.fanuc.FanucApi.ODBST_OTHER;
import meca.atlantique.fanuc.FanucApi.ODBSYS;
import meca.atlantique.spring.Data.EnumSeries;
import meca.atlantique.spring.Data.FanucMachine;
import meca.atlantique.spring.Data.Machine;
import meca.atlantique.spring.Data.ODBSTDto;
import meca.atlantique.spring.Mapper.ODBSTMapper;
import meca.atlantique.spring.Services.FanucMachineService;

@RestController
@RequestMapping("/fanuc")
@AllArgsConstructor
public class FanucController {
    static final short DEFAULT_PORT = 8193;
    static final long CONNECTION_TIMEOUT = 2L;
    
    @Autowired
    private final FanucMachineService fanucMachineService;

    @GetMapping("/getMachine")
    Machine getFanucMachine(@RequestParam String ip, @RequestParam("port") Optional<Short> portOptional) {
        if (fanucMachineService.has(ip)) {
            return fanucMachineService.getByIp(ip);
        }

        short port = portOptional.orElse(DEFAULT_PORT);
        FanucMachine machine = collectFanucMachine(ip, port);
        fanucMachineService.add(machine);
        
        return machine;
    }

    @GetMapping("/getAllMachine")
    List<Machine> getAllMachine() {
        return fanucMachineService.getAll().stream().map(m -> (Machine) m).collect(Collectors.toList());
    }

    @GetMapping("/getMachineStatus")
    ODBSTDto getFanucStatus(@RequestParam String ip, @RequestParam("port") Optional<Short> portOptional) {
        
        FanucMachine machine;
        if (fanucMachineService.has(ip)) {
            machine = fanucMachineService.getByIp(ip);
        } else {
            short port = portOptional.orElse(DEFAULT_PORT);
            machine = collectFanucMachine(ip, port);
        }

        short handle = connectFanucMachine(ip, machine.getPort());

        if (machine.getSerie() == EnumSeries.SERIE_15 || machine.getSerie() == EnumSeries.SERIE_15i) {
            ODBST_15 stats = new ODBST_15();
            FanucApi.INSTANCE.cnc_statinfo(handle, stats);
            return ODBSTMapper.INSTANCE.ODBST_15ToODBSTDto(stats);
        } else {
            ODBST_OTHER stats = new ODBST_OTHER();
            FanucApi.INSTANCE.cnc_statinfo(handle, stats);
            return ODBSTMapper.INSTANCE.ODBST_OTHERToODBSTDto(stats);
        }
    }

    @GetMapping("/removeMachine")
    void removeFanucMachine(@RequestParam String ip) {
        fanucMachineService.removeByIp(ip);
    }

    private FanucMachine collectFanucMachine(String ip, short port) {
        short handle = connectFanucMachine(ip, port);
        
        ODBSYS info_system = new ODBSYS();
        FanucApi.INSTANCE.cnc_sysinfo(handle, info_system);

        String name = "Series " + 
            new String(info_system.cnc_type).trim() + 
            ((info_system.addinfo & 0x70) != 0 ? "i" : "") + "-" +
            new String(info_system.mt_type).trim() + " (" +
            new String(info_system.series).trim() + "-" +
            new String(info_system.version).trim() + ")";

        EnumSeries serie = FanucMachine.getEnumSeriesFromSysInfos(info_system.cnc_type, info_system.addinfo);

        return new FanucMachine(ip, port, name, serie);
    }

    private short connectFanucMachine(String ip, short port) {
        ShortByReference handle = new ShortByReference();
        short error_code = FanucApi.INSTANCE.cnc_allclibhndl3(ip, port, new NativeLong(CONNECTION_TIMEOUT), handle);
        if (error_code != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot connect to " + ip + ":" + port + ", error code : " + error_code);
        }
        return handle.getValue();
    }
}
