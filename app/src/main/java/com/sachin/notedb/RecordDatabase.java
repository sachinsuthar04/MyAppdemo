package com.sachin.notedb;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sachin.notedb.dao.RecordDao;
import com.sachin.notedb.model.Recorduser;
import com.sachin.util.Constants;

/**
 * Created by sachin suthar 23 june 2020.
 */
@Database(entities = {Recorduser.class}, version = 1)
public abstract class RecordDatabase extends RoomDatabase {

    public abstract RecordDao getNoteDao();


    private static RecordDatabase recordDatabase;

    // synchronized is use to avoid concurrent access in multithred environment
    public static /*synchronized*/ RecordDatabase getInstance(Context context) {
        if (null == recordDatabase) {
            recordDatabase = buildDatabaseInstance(context);
        }
        return recordDatabase;
    }

    private static RecordDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                RecordDatabase.class,
                Constants.DB_NAME).allowMainThreadQueries().build();
    }

    public void cleanUp() {
        recordDatabase = null;
    }
}