package com.example.a2023_12_15_restapiqrcode;

public class Person {
    private int id;
    private String name;
    private String grade;

    public Person(int id, String name, String grade){
        this.id = id;
        this.grade = grade;
        this.name = name;
    }
    public int getId(){ return id; }

    public void setId(int id) {this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
