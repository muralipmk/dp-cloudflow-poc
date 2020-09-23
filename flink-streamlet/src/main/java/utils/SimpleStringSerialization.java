package utils;

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SimpleStringSerialization implements KafkaSerializationSchema<String> {
    private String topic;

    public SimpleStringSerialization(String topic) {
        super();
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(String obj, Long timestamp) {
        return new ProducerRecord<byte[], byte[]>(topic, obj.getBytes());
    }

}
