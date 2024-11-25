/*************
 * cil
 **************/
/*
 *  Utility methods to process DipData structures
 *
 */
package alice.dip;

import cern.dip.DipData;
import cern.dip.DipTimestamp;
import cern.dip.TypeMismatch;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Util {

    public static NumberFormat nf = NumberFormat.getInstance();
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");


    // DIP DATA TYPES
    // TYPE_NULL      0
    // TYPE_BOOLEAN   1     TYPE_BOOLEAN_ARRAY        10
    // TYPE_BYTE      2     TYPE_BYTE_ARRAY           20
    // TYPE_SHORT     3     TYPE_SHORT_ARRAY          30
    // TYPE_INT       4     TYPE_INT_ARRAY            40
    // TYPE_LONG      5     TYPE_LONG_ARRAY           50
    // TYPE_FLOAT     6     TYPE_FLOAT_ARRAY          60
    // TYPE_DOUBLE    7     TYPE_DOUBLE_ARRAY         70
    // TYPE_STRING    8     TYPE_STRING_ARRAY         80


    public static float meanTS(ArrayList<floatTS> tsa) {

        int N = tsa.size();
        float ans = -1;

        if (N == 0) {
            ans = -1;
        } else if (N == 1) {
            floatTS t = tsa.get(0);
            ans = t.value;
        } else {
            double sum = 0.0;

            for (int i = 0; i < (tsa.size() - 1); i++) {
                floatTS t1 = tsa.get(i);
                floatTS t2 = tsa.get(i + 1);
                sum = sum + (t1.value + t2.value) * ((double) (t2.time - t1.time));
            }
            floatTS ts = tsa.get(0);
            floatTS te = tsa.get(tsa.size() - 1);

            double x = sum * 0.5 / ((double) (te.time - ts.time));
            ans = (float) x;
        }

        AliDip2BK.log(1, "Util.meanTS", " N=" + N + " mean value =" + ans);
        return ans;

    }

    public static String parseDipMess(String parameter, DipData data) {


        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        boolean ok = true;

        DipTimestamp dts = data.extractDipTime();
        long ldts = dts.getAsMillis();
        Date dtime = new Date(ldts);

        String ans = df.format(dtime) + " " + parameter + "::";

        if (data.isPrimitive()) {


            try {
                if (data.getValueType() == DipData.TYPE_INT) {
                    int value = data.extractInt();
                    ans = ans + value;
                } else if (data.getValueType() == DipData.TYPE_FLOAT) {
                    float value = data.extractFloat();
                    ans = ans + nf.format(value);
                } else if (data.getValueType() == DipData.TYPE_DOUBLE) {
                    double value = data.extractFloat();
                    ans = ans + nf.format(value);
                } else if (data.getValueType() == DipData.TYPE_STRING) {
                    String value = data.extractString();
                    ans = ans + value;

                } else if (data.getValueType() == DipData.TYPE_BOOLEAN) {
                    boolean value = data.extractBoolean();
                    ans = ans + value;

                } else if (data.getValueType() == DipData.TYPE_LONG) {
                    long value = data.extractLong();
                    ans = ans + value;
                } else {
                    AliDip2BK.log(4, "Util.parseDipMess", " ERROR primitive type param=" + parameter + " DIIFERENT data TYPE=" + data.getValueType());
                    ok = false;
                }
            } catch (TypeMismatch e) {

                AliDip2BK.log(4, "Util.parseDipMess", " ERROR primitive type param=" + parameter + " TYPE_MISMATCH=" + e);

                //e.printStackTrace();
                ok = false;


            }
        } else {

            try {
                String[] tlist = data.getTags();

                for (int i = 0; i < tlist.length; i++) {
                    int dtype = data.getValueType(tlist[i]);


                    if (dtype == DipData.TYPE_INT) {

                        int v = data.extractInt(tlist[i]);
                        ans = ans + " " + tlist[i] + "=" + v;
                    } else if (dtype == DipData.TYPE_BOOLEAN) {

                        boolean v = data.extractBoolean(tlist[i]);
                        ans = ans + " " + tlist[i] + "=" + v;

                    } else if (dtype == DipData.TYPE_FLOAT) {

                        float v = data.extractFloat(tlist[i]);
                        ans = ans + " " + tlist[i] + "=" + nf.format(v);
                    } else if (dtype == DipData.TYPE_DOUBLE) {

                        double v = data.extractDouble(tlist[i]);
                        ans = ans + " " + tlist[i] + "=" + nf.format(v);
                    } else if (dtype == DipData.TYPE_LONG) {

                        long v = data.extractLong(tlist[i]);
                        if (tlist[i].toLowerCase().contains("acqstamp")) {
                            Date d1 = new Date(v / 1000000);

                            ans = ans + " " + tlist[i] + "=" + df.format(d1);
                        } else {
                            ans = ans + " " + tlist[i] + "=" + v;
                        }

                    } else if (dtype == DipData.TYPE_STRING) {

                        String v = data.extractString(tlist[i]);
                        ans = ans + " " + tlist[i] + "=" + v;
                    } else if (dtype == DipData.TYPE_BYTE_ARRAY) {
                        System.out.println(" TYPE BYTE_ARRAY NO IMPLEMENT");

                    } else if (dtype == DipData.TYPE_FLOAT_ARRAY) {


                        float[] va = data.extractFloatArray(tlist[i]);

                        ans = ans + " " + tlist[i] + "[" + va.length + "]=";
                        for (int k = 0; k < va.length; k++) {
                            ans = ans + va[k] + ";";
                        }


                    } else if (dtype == DipData.TYPE_DOUBLE_ARRAY) {
                        System.out.println(" TYPE DOUBLE_ARRAY NO IMPLENET");

                    } else if (dtype == DipData.TYPE_STRING_ARRAY) {
                        System.out.println(" TYPE STRING_ARRAY NO IMPLEMENT");

                    } else if (dtype == DipData.TYPE_NULL) {
                        System.out.println(" TYPE NULL");

                    } else if (dtype == DipData.TYPE_INT_ARRAY) {


                        int[] va = data.extractIntArray(tlist[i]);

                        ans = ans + " " + tlist[i] + "[" + va.length + "]=";
                        for (int k = 0; k < va.length; k++) {
                            ans = ans + va[k] + ";";
                        }
                    } else if (dtype == DipData.TYPE_LONG_ARRAY) {
                        System.out.println(" TYPE LONG_ARRAY NO IMPLENET");
                    } else {
                        ok = false;
                        AliDip2BK.log(4, "Util.parseDipMess", " ERROR NonPrimitive type param=" + parameter + " DIIFERENT data TYPE=" + data.getValueType());

                    }
                }

            } catch (Exception e) {
                AliDip2BK.log(4, "Util.parseDipMess", " ERROR NonPrimitive type param=" + parameter + " TYPE_MISMATCH=" + e);
                // TODO Auto-generated catch block
                //e.printStackTrace();
                ok = false;
            }
        }


        if (!ok) {
            // System.out.println ( "Utils->parseDipMess ERROR returns NULL");
            return null;
        } else {
            //  System.out.println ( "Utils->parseDipMess returns =="+ ans);
            return ans;
        }

    }
}
