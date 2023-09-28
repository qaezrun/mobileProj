package com.example.apicalls;

public class Users {
    private String fullName;
    private String userName;
    private String urlImage;

    public Users(String fullName, String userName, String urlImage){
        this.fullName = fullName;
        this.userName = userName;
        this.urlImage = urlImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
