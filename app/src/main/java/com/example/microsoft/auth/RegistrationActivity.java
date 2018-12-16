package com.example.microsoft.auth;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {

    private TextView alredayMemText;
    private Button registerButton;
    private EditText username, registerMail, registerPass;
    private Drawable errorIcon;
    private String usernameResult, mailResult, passResult;
    private boolean successfulSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        alredayMemText = (TextView) findViewById(R.id.already_member);
        username = (EditText) findViewById(R.id.username);
        registerMail = (EditText) findViewById(R.id.register_mail);
        registerPass = (EditText) findViewById(R.id.register_password);




    }
}
