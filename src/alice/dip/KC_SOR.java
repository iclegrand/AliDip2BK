/*************
 * cil
 **************/

package alice.dip;


import alice.dip.AlicePB.NewStateNotification;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class KC_SOR implements Runnable {
    Properties properties;
    ProcData process;

    public int NoMess = 0;
    public boolean status = true;


    public KC_SOR(ProcData process) {

        String grp_id = AliDip2BK.KAFKA_group_id;
        this.process = process;


        properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AliDip2BK.bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());

        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grp_id);
        // properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");


        // properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AlicePB.class);


        Thread t = new Thread(this);
        t.start();

    }

    public void run() {

        try (//creating consumer  
             KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<String, byte[]>(properties)) {
            //Subscribing
            consumer.subscribe(Collections.singletonList(AliDip2BK.KAFKAtopic_SOR));

            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, byte[]> record : records) {

                    byte[] cucu = record.value();

                    NoMess = NoMess + 1;

                    try {
                        NewStateNotification info = NewStateNotification.parseFrom(cucu);
                        AliDip2BK.log(1, "KC_SOR.run", "New Kafka mess; partition=" + record.partition() + " offset=" + record.offset() + " L=" + cucu.length + " RUN=" + info.getEnvInfo().getRunNumber() + "  " + info.getEnvInfo().getState() + " ENVID = " + info.getEnvInfo().getEnvironmentId());


                        long time = info.getTimestamp();
                        int rno = info.getEnvInfo().getRunNumber();

                        process.newRunSignal(time, rno);
                    } catch (InvalidProtocolBufferException e) {
                        AliDip2BK.log(4, "KC_SOR.run", "ERROR pasing data into obj e=" + e);
                        status = false;
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
                //consumer.commitAsync();
            }


        }

    }


}
