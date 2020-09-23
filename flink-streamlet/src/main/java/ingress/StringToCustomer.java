package ingress;

import datamodel.customer.Customer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class StringToCustomer implements MapFunction<String, Customer> {
    ObjectMapper mapper= new ObjectMapper();

    @Override
    public Customer map(String value) throws Exception {
        JsonNode node= mapper.readValue(value, JsonNode.class);
        Customer customer= new Customer(
                node.get("customerId").asLong(),
                node.get("customerName").textValue(),
                node.get("customerLastName").textValue(),
                node.get("customerAge").asInt());
        return customer;
    }
}
