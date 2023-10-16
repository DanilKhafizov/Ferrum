package com.khafizov.ferrum.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


    @Entity(tableName = "User")
    public class User {
        @PrimaryKey(autoGenerate = true)
        private int id;
        private String name;
        private String surname;
        private String email;

        public User() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }



        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

