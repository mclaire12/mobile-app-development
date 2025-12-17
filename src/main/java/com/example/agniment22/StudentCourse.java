package com.example.agniment22;

public class StudentCourse {
    private long id;
    private long studentIdFk;
    private String courseName;
    private int grade;
    private String courseDate;

    public StudentCourse() {
    }

    public StudentCourse(long studentIdFk, String courseName, int grade, String courseDate) {
        this.studentIdFk = studentIdFk;
        this.courseName = courseName;
        this.grade = grade;
        this.courseDate = courseDate;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudentIdFk() {
        return studentIdFk;
    }

    public void setStudentIdFk(long studentIdFk) {
        this.studentIdFk = studentIdFk;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }
}
