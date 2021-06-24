package com.modesto.customnotes.Models;

public class Notes {
    private String id_Organizador;
    private String id_Users;
    private String id;
    private String title;
    private String Informacion;
    private Long timestamp;

    public Notes(){
    }

    public Notes(String id_Organizador, String id_Users, String id, String title, String informacion, Long timestamp) {
        this.id_Organizador = id_Organizador;
        this.id_Users = id_Users;
        this.id = id;
        this.title = title;
        Informacion = informacion;
        this.timestamp = timestamp;
    }

    public String getId_Organizador() {
        return id_Organizador;
    }

    public void setId_Organizador(String id_Organizador) {
        this.id_Organizador = id_Organizador;
    }

    public String getId_Users() {
        return id_Users;
    }

    public void setId_Users(String id_Users) {
        this.id_Users = id_Users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformacion() {
        return Informacion;
    }

    public void setInformacion(String informacion) {
        Informacion = informacion;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
