package ingress;

import cloudflow.flink.FlinkStreamlet;
import cloudflow.flink.FlinkStreamletLogic;
import cloudflow.streamlets.StreamletShape;
import cloudflow.streamlets.avro.AvroOutlet;
import datamodel.customer.Customer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class RawEventIngress extends FlinkStreamlet {
    AvroOutlet<Customer> out = AvroOutlet.create("out", Customer.class);

    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithOutlets(out);
    }

    @Override
    public FlinkStreamletLogic createLogic() {
        return new FlinkStreamletLogic(getContext()){

            @Override
            public void buildExecutionGraph() {

                Properties kafkaConsumerProperties= new Properties();
                //Change the bootstrap.server address
                kafkaConsumerProperties.put("bootstrap.servers", "localhost:9092");

                //Create Flink Kafka Consumer to add as a source.
                FlinkKafkaConsumer<String> flinkKafkaConsumer= new FlinkKafkaConsumer<>(
                        "raw-events",
                        new SimpleStringSchema(),
                        kafkaConsumerProperties
                );

                //Add kafka as a source and transform the ingress string data to Customer type.
                DataStream<Customer> customerDataStream= context().env().getJavaEnv()
                        .addSource(flinkKafkaConsumer)
                        .map(new StringToCustomer())
                        .returns(Customer.class);

                //Write datastream to outlet.
                writeStream(out,customerDataStream,Customer.class);
            }
        };
    }
}
