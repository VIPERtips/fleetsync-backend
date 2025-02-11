package com.example.fleetsync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Vehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int vehicleId;

	@Column(unique = true)
	private String vin;
	private String make, model, licencePlate;
	private int year;
	private Status status;

	@ManyToOne
	@JoinColumn(name = "company_id", referencedColumnName = "companyId", nullable = false)
	private Company company;

	public Vehicle() {
		// TODO Auto-generated constructor stub
	}

	public Vehicle(String vin, String make, String model, String licencePlate, int year, Status status,
			Company company) {
		this.vin = vin;
		this.make = make;
		this.model = model;
		this.licencePlate = licencePlate;
		this.year = year;
		this.status = status;
		this.company = company;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getLicencePlate() {
		return licencePlate;
	}

	public void setLicencePlate(String licencePlate) {
		this.licencePlate = licencePlate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	
}
