package com.pahwa.rideaway.Notification;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotiDao {

    @Query("Select * from notis")
    LiveData<List<NotiDetails>> getNotifications();

    @Insert
    void add(NotiDetails notiDetails);

    @Query("DELETE from notis")
    void deleteall();

    @Query("UPDATE  notis SET notiread=1")
    void markallasread();

    @Query("UPDATE notis SET notiread=1 WHERE uid= :id")
    void markasread(int id);

    @Query("SELECT * from notis where notiread=0")
    LiveData<List<NotiDetails>> getunread();

    @Query("SELECT * from notis where notiread=0")
    List<NotiDetails> getunread1();

}
