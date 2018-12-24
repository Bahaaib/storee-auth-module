package com.example.microsoft.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private Button logout;
    private SharedPreferences sharedPreferences;
    private final String LOGOUT_KEY = "logout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        logout = (Button)findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setLogoutPreference();
                startActivity(logoutIntent);
            }
        });
    }

    private void setLogoutPreference(){
        sharedPreferences.edit()
                .putBoolean(LOGOUT_KEY, true)
                .apply();
    }
}
