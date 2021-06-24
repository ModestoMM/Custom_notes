package com.modesto.customnotes.Models;

//Creamos el modelo que utilizaremos para nuestra logica a la hora de llamar a la base de datos el modelo Users sera de los usuarios
//Que esten registrados en la base de datos
public class Users {

    private String id;
    private String email;
    private String username;
    //Contendra la fecha en la que se creo el usuario
    private Long timestamp;

    public Users() {
    }

    public Users(String id, String email, String username, Long timestamp) {
        this.id = id;
        this.email = email;
        this.username = username;

        this.timestamp = timestamp;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
