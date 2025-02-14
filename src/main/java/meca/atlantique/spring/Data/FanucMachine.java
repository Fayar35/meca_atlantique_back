package meca.atlantique.spring.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Table(name = "fanucMachine")
public class FanucMachine extends Machine {
    @Enumerated(EnumType.STRING)
    private EnumSeries serie;
    private short handle;

    public FanucMachine(String ip, short port, String name, EnumSeries serie, short handle) {
        super(ip, port, name);
        this.setSerie(serie);
        this.setHandle(handle);
    }

    public static EnumSeries getEnumSeriesFromSysInfos(byte[] cncType, short addinfo) {
        switch (new String(cncType)) {
            case "15": 
                if ((addinfo & (short) 0x70) != 0) { // bit 1 :  0 = not an i Series CNC, 1 = i Series CNC
                    return EnumSeries.SERIE_15i;
                } else {
                    return EnumSeries.SERIE_15;
                }
            case "16": 
                if ((addinfo & (short) 0x70) != 0) { // bit 1 :  0 = not an i Series CNC, 1 = i Series CNC
                    return EnumSeries.SERIE_16i;
                } else {
                    return EnumSeries.SERIE_16;
                }
            case "18": 
                if ((addinfo & (short) 0x70) != 0) { // bit 1 :  0 = not an i Series CNC, 1 = i Series CNC
                    return EnumSeries.SERIE_18i;
                } else {
                    return EnumSeries.SERIE_18;
                }
            case "21": 
                if ((addinfo & (short) 0x70) != 0) { // bit 1 :  0 = not an i Series CNC, 1 = i Series CNC
                    return EnumSeries.SERIE_21i;
                } else {
                    return EnumSeries.SERIE_21;
                }
            case "30": return EnumSeries.SERIE_30i;
            case "31": return EnumSeries.SERIE_31i;
            case "32": return EnumSeries.SERIE_32i;
            case "35": return EnumSeries.SERIE_35i;
            case " 0": return EnumSeries.SERIE_0i;
            case "PD": return EnumSeries.POWER_MATE_ID;
            case "PH": return EnumSeries.POWER_MATE_IH;
            case "PM": return EnumSeries.POWER_MOTION_I;
            default: return null;
        }
    }
}
