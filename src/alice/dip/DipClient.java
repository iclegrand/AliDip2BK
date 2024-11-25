/*************
 * cil
 **************/


// Dip Client 
// Subscribe to  Dip data providers defined in the DipParametersFile
// Send the received information to ProcData for creating the Fill and Run data structures
//

package alice.dip;

import cern.dip.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//import cern.dip.dim.Dip;


public class DipClient implements Runnable {

    DipFactory dip;
    DipBrowser dipBrowser;
    long MAX_TIME_TO_UPDATE = 60;// in s

    int NP = 0;

    public int NoMess = 0;
    public boolean status = true;

    ProcData procData;
    HashMap<String, DipSubscription> SubscriptionMap = new HashMap<>();
    HashMap<String, DipData> DataMap = new HashMap<>();


    public DipClient(String DipParametersFile, ProcData procData) {

        readParamFile(DipParametersFile);

        initDIP();

        this.procData = procData;

        // verifyData();


        Thread t = new Thread(this);
        t.start();
    }


    public void run() {
        for (; ; ) {
            try {
                Thread.sleep(10000);
                //verifyData();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void initDIP() {
        // initialize the DIP client

        dip = Dip.create();
        dip.setDNSNode(AliDip2BK.DNSnode);

        //*
        // if configured to LIST data providers create a browser an list available  data providers
        //*
        if (AliDip2BK.LIST_PARAM) {
            AliDip2BK.log(1, "DipClient.initDP", " START DIP BROWSER patt=" + AliDip2BK.LIST_PARAM_PAT);
            dipBrowser = dip.createDipBrowser();
            list();
        }
        // Handles all data subscriptions
        GeneralDataListener handler = new GeneralDataListener();


        for (Map.Entry<String, DipSubscription> m : SubscriptionMap.entrySet()) {
            String k = m.getKey();

            try {
                DipSubscription subx = dip.createDipSubscription(k, handler);
                SubscriptionMap.put(k, subx);
            } catch (DipException e) {
                //e.printStackTrace();
                AliDip2BK.log(4, "DipClient.initDP", " error creating new subscription for param=" + k + " e=" + e);
            }


        }

        AliDip2BK.log(1, "DipClient.initDP", " Subscribed to " + SubscriptionMap.size() + " data provides");
        status = true;
    }

    // Used when the programs stops
    public void closeSubscriptions() {

        for (Map.Entry<String, DipSubscription> m : SubscriptionMap.entrySet()) {
            String k = m.getKey();
            DipSubscription subx = m.getValue();

            try {
                dip.destroyDipSubscription(subx);
            } catch (DipException e) {
                //e.printStackTrace();
                AliDip2BK.log(4, "DipClient.CloseSubscriptions", " error closing subscription for param=" + k + " e=" + e);
            }

        }
        SubscriptionMap.clear();
        AliDip2BK.log(1, "DipClient.CloseSubscriptions", " Succesfuly closed all DIP Subscriptions");
    }

    /**
     * handler for connect/disconnect/data reception events
     */
    class GeneralDataListener implements DipSubscriptionListener {
        /**
         * handle changes to subscribed to publications
         */
        public void handleMessage(DipSubscription subscription, DipData message) {

            String p_name = subscription.getTopicName();
            String ans = Util.parseDipMess(p_name, message);

            AliDip2BK.log(0, "DipClient", " new Dip mess from " + p_name + " " + ans);

            NoMess = NoMess + 1;

            procData.addData(p_name, ans, message);

        }

        /**
         * called when a publication subscribed to is available.
         *
         * @param arg0 - the subsctiption who's publication is available.
         */
        public void connected(DipSubscription arg0) {
            // AliDip2BK.log(3, "DpiClient.GeneralDataListener.connect", "Publication source available "+arg0);
        }

        /**
         * called when a publication subscribed to is unavailable.
         *
         * @param arg0 - the subsctiption who's publication is unavailable.
         * @param arg1 - string providing more information about why the publication is unavailable.
         */
        public void disconnected(DipSubscription arg0, String arg1) {
            AliDip2BK.log(4, "DipClient.GeneralDataListener.disconnect", "Publication source unavailable " + arg0 + "  " + arg1);
            status = false;
        }

        @Override
        public void handleException(DipSubscription arg0, Exception arg1) {
            AliDip2BK.log(4, "DipClient.GeneralDataListener.Exception ", "Exception= " + arg0 + " arg1=" + arg1);


        }
    }

    public void list() {
        String[] list = dipBrowser.getPublications(AliDip2BK.LIST_PARAM_PAT);
        AliDip2BK.log(1, "DipClient.list", " Size of the Data Providers =" + list.length);
        for (int i = 0; i < list.length; i++) {
            // System.out.println ( "  i="+i + " PROVIDER="+ list[i] );
            System.out.println(list[i]);
            try {
                String[] tags = dipBrowser.getTags(list[i]);
                if (tags.length > 1) {
                    for (int j = 0; j < tags.length; j++) {
                        System.out.println("   * j=" + j + " TAG=" + tags[j]);
                    }
                }
            } catch (DipException e) {

                e.printStackTrace();
            }

        }
    }

    public void verifyData() {

        for (Map.Entry<String, DipData> m : DataMap.entrySet()) {
            String k = m.getKey();

            DipData d1 = m.getValue();

            if (d1 == null) {
                DipSubscription ds = SubscriptionMap.get(k);
                ds.requestUpdate();
            } else {
                DipTimestamp ts = d1.extractDipTime();
                long tf = (new Date().getTime() - ts.getAsMillis());
                if (tf > MAX_TIME_TO_UPDATE * 1000) {
                    DipSubscription ds = SubscriptionMap.get(k);
                    ds.requestUpdate();
                }
            }
        }
    }


    public void readParamFile(String file_name) {

        File file = new File(file_name);
        BufferedReader br;
        String st;
        NP = 0;
        try {
            br = new BufferedReader(new FileReader(file));

            while ((st = br.readLine()) != null) {

                String pi = st.trim();
                if (!pi.startsWith("#")) {

                    if (!SubscriptionMap.containsKey(pi)) {
                        SubscriptionMap.put(pi, null);
                        NP = NP + 1;
                    } else {
                        AliDip2BK.log(3, "DipClient.readParam", " DUPLICATE Parameter =" + pi);

                    }
                }
            }
        } catch (IOException e) {
            AliDip2BK.log(4, "DipClient.readParam", " ERROR reading paramter file=" + file + "ex=" + e);
            //e.printStackTrace();
        }
        AliDip2BK.log(1, "DipClient.readParam", " Configuration:  number of parameters NP=" + NP);


    }

}