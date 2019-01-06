package com.example.microsoft.auth.Privacy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
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

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PasswordDialog extends DialogFragment {

    private final static String pass_TAG = "Password_dialog";
    private View view;

    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    @BindView(R.id.ok_button)
    Button okButton;
    @BindView(R.id.cancel_button)
    Button cancelButton;
    @BindView(R.id.new_password_layout)
    TextInputLayout newPasswordLayout;
    @BindView(R.id.confirm_password_layout)
    TextInputLayout confirmPasswordLayout;
    @BindDrawable(R.drawable.ic_error)
    Drawable errorIcon;

    private DialogListener dialogListener;
    private UserModel model;
    private Unbinder unbinder;


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        dialogListener = (DialogListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.password_dialog, container, false);

        //inject views via Buuterknife..
        unbinder = ButterKnife.bind(this, view);

        errorIcon.setBounds(1, 0, errorIcon.getMinimumWidth(), errorIcon.getMinimumHeight());

        newPassword.setError(null);
        confirmPassword.setError(null);
        model = new UserModel();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Free up memory from views
        unbinder.unbind();
    }

    //Views events
    @OnClick(R.id.ok_button)
    void onOKPressed() {
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

    @OnClick(R.id.cancel_button)
    void onCancelPressed() {
        getDialog().dismiss();
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
