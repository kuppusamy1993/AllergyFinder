package com.kuppu.allergyfinder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Database table name
@Entity(tableName = "AllergyNotes")
public class AllergyNote {
@PrimaryKey
@NonNull
    private String id;

   @ColumnInfo(name = "name")
   @NonNull
private String mName;


    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return this.mName;
    }
    public AllergyNote(String id, @NonNull String name) {
        this.id = id;
        this.mName = name;
    }
}
