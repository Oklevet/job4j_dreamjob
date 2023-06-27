package ru.job4j.dreamjob.model;

import java.util.Objects;

public class Example {
    private int id;
    private String link;
    private String controller;

    public Example(int id, String link, String controller) {
        this.id = id;
        this.link = link;
        this.controller = controller;
    }

    public String getLink() {
        return link;
    }

    public String getController() {
        return controller;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Example example = (Example) o;
        return id == example.id && Objects.equals(link, example.link) && Objects.equals(controller, example.controller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link, controller);
    }
}
