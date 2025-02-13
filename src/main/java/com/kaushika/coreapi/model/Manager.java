package com.kaushika.coreapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "manager")
@Data
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {
    private String departmentCode;
}
