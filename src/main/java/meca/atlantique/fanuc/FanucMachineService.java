package meca.atlantique.fanuc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.ShortByReference;

import lombok.AllArgsConstructor;
import meca.atlantique.fanuc.FanucApi.ODBEXEPRG;
import meca.atlantique.fanuc.FanucApi.ODBST;
import meca.atlantique.fanuc.FanucApi.ODBST_15;
import meca.atlantique.fanuc.FanucApi.ODBST_OTHER;
import meca.atlantique.fanuc.FanucApi.ODBSYS;
import meca.atlantique.spring.Data.EnumSeries;
import meca.atlantique.spring.Data.MachineState;
import meca.atlantique.spring.Data.MachineStatus;

@Service
@AllArgsConstructor
public class FanucMachineService {
    public final short DEFAULT_PORT = 8193;
    public final long CONNECTION_TIMEOUT = 2L;

    @Autowired
    private final FanucMachineRepository repository;

    @Transactional
    public List<FanucMachine> getAll() {
        return repository.findAll();
    }

    @Transactional
    public FanucMachine getByIp(String ip) {
        return repository.findByIp(ip).orElse(null);
    }

    @Transactional
    public FanucMachine add(FanucMachine machine) {
        return repository.save(machine);
    }

    @Transactional
    public boolean has(String ip) {
        return repository.existsById(ip);
    }

    @Transactional
    public void removeByIp(String ip) {
        repository.deleteByIp(ip);
    }

    public FanucMachine collectFanucMachine(String ip, String name, short port) {
        short handle = connectFanucMachine(ip, port);
        
        ODBSYS info_system = new ODBSYS();
        FanucApi.INSTANCE.cnc_sysinfo(handle, info_system);

        String serialNumber = "Series " + 
            new String(info_system.cnc_type).trim() + 
            ((info_system.addinfo & 0x70) != 0 ? "i" : "") + "-" +
            new String(info_system.mt_type).trim() + " (" +
            new String(info_system.series).trim() + "-" +
            new String(info_system.version).trim() + ")";

        EnumSeries serie = FanucMachine.getEnumSeriesFromSysInfos(info_system.cnc_type, info_system.addinfo);

        return new FanucMachine(ip, port, name, serie, serialNumber);
    }

    public short connectFanucMachine(String ip, short port) {
        ShortByReference handle = new ShortByReference();
        short error_code = FanucApi.INSTANCE.cnc_allclibhndl3(ip, port, new NativeLong(CONNECTION_TIMEOUT), handle);
        if (error_code != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot connect to " + ip + ":" + port + ", error code : " + error_code);
        }
        return handle.getValue();
    }

    public List<MachineStatus> updateFanucMachineStatus() {
        List<FanucMachine> machines = repository.findAll();
        List<MachineStatus> machinesStatus = new ArrayList<>();
        machines.forEach(machine -> {
            try {
                short handle = connectFanucMachine(machine.getIp(), machine.getPort());
                
                ODBST stats;
                MachineState state;
                if (machine.getSerie() == EnumSeries.SERIE_15 || machine.getSerie() == EnumSeries.SERIE_15i) {
                    stats = new ODBST_15();
                    FanucApi.INSTANCE.cnc_statinfo(handle, stats);

                    // status code définis ici :
                    // https://www.inventcom.net/fanuc-focas-library/Misc/cnc_statinfo
                    switch(((ODBST_15) stats).run) {
                        case 0: {
                            // STOP
                            state = MachineState.STOPPED;
                            break;
                        }
                        case 1: {
                            // HOLD
                            state = MachineState.HOLD;
                            break;
                        }
                        case 2: {
                            // STaRT
                            state = MachineState.RUNNING;
                            break;
                        }
                        default: {
                            System.out.println("Fanuc status inconnu : " + ((ODBST_15) stats).run);
                            state = MachineState.STOPPED;
                        }
                    }
                } else {
                    stats = new ODBST_OTHER();
                    FanucApi.INSTANCE.cnc_statinfo(handle, stats);
                    
                    switch(((ODBST_OTHER) stats).run) {
                        case 0: {
                            // ****(reset)
                            state = MachineState.UNKNOWN;
                            break;
                        }
                        case 1: {
                            // STOP
                            state = MachineState.STOPPED;
                            break;
                        }
                        case 2: {
                            // HOLD
                            state = MachineState.HOLD;
                            break;
                        }
                        case 3: {
                            // STaRT
                            state = MachineState.RUNNING;
                            break;
                        }
                        case 4: {
                            // MSTR (during retraction and re-positioning of tool retraction and recovery, and operation of JOG MDI)
                            state = MachineState.RUNNING;
                            break;
                        }
                        default: {
                            System.out.println("Fanuc status inconnu : " + ((ODBST_OTHER) stats).run);
                            state = MachineState.STOPPED;
                        }
                    }
                }

                ODBEXEPRG exeprg = new ODBEXEPRG();
                FanucApi.INSTANCE.cnc_exeprgname(handle, exeprg);
                String programName = new String(exeprg.name, StandardCharsets.UTF_8).trim().replace("\u0000", "");

                MachineStatus status = new MachineStatus();
                status.setMachine(machine);
                status.setState(state);
                status.setProgramName(programName);
                
                machinesStatus.add(status);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });

        return machinesStatus;
    }
}
