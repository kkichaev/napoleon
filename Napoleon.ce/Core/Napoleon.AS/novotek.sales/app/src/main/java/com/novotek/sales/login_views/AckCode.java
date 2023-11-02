package com.novotek.sales.login_views;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.novotek.sales.R;

public class AckCode extends Fragment {
    public static final String RESULT_CODE = "ack_code";

    EditText phoneEdit;
    Model model;

    View waitView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_code_frame_new, container, false);
        TextInputLayout te = v.findViewById(R.id.phone);

        model = new ViewModelProvider(getActivity()).get(Model.class);
        waitView = v.findViewById(R.id.llWait);

        phoneEdit = te.getEditText();
        phoneEdit.setText(model.getPhone().getValue());
//        phoneEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher("+7"));

        final View doButton = v.findViewById(R.id.doButton);
        doButton.setOnClickListener(view -> {
            makeCall();
        });

        model.getRequestInProgress().observe(getViewLifecycleOwner(), inProgress -> {
            doButton.setEnabled(!inProgress);

            if(waitView != null) {
                waitView.setVisibility(inProgress ? View.VISIBLE : View.GONE);

                waitView.findViewById(R.id.load_progress).setVisibility(View.GONE);
                waitView.findViewById(R.id.waiting).setVisibility(View.VISIBLE);
            }
        });

        model.getLoadProgress().observe(getViewLifecycleOwner(), progress -> {
            waitView.findViewById(R.id.load_progress).setVisibility(View.VISIBLE);
            waitView.findViewById(R.id.waiting).setVisibility(View.GONE);

            ProgressBar pb = waitView.findViewById(R.id.loading);
            pb.setMax(progress.total);
            pb.setProgress(progress.current);
        });

        return v;
    }

    void makeCall() {
        if(model.getRequestInProgress().getValue()) {
            return;
        }

        String phone = phoneEdit.getText().toString();
        if(!phone.startsWith("+7")) {
            if(phone.startsWith("8"))
                phone = phone.substring(1);
            phone = "+7" + phone;
        }

        if(phone.length() < 10) {
            Toast.makeText(getContext(), R.string.phone_number_incorrect, Toast.LENGTH_SHORT).show();
            return;
        }

        model.getPhone().setValue(phone);
        model.ackCode(getActivity());
    }
}
