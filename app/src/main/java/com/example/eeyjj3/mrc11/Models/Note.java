package com.example.eeyjj3.mrc11.Models;

/**
 * Created by eeyjj3 on 29/04/2018.
 * Each note contain a ID, a title and its description.
 */

public class Note {
    private String id,title,description;

    public Note(){
    }

    public Note(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
