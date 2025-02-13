package com.kaushika.coreapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class RentalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rentalId;

    @Column(nullable = false)
    private LocalDate rentStartDate;

    @Column(nullable = false)
    private LocalDate rentEndDate;

    private String additionalService;
    private Double totalCost;

    // Relationships with other entities
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Constructors
    public RentalDetails() {
    }

    public RentalDetails(LocalDate rentStartDate, LocalDate rentEndDate, String additionalService, Double totalCost, Customer customer, Vehicle vehicle) {
        this.rentStartDate = rentStartDate;
        this.rentEndDate = rentEndDate;
        this.additionalService = additionalService;
        this.totalCost = totalCost;
        this.customer = customer;
        this.vehicle = vehicle;
    }

    // Getters and Setters
    public Integer getRentalId() {
        return rentalId;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public LocalDate getRentStartDate() {
        return rentStartDate;
    }

    public void setRentStartDate(LocalDate rentStartDate) {
        this.rentStartDate = rentStartDate;
    }

    public LocalDate getRentEndDate() {
        return rentEndDate;
    }

    public void setRentEndDate(LocalDate rentEndDate) {
        this.rentEndDate = rentEndDate;
    }

    public String getAdditionalService() {
        return additionalService;
    }

    public void setAdditionalService(String additionalService) {
        this.additionalService = additionalService;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    // Method to calculate total cost (example implementation)
    public void calculateTotalCost(double dailyRate, int numberOfDays, double additionalServiceCost) {
        this.totalCost = (dailyRate * numberOfDays) + additionalServiceCost;
    }
}