package com.example.karthikfirebase;

public class UserModel {
    String Name,Mobile,Course,State;

    public UserModel(String name, String mobile, String course, String state) {
        Name = name;
        Mobile = mobile;
        Course = course;
        State = state;
    }

    public void setState(String state) {
        State = state;
    }

    public String getState() {
        return State;
    }

    public UserModel(String name, String mobile, String course) {
        Name = name;
        Mobile = mobile;
        Course = course;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getCourse() {
        return Course;
    }

    public void setCourse(String course) {
        Course = course;
    }
}
