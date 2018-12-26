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
import com.example.microsoft.auth.Root.UserModel;


public class GenderDialog extends DialogFragment {

    private final String TAG = "genderDialog";

    private Button okButton;
    private Button cancelButton;
    private RadioButton male;
    private RadioButton female;
    private UserModel data;
    private View view;
    private DialogListener dialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogListener = (DialogListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gender_dialog, container, false);

        data = new UserModel();

        initViews();


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();

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
        okButton = view.findViewById(R.id.ok_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        male = view.findViewById(R.id.male_radio_button);
        female = view.findViewById(R.id.female_radio_button);

    }
}
