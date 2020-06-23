package com.sachin.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sachin.roomdb.model.Recorduser;
import com.sachin.util.Constants;

import java.util.List;

/**
 * Created by sachin suthar 23 june 2020.
 */

@Dao
public interface RecordDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_RECORD)
    List<Recorduser> getrecords();

    @Insert
    long insertRecorduser(Recorduser recorduser);


}
