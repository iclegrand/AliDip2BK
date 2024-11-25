/*************
 * cil
 **************/
/*
 * Keeps ALICE specific information
 */
package alice.dip;

public class AliceInfoObj {

    public float L3_magnetCurrent;
    public float Dipole_magnetCurrent;
    public String L3_polarity;
    public String Dipole_polarity;

    public AliceInfoObj() {
        L3_magnetCurrent = -1;
        Dipole_magnetCurrent = -1;
        L3_polarity = "?";
        Dipole_polarity = "?";

    }

    public AliceInfoObj clone() {
        AliceInfoObj n = new AliceInfoObj();
        n.L3_magnetCurrent = L3_magnetCurrent;
        n.Dipole_magnetCurrent = Dipole_magnetCurrent;
        n.L3_polarity = L3_polarity;
        n.Dipole_polarity = Dipole_polarity;

        return n;
    }

    public String toString() {
        String ans = "L3_Magnet_Current=";
        if (L3_magnetCurrent >= 0) {
            ans = ans + L3_magnetCurrent + " Polarity:" + L3_polarity;
        } else {
            ans = ans + "No Data";
        }
        ans = ans + " Dipole_Magnet_Current=";
        if (Dipole_magnetCurrent >= 0) {
            ans = ans + Dipole_magnetCurrent + " Polarity:" + Dipole_polarity;
        } else {
            ans = ans + "No Data";
        }
        return ans;
    }
}

