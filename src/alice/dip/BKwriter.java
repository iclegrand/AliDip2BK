/*************
 * cil
 **************/

/*
 * This class is used to write the Dip information into the
 * Bookkeeping Data Base
 */
package alice.dip;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class BKwriter {


    //private static String path;
    HttpClient httpClient;


    public BKwriter() {
        //path = getClass().getClassLoader().getResource(".").getPath();

        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

    }

    public void X_InsertLHC(LhcInfoObj lhc) {


        boolean ok = XTestFillNo(lhc);

        if (ok) {
            AliDip2BK.log(3, "BKwriter.InserFill", "INSERT FILL ... BUT Fill No=" + lhc.fillNo + " is in BK ... trying to update record");
            XUpdateFill(lhc);
            return;
        }


        String furl = AliDip2BK.BKURL + "/api/lhcFills";
        String mydata = "{";
        mydata = mydata + "\n\"fillingSchemeName\":\"" + lhc.LHCFillingSchemeName + "\",";
        mydata = mydata + "\n\"beamType\":\"" + lhc.beamType + "\",";
        mydata = mydata + "\n\"fillNumber\":" + lhc.fillNo + ",";


        if (mydata.endsWith(",")) {
            mydata = mydata.substring(0, mydata.length() - 1);

        }
        mydata = mydata + "\n}";


        AliDip2BK.log(1, "BKwriter.InserFill", "FILL INSERT JSON request=\n" + mydata);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(furl))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(mydata))
                .build();


        HttpResponse<String> response;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            AliDip2BK.log(2, "BKwriter.InserFill", " INSERT new FILL No=" + lhc.fillNo + "  Code=" + response.statusCode());

        } catch (Exception e) {

            AliDip2BK.log(4, "BKwriter.InserFill", "HTTP ERROR=" + e);
            e.printStackTrace();
        }

    }


    public boolean XTestFillNo(LhcInfoObj lhc) {

        String furl = AliDip2BK.BKURL + "/api/lhcFills/" + lhc.fillNo;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(furl))
                .GET() // default
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String prob = "\"fillNumber\":" + lhc.fillNo;
                String ras = response.body();


                return ras.contains(prob);

            } else {

                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }


    }

    public boolean TestRunNo(int N) {

        String furl = AliDip2BK.BKURL + "/api/runs?filter[runNumbers]=" + N;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(furl))
                .GET() // default
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String prob = "\"runNumber\":" + N;
                String ras = response.body();

                return ras.contains(prob);

            } else {
                AliDip2BK.log(3, "BKwriter.TestRunNo", " Reguest error =" + response.statusCode() + " Mesage=" + response.body());
                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }


    }


    /*
     *  This method is used to update the RUN info entry
     */
    public void UpdateRun(RunInfoObj runObj) {

        int mod = 0;

        boolean okr = TestRunNo(runObj.RunNo);

        int k = -1;
        if (!okr) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                boolean ok = TestRunNo(runObj.RunNo);
                if (ok) {
                    k = i;
                    break;
                }
            } //for
        }

        if (k >= 0) AliDip2BK.log(1, "BKwriter.UpdateRun", "DELAY Loop Count=" + (k + 1));


        String mydata = "{";
        float be = runObj.getBeamEnergy();

        if (be > 0) {
            mydata = mydata + "\n\"lhcBeamEnergy\":" + be + ",";
            mod = mod + 1;
        }

        String bm = runObj.getBeamMode();
        if (bm != null) {
            mydata = mydata + "\n\"lhcBeamMode\":\"" + bm + "\",";
            mod = mod + 1;
        }


        float l3m = runObj.getL3_magnetCurrent();


        if (l3m < 0) l3m = 0;
        if (l3m >= 0) {
            mydata = mydata + "\n\"aliceL3Current\":" + l3m + ",";
            mod = mod + 1;
            String l3p = runObj.getL3_magnetPolarity();
            if ((l3p.length() > 2) && (l3m > 0)) {
                mydata = mydata + "\n\"aliceL3Polarity\":\"" + l3p + "\",";
            }
            mod = mod + 1;
        }
        float dm = runObj.getDipole_magnetCurrent();
        if (dm < 0) dm = 0;

        if (dm >= 0) {
            mydata = mydata + "\n\"aliceDipoleCurrent\":" + dm + ",";
            mod = mod + 1;
            String dip = runObj.getDipole_magnetPolarity();
            if ((dip.length() > 2) && (dm > 0)) {
                mydata = mydata + "\n\"aliceDipolePolarity\":\"" + dip + "\",";
            }

        }

        int fn = runObj.getFillNo();
        if (fn > 0) {
            mydata = mydata + "\n\"fillNumber\":" + fn + ",";
        }

        float bs = runObj.getLHCBetaStar();
        if (bs >= 0) {
            mydata = mydata + "\n\"lhcBetaStar\":" + bs + ",";
            mod = mod + 1;
        }


        if (mod == 0) {  // no updates to be done !
            AliDip2BK.log(3, "BKwriter.UpdateRun", "NO data to Update for Run=" + runObj.RunNo);
            return;
        }
        if (mydata.endsWith(",")) {
            mydata = mydata.substring(0, mydata.length() - 1);

        }
        mydata = mydata + "\n}";


        AliDip2BK.log(1, "BKwriter.UpdateRun", "RUN =" + runObj.RunNo + " UPDATE JSON request=\n" + mydata);

        String furl = AliDip2BK.BKURL + "/api/runs?runNumber=" + runObj.RunNo;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(furl))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(mydata))
                .build();


        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200) {
                AliDip2BK.log(2, "BKwriter.UpdateRun", "Succesful Update for RUN=" + runObj.RunNo);
            } else {
                AliDip2BK.log(3, "BKwriter.UpdateRun", "ERROR for RUN=" + runObj.RunNo + " Code=" + +response.statusCode() + " Message=" + response.body());
            }

        } catch (Exception e) {

            AliDip2BK.log(4, "BKwriter.UpdateRun", "ERROR Update for RUN=" + runObj.RunNo + "\n Exception=" + e);
            e.printStackTrace();
        }


    }
    /*
     *  This method is used when new updates are received on the current Fill
     *  The modified values are updated in the DB
     */


    public void XUpdateFill(LhcInfoObj cfill) {


        boolean ok = XTestFillNo(cfill);

        if (!ok) {
            AliDip2BK.log(4, "BKwriter.UPdate FILL", "Fill No=" + cfill.fillNo + " is NOT in BK ");
            return;
        }


        int mod = 0;

        String mydata = "{";

        long t1 = cfill.getStableBeamStart();

        if (t1 > 0) {
            mydata = mydata + "\n\"stableBeamsStart\":" + t1 + ",";
            mod = mod + 1;
        }

        long t2 = cfill.getStableBeamStop();
        if (t2 > 0) {
            mydata = mydata + "\n\"stableBeamsEnd\":" + t2 + ",";
            mod = mod + 1;
        }

        int sbd = cfill.getStableBeamDuration();

        if (sbd > 0) {
            mydata = mydata + "\n\"stableBeamsDuration\":" + sbd + ",";
            mod = mod + 1;
        }

        mydata = mydata + "\n\"fillingSchemeName\":\"" + cfill.LHCFillingSchemeName + "\",";

        mod = mod + 1;

        if (mod == 0) {  // no updates to be done !
            AliDip2BK.log(3, "BKwriter.UpdateFILL", "NO data to Update");
            return;
        }
        if (mydata.endsWith(",")) {
            mydata = mydata.substring(0, mydata.length() - 1);

        }
        mydata = mydata + "\n}";


        AliDip2BK.log(1, "BKwriter.UpdateFILL", "UPDATE FILL=" + cfill.fillNo + " JSON request=\n" + mydata);


        String furl = AliDip2BK.BKURL + "/api/lhcFills/" + cfill.fillNo;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(furl))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(mydata))
                .build();


        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            if ((response.statusCode() == 201)) {
                AliDip2BK.log(2, "BKwriter.UpdateFILL", "Succesful Update for FILL=" + cfill.fillNo);
            } else {
                AliDip2BK.log(3, "BKwriter.UpdateFILL", "ERROR for FILL=" + cfill.fillNo + " Code=" + +response.statusCode() + " Message=" + response.body());
            }

        } catch (Exception e) {

            AliDip2BK.log(4, "BKwriter.UpdateFILL", "ERROR Update for FILL=" + cfill.fillNo + "\n Exception=" + e);
            e.printStackTrace();
        }


    }




    /*
     *  This method is used to close  the connection to the DB . Used in the shutdown procedure
     */

    public void close() {

    }


}

