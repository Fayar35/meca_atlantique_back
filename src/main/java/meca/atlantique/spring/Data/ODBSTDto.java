package meca.atlantique.spring.Data;

import lombok.Data;

@Data
public class ODBSTDto {
    short manual;        /* MANUAL mode selection              */
    short write;         /* Status of writing backupped memory */
    short labelskip;     /* Status of label skip               */
    short warning;       /* Status of warning                  */
    short battery;       /* Status of battery                  */
    short hdck ;         /* Status of manual handle re-trace   */
    short tmmode ;       /* T/M mode selection                 */
    short aut ;          /* AUTOMATIC/MANUAL mode selection    */
    short run ;          /* Status of automatic operation      */
    short motion ;       /* Status of axis movement,dwell      */
    short mstb ;         /* Status of M,S,T,B function         */
    short emergency ;    /* Status of emergency                */
    short alarm ;        /* Status of alarm                    */
    short edit ;         /* Status of program editing          */
}
