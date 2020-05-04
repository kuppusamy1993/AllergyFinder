package com.kuppu.allergyfinder.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kuppu.allergyfinder.AllergyDao;
import com.kuppu.allergyfinder.AllergyDatabase;
import com.kuppu.allergyfinder.AllergyNote;

public class EditAllergyViewModel extends AndroidViewModel {
    private String TAG=this.getClass().getSimpleName();
    private AllergyDao allergyDao;
    private AllergyDatabase db;
    public EditAllergyViewModel(@NonNull Application application) {
        super(application);
        Log.i(TAG,"Edit ViewModel");
        db=AllergyDatabase.getDatabase(application);
        allergyDao=db.allergyDao();
    }
    public LiveData<AllergyNote> getNote(String noteId) {
        return allergyDao.getAllergyNote(noteId);
    }
}
