package com.example.microsoft.auth;

public interface TokenListener {

    void onTokenReceived(String token);

    void onTokenError();

}
