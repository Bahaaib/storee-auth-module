package com.example.microsoft.auth.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserModel;
import com.example.microsoft.auth.Root.VolleyHelper;

public class RegistrationActivity extends AppCompatActivity implements AuthResponseListener {

    private TextView alredayMemText;
    private Button registerButton;
    private EditText username, registerMail, registerPass, confirmPass, mobileNumber;
    private TextInputLayout passwordLayout, confirmPasswordLayout;
    private Drawable errorIcon;
    private SharedPreferences preferences;
    private UserModel user;

    private final String TOKEN_KEY = "token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Prevent keyboard from automatic popping up once onCreate called..
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Init|Recall SharedPrefs..
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (user == null) {
            user = new UserModel();
        }

        //init Views
        alredayMemText = findViewById(R.id.already_member);
        username = findViewById(R.id.username);
        registerMail = findViewById(R.id.register_mail);
        registerPass = findViewById(R.id.register_password);
        confirmPass = findViewById(R.id.confirm_password);
        mobileNumber = findViewById(R.id.mobile_number);
        passwordLayout = findViewById(R.id.register_password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        registerButton = findViewById(R.id.register_button);
        errorIcon = ContextCompat.getDrawable(this, R.drawable.ic_error);


        //If already A User..Go back to Login
        alredayMemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                //Prevent Restarting activity onBack Pressed
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameStr = username.getText().toString();
                final String registerMailStr = registerMail.getText().toString();
                final String registerPassStr = registerPass.getText().toString();
                final String confirmPassStr = confirmPass.getText().toString();
                final String mobileNumberStr = mobileNumber.getText().toString();
                errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());


                if (!isValidUsername(usernameStr)) {
                    username.setError(getString(R.string.invalid_username), errorIcon);
                } else {
                    username.setError(null);
                    //Assign valid data
                    user.setUsername(usernameStr);
                }


                if (!isValidEmail(registerMailStr)) {

                    registerMail.setError(getString(R.string.invalid_format), errorIcon);
                } else {//Assign valid data
                    user.setEmail(registerMailStr);
                }

                if (!isValidPassword(registerPassStr)) {
                    registerPass.setError(getString(R.string.pass_char_less), errorIcon);

                    //Hide password Toggle icon to avoid icons overlay
                    passwordLayout.setPasswordVisibilityToggleEnabled(false);

                    //Reveal password Toggle icon again once user restart typing
                    callTextWatcher(registerPass, passwordLayout);
                } else {//send valid data
                    //passResult = registerPassStr;
                }

                if (!isMatchedPassword(registerPassStr, confirmPassStr)) {
                    confirmPass.setError(getString(R.string.pass_mismatch), errorIcon);

                    //Hide password Toggle icon to avoid icons overlay
                    confirmPasswordLayout.setPasswordVisibilityToggleEnabled(false);

                    //Reveal password Toggle icon again once user restart typing
                    callTextWatcher(confirmPass, confirmPasswordLayout);

                } else {
                    user.setPassword(registerPassStr);
                }

                if (!isValidMobileNumber(mobileNumberStr)) {
                    mobileNumber.setError(getString(R.string.mobile_number_invalid), errorIcon);
                } else {
                    user.setMobile(mobileNumberStr);
                }

                if (hasErrors()) {
                    Toast.makeText(getApplicationContext(), R.string.register_data_problem, Toast.LENGTH_LONG)
                            .show();

                } else {
                    registerUser();
                }


            }


        });


    }

    // validating Username
    private boolean isValidUsername(String userName) {
        return !TextUtils.isEmpty(userName) && (userName.length() > 2);
    }


    // validating email id
    private boolean isValidEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        return !TextUtils.isEmpty(pass) && pass.length() > 7;
    }

    // validating Passwords matching
    private boolean isMatchedPassword(String pass1, String pass2) {
        return !TextUtils.isEmpty(pass2) && pass2.equals(pass1);
    }

    // validating mobile number format
    private boolean isValidMobileNumber(String number) {
        return Patterns.PHONE.matcher(number).matches() && (number.length() > 10);
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

    // Check all fields contain No Errors before request
    private boolean hasErrors() {
        CharSequence userError = username.getError();
        CharSequence mailError = registerMail.getError();
        CharSequence passError = registerPass.getError();
        CharSequence confirmError = confirmPass.getError();
        CharSequence mobileError = mobileNumber.getError();

        return userError != null
                || mailError != null
                || passError != null
                || confirmError != null
                || mobileError != null;
    }

    private void registerUser() {
        VolleyHelper.volleyInitialize(getBaseContext());
        VolleyHelper.registerUser(user);
        VolleyHelper.setAuthResponseListener(this);
        VolleyHelper.performRequest();
    }

    //Save Token to SharedPreferences once received..
    @Override
    public void onResponseReceived(UserModel receivedModel) {
        user = receivedModel;
        preferences.edit()
                .putString(TOKEN_KEY, user.getToken())
                .apply();
        Log.i("Statuss", "Token Saved!");
    }

    @Override
    public void onResponseError() {
        Toast.makeText(getApplicationContext(), "Login Error!", Toast.LENGTH_LONG)
                .show();
    }
}
