package com.kaushika.coreapi.repository;

import com.kaushika.coreapi.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
}