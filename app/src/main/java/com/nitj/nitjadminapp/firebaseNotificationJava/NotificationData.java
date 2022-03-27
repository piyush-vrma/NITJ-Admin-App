package com.nitj.nitjadminapp.firebaseNotificationJava;

public class NotificationData {
    private String title;
    private String message;
    private String image;


    public NotificationData(String title, String message,String image){
        this.title = title;
        this.message = message;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
