package com.example.myprototype;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String title;
    private String description;
    private int priority;


    //CONSTRUCTOR
    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    //GETTER METHODS
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public Integer getId() {
        return id;
    }

    //SETTER
    public void setId(int id) {
        this.id = id;
    }
}
