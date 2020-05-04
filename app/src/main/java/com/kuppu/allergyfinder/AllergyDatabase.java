package com.kuppu.allergyfinder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = AllergyNote.class,version = 1,exportSchema = false)
public abstract class AllergyDatabase extends RoomDatabase {
    public abstract AllergyDao allergyDao();

    public static volatile AllergyDatabase allergyinstance;

  public   static AllergyDatabase getDatabase( Context context){
        if (allergyinstance==null){
            synchronized (AllergyDatabase.class){
                if(allergyinstance==null){
                    allergyinstance= Room.databaseBuilder(context.getApplicationContext(),AllergyDatabase.class,"allergy database")
                            .build();
                }
            }
        }
        return allergyinstance;
    }

}
