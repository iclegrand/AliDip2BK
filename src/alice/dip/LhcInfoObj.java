/*************
 * cil
 **************/
/*
 * Keeps the required LHC information
 */
package alice.dip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class LhcInfoObj implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    static String stableBeamName = "STABLE BEAMS";

    public int fillNo;
    public long createdTime = -1;
    public long endedTime = -1;
    public String Beam1ParticleType;
    public String Beam2ParticleType;

    private float beamEnergy;
    public String beamType;
    public int LHCTotalInteractingBunches;
    public int LHCTotalNonInteractingBuchesBeam1;
    public int LHCTotalNonInteractingBuchesBeam2;
    private float LHCBetaStar;
    public String LHCFillingSchemeName;

    public int IP2_NO_COLLISIONS;
    public int NO_BUNCHES;

    public ArrayList<floatTS> beamEnergyHist;
    public ArrayList<floatTS> LHCBetaStarHist;

    public ArrayList<strTS> beamModeHist;
    public ArrayList<strTS> FillingSchemeHist;
    public ArrayList<strTS> ActiveFillingSchemeHist;

    public LhcInfoObj() {
    }

    public LhcInfoObj(long date, int no, String part1, String part2, String fs, int p2col, int nob) {
        createdTime = date;
        fillNo = no;

        Beam1ParticleType = part1;
        Beam2ParticleType = part2;
        beamType = part1 + " - " + part2;
        LHCFillingSchemeName = fs;
        IP2_NO_COLLISIONS = p2col;
        NO_BUNCHES = nob;

        beamModeHist = new ArrayList<strTS>();
        FillingSchemeHist = new ArrayList<strTS>();
        beamEnergyHist = new ArrayList<floatTS>();
        LHCBetaStarHist = new ArrayList<floatTS>();
        ActiveFillingSchemeHist = new ArrayList<strTS>();

        endedTime = -1;
        beamEnergy = -1;
        LHCTotalInteractingBunches = IP2_NO_COLLISIONS;
        LHCTotalNonInteractingBuchesBeam1 = NO_BUNCHES - IP2_NO_COLLISIONS;

        if (LHCTotalNonInteractingBuchesBeam1 < 0) LHCTotalNonInteractingBuchesBeam1 = 0;
        LHCTotalNonInteractingBuchesBeam2 = LHCTotalNonInteractingBuchesBeam1;
        LHCBetaStar = -1;

        strTS ts1 = new strTS(date, fs + " *");
        FillingSchemeHist.add(ts1);


    }


    public String toString() {
        String ans = " FILL No=" + fillNo + " StartTime=" + AliDip2BK.myDateFormat.format(createdTime);
        if (endedTime > 0) {
            ans = ans + " EndTime=" + AliDip2BK.myDateFormat.format(endedTime);
        }
        //ans = ans + " Beam1_ParticleType="+ Beam1ParticleType + " Beam2_ParticleType="+ Beam2ParticleType;
        ans = ans + " Beam Mode=" + getBeamMode();
        ans = ans + " Beam Type=" + beamType;
        ans = ans + " LHC Filling Scheme =" + LHCFillingSchemeName;
        ans = ans + " Beam  Energy=" + beamEnergy + " Beta Star=" + LHCBetaStar;
        ans = ans + " LHCTotalInteractingBunches =" + LHCTotalInteractingBunches + " LHCTotalNonInteractingBuchesBeam1=" + LHCTotalNonInteractingBuchesBeam1;
        ans = ans + " Stable Beam Duration=" + getStableBeamDuration();
        return ans;
    }

    public String history() {
        String ans = " FILL No=" + fillNo + " StartTime=" + AliDip2BK.myDateFormat.format(createdTime);
        if (endedTime > 0) {
            ans = ans + " EndTime=" + AliDip2BK.myDateFormat.format(endedTime) + "\n";
        }
        ans = ans + " Beam1_ParticleType=" + Beam1ParticleType + " Beam2_ParticleType=" + Beam2ParticleType;
        ans = ans + " Beam Type=" + beamType;
        ans = ans + " LHC Filling Scheme =" + LHCFillingSchemeName + "\n";
        ans = ans + " Beam Energy=" + beamEnergy + " Beta Star=" + LHCBetaStar + "\n";
        ans = ans + " No_BUNCHES=" + NO_BUNCHES + "\n";
        ans = ans + " LHCTotalInteractingBunches =" + LHCTotalInteractingBunches + " LHCTotalNonInteractingBuchesBeam1=" + LHCTotalNonInteractingBuchesBeam1 + "\n";
        ans = ans + " Start Stable Beams =" + getStableBeamStartStr();
        ans = ans + " Stop Stable Beams =" + getStableBeamStopStr() + "\n";
        ans = ans + " Stable Beam Duration [s] =" + getStableBeamDuration() + "\n";

        if (beamModeHist.size() >= 1) {
            ans = ans + " History:: Beam Mode\n";

            for (int i = 0; i < beamModeHist.size(); i++) {
                strTS a1 = beamModeHist.get(i);
                ans = ans + " - " + AliDip2BK.myDateFormat.format(a1.time) + "  " + a1.value + "\n";
            }
        }

        if (FillingSchemeHist.size() >= 1) {
            ans = ans + " History:: Filling Scheme \n";

            for (int i = 0; i < FillingSchemeHist.size(); i++) {
                strTS a1 = FillingSchemeHist.get(i);
                ans = ans + " - " + AliDip2BK.myDateFormat.format(a1.time) + "  " + a1.value + "\n";
            }
        }

        if (ActiveFillingSchemeHist.size() >= 1) {
            ans = ans + " History:: Active Filling Scheme \n";

            for (int i = 0; i < ActiveFillingSchemeHist.size(); i++) {
                strTS a1 = ActiveFillingSchemeHist.get(i);
                ans = ans + " - " + AliDip2BK.myDateFormat.format(a1.time) + "  " + a1.value + "\n";
            }
        }

        if (beamEnergyHist.size() >= 1) {
            ans = ans + " History:: Beam Energy\n";

            for (int i = 0; i < beamEnergyHist.size(); i++) {
                floatTS a1 = beamEnergyHist.get(i);
                ans = ans + " - " + AliDip2BK.myDateFormat.format(a1.time) + "  " + a1.value + "\n";
            }
        }

        if (LHCBetaStarHist.size() >= 1) {
            ans = ans + " History:: LHC Beta Star\n";

            for (int i = 0; i < LHCBetaStarHist.size(); i++) {
                floatTS a1 = LHCBetaStarHist.get(i);
                ans = ans + " - " + AliDip2BK.myDateFormat.format(a1.time) + "  " + a1.value + "\n";
            }
        }

        return ans;
    }

    public LhcInfoObj clone() {
        LhcInfoObj n = new LhcInfoObj(createdTime, fillNo, Beam1ParticleType, Beam2ParticleType, LHCFillingSchemeName, IP2_NO_COLLISIONS, NO_BUNCHES);

        @SuppressWarnings("unchecked")
        ArrayList<strTS> bmh = (ArrayList<strTS>) beamModeHist.clone();
        @SuppressWarnings("unchecked")
        ArrayList<strTS> fsh = (ArrayList<strTS>) FillingSchemeHist.clone();
        @SuppressWarnings("unchecked")
        ArrayList<floatTS> eh = (ArrayList<floatTS>) beamEnergyHist.clone();
        @SuppressWarnings("unchecked")
        ArrayList<floatTS> bsh = (ArrayList<floatTS>) LHCBetaStarHist.clone();
        @SuppressWarnings("unchecked")
        ArrayList<strTS> afsh = (ArrayList<strTS>) ActiveFillingSchemeHist.clone();

        n.beamModeHist = bmh;
        n.FillingSchemeHist = fsh;
        n.ActiveFillingSchemeHist = afsh;
        n.beamEnergyHist = eh;
        n.LHCBetaStarHist = bsh;

        n.endedTime = endedTime;

        n.beamEnergy = beamEnergy;
        n.LHCTotalInteractingBunches = LHCTotalInteractingBunches;
        n.LHCTotalNonInteractingBuchesBeam1 = LHCTotalNonInteractingBuchesBeam1;
        n.LHCTotalNonInteractingBuchesBeam2 = LHCTotalNonInteractingBuchesBeam2;
        n.LHCBetaStar = LHCBetaStar;


        return n;
    }

    public boolean verifyAndUpdate(long time, int fillNumber, String fs, int ip2c, int nob) {

        boolean isInPIB = false;
        boolean update = false;


        if (!fs.contentEquals(LHCFillingSchemeName)) {
            AliDip2BK.log(4, "LHCInfo.verify", "FILL=" + fillNo + "  Filling Scheme is different OLD=" + LHCFillingSchemeName + " NEW=" + fs);


            String bm = getBeamMode();
            if (bm != null) {
                if (bm.contains("INJECTION") && bm.contains("PHYSICS")) {
                    isInPIB = true;
                    LHCFillingSchemeName = fs;
                    IP2_NO_COLLISIONS = ip2c;
                    NO_BUNCHES = nob;
                    update = true;
                    AliDip2BK.log(5, "LHCInfo.verify", "FILL=" + fillNo + " is IPB-> Changed Filling Scheme to :" + LHCFillingSchemeName);
                } else {
                    AliDip2BK.log(4, "LHCInfo.verify", "FILL=" + fillNo + " is NOT in IPB keepFilling scheme to: " + LHCFillingSchemeName);
                }

            }
            addNewFS(time, fs, isInPIB);
        }


        if (ip2c != IP2_NO_COLLISIONS) {
            AliDip2BK.log(4, "LHCInfo.verify", " FILL=" + fillNo + " IP2 COLLis different OLD=" + IP2_NO_COLLISIONS + " new=" + ip2c
            );
            IP2_NO_COLLISIONS = ip2c;

        }

        if (nob != NO_BUNCHES) {
            AliDip2BK.log(4, "LHCInfo.verify", " FILL=" + fillNo + " INO_BUNCHES is different OLD=" + NO_BUNCHES + " new=" + nob);
            NO_BUNCHES = nob;

        }

        return update;

    }


    public void addNewAFS(long time, String fs) {

        strTS ts2 = new strTS(time, fs);
        ActiveFillingSchemeHist.add(ts2);

    }


    public void addNewFS(long time, String fs, boolean isInPIB) {

        //strTS ts1 = FillingSchemeHist.get(FillingSchemeHist.size()-1);

        //String lfs = ts1.value ;
        //if ( fs.contentEquals(lfs)) {
        // last one is the same
        //AliDip2BK.log(2,"LHCInfo.addNewFS"," FILL="+fillNo + " last Filling Scheme is the same ="+ fs);
        //} else {
        String nfs = fs;
        if (isInPIB) nfs = nfs + " *";
        strTS ts2 = new strTS(time, nfs);
        FillingSchemeHist.add(ts2);
        //}
    }

    public void setBeamMode(long date, String mode) {
        strTS nv = new strTS(date, mode);
        beamModeHist.add(nv);
        //System.out.println ( "  Added beam mode "+ mode);
    }

    public float getEnergy() {
        return beamEnergy;
    }

    public float getLHCBetaStar() {
        return LHCBetaStar;
    }

    public void setEnergy(long date, float v) {

        if (beamEnergyHist.size() == 0) {
            floatTS v1 = new floatTS(date, v);
            beamEnergyHist.add(v1);
            beamEnergy = v;
            return;
        }

        double re = Math.abs(beamEnergy - v);
        if (re < AliDip2BK.DIFF_ENERGY) {
        } else {
            floatTS v1 = new floatTS(date, v);
            beamEnergyHist.add(v1);
            beamEnergy = v;
        }
    }

    public void setLHCBetaStar(long date, float v) {
        if (LHCBetaStarHist.size() == 0) {
            floatTS v1 = new floatTS(date, v);
            LHCBetaStarHist.add(v1);
            LHCBetaStar = v;
            return;
        }

        double re = Math.abs(LHCBetaStar - v);
        if (re < AliDip2BK.DIFF_BETA) {
        } else {
            floatTS v1 = new floatTS(date, v);
            LHCBetaStarHist.add(v1);
            LHCBetaStar = v;
        }

    }

    public String getBeamMode() {
        if (beamModeHist.size() == 0) {
            return null;
        }
        strTS last = beamModeHist.get(beamModeHist.size() - 1);
        return last.value;
    }

    public String getStableBeamStartStr() {

        long t = getStableBeamStart();
        if (t < 0) {
            return "No Stable Beam";
        } else {
            return AliDip2BK.myDateFormat.format(t);
        }
    }

    public String getStableBeamStopStr() {

        long t = getStableBeamStop();
        if (t < 0) {
            return "No Stable Beam";
        } else {
            return AliDip2BK.myDateFormat.format(t);
        }
    }

    public long getStableBeamStart() {
        long ans = -1;


        for (int i = 0; i < beamModeHist.size(); i++) {

            strTS a1 = beamModeHist.get(i);

            if (a1.value.equalsIgnoreCase(stableBeamName)) {
                ans = a1.time;
                break;
            }
        }


        return ans;
    }

    public long getStableBeamStop() {

//   return -1 is not define
//   return 0 if in stable beams
//   
        long sbs = getStableBeamStart();
        if (sbs == -1) {
            return -1;
        }
        int idx = -1;

        for (int i = beamModeHist.size() - 1; i >= 0; i--) {

            strTS a1 = beamModeHist.get(i);
            if (a1.value.equalsIgnoreCase(stableBeamName)) {
                idx = i;
                break;
            }
        }


        if (idx < 0) {
            return -1;
        }
        if (idx == (beamModeHist.size() - 1)) {  //last entry
            if (endedTime < 0) {  // fill is still active
                return 0;    // going on
            } else {
                return endedTime;
            }
        }
        strTS a2 = beamModeHist.get(idx + 1);

        long sbstop = a2.time;


        return sbstop;

    }

    public int getStableBeamDuration() {
        long sum = 0;


        if (beamModeHist.size() == 0) return 0;

        if (beamModeHist.size() == 1) {
            strTS a1 = beamModeHist.get(0);
            if (a1.value.equalsIgnoreCase(stableBeamName) && (endedTime > 0)) {
                long dt = endedTime - a1.time;
                int ans = (int) (dt / 1000);
                return ans;
            } else if (a1.value.equalsIgnoreCase(stableBeamName) && (endedTime < 0)) {
                long now = (new Date()).getTime();
                long dt = now - a1.time;
                int ans = (int) (dt / 1000);
                return ans;
            } else {
                return 0;
            }

        }

        for (int i = 0; i < (beamModeHist.size() - 1); i++) {

            strTS a1 = beamModeHist.get(i);
            strTS a2 = beamModeHist.get(i + 1);
            if (a1.value.equalsIgnoreCase(stableBeamName)) {
                sum = sum + (a2.time - a1.time);
            }
        }

        strTS a3 = beamModeHist.get(beamModeHist.size() - 1); // last entry

        if (a3.value.equalsIgnoreCase(stableBeamName) && (endedTime > 0)) {

            sum = sum + (endedTime - a3.time);
        }

        if (a3.value.equalsIgnoreCase(stableBeamName) && (endedTime == -1)) {
            long now = (new Date()).getTime();

            sum = sum + (now - a3.time);
        }

        int ans = (int) (sum / 1000);
        return ans;

    }

}

