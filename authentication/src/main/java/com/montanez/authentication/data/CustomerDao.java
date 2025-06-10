package com.montanez.authentication.data;

import java.util.List;

import com.montanez.authentication.models.customer.Customer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class CustomerDao {

    @PersistenceContext(name = "users-jpa-unit")
    private EntityManager em;

    public void createCustomer(Customer customer) {
        em.persist(customer);
    }

    public void updateCustomer(Customer customer) {
        em.merge(customer);
    }

    public Customer readCustomer(Long id) {
        return em.find(Customer.class, id);
    }

    public List<Customer> readAllCustomers() {
        return em.createNamedQuery("Customer.findAll", Customer.class).getResultList();
    }

}
