package com.example.microsoft.auth.Auth;

public interface TokenListener {

    void onTokenReceived(String token);

    void onTokenError();

}
