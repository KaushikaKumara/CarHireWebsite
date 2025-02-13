package com.kaushika.coreapi.service;

import com.kaushika.coreapi.model.Manager;
import com.kaushika.coreapi.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    public Manager createNewAccount(Manager manager) {
        return managerRepository.save(manager);
    }
}