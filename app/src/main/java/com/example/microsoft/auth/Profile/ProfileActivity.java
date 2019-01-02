package com.example.microsoft.auth.Profile;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.microsoft.auth.Auth.AuthResponseListener;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;
import com.example.microsoft.auth.Root.VolleyHelper;


public class ProfileActivity extends AppCompatActivity implements DialogListener, AuthResponseListener {

    private final String GENDER_TAG = "genderDialog";
    private final String NUMBER_TAG = "numberDialog";
    private final String TOKEN_KEY = "token";
    private final String TOKEN_NOT_FOUND = "empty";


    private TextView username;
    private TextView myOredrs;
    private TextView disputedOrders;
    private TextView wishingList;
    private TextView myPoints;
    private TextView genderType;
    private TextView mobileNumber;
    private DialogFragment genderDialog;
    private DialogFragment numberDialog;

    private SharedPreferences preferences;
    private UserModel user;
    private UserHandler userHandler;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();

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


        genderType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(GENDER_TAG);
                if (prev != null) {
                    transaction.remove(prev);
                }

                transaction.add(genderDialog, GENDER_TAG).commit();


            }
        });

        mobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(NUMBER_TAG);
                if (prev != null) {
                    transaction.remove(prev);
                }

                transaction.add(numberDialog, NUMBER_TAG).commit();
            }
        });

        myOredrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        disputedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        wishingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onDataChanged(UserModel modelReceived) {
        user = modelReceived;

        mobileNumber.setText(user.getMobile());
        genderType.setText(user.getGender());

        requestChangeUserData(user);

    }

    private void initViews() {
        username = findViewById(R.id.profile_user_name);
        myOredrs = findViewById(R.id.my_order);
        disputedOrders = findViewById(R.id.disputed_order);
        wishingList = findViewById(R.id.wishing_list);
        myPoints = findViewById(R.id.my_points);
        genderType = findViewById(R.id.gender);
        mobileNumber = findViewById(R.id.mobile_number);

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