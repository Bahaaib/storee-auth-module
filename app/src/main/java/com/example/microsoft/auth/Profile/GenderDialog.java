package com.example.microsoft.auth.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class GenderDialog extends DialogFragment {

    private final String TAG = "genderDialog";

    @BindView(R.id.ok_button)
    Button okButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;
    @BindView(R.id.male_radio_button)
    RadioButton male;
    @BindView(R.id.female_radio_button)
    RadioButton female;

    private UserModel data;
    private View view;
    private DialogListener dialogListener;
    private UserHandler userHandler;
    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogListener = (DialogListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gender_dialog, container, false);

        //inject views via Buuterknife..
        unbinder = ButterKnife.bind(this, view);

        if (data == null) {
            data = new UserModel();
        }

        if (userHandler == null) {
            userHandler = UserHandler.getInstance();

            //Load logged user data in Activity
            data = userHandler.getUser();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Free up memory from views
        unbinder.unbind();
    }

    @OnClick(R.id.ok_button)
    void onOKPressed() {
        String gender;
        if (male.isChecked()) {
            gender = getString(R.string.male);
        } else {
            gender = getString(R.string.female);
        }
        data.setMobile(gender);
        //Pass the changes to singleton
        userHandler.setUser(data);
        //Notify Activity about the new changes
        dialogListener.onDataChanged(userHandler.getUser());

        getDialog().dismiss();
    }

    @OnClick(R.id.cancel_button)
    void onCancelPressed() {
        getDialog().dismiss();
    }
}
