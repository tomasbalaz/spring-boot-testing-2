package sk.balaz.springboottesting.customer;

import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        // 1. Phone number is taken
        // 2. if taken lets check if belongs to same customer
        // - 2.1 if yes return
        // - 2.2 thrown an exception
        // 3. Save customer
    }
}
