package testdatagenerator;

import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import datamodel.customer.Customer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import utils.CustomerToJsonString;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class GenerateCustomerData<U, V> {
    private String topics= "raw-events";
    private KafkaProducer<U, V> eventProducer= null;

    public void initializeProducer(){
        Properties props= new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        eventProducer = new KafkaProducer<U, V>(props);
    }

    public void sendMessage(U key, V value){
        ProducerRecord<U, V> producerRecord= new ProducerRecord<>(topics, key, value);
        System.out.println(value);
        eventProducer.send(producerRecord);
        eventProducer.flush();
    }

    public static void main(String[] args) throws IOException {
        int NO_OF_RECORDS= 5;
        GenerateCustomerData<String, String> eventProducer= new GenerateCustomerData<>();
        eventProducer.initializeProducer();

        Faker faker= new Faker();

        for(int i= 0; i < NO_OF_RECORDS; i++){
            Customer customer= Customer.newBuilder()
                    .setCustomerId(new Random().nextLong())
                    .setCustomerName(faker.name().name())
                    .setCustomerLastName(faker.name().name())
                    .setCustomerAge(new RandomService().nextInt(12, 55))
                    .build();

            eventProducer.sendMessage(String.valueOf(customer.getCustomerId()), CustomerToJsonString.convertAvroToJson(customer));
        }
    }
}
