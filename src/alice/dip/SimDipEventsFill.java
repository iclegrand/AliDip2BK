/*************
 * cil
 **************/

/*
 *  Simulates a set of DIP events for testing ONLY
 *
 */
package alice.dip;

import java.util.Date;

public class SimDipEventsFill implements Runnable {

    ProcData myProcData;

//	String [] BeamModeList = {"NOMODE","SETUP","INJECTION PROBE BEAM","INJECTION SETUP BEAM","INJECTION PHYSICS BEAM","PREPARE RAMP","RAMP","FLAT TOP","SQUEEZE","ADJUST","STABLE BEAMS","UNSTABLE BEAMS","BEAM DUMP","RAMP DOWN","RECOVERY","INJECT AND DUMP","CIRCULATE AND DUMP","ABORT","CYCLING","WARNING BEAM DUMP","NO BEAM" };

    String[] BeamModeList = {"SETUP", "STABLE BEAMS", "ADJUST"};
/*	
    BMode[0]="NOMODE";
    BMode[1]="SETUP";
    BMode[2]="INJECTION PROBE BEAM";
    BMode[3]="INJECTION SETUP BEAM";
    BMode[4]="INJECTION PHYSICS BEAM";
    BMode[5]="PREPARE RAMP";
    BMode[6]="RAMP";
    BMode[7]="FLAT TOP";
    BMode[8]="SQUEEZE";
    BMode[9]="ADJUST";
    BMode[10]="STABLE BEAMS";
    BMode[11]="UNSTABLE BEAMS";
    BMode[12]="BEAM DUMP";
    BMode[13]="RAMP DOWN";
    BMode[14]="RECOVERY";
    BMode[15]="INJECT AND DUMP";
    BMode[16]="CIRCULATE AND DUMP";
    BMode[17]="ABORT";
    BMode[18]="CYCLING";
    BMode[19]="WARNING BEAM DUMP";
    BMode[20]="NO BEAM";
*/


    int NT = 10;
    int NM = 10;


    public SimDipEventsFill(ProcData proc) {
        myProcData = proc;

        Thread t = new Thread(this);
        t.start();
    }


    public void run() {

        int RN = 1200;

        System.out.println(" START SIM FILL");


        for (int i = 0; i < 10; i++) {

            try {

                Thread.sleep(1000);


                myProcData.newFillNo((new Date()).getTime(), "" + RN, "p", "p", "Cucu", "10", "5");
                RN = RN + 1;

                Thread.sleep(60 * 1000);


                for (int j = 1; j < 5; j++) {
                    myProcData.newBeamMode((new Date()).getTime(), BeamModeList[1]);

                    Thread.sleep(30 * 1000);

                    myProcData.newBeamMode((new Date()).getTime(), BeamModeList[0]);
                    Thread.sleep(30 * 1000);
                }

                Thread.sleep(20 * 1000);

                myProcData.newBeamMode((new Date()).getTime(), "NO BEAM");


                Thread.sleep(60 * 1000);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}


