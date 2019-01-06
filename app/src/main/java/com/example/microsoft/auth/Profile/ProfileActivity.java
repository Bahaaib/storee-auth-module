package com.example.microsoft.auth.Profile;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.microsoft.auth.Auth.AuthResponseListener;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;
import com.example.microsoft.auth.Root.VolleyHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ProfileActivity extends AppCompatActivity implements DialogListener, AuthResponseListener {

    private final String GENDER_TAG = "genderDialog";
    private final String NUMBER_TAG = "numberDialog";
    private final String TOKEN_KEY = "token";
    private final String TOKEN_NOT_FOUND = "empty";


    @BindView(R.id.profile_user_name)
    TextView username;
    @BindView(R.id.my_order)
    TextView myOredrs;
    @BindView(R.id.disputed_order)
    TextView disputedOrders;
    @BindView(R.id.wishing_list)
    TextView wishingList;
    @BindView(R.id.my_points)
    TextView myPoints;
    @BindView(R.id.gender)
    TextView genderType;
    @BindView(R.id.mobile_number)
    TextView mobileNumber;

    DialogFragment genderDialog;
    DialogFragment numberDialog;

    private SharedPreferences preferences;
    private UserModel user;
    private UserHandler userHandler;
    private Unbinder unbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //inject views via Butterknife
        unbinder = ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (user == null) {
            user = new UserModel();
        }

        if (userHandler == null) {
            userHandler = UserHandler.getInstance();

            //Load logged user data in Activity
            user = userHandler.getUser();
            Log.i("Statuss", user.getEmail());

        }

        //Request the most recent user data..
        //requestChangeUserData(null);


        //Update user data views
        setViewsValue();


        genderDialog = new GenderDialog();
        numberDialog = new NumberDialog();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Free up memory from views
        unbinder.unbind();
    }

    //Views events
    @OnClick(R.id.gender)
    void showGenderDialog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(GENDER_TAG);
        if (prev != null) {
            transaction.remove(prev);
        }

        transaction.add(genderDialog, GENDER_TAG).commit();

    }

    @OnClick(R.id.mobile_number)
    void showMobileDialog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(NUMBER_TAG);
        if (prev != null) {
            transaction.remove(prev);
        }

        transaction.add(numberDialog, NUMBER_TAG).commit();
    }

    //TODO: Linkup MY Orders Activity
    @OnClick(R.id.my_order)
    void goToMyOrders() {
    }

    //TODO: Linkup Disputed Orders Activity
    @OnClick(R.id.disputed_order)
    void goToDisputedOrders() {
    }

    //TODO: Linkup Wishing List Activity
    @OnClick(R.id.wishing_list)
    void goToWishingList() {
    }


    @Override
    public void onDataChanged(UserModel modelReceived) {
        user = modelReceived;

        mobileNumber.setText(user.getMobile());
        genderType.setText(user.getGender());

        requestChangeUserData(user);

    }

    private void setViewsValue() {
        username.setText(user.getUsername());
        genderType.setText(user.getGender());
        mobileNumber.setText(user.getMobile());
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

    @Override
    public void onResponseReceived(UserModel recModel) {
        user = recModel;
        //avoid destroying singleton data
        //avoid NPE
        if (user != null) {
            userHandler.setUser(recModel);
            setViewsValue();
            Log.i("Statuss", "Retrieved!");

        }

    }

    @Override
    public void onResponseError() {
        showUnexpectedError();
    }


    private void showUnexpectedError() {
        Toast.makeText(getApplicationContext(), "Unexpected Error", Toast.LENGTH_LONG)
                .show();
    }
}
