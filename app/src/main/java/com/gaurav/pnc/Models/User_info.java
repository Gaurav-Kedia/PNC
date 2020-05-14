package com.gaurav.pnc.Models;

public class User_info {
    private String name, phone, status, designation;

    public User_info() {
    }

    public User_info(String name, String phone, String status, String designation) {
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.designation = designation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
