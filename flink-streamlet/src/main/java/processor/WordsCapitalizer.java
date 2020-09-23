package processor;

import datamodel.customer.Customer;
import org.apache.flink.api.common.functions.MapFunction;

public class WordsCapitalizer implements MapFunction<Customer,Customer> {

    @Override
    public Customer map(Customer value) throws Exception {

        Customer customer = new Customer(
                value.getCustomerId(),
                value.getCustomerName().toUpperCase(),
                value.getCustomerLastName().toUpperCase(),
                value.getCustomerAge()
        );
        return customer;
    }
}
