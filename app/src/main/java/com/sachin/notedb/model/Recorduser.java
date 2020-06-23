package com.sachin.notedb.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sachin.util.Constants;

import java.io.Serializable;

/**
 * Created by sachin suthar 23 june 2020.
 */

@Entity(tableName = Constants.TABLE_NAME_RECORD)
public class Recorduser implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long record_id;

    @ColumnInfo(name = "_content")
    // column name will be "note_content" instead of "content" in table
    private String content;
    @ColumnInfo(name = "_image")
    // column name will be "note_content" instead of "content" in table
    private String image;

    private String title;

    private boolean ischeck;

    public Recorduser(String content, String title,String image) {
        this.content = content;
        this.title = title;
        this.image = image;
    }

    @Ignore
    public Recorduser() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getRecord_id() {
        return record_id;
    }

    public void setRecord_id(long record_id) {
        this.record_id = record_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recorduser)) return false;

        Recorduser note = (Recorduser) o;

        if (record_id != note.record_id) return false;
        return title != null ? title.equals(note.title) : note.title == null;
    }

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    @Override
    public int hashCode() {
        int result = (int) record_id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "record_id=" + record_id +
                ", content='" + content + '\'' +
                ", title='" + title +
                ", image='" + image +
                '}';
    }
}
