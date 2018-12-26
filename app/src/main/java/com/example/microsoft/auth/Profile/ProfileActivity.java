package com.example.microsoft.auth.Profile;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserModel;
import com.example.microsoft.auth.Root.VolleyHelper;


public class ProfileActivity extends AppCompatActivity implements DialogListener {

    private final String GENDER_TAG = "genderDialog";
    private final String NUMBER_TAG = "numberDialog";
    private final String TOKEN_KEY = "token";
    private final String TOKEN_NOT_FOUND = "empty";


    private String female, male;
    private TextView myOredrs;
    private TextView disputedOrders;
    private TextView wishingList;
    private TextView myPoints;
    private TextView genderType;
    private TextView mobileNumber;
    private DialogFragment genderDialog;
    private DialogFragment numberDialog;

    private SharedPreferences preferences;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        female = getString(R.string.female);
        male = getString(R.string.male);

        initViews();

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
    public void onDataPass(UserModel data) {
        mobileNumber.setText(data.getMobile());

        if (data.isMale()) {
            genderType.setText(male);
        } else
            genderType.setText(female);

    }

    private void initViews() {
        myOredrs = findViewById(R.id.my_order);
        disputedOrders = findViewById(R.id.disputed_order);
        wishingList = findViewById(R.id.wishing_list);
        myPoints = findViewById(R.id.my_points);
        genderType = findViewById(R.id.gender);
        mobileNumber = findViewById(R.id.mobile_number);

    }

    private void requestUserData() {
        VolleyHelper.volleyInitialize(getBaseContext());

        if (!getRecentToken().equals(TOKEN_NOT_FOUND)) {
            VolleyHelper.setUserToken(getRecentToken());
            //VolleyHelper.
        }else {
            showRandomError();
        }


    }

    private String getRecentToken() {
        return preferences.getString(TOKEN_KEY, TOKEN_NOT_FOUND);
    }

    private void showRandomError(){
        Toast.makeText(getApplicationContext(),"Unexpected Error", Toast.LENGTH_LONG)
                .show();
    }
}
