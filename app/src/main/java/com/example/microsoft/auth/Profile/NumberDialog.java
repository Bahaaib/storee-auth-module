package com.example.microsoft.auth.Profile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserHandler;
import com.example.microsoft.auth.Root.UserModel;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class NumberDialog extends DialogFragment {

    private static final String TAG = "numberDialog";

    private View view;

    @BindView(R.id.ok_button)
    Button okButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;
    @BindView(R.id.number_value)
    EditText numberValue;
    @BindDrawable(R.drawable.ic_error)
    Drawable errorIcon;


    private DialogListener dialogListener;
    private UserModel data;
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
        view = inflater.inflate(R.layout.number_dialog, container, false);

        //inject views via Butterknife..
        unbinder = ButterKnife.bind(this, view);

        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

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

    @OnClick(R.id.cancel_button)
    void onCancelPressed() {
        getDialog().dismiss();
    }

    // validating mobile number format
    private boolean isValidMobileNumber(String number) {
        return Patterns.PHONE.matcher(number).matches() && (number.length() > 10);
    }

}
