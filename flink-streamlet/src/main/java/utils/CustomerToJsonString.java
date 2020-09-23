package utils;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomerToJsonString {

    public static String convertAvroToJson(SpecificRecord record) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EncoderFactory encoderFactory= new EncoderFactory();
        JsonEncoder jsonEncoder= encoderFactory.jsonEncoder(record.getSchema(), outputStream);
        DatumWriter<SpecificRecord> specificRecordDatumWriter= new SpecificDatumWriter<>(record.getSchema());
        specificRecordDatumWriter.write(record, jsonEncoder);
        jsonEncoder.flush();
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
