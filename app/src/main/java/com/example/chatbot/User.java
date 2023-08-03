package com.example.chatbot;

import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {
    String id , Name , Avatar;
    public User(String id , String Name , String Avatar){
        this.id = id;
        this.Name = Name;
        this.Avatar = Avatar;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getAvatar() {
        return Avatar;
    }
}
