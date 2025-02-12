package meca.atlantique.fanuc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ShortByReference;

public interface FanucApi extends Library {
    FanucApi INSTANCE = (FanucApi) Native.load("lib/Fwlib32", FanucApi.class);  

    public abstract class ODBST extends Structure {
        
    }

    public class ODBST_15 extends ODBST {
        public short[]  dummy = new short[2];  /* Not used                           */
        public short    aut;                   /* AUTOMATIC mode selection           */
        public short    manual;                /* MANUAL mode selection              */
        public short    run;                   /* Status of automatic operation      */
        public short    edit;                  /* Status of program editing          */
        public short    motion;                /* Status of axis movement,dwell      */
        public short    mstb;                  /* Status of M,S,T,B function         */
        public short    emergency;             /* Status of emergency                */
        public short    write;                 /* Status of writing backupped memory */
        public short    labelskip;             /* Status of label skip               */
        public short    alarm;                 /* Status of alarm                    */
        public short    warning;               /* Status of warning                  */
        public short    battery;               /* Status of battery                  */
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }        
    }

    public class ODBST_OTHER extends ODBST {
        public short  hdck ;        /* Status of manual handle re-trace */
        public short  tmmode ;      /* T/M mode selection              */
        public short  aut ;         /* AUTOMATIC/MANUAL mode selection */
        public short  run ;         /* Status of automatic operation   */
        public short  motion ;      /* Status of axis movement,dwell   */
        public short  mstb ;        /* Status of M,S,T,B function      */
        public short  emergency ;   /* Status of emergency             */
        public short  alarm ;       /* Status of alarm                 */
        public short  edit ;        /* Status of program editing       */
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }        
    }

    // Reads the status information of CNC. The various information is stored in each member of "ODBST". 
    // use "ODBST_15" for series 15/15i, and "ODBST_OTHER" for others series
    short cnc_statinfo(short FlibHndl, ODBST statinfo);

    public class LOADELM extends Structure {
        public NativeLong   data;       /* load meter data, motor speed */
        public short        dec;        /* place of decimal point */
        public short        unit;       /* unit */
        public char         name;       /* spindle name */
        public char         suff1;      /* subscript of spindle name 1 */
        public char         suff2;      /* subscript of spindle name 2 */
        public char         reserve;    /* */

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }
    }

    public class ODBSPLOAD extends Structure {
        public LOADELM  spload;     /* spindle load meter data */
        public LOADELM  spspeed;    /* spindle motor data */

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }
    }

    public class ODBEXEPRG extends Structure {
        public byte[]       name = new byte[36];    /* the program name being executed */
        public NativeLong   o_num;                  /* the program number being executed */

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }
    } 

    public class ODBSYS extends Structure {
        public short    addinfo;                /* Additional information */
        public byte[]    max_axis = new byte[2];               /* Max. controlled axes */
        public byte[]   cnc_type = new byte[2]; /* Kind of CNC (ASCII) */
        public byte[]   mt_type = new byte[2];  /* Kind of M/T/TT (ASCII) */
        public byte[]   series = new byte[4];   /* Series number (ASCII) */
        public byte[]   version = new byte[4];  /* Version number (ASCII) */
        public byte[]   axes = new byte[2];     /* Current controlled axes(ASCII)*/

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }
    }

    public class IODBPMAINTE extends Structure {
        public byte[]       name = new byte[62];    /* Name string */
        public NativeLong   type;                   /* Life count type */
        public NativeLong   total;                  /* Life time (unit:minute) */
        public NativeLong   remain;                 /* Life remained time */
        public NativeLong   stat;                   /* Life count state */

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(Field::getName)
                .collect(Collectors.toList());
        }
    }
    
    // Allocates the library handle and connects to CNC that has the specified IP address or the Host Name.
    short cnc_allclibhndl3(String ipaddr, short port, NativeLong timeout, ShortByReference FlibHndl);

    // Reads system information such as kind of CNC system, Machining(M) or Turning(T), series and version of CNC system software and number of the controlled axes. 
    // Use this function to confirm compatibility of CNC's system software and PMC's software or to get the number of controllable axes before reading axis coordinate data such as absolute, machine position. 
    short cnc_sysinfo(short FlibHndl, ODBSYS sysinfo); 

    // Reads the spindle load meter data and the spindle motor speed data from 1st spindle to the specified spindle number.
    // In case that "data_num" is bigger than the current spindle number, this function sets the actual read spindle number (the current spindle number) to "data_num" variable after execution.
    // And in case that "data_num" is smaller than the current spindle number, this function reads data for the specified spindle number which is specified by "data_num". 
    // data_num MUST BE greater than zero
    short cnc_rdspmeter(short FlibHndl, short type, ShortByReference data_num, ODBSPLOAD loadmeter);

    // Reads full path name of the program which is being currently executed in CNC.
    short cnc_exeprgname(short FlibHndl, ODBEXEPRG exeprg);

    // Frees the library handle which was used by the Data window library. 
    short cnc_freelibhndl(short FlibHndl);

    // Reads all data specified by the item index.
    short cnc_rdpm_item(short FlibHndl, short start_num, ShortByReference data_num, IODBPMAINTE item);
}
