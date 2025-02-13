package com.example.fleetsync.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int companyId;
	@Column(unique = true)
	private String name;
	private int fleetSize;
	private String address;
	private String city;
	private String country;
	private double latitude; 
    private double longitude;

	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false, unique = true)
	private User user;
	
	@JsonIgnore
    @OneToMany(mappedBy = "company")
    private List<Vehicle> vehicles;

	public Company() {
	}

	public Company(String name, int fleetSize, String address, String city, String country, User user) {
		this.name = name;
		this.fleetSize = fleetSize;
		this.address = address;
		this.city = city;
		this.country = country;
		this.user = user;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFleetSize() {
		return fleetSize;
	}

	public void setFleetSize(int fleetSize) {
		this.fleetSize = fleetSize;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	
	

}
