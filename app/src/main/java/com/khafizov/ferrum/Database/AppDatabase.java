package com.khafizov.ferrum.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Employee.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract EmployeeDao employeeDao();

}

