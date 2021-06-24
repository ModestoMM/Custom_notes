package com.modesto.customnotes.Models;

public class Organizer {

    private String id;
    private String id_Users;
    private String Name_Category;
    private Long timestamp;

    public Organizer() {
    }

    public Organizer(String id, String id_Users, String name_Category, Long timestamp) {
        this.id = id;
        this.id_Users = id_Users;
        Name_Category = name_Category;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_Users() {
        return id_Users;
    }

    public void setId_Users(String id_Users) {
        this.id_Users = id_Users;
    }

    public String getName_Category() {
        return Name_Category;
    }

    public void setName_Category(String name_Category) {
        Name_Category = name_Category;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
