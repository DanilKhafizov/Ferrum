package com.khafizov.ferrum.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "User")
    public class User {
        @PrimaryKey(autoGenerate = true)
        private int id;
        private String name;
        private String surname;
        private String email;
       private String birthday;
       private String phone;
       private String role;
       private String photoUrl;


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

   public String getBirthday() {
       return birthday;
   }

   public void setBirthday(String birthday) {
       this.birthday = birthday;
   }

   public String getPhone() {
       return phone;
   }

   public void setPhone(String phone) {
       this.phone = phone;
   }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

