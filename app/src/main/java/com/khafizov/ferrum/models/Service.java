package com.khafizov.ferrum.models;


public class Service {
    private String id;
    private String nameService;
    private String employee1;
    private String image;
    private String vk;

    public Service() {}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }


    public String getNameService() {
        return nameService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public String getEmployee1() {
        return employee1;
    }

    public void setEmployee1(String employee1) {
        this.employee1 = employee1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}