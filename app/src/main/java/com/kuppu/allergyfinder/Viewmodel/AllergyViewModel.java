package com.kuppu.allergyfinder.Viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kuppu.allergyfinder.AllergyDao;
import com.kuppu.allergyfinder.AllergyDatabase;
import com.kuppu.allergyfinder.AllergyNote;

import java.util.List;

public class AllergyViewModel extends AndroidViewModel {
    private   String TAG=this.getClass().getSimpleName();
    private AllergyDao allergyDao;
    private AllergyDatabase allergyDB;
    private LiveData<List<AllergyNote>>mAllitems;
    public AllergyViewModel(@NonNull Application application) {
        super(application);
        allergyDB=AllergyDatabase.getDatabase(application);
        allergyDao=allergyDB.allergyDao();
        mAllitems=allergyDao.getAllItem();
    }

    public void insert(AllergyNote allergyNote){
        new InsertAsyncTask(allergyDao).execute(allergyNote);
    }
    public void update(AllergyNote allergyNote){
        new UpdateAsyncTask(allergyDao).execute(allergyNote);
    }
    public void delete(AllergyNote allergyNote){
        new DeleteAsyncTask(allergyDao).execute(allergyNote);

    }
  public   LiveData<List<AllergyNote>>getAllItem(){
        return mAllitems;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG,"Viewmodel destroyed");

    }

    private class OperationsAsyncTask extends AsyncTask<AllergyNote,Void,Void> {
        AllergyDao mAsyncTaskDemo;
        OperationsAsyncTask(AllergyDao dao){
            this.mAsyncTaskDemo=dao;
        }

        @Override
        protected Void doInBackground(AllergyNote... allergyNotes) {
            return null;
        }
    }
    private class InsertAsyncTask extends OperationsAsyncTask{
        InsertAsyncTask(AllergyDao mallergyDao) {
            super(mallergyDao);
        }
        @Override
        protected Void doInBackground(AllergyNote... allergyNotes) {
            mAsyncTaskDemo.insert(allergyNotes[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends OperationsAsyncTask {

        public UpdateAsyncTask(AllergyDao mallergyDao) {
            super(mallergyDao);
        }

        @Override
        protected Void doInBackground(AllergyNote... allergyNotes) {
            mAsyncTaskDemo.update(allergyNotes[0]);
            return null;
        }
    }

    private class DeleteAsyncTask extends OperationsAsyncTask {

        public DeleteAsyncTask(AllergyDao mallergyDao) {
            super(mallergyDao);
        }

        @Override
        protected Void doInBackground(AllergyNote... allergyNotes) {
            mAsyncTaskDemo.delete(allergyNotes[0]);
            return null;
        }
    }

}
