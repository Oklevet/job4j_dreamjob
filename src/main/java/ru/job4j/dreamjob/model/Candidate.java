package ru.job4j.dreamjob.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Candidate {
    private int id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private String workingPosition;
    private int salary;

    public Candidate(int id, String name, String description, String workingPosition, int salary) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.workingPosition = workingPosition;
        this.salary = salary;
    }

    public Candidate(String name, String description, String workingPosition, int salary) {
        this.name = name;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.workingPosition = workingPosition;
        this.salary = salary;
    }

    public Candidate(int id, String name, String description, LocalDateTime creationDate, String workingPosition, int salary) {
        super();
    }

    public Candidate() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getWorkingPosition() {
        return workingPosition;
    }

    public int getSalary() {
        return salary;
    }

    public void setWorkingPosition(String workingPosition) {
        this.workingPosition = workingPosition;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate candidate = (Candidate) o;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}