package com.example.microsoft.auth.Root;

//Singleton Class to ensure a single instance
// of user data everywhere in the App


public class UserHandler {

    private UserModel mUser;

    private UserHandler() {
        //Private Constructor for Singleton
        mUser = new UserModel();
    }

    public static UserHandler getInstance() {
        return DataBuilder.DATA;
    }

    public UserModel getUser() {
        return mUser;
    }

    public void setUser(UserModel user) {
        mUser.setUsername(user.getUsername());
        mUser.setEmail(user.getEmail());
        mUser.setPassword(user.getPassword());
        mUser.setMobile(user.getMobile());
        mUser.setToken(user.getToken());
        mUser.setGender(user.getGender());
        mUser.setImgURL(user.getImgURL());
    }

    private static class DataBuilder {
        private static final UserHandler DATA = new UserHandler();
    }
}
