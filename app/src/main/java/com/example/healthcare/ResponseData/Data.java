package com.example.healthcare.ResponseData;

import java.util.List;

public class Data {
    private String token;
    private String isNewUser;
    private Integer userId;
    private Integer clinicianId;
    private String userRole;
    private String fullName;


    private List<EnrolledProduct> enrolledProducts;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClinicianId() {
        return clinicianId;
    }

    public void setClinicianId(int clinicianId) {
        this.clinicianId = clinicianId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<EnrolledProduct> getEnrolledProducts() {
        return enrolledProducts;
    }

    public void setEnrolledProducts(List<EnrolledProduct> enrolledProducts) {
        this.enrolledProducts = enrolledProducts;
    }
}
