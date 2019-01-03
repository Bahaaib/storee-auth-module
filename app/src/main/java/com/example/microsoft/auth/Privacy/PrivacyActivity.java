package com.example.microsoft.auth.Privacy;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.microsoft.auth.Profile.DialogListener;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserModel;

public class PrivacyActivity extends AppCompatActivity implements DialogListener {

    private final String password_TAG = "password_dialog";
    DialogFragment passwordDialog;
    private TextView userPassword;
    private TextView privacyPolicy;
    private TextView legalInfo;
    private ImageView editPen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        passwordDialog = new PasswordDialog();

        initViews();


        editPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(passwordDialog, password_TAG);
            }
        });


        //TODO: show privacy policy
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //TODO: show legal info
        legalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    private void showDialog(DialogFragment dialog, String dialogTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            transaction.remove(prev);
        }

        transaction.add(dialog, dialogTag).commit();

    }

    private void initViews() {
        userPassword = findViewById(R.id.user_password);
        privacyPolicy = findViewById(R.id.privacy_policy);
        legalInfo = findViewById(R.id.legal_info);
        editPen = findViewById(R.id.edit_pen);
    }

    @Override
    public void onDataChanged(UserModel model) {
        //userPassword.setText(model.getUserPassword());
    }
}
