package com.amdocs.queryexec.utilities.model;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    private Long id;

    private String name;

    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return name;
    }

    public void setCourseName(String courseName) {
        this.name = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}