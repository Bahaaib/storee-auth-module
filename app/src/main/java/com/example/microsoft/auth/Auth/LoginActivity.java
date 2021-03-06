package com.example.microsoft.auth.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.microsoft.auth.Privacy.PrivacyActivity;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;
import com.example.microsoft.auth.Root.VolleyHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements AuthResponseListener {

    //Mail Auth
    @BindView(R.id.register_text)
    TextView regText;
    @BindView(R.id.login_mail)
    EditText loginMail;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_password_layout)
    TextInputLayout loginPasswordLayout;
    @BindView(R.id.mail_login)
    Button mailLoginButton;
    @BindDrawable(R.drawable.ic_error)
    Drawable errorIcon;

    private final String TOKEN_KEY = "token";
    private final String TOKEN_NOT_FOUND = "empty";
    private final String LOGOUT_KEY = "logout";
    private String token;
    private boolean isLoggedOut;
    private SharedPreferences preferences;
    private UserModel user;
    private UserHandler userHandler;
    private Unbinder unbinder;

    @BindView(R.id.debug_login)
    TextView debugLog;


    //FB OAuth
    private CallbackManager callbackManager;
    private static final String PUPLIC_PROFILE = "public_profile";
    private static final String EMAIL = "email";

    @BindView(R.id.login_button)
    LoginButton loginButton;
    private ProfileTracker profileTracker;

    @Override
    protected void onStart() {
        super.onStart();
        restoreSavedPrefs();
        if (!token.equals(TOKEN_NOT_FOUND) && !isLoggedOut) {
            Log.i("Statuss", "Logged in AUTO");
        } else {
            Log.i("Statuss", "Logged in NEW SESSION!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inject Views via Butterknife..
        unbinder = ButterKnife.bind(this);

        //Prevent keyboard from automatic popping up once onCreate called..
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Init|Recall SharedPrefs..
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (user == null) {
            user = new UserModel();
        }

        if (userHandler == null) {
            userHandler = UserHandler.getInstance();
            Log.i("SingletonStatuss", userHandler.toString());
        }


        //FB OAuth init..
        loginButton.setReadPermissions(Arrays.asList(EMAIL));


        callbackManager = CallbackManager.Factory.create();

        loginButton.setLoginBehavior(LoginBehavior.DIALOG_ONLY);

        facebookRequest();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Free up memory from views
        unbinder.unbind();
    }

    //Views events
    @OnClick(R.id.register_text)
    void registerIfNotUser() {
        Intent registerIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(registerIntent);
    }

    @OnClick(R.id.debug_login)
    void debugLogin() {
        user.setUsername("iBahaa");
        user.setEmail("Bahaa@Bahaa.com");
        user.setMobile("0123456789");
        user.setToken("vertualToken");
        user.setGender("Male");

        userHandler.setUser(user);

        //Reset Logout FLAG
        preferences.edit().putBoolean(LOGOUT_KEY, false).apply();

        moveToActivity();
    }

    @OnClick(R.id.mail_login)
    void doLogin() {
        final String loginMailStr = loginMail.getText().toString();
        final String loginPasswordStr = loginPassword.getText().toString();
        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());


        if (!isValidEmail(loginMailStr)) {
            loginMail.setError(getString(R.string.invalid_mail), errorIcon);

        } else {//Assign valid data
            user.setEmail(loginMailStr);
        }

        if (!isValidPassword(loginPasswordStr)) {
            loginPassword.setError(getString(R.string.pass_char_less), errorIcon);

            //Hide password Toggle icon to avoid icons overlay
            loginPasswordLayout.setPasswordVisibilityToggleEnabled(false);

            //Reveal password Toggle icon again once user restart typing
            callTextWatcher(loginPassword, loginPasswordLayout);
        } else {
            //Assign valid Data
            user.setPassword(loginPasswordStr);


        }
        if (hasErrors()) {
            Toast.makeText(getApplicationContext(), R.string.login_data_problem, Toast.LENGTH_LONG).show();
        } else {
            requestLogIn();
        }


    }

    // validating email id
    private boolean isValidEmail(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        return !TextUtils.isEmpty(pass) && pass.length() > 7;
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

        VolleyHelper.loginUser(user);
        VolleyHelper.setAuthResponseListener(this);
        VolleyHelper.performRequest();

        //Reset Logout FLAG
        preferences.edit().putBoolean(LOGOUT_KEY, false).apply();


    }


    private void restoreSavedPrefs() {
        token = preferences.getString(TOKEN_KEY, TOKEN_NOT_FOUND);
        isLoggedOut = preferences.getBoolean(LOGOUT_KEY, false);
    }

    @Override
    public void onResponseReceived(UserModel recModel) {
        user = recModel;

        //avoid destroying singleton data
        //avoid NPE
        if (user != null) {
            userHandler.setUser(user);
            preferences.edit()
                    .putString(TOKEN_KEY, userHandler.getUser().getToken())
                    .apply();
            Log.i("Statuss", "Token Saved!");

            moveToActivity();
        }


    }

    @Override
    public void onResponseError() {
        Log.i("Statuss", "Error!");
        Toast.makeText(getApplicationContext(), "Login Error!", Toast.LENGTH_LONG)
                .show();
    }


    private boolean hasErrors() {
        CharSequence mailError = loginMail.getError();
        CharSequence passError = loginPassword.getError();

        return mailError != null || passError != null;
    }


    private void moveToActivity() {
        Intent intent = new Intent(LoginActivity.this, PrivacyActivity.class);
        startActivity(intent);
    }


    //FB events
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
