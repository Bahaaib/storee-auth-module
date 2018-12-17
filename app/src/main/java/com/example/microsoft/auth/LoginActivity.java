package com.example.microsoft.auth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    //Mail Auth
    private TextView regText;
    private EditText loginMail, loginPassword;
    private Button mailLoginButton;
    private Drawable errorIcon;

    private String mailResult, passResult;
    private boolean successfulLogin;

    //FB OAuth
    private CallbackManager callbackManager;
    private static final String PUPLIC_PROFILE = "public_profile";
    private static final String EMAIL = "email";

    private LoginButton loginButton;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Mail Auth init..
        regText = (TextView) findViewById(R.id.register_text);
        loginMail = (EditText) findViewById(R.id.login_mail);
        loginPassword = (EditText) findViewById(R.id.login_password);
        mailLoginButton = (Button) findViewById(R.id.mail_login);
        errorIcon = (Drawable) ContextCompat.getDrawable(this, R.drawable.ic_error);


        //Not A member.. Go to Register
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(loginIntent);
            }
        });

        //Perform Login..
        mailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                successfulLogin = false;
                final String loginMailStr = loginMail.getText().toString();
                final String loginPasswordStr = loginPassword.getText().toString();
                errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());


                if (!isValidEmail(loginMailStr)) {
                    loginMail.setError(getString(R.string.invalid_mail), errorIcon);
                    successfulLogin = false;

                } else {//send valid data
                    successfulLogin = true;
                    mailResult = loginMailStr;
                }

                if (!isValidPassword(loginPasswordStr)) {
                    successfulLogin = false;
                    loginPassword.setError(getString(R.string.pass_char_less), errorIcon);
                } else {
                    //Send valid Data
                    successfulLogin = true;
                    passResult = loginPasswordStr;


                }
                if (!successfulLogin) {
                    Toast.makeText(getApplicationContext(), R.string.login_data_problem, Toast.LENGTH_LONG).show();
                } else {
                    requestLogIn();
                }


            }


        });


        //FB OAuth init..
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));


        callbackManager = CallbackManager.Factory.create();

        loginButton.setLoginBehavior(LoginBehavior.DIALOG_ONLY);

        facebookRequest();


    }

    // validating email id
    private boolean isValidEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (!TextUtils.isEmpty(pass) && pass.length() > 7) {
            return true;
        }
        return false;
    }

    //
    public void requestLogIn() {
        VolleyHelper.volleyInitialize(getBaseContext());
        VolleyHelper.loadUser();
    }

    public void facebookRequest() {

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.i("Statuss", loginResult.getRecentlyGrantedPermissions().toString());


                Profile profile = Profile.getCurrentProfile();

                if (profile == null) {

                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.i("Statuss", currentProfile.getFirstName());
                            Log.i("Statuss", currentProfile.getLastName());
                            Log.i("Statuss", currentProfile.getId());

                            profileTracker.stopTracking();
                        }
                    };

                } else {
                    Log.i("Statuss", profile.getFirstName());
                    Log.i("Statuss", profile.getLastName());
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();

                Log.i("Statuss", error.toString());


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
