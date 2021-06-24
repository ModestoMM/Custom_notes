package com.modesto.customnotes.Models;

public class ImageR {

    private String id;
    private String uri;
    private String id_Note;
    private Long timestamp;

    public ImageR() {
    }

    public ImageR(String id, String uri, String id_Note, Long timestamp) {
        this.id = id;
        this.uri = uri;
        this.id_Note = id_Note;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId_Note() {
        return id_Note;
    }

    public void setId_Note(String id_Note) {
        this.id_Note = id_Note;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
