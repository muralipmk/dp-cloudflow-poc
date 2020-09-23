package egress;

import cloudflow.flink.FlinkStreamlet;
import cloudflow.flink.FlinkStreamletLogic;
import cloudflow.streamlets.StreamletShape;
import cloudflow.streamlets.avro.AvroInlet;
import datamodel.customer.Customer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import utils.CustomerToJsonString;
import utils.SimpleStringSerialization;

import java.util.Properties;

public class TransformedEventEgress extends FlinkStreamlet {

    AvroInlet<Customer> in = AvroInlet.create("in", Customer.class);
    @Override
    public FlinkStreamletLogic createLogic() {
        return new FlinkStreamletLogic(getContext()) {

            @Override
            public void buildExecutionGraph() {
                DataStream<Customer> transformedDataStream = readStream(in, Customer.class);

                DataStream<String> stringDataStream= transformedDataStream
                        .map(CustomerToJsonString::convertAvroToJson)
                        .returns(String.class);

                Properties kafkaProducerProperties= new Properties();
                //Change the bootstrap.server address
                kafkaProducerProperties.put("bootstrap.servers", "localhost:9092");

                //Uncomment below lines to check the data is reaching to this streamlet and comment addsink.
                //transformedDataStream.print("Received Records");
                //stringDataStream.print("String Records: ");
                String topicName = "qualified-events";

                FlinkKafkaProducer<String> kafkaSink= new FlinkKafkaProducer<>(
                        topicName,
                        new SimpleStringSerialization(topicName),
                        kafkaProducerProperties,
                        FlinkKafkaProducer.Semantic.EXACTLY_ONCE
                );

                //When the producer code is introduced the streamlet is not running.
                stringDataStream.addSink(kafkaSink);
            }
        };
    }

    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithInlets(in);
    }
}
