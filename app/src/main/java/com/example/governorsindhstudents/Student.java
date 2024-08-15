package com.example.governorsindhstudents;

public class Student {
    private String studentName;
    private String rollNo;
    private String cnic;
    private String phoneNo;
    private String email;
    private String password;

    // Constructor
    public Student() {
    }

    public Student(String studentName, String rollNo, String cnic, String phoneNo, String email, String password) {
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.cnic = cnic;
        this.phoneNo = phoneNo;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
