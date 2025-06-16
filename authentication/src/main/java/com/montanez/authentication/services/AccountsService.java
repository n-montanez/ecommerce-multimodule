package com.montanez.authentication.services;

import java.util.ArrayList;
import java.util.List;

import com.montanez.authentication.data.CustomerDao;
import com.montanez.authentication.exceptions.auth.UserNotRegisteredException;
import com.montanez.authentication.models.auth.CreateCustomer;
import com.montanez.authentication.models.customer.Customer;
import com.montanez.authentication.models.customer.dto.CustomerInfo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountsService {

    @Inject
    CustomerDao customerDao;

    public List<CustomerInfo> getAllCustomers() {
        List<Customer> customers = customerDao.readAllCustomers();
        List<CustomerInfo> result = new ArrayList<>();

        for (Customer c : customers) {
            result.add(
                CustomerInfo.builder()
                .email(c.getEmail())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .dob(c.getDob())
                .build()
            );
        }

        return result;
    }

    public CustomerInfo getCustomerInfo(Long id) {
        Customer customer = customerDao.readCustomer(id);

        if (customer == null)
            throw new UserNotRegisteredException("Account not found");

        return CustomerInfo.builder()
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .dob(customer.getDob())
                .build();
    }

    public CustomerInfo updateCustomerInfo(Long id, CreateCustomer customerInfo) {
        Customer customer = customerDao.readCustomer(id);

        if (customer == null)
            throw new UserNotRegisteredException("Account not found");
        
        customer.setFirstName(customerInfo.getFirstName());
        customer.setLastName(customerInfo.getLastName());
        customer.setDob(customerInfo.getDob());

        customerDao.updateCustomer(customer);

        return CustomerInfo.builder()
            .firstName(customer.getFirstName())
            .lastName(customer.getLastName())
            .email(customer.getEmail())
            .dob(customer.getDob())
            .build();
    }
}
