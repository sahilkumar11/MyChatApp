package com.sahil.mychatapp.Model;

public class User {
  private String Id;
   private String UserName;
   private String ImageURL;
   private String status;


    // constructors


    public User() {
    }

    public User(String Id, String UserName, String ImageURL ,String status ) {
        this.Id = Id;
        this.UserName = UserName;
       this.ImageURL = ImageURL;
       this.status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
