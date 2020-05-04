package com.kuppu.allergyfinder;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AllergyDao {

@Insert
void insert(AllergyNote allergyNote);

@Query("SELECT * FROM AllergyNotes")
LiveData<List<AllergyNote>>getAllItem();

@Query("SELECT * FROM AllergyNotes WHERE id=:allergyId")
    LiveData<AllergyNote> getAllergyNote(String allergyId);

@Update
void update(AllergyNote allergyNote);

@Delete
int delete(AllergyNote  allergyNote);

}
