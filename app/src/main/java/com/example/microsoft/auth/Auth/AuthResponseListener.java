package com.example.microsoft.auth.Auth;

import com.example.microsoft.auth.Root.UserModel;

public interface AuthResponseListener {

    void onResponseReceived(UserModel model);

    void onResponseError();

}
