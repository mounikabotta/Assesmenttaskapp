package com.mouni.assesmenttaskapp.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :userId  " )
    LiveData<User> getUserById(int userId);

    @Query("UPDATE users SET localImagePath = :localImagePath WHERE id = :userId")
    void updateLocalImagePath(int userId, String localImagePath);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1 " )
    User getUserByIdSync(int userId);}


