package com.khafizov.ferrum.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE email = :userEmail LIMIT 1")
    User getUserByEmail(String userEmail);

    @Query("SELECT * FROM User ORDER BY id DESC LIMIT 1")
    User getLastRegisteredUser();

    @Update
    void updateUser(User user); // добавлено
}
