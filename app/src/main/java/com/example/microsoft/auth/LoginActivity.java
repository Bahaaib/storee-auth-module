package com.example.microsoft.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements TokenListener {

    //Mail Auth
    private TextView regText;
    private EditText loginMail, loginPassword;
    private TextInputLayout loginPasswordLayout;
    private Button mailLoginButton;
    private Drawable errorIcon;

    private String mailResult, passResult;
    private boolean successfulLogin;
    private final String TOKEN_KEY = "token";
    private final String TOKEN_NOT_FOUND = "empty";
    private final String LOGOUT_KEY = "logout";
    private String token;
    private boolean isLoggedOut;
    private SharedPreferences preferences;


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

        //Prevent keyboard from automatic popping up once onCreate called..
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Init SharedPrefs..
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        //Mail Auth init..
        regText = (TextView) findViewById(R.id.register_text);
        loginMail = (EditText) findViewById(R.id.login_mail);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loginPasswordLayout = (TextInputLayout) findViewById(R.id.login_password_layout);
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

                } else {//Assign valid data
                    successfulLogin = true;
                    mailResult = loginMailStr;
                }

                if (!isValidPassword(loginPasswordStr)) {
                    successfulLogin = false;
                    loginPassword.setError(getString(R.string.pass_char_less), errorIcon);

                    //Hide password Toggle icon to avoid icons overlay
                    loginPasswordLayout.setPasswordVisibilityToggleEnabled(false);

                    //Reveal password Toggle icon again once user restart typing
                    callTextWatcher(loginPassword, loginPasswordLayout);
                } else {
                    //Assign valid Data
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

    @Override
    protected void onStart() {
        super.onStart();
        restoreSavedPrefs();
        if (!token.equals(TOKEN_NOT_FOUND) && !isLoggedOut){
            Log.i("Statuss", "Logged in AUTO");
        } else {
            Log.i("Statuss", "Logged in NEW SESSION!");
        }
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


    private void callTextWatcher(EditText editText, final TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void requestLogIn() {
        VolleyHelper.volleyInitialize(getBaseContext());

        VolleyHelper.loginUser(mailResult, passResult);
        VolleyHelper.setTokenListener(this);
        VolleyHelper.performRequest();

        //Reset Logout FLAG
        preferences.edit().putBoolean(LOGOUT_KEY, false).apply();

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }


    private void restoreSavedPrefs() {
        token = preferences.getString(TOKEN_KEY, TOKEN_NOT_FOUND);
        isLoggedOut = preferences.getBoolean(LOGOUT_KEY, false);
    }

    @Override
    public void onTokenReceived(String token) {
        preferences.edit()
                .putString(TOKEN_KEY, token)
                .apply();
        Log.i("Statuss", "Token Saved!");
    }

    @Override
    public void onTokenError() {
        Toast.makeText(getApplicationContext(), "Login Error!", Toast.LENGTH_LONG)
                .show();
    }


    private void facebookRequest() {

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
