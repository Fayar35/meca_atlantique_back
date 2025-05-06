package meca.atlantique.fanuc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.ShortByReference;

import lombok.AllArgsConstructor;
import meca.atlantique.Utils;
import meca.atlantique.fanuc.FanucApi.ODBAHIS;
import meca.atlantique.fanuc.FanucApi.ODBAHIS2;
import meca.atlantique.fanuc.FanucApi.ODBAHIS5;
import meca.atlantique.fanuc.FanucApi.ODBEXEPRG;
import meca.atlantique.fanuc.FanucApi.ODBST;
import meca.atlantique.fanuc.FanucApi.ODBST_15;
import meca.atlantique.fanuc.FanucApi.ODBST_OTHER;
import meca.atlantique.fanuc.FanucApi.ODBSYS;
import meca.atlantique.fanuc.FanucApi.alm_his;
import meca.atlantique.fanuc.FanucApi.alm_his2;
import meca.atlantique.fanuc.FanucApi.alm_his5;
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
        if (handle == -1) {
            return new FanucMachine(ip, port, name, null, null);
        }
        
        ODBSYS info_system = new ODBSYS();
        FanucApiProvider.getInstance().cnc_sysinfo(handle, info_system);

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
        short error_code = FanucApiProvider.getInstance().cnc_allclibhndl3(ip, port, new NativeLong(CONNECTION_TIMEOUT), handle);
        if (error_code != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot connect to " + ip + ":" + port + ", error code : " + error_code);
        }
        return handle.getValue();
    }

    public List<MachineStatus> updateFanucMachineStatus() {
        List<FanucMachine> machines = repository.findAll();
        List<MachineStatus> machinesStatus = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        machines.forEach(machine -> {
            Future<?> future = executor.submit(() -> { 
                MachineStatus status = updateMachine(machine);
                if (status != null) {
                    machinesStatus.add(status);
                }
            });
            try {
                future.get(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.err.println(Utils.getTime() + " erreur lors de l'update machine fanuc : " + machine.getIp());
            }
        });

        return machinesStatus;
    }

    private MachineStatus updateMachine(FanucMachine machine) {
        try {
            short handle = connectFanucMachine(machine.getIp(), machine.getPort());
            if (handle == -1) {
                System.err.println(Utils.getTime() + " Erreur lors de la connexion à la machine Fanuc");
            }
            
            ODBST stats;
            MachineState state;
            if (machine.getSerie() == EnumSeries.SERIE_15 || machine.getSerie() == EnumSeries.SERIE_15i) {
                stats = new ODBST_15();
                FanucApiProvider.getInstance().cnc_statinfo(handle, stats);

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
                        System.out.println(Utils.getTime() + " Fanuc status inconnu : " + ((ODBST_15) stats).run);
                        state = MachineState.STOPPED;
                    }
                }
            } else {
                stats = new ODBST_OTHER();
                FanucApiProvider.getInstance().cnc_statinfo(handle, stats);
                
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
                        System.out.println(Utils.getTime() + " Fanuc status inconnu : " + ((ODBST_OTHER) stats).run);
                        state = MachineState.STOPPED;
                    }
                }
            }

            ODBEXEPRG exeprg = new ODBEXEPRG();
            FanucApiProvider.getInstance().cnc_exeprgname(handle, exeprg);
            String programName = new String(exeprg.name, StandardCharsets.UTF_8).trim().replace("\u0000", "");

            MachineStatus status = new MachineStatus();
            status.setMachine(machine);
            status.setState(state);
            status.setProgramName(programName);
            
            return status;
        } catch (Exception e) {
            System.err.println(Utils.getTime() + " " + e.getMessage());
            return null;
        }
    }

    public List<String> getAlarmeMessages(FanucMachine machine) {
        ShortByReference refHandle = new ShortByReference();
        FanucApiProvider.getInstance().cnc_allclibhndl3(machine.getIp(), machine.getPort(), new NativeLong(2), refHandle);
        short handle = refHandle.getValue();

        if (machine.getSerie() == EnumSeries.SERIE_15i) {
            return readAlarmHistory2(handle);
        } else if (Arrays.asList(EnumSeries.SERIE_0i, EnumSeries.SERIE_30i, EnumSeries.SERIE_31i, EnumSeries.SERIE_32i).contains(machine.getSerie())) {
            return readAlarmHistory5(handle);         
        } else {
            return readAlarmHistory(handle);
        }
    }

    private List<String> readAlarmHistory(short handle) {
        FanucApi api = FanucApiProvider.getInstance();
        ODBAHIS hist = new ODBAHIS();
        
        ShortByReference alarmNumber = new ShortByReference();
        
        api.cnc_stopophis(handle);
        api.cnc_rdalmhisno(handle, alarmNumber);

        hist.almHis = new alm_his[alarmNumber.getValue()];

        api.cnc_rdalmhistry(handle, (short) 1, alarmNumber.getValue(), (short) (6 + 48*alarmNumber.getValue()), hist);
        api.cnc_startophis(handle);

        List<String> list = new ArrayList<>();
        Arrays.asList(hist.almHis).forEach(his -> {
            list.add(formatAlarmMessage(his.year, his.month, his.day, his.hour, his.minute, his.second, (new String(his.alm_msg, StandardCharsets.UTF_8)).trim()));
        });

        return list;
    }

    private List<String> readAlarmHistory2(short handle) {
        FanucApi api = FanucApiProvider.getInstance();
        ODBAHIS2 hist = new ODBAHIS2();
        
        ShortByReference alarmNumber = new ShortByReference();
        
        api.cnc_stopophis(handle);
        api.cnc_rdalmhisno(handle, alarmNumber);

        hist.almHis = new alm_his2[alarmNumber.getValue()];

        api.cnc_rdalmhistry2(handle, (short) 1, alarmNumber.getValue(), (short) (6 + 48*alarmNumber.getValue()), hist);
        api.cnc_startophis(handle);

        List<String> list = new ArrayList<>();
        Arrays.asList(hist.almHis).forEach(his -> {
            list.add(formatAlarmMessage(his.year, his.month, his.day, his.hour, his.minute, his.second, (new String(his.alm_msg, StandardCharsets.UTF_8)).trim()));
        });

        return list;
    }

    private List<String> readAlarmHistory5(short handle) {
        FanucApi api = FanucApiProvider.getInstance();
        ODBAHIS5 hist = new ODBAHIS5();
        
        ShortByReference alarmNumber = new ShortByReference();
        
        api.cnc_stopophis(handle);
        api.cnc_rdalmhisno(handle, alarmNumber);

        hist.almHis = new alm_his5[alarmNumber.getValue()];

        api.cnc_rdalmhistry5(handle, (short) 1, alarmNumber.getValue(), (short) (6 + 48*alarmNumber.getValue()), hist);
        api.cnc_startophis(handle);

        List<String> list = new ArrayList<>();
        Arrays.asList(hist.almHis).forEach(his -> {
            if (his.year > 0) {
                list.add(formatAlarmMessage(his.year, his.month, his.day, his.hour, his.minute, his.second, (new String(his.alm_msg, StandardCharsets.UTF_8)).trim()));
            }
        });

        return list;
    }

    private String formatAlarmMessage(short year, short month, short day, short hour, short minute, short second, String message) {
        return String.format("%2d/%2d/%2d [%2d:%2d:%2d] %s", day, month, year, hour, minute, second, message);
    }
}
