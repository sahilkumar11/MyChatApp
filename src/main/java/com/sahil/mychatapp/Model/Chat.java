package com.sahil.mychatapp.Model;

public class Chat {
     private String Sender;
     private  String Receiver;
    private  String Message;
    private boolean isseen;
    private int  iscount ;

    public Chat(String sender, String receiver, String message, boolean isseen , int iscount) {
        this.Sender = sender;
        this.Receiver = receiver;
       this. Message = message;
        this.isseen = isseen;
        this.iscount = iscount;
    }

    public Chat() {
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        this.Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        this.Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public int getIscount() {
        return iscount;
    }

    public void setIscount(int iscount) {
        this.iscount = iscount;
    }
}
