/*************
 * cil
 **************/

/*
 *  Main Class
 *
 */

package alice.dip;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class AliDip2BK implements Runnable {


    public static String Version = "2.0  14-Nov-2023";
    String confFile = "AliDip2BK.properties";

    public static String DNSnode = "dipnsgpn1,dipnsgpn2";


    public static String[] endFillCases = {"CUCU"};

    public static boolean LIST_PARAM = false;
    static public String LIST_PARAM_PAT = "*";

    static public int DEBUG_LEVEL = 1;

    static public String OUTPUT_FILE = null;

    public static String BKURL = "http://aliecs-kafka-1.cern.ch:4000";

    public static boolean SAVE_PARAMETERS_HISTORY_PER_RUN = false;

    public static String KEEP_RUNS_HISTORY_DIRECTORY = null;
    public static String KEEP_FILLS_HISTORY_DIRECTORY = null;

    public static String KEEP_STATE_DIR = "STATE/";

    public static String bootstrapServers = "127.0.0.1:9092";
    public static String KAFKAtopic_SOR = "aliecs.env_state.RUNNING";
    public static String KAFKAtopic_EOR = "aliecs.env_leave_state.RUNNING";

    public static String KAFKA_group_id = "AliDip";


    public static String STORE_HIST_FILE_DIR = "HistFiles";
    public String DipParametersFile = null;

    public static boolean SIMULATE_DIP_EVENTS = false;

    public static SimpleDateFormat myDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");
    public static SimpleDateFormat logDateFormat = new SimpleDateFormat("dd-MM HH:mm:ss");

    public static double DIFF_ENERGY = 5;
    public static double DIFF_BETA = 0.001;
    public static double DIFF_CURRENT = 5;


    DipClient client;
    ProcData process;
    BKwriter dbw;
    KC_SOR kcs;
    KC_EOR kce;

    private final long startDate;
    private long stopDate;

    public static String ProgPath;

    public AliDip2BK() {

        startDate = (new Date()).getTime();

        ProgPath = getClass().getClassLoader().getResource(".").getPath();

        loadConf(confFile);

        showConfig();

        verifyDirs();

        dbw = new BKwriter();
        process = new ProcData(dbw);

        client = new DipClient(DipParametersFile, process);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        kcs = new KC_SOR(process);

        kce = new KC_EOR(process);


        shutdownProc();

        Thread t = new Thread(this);
        t.start();


    }


    public void run() {

        int stat_count = 0;

        for (; ; ) {

            try {
                Thread.sleep(10000);
                stat_count = stat_count + 10;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            if (stat_count >= 3600) {
                writeStat("StatHist.txt", false);
                stat_count = 0;
            }
        }
    }

    public void shutdownProc() {
        Runtime r = Runtime.getRuntime();
        r.addShutdownHook(new Thread() {
                              public void run() {
                                  log(4, "AliDip2BK", " Main class  ENTERS in Shutdown hook");
                                  client.closeSubscriptions();
                                  process.closeInputQueue();
                                  if (process.QueueSize() > 0) {
                                      for (int i = 0; i < 5; i++) {
                                          try {
                                              Thread.sleep(1000);
                                          } catch (InterruptedException ex) {
                                              Thread.currentThread().interrupt();
                                          }

                                          if (process.QueueSize() == 0) break;
                                      }
                                  }

                                  if (process.QueueSize() != 0) {
                                      log(4, "AliDip2BK Shutdown", " Data Proc queue is not EMPTY ! Close it anyway ");
                                  } else {
                                      log(2, "AliDip2BK Shutdown", " Data Proc queue is EMPTY and it was correctly closed  ");
                                  }
                                  process.saveState();
                                  writeStat("AliDip2BK.stat", true);
                                  dbw.close();
                              }
                          }
        );
    }

    public void showConfig() {
        String con = "*************************************************\n";

        con = con + "* \n";
        con = con + "* AkiDip2BK Version =" + Version + "\n";
        con = con + "* DIP/DIM =" + DNSnode + "\n";
        con = con + "* KAFKA Server = " + bootstrapServers + "\n";
        con = con + "* KAFKA Group ID=" + KAFKA_group_id + "\n";
        con = con + "* Bookkeeping URL =" + BKURL + "\n";
        con = con + "* \n";
        con = con + "*************************************************\n";

        System.out.println(con);

    }

    public void loadConf(String filename) {


        String input = ProgPath + "/" + filename;

        Properties prop = new Properties();


        try {
            prop.load(new FileInputStream(input));


            String dns1 = prop.getProperty("DNSnode");
            if (dns1 != null) {
                DNSnode = dns1;
            } else {
                log(4, "AliDip2BK.loadConf", " DNSnode is undefined in the conf file ! Use defult=" + DNSnode);
            }


            String para_file_name = prop.getProperty("DipDataProvidersSubscritionFile");

            if (para_file_name != null) {

                DipParametersFile = ProgPath + para_file_name;
            } else {
                log(4, "AliDip2BK.loadConf", " Dip Data Providers Subscription  file name is undefined in the conf file ");

            }

            String list_param = prop.getProperty("ListDataProvidersPattern");

            if (list_param != null) {

                LIST_PARAM = true;
                LIST_PARAM_PAT = list_param;


            } else {
                log(4, "AliDip2BK.loadConf ", "  List DIP Data Providers  Pattern is undefined ! The DIP broswer will not start ");

            }

            String debug_n = prop.getProperty("DEBUG_LEVEL");
            if (debug_n != null) {

                DEBUG_LEVEL = Integer.parseInt(debug_n);
                log(1, "AliDip2BK.loadConf ", "  Debug Level = " + DEBUG_LEVEL);
            }

            String out = prop.getProperty("DIP_SUBSCRIPTION_OUTPUT_FILE");
            if (out != null) {
                OUTPUT_FILE = out;
            }

            String keh = prop.getProperty("SAVE_PARAMETERS_HISTORY_PER_RUN");
            if (keh != null) {
                keh = keh.trim();
                SAVE_PARAMETERS_HISTORY_PER_RUN = keh.equalsIgnoreCase("Y");
                if (keh.equalsIgnoreCase("YES")) SAVE_PARAMETERS_HISTORY_PER_RUN = true;
                if (keh.equalsIgnoreCase("true")) SAVE_PARAMETERS_HISTORY_PER_RUN = true;

            }


            String kfhd = prop.getProperty("KEEP_FILLS_HISTORY_DIRECTORY");
            if (kfhd != null) {
                KEEP_FILLS_HISTORY_DIRECTORY = kfhd.trim();
            }

            String krhd = prop.getProperty("KEEP_RUNS_HISTORY_DIRECTORY");
            if (krhd != null) {
                KEEP_RUNS_HISTORY_DIRECTORY = krhd.trim();
            }


            String sde = prop.getProperty("SIMULATE_DIP_EVENTS");
            if (sde != null) {

                if (sde.equalsIgnoreCase("Y")) SIMULATE_DIP_EVENTS = true;
                if (sde.equalsIgnoreCase("YES")) SIMULATE_DIP_EVENTS = true;
                if (sde.equalsIgnoreCase("true")) SIMULATE_DIP_EVENTS = true;
            }

            String kgid = prop.getProperty("KAFKA_group_id");
            if (kgid != null) {

                KAFKA_group_id = kgid;
            }

            String kbs = prop.getProperty("bootstrapServers");
            if (kbs != null) {
                bootstrapServers = kbs;
            }

            String kt1 = prop.getProperty("KAFKAtopic_SOR");

            if (kt1 != null) {
                KAFKAtopic_SOR = kt1;
            }

            String kt2 = prop.getProperty("KAFKAtopic_EOR");

            if (kt2 != null) {
                KAFKAtopic_EOR = kt2;
            }


            String bkurl = prop.getProperty("BookkeepingURL");

            if (bkurl != null) {
                BKURL = bkurl;
            }


        } catch (IOException ex) {
            log(4, "AliDip2BK.loadCong", "Failed to access properties file " + ex);

        }


    }

    static public void log(int level, String module, String mess) {
        if (level >= DEBUG_LEVEL) {
            String date = logDateFormat.format((new Date()).getTime());

            System.out.println("#" + level + " [" + date + "] " + module + " =>" + mess);
        }
    }

    public void writeStat(String file, boolean final_report) {

        String full_file = ProgPath + AliDip2BK.KEEP_STATE_DIR + file;

        stopDate = (new Date()).getTime();
        double dur = (double) (stopDate - startDate) / (1000 * 60 * 60);

        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;

        String mess = "\n\n AliDip2BK Statistics \n";
        mess = mess + " Started :" + AliDip2BK.myDateFormat.format(startDate) + "\n";
        if (final_report) {
            mess = mess + " Stopped :" + AliDip2BK.myDateFormat.format(stopDate) + "\n";
        }
        mess = mess + " Duration [h]=" + dur + "\n";
        mess = mess + " Memory Used [MB]=" + usedMB + "\n";
        mess = mess + " No of DIP messages=" + process.statNoDipMess + "\n";
        mess = mess + " No of KAFKA  messages=" + process.statNoKafMess + "\n";
        mess = mess + " No of KAFKA SOR messages=" + kcs.NoMess + "\n";
        mess = mess + " No of KAFKA EOR messages=" + kce.NoMess + "\n";
        mess = mess + " No of new Fill messgaes =" + process.statNoNewFills + "\n";
        mess = mess + " No of new Run messgaes =" + process.statNoNewRuns + "\n";
        mess = mess + " No of end Run messages =" + process.statNoEndRuns + "\n";
        mess = mess + " No of Duplicated end Run messages =" + process.statNoDuplicateEndRuns + "\n";


        try {
            File of = new File(full_file);
            if (!of.exists()) {
                of.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(full_file, true));

            writer.write(mess);
            writer.close();
        } catch (IOException e) {

            AliDip2BK.log(4, "ProcData.writeStat", " ERROR writing file=" + full_file + "   ex=" + e);
        }

    }

    public void verifyDirs() {

        verifyDir(KEEP_RUNS_HISTORY_DIRECTORY);
        verifyDir(KEEP_FILLS_HISTORY_DIRECTORY);
        verifyDir(STORE_HIST_FILE_DIR);
        verifyDir(KEEP_STATE_DIR);


    }

    public void verifyDir(String name) {

        if (name != null) {

            File directory = new File(ProgPath + "/" + name);

            if (!directory.exists()) {
                directory.mkdir();
                AliDip2BK.log(2, "AliDip2BK->verifyDir", "created new Directory=" + name);
            }

        }
    }

    public static void main(String[] args) {

        @SuppressWarnings("unused")
        AliDip2BK service = new AliDip2BK();


    }

}

