/*
 * Toy DIP server
 * It is used only for tests
 */

package alice.dip;

import cern.dip.*;

public class Server {


    public static void main(String[] args) throws DipException {
        // Create the publications

        class dpeh implements DipPublicationErrorHandler {

            @Override
            public void handleException(DipPublication arg0, DipException arg1) {

                System.out.println(" Dip Pub Error !");
            }
        }

        DipFactory dip = Dip.create("cilPublisher");
        dip.setDNSNode("192.168.1.127");
        DipPublication[] pub = new DipPublication[2];

        String[] itemName = new String[]{"test.item1", "test.item2"};
        for (int i = 0; i < 2; i++) {
            try {
                pub[i] = dip.createDipPublication(itemName[i], new dpeh());
            } catch (DipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // set up vars who's values are to be sent
        DipData data = dip.createDipData();
        int intVal = 1;
        float floatVal = 0.1f;
        // send data via DIP
        for (int i = 0; i < 200; i++) {

            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            try {
                data.insert("field1", intVal);
                data.insert("field2", floatVal);
                System.out.println("sending values " + intVal + " and " + floatVal);
                pub[0].send(intVal + 1, new DipTimestamp());
                pub[1].send(data, new DipTimestamp());
                intVal += 1;
                floatVal += 0.1;
            } catch (DipException e) {
                System.out.println("Failed to send data");
            }
        }
        // shutdown
        try {
            dip.destroyDipPublication(pub[0]);
        } catch (DipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dip.destroyDipPublication(pub[1]);

    }
}