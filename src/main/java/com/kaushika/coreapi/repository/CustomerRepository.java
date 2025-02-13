package com.kaushika.coreapi.repository;

import com.kaushika.coreapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Custom query methods if needed
}