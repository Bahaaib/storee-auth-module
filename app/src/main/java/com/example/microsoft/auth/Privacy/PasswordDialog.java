package com.example.microsoft.auth.Privacy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.microsoft.auth.Profile.DialogListener;
import com.example.microsoft.auth.R;
import com.example.microsoft.auth.Root.UserModel;

public class PasswordDialog extends DialogFragment {

    private final static String pass_TAG = "Password_dialog";
    private View view;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button okButton;
    private Button cancelButton;
    private TextInputLayout newPasswordLayout, confirmPasswordLayout;
    private Drawable errorIcon;
    private DialogListener dialogListener;
    private UserModel model;


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        dialogListener = (DialogListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.password_dialog, container, false);

        initViews();
        model = new UserModel();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPasswordStr = newPassword.getText().toString();
                String confirmPasswordStr = confirmPassword.getText().toString();

                if (isValidPassword(newPasswordStr) && isMatchedPassword(newPasswordStr, confirmPasswordStr)) {
                    // new password is ready
                    // model.setUserPassword(newPasswordStr);
                    dialogListener.onDataChanged(model);

                    getDialog().dismiss();
                } else {

                    if (!(isValidPassword(newPasswordStr))) {
                        newPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                        newPassword.setError(getString(R.string.pass_char_less), errorIcon);
                        callTextWatcher(newPassword, newPasswordLayout);
                    }

                    if (!isMatchedPassword(newPasswordStr, confirmPasswordStr)) {
                        confirmPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                        confirmPassword.setError(getString(R.string.pass_mismatch), errorIcon);
                        callTextWatcher(confirmPassword, confirmPasswordLayout);
                    }


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
        newPassword = view.findViewById(R.id.new_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        okButton = view.findViewById(R.id.ok_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        newPasswordLayout = view.findViewById(R.id.new_password_layout);
        confirmPasswordLayout = view.findViewById(R.id.confirm_password_layout);
        errorIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_error);
        errorIcon.setBounds(1, 0, errorIcon.getMinimumWidth(), errorIcon.getMinimumHeight());

        newPassword.setError(null);
        confirmPassword.setError(null);
    }

    private boolean isValidPassword(String pass) {
        return !TextUtils.isEmpty(pass) && pass.length() > 7;
    }

    private boolean isMatchedPassword(String pass1, String pass2) {
        return !TextUtils.isEmpty(pass2) && pass2.equals(pass1);
    }


    private void callTextWatcher(final EditText editText, final TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setError(null);
                layout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
