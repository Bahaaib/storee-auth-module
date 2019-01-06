package com.example.microsoft.auth.Privacy;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.microsoft.auth.Auth.AuthResponseListener;
import com.example.microsoft.auth.Profile.DialogListener;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;
import com.example.microsoft.auth.Root.VolleyHelper;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PrivacyActivity extends AppCompatActivity implements DialogListener, AuthResponseListener {

    private final String password_TAG = "password_dialog";
    private final String TOKEN_KEY = "token";
    private final String TOKEN_NOT_FOUND = "empty";


    @BindView(R.id.user_password)
    TextView userPassword;
    @BindView(R.id.privacy_policy)
    TextView privacyPolicy;
    @BindView(R.id.legal_info)
    TextView legalInfo;
    @BindView(R.id.edit_pen)
    ImageView editPen;
    @BindDrawable(R.drawable.edit_ic)
    Drawable editIcon;

    private DialogFragment passwordDialog;
    private Unbinder unbinder;
    private SharedPreferences preferences;
    private UserModel user;
    private UserHandler userHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        //inject Views via Butterknife..
        unbinder = ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        passwordDialog = new PasswordDialog();

        editIcon.setBounds(1, 0, editIcon.getMinimumWidth(), editIcon.getMinimumHeight());
        editPen.setImageDrawable(editIcon);

        if (user == null) {
            user = new UserModel();
        }

        if (userHandler == null) {
            userHandler = UserHandler.getInstance();

            //Load logged user data in Activity
            user = userHandler.getUser();
            Log.i("Statuss", user.getEmail());

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Free up memory from views
        unbinder.unbind();
    }

    //Views events
    @OnClick(R.id.edit_pen)
    void editPassword() {
        showDialog(passwordDialog, password_TAG);
    }

    //TODO: show privacy policy
    @OnClick(R.id.privacy_policy)
    void showPrivacyPolicy() {
    }

    //TODO: show legal info
    @OnClick(R.id.legal_info)
    void showLegalInfo() {
    }


    private void showDialog(DialogFragment dialog, String dialogTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            transaction.remove(prev);
        }

        transaction.add(dialog, dialogTag).commit();

    }


    @Override
    public void onDataChanged(UserModel modelReceived) {
        user = modelReceived;

        requestChangeUserData(user);
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
        }
    }

    @Override
    public void onResponseError() {
        Log.i("Statuss", "Error!");
        showUnexpectedError();
    }


    private void requestChangeUserData(UserModel chModel) {
        VolleyHelper.volleyInitialize(getBaseContext());

        if (!getRecentToken().equals(TOKEN_NOT_FOUND)) {
            VolleyHelper.setUserToken(getRecentToken());
            VolleyHelper.changeUserData(chModel);
            VolleyHelper.setAuthResponseListener(this);
            VolleyHelper.performUserDataRequest();
        } else {
            showUnexpectedError();
        }


    }


    private String getRecentToken() {
        return preferences.getString(TOKEN_KEY, TOKEN_NOT_FOUND);
    }


    private void showUnexpectedError() {
        Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_LONG)
                .show();
    }
}
