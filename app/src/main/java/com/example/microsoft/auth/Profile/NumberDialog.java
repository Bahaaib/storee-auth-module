package com.example.microsoft.auth.Profile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;


public class NumberDialog extends DialogFragment {

    private static final String TAG = "numberDialog";

    private View view;
    private Button okButton;
    private Button cancelButton;
    private EditText numberValue;
    private Drawable errorIcon;
    private DialogListener dialogListener;
    private UserModel data;
    private UserHandler userHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogListener = (DialogListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.number_dialog, container, false);

        initViews();

        if (data == null) {
            data = new UserModel();
        }
        if (userHandler == null) {
            userHandler = UserHandler.getInstance();

            //Load logged user data in Activity
            data = userHandler.getUser();
        }


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = numberValue.getText().toString();
                if (!isValidMobileNumber(number)) {
                    numberValue.setError(getString(R.string.mobile_number_invalid), errorIcon);
                } else {
                    data.setMobile(number);
                    //Pass the changes to singleton
                    userHandler.setUser(data);
                    //Notify Activity about the new changes
                    dialogListener.onDataChanged(userHandler.getUser());

                    getDialog().dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    public void initViews() {
        okButton = view.findViewById((R.id.ok_button));
        cancelButton = view.findViewById((R.id.cancel_button));
        numberValue = view.findViewById((R.id.number_value));
        errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error);
        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
    }

    // validating mobile number format
    private boolean isValidMobileNumber(String number) {
        return Patterns.PHONE.matcher(number).matches() && (number.length() > 10);
    }

}
