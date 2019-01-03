package com.example.microsoft.auth.Privacy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.microsoft.auth.Profile.DialogListener;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserModel;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PrivacyActivity extends AppCompatActivity implements DialogListener {

    private final String password_TAG = "password_dialog";
    DialogFragment passwordDialog;
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
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        //inject Views via Butterknife..
        unbinder = ButterKnife.bind(this);

        passwordDialog = new PasswordDialog();

        editPen.setImageDrawable(editIcon);
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
    public void onDataChanged(UserModel model) {
        //userPassword.setText(model.getUserPassword());
    }
}
