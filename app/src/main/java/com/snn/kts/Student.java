package com.snn.kts;

public class Student extends Participant {

    public String Department;
    public String schoolNumber;

    Student() {
    }
//fuck the police
    Student(String id, String name, String email, String phone, String department, String schoolNumber) {
        super(id, name, email, phone);
        Department = department;
        this.schoolNumber = schoolNumber;
    }
}
