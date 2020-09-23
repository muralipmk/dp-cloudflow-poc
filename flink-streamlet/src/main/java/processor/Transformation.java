package processor;

import cloudflow.flink.FlinkStreamlet;
import cloudflow.flink.FlinkStreamletLogic;
import cloudflow.streamlets.StreamletShape;
import cloudflow.streamlets.avro.AvroInlet;
import cloudflow.streamlets.avro.AvroOutlet;
import datamodel.customer.Customer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;

public class Transformation extends FlinkStreamlet {

    // 1. Create inlets and outlets
    transient AvroInlet<Customer> in= AvroInlet.<Customer>create("in",Customer.class);
    transient AvroOutlet<Customer> out= AvroOutlet.<Customer>create("out",Customer.class);

    // 2. Define the shape of the streamlet
    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithInlets(in).withOutlets(out);
    }

    // 3. Override createLogic to provide StreamletLogic
    @Override
    public FlinkStreamletLogic createLogic() {
        return new FlinkStreamletLogic(getContext()) {

            @Override
            public void buildExecutionGraph() {
                DataStream<Customer> customerDataStream= readStream(in, Customer.class);

                DataStream<Customer> transformedDataStream= customerDataStream
                        .map(new WordsCapitalizer())
                        .returns(Customer.class);

                writeStream(out,transformedDataStream,Customer.class);

            }
        };
    }
}
