package com.khafizov.ferrum.models;

public class Trainers {
    private String id;
    private String nameTrainer;
    private String roleTrainer;
    private String imageTrainer;
    public Trainers() {}
    public String getNameTrainer() {
        return nameTrainer;
    }
    public void setNameTrainer(String nameTrainer) {
        this.nameTrainer = nameTrainer;
    }
    public String getRoleTrainer() {
        return roleTrainer;
    }
    public void setRoleTrainer(String roleTrainer) {
        this.roleTrainer = roleTrainer;
    }
    public String getImageTrainer() {
        return imageTrainer;
    }
    public void setImageTrainer(String imageTrainer) {
        this.imageTrainer = imageTrainer;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}