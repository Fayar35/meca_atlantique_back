package meca.atlantique.fanuc;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ShortByReference;

// documentation fanuc ici : https://www.inventcom.net/fanuc-focas-library/general/flist_all
public interface FanucApi extends Library {

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

    public class ODBAHIS extends Structure {
        public short s_no;       /* Start record number      */
        public short type;       /* Not used                 */
        public short e_no;       /* Most recently entered    */
        public alm_his[] almHis;
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
            .map(Field::getName)
            .collect(Collectors.toList());
        }
    }
    
    public class alm_his extends Structure {
        public short      dummy;                  /* Not used            */
        public short      alm_grp;                /* Alarm type          */
        public short      alm_no;                 /* Alarm number        */
        public byte       axis_no;                /* Axis number         */
        public byte       year;                   /* Year                */
        public byte       month;                  /* Month               */
        public byte       day;                    /* Day                 */
        public byte       hour;                   /* Hour                */
        public byte       minute;                 /* Minute              */
        public byte       second;                 /* Second              */
        public byte       dummy2;                 /* Not used            */
        public short      len_msg;                /* Length of alarm     */
        public byte[]     alm_msg = new byte[32]; /* Alarm message       */

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
            .map(Field::getName)
            .collect(Collectors.toList());
        }
    }

    public class ODBAHIS2 extends Structure {
        public short s_no;       /* Start record number      */
        public short e_no;       /* Most recently entered    */
        public alm_his2[] almHis;
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
            .map(Field::getName)
            .collect(Collectors.toList());
        }
    }
    
    public class alm_his2 extends Structure {
        public short      alm_grp;                /* Alarm type          */
        public short      alm_no;                 /* Alarm number        */
        public byte       axis_no;                /* Axis number         */
        public byte       year;                   /* Year                */
        public byte       month;                  /* Month               */
        public byte       day;                    /* Day                 */
        public byte       hour;                   /* Hour                */
        public byte       minute;                 /* Minute              */
        public byte       second;                 /* Second              */
        public short      len_msg;                /* Length of alarm     */
        public byte[]     alm_msg = new byte[32]; /* Alarm message       */

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
            .map(Field::getName)
            .collect(Collectors.toList());
        }
    }
    
    public class ODBAHIS5 extends Structure {
        public short s_no;       /* Start record number      */
        public short e_no;       /* Most recently entered    */
        public alm_his5[] almHis;
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(this.getClass().getDeclaredFields()).stream()
            .map(Field::getName)
            .collect(Collectors.toList());
        }
    }
    
    public class alm_his5 extends Structure {
        public short         alm_grp;        /* Alarm type              */
        public short         alm_no;         /* Alarm number            */
        public short         axis_no;        /* Axis number or Spindle number */
        public short         year;           /* Year                    */
        public short         month;          /* Month                   */
        public short         day;            /* Day                     */
        public short         hour;           /* Hour                    */
        public short         minute;         /* Minute                  */
        public short         second;         /* Second                  */
        public short         len_msg;        /* Length of alarm message */
        public short         pth_no;         /* path number             */
        public short         dammy;
        public short         dsp_flg;        /* Flag for displaying */
        public short         axis_num;       /* Total axis number */
        public byte[]        alm_msg = new byte[64];        /* Alarm message           */
        public NativeLong[]  g_modal= new NativeLong[10];   /* Modal data of G code */
        public byte[]        g_dp = new byte[10];           /* #7=1 There is a command in the present block. */
                                                            /* #6~#0 place of decimal point */
        public NativeLong[]  a_modal = new NativeLong[10];  /* Modal data of B,D,E,F,H,M,N,O,S,T code */
        public byte[]        a_dp = new byte[10];           /* #7=1 There is a command in the present block. */
                                                            /* #6~#0 place of decimal point */
        public NativeLong[]  abs_pos = new NativeLong[32];  /* Absolute position in alarm occuring */
        public byte[]        abs_dp = new byte[32];         /* Place of decimal point for absolute position in alarm occuring */
        public NativeLong[]  mcn_pos = new NativeLong[32];  /* Machine position in alarm occuring */
        public byte[]        mcn_dp = new byte[32];         /* Place of decimal point for machine position in alarm occuring */

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

    // Reads the alarm history data. The unit of one alarm history data is called a record.
    // The operation history data and the alarm history data are automatically recorded on the CNC. 
    // When these data are accessed, it is necessary to temporarily stop sampling on the CNC. 
    // Therefore, it is necessary to execute "Stop logging operation history data"(cnc_stopophis) before this function is used. 
    short cnc_rdalmhistry(short FlibHndl, short s_no, short e_no, short length, ODBAHIS his);

    // Reads the alarm history data. Please use this function instead of cnc_rdalmhistry for Series 15i. 
    short cnc_rdalmhistry2(short FlibHndl, short s_no, short e_no, short length, ODBAHIS2 his);

    // Reads the alarm history data. Please use this function instead of cnc_rdalmhistry for Series 30i, 0i-D/F and PMi-A. 
    short cnc_rdalmhistry5(short FlibHndl, short s_no, short e_no, short length, ODBAHIS5 his);

    // Stops sampling the operation history data and the alarm history data of CNC.
    // In Series 30i/31i/32i, 0i-D/F and PMi-A, the sampling stop of the external operator's message history is also directed.
    short cnc_stopophis(short FlibHndl); 

    // Restarts sampling the operation history data and the alarm history data of CNC. 
    short cnc_startophis(short FlibHndl); 

    // Reads the number of alarm history data.
    // It is necessary to stop sampling the alarm history data by using cnc_stopophis function before this function is used. 
    short cnc_rdalmhisno(short FlibHndl, ShortByReference hisno); 
}
