package com.example.fleetsync.model;

public class UserDto {
    private int id;
    private String password;
    private String confirmPassword;
    private String email;
    private String username;
    private String fullname;
    private String phonenumber;
    private String profilePicture;

    public UserDto() {}

    public UserDto(int id, String fullname, String email, String username) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.username = username;
    }
    
    public UserDto(int id, String fullname, String email, String profilePicture, String phonenumber) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.profilePicture = profilePicture;
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
}
