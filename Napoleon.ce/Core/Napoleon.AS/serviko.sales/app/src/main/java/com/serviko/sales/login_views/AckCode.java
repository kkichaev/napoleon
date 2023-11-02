package com.serviko.sales.login_views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.serviko.sales.BuildConfig;
import com.serviko.sales.R;

import java.util.Date;

public class AckCode extends Fragment {
    public static final String RESULT_CODE = "ack_code";

    static final String PREF_NAME = "AppCode";
    static final String PREF_KEY = "lastConnect";
    static final long SEND_INTERVAL = BuildConfig.DEBUG ? 0 : 90 * 1000;

    CountDownTimer cdTimer = null;
    EditText phoneEdit;
    Model model;

    View waitView, rootView;
    View doButton, doButton2;
    boolean canSendRequest = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.get_code_frame_new, container, false);
        TextInputLayout te = rootView.findViewById(R.id.phone);

        model = new ViewModelProvider(getActivity()).get(Model.class);
        waitView = rootView.findViewById(R.id.llWait);

        phoneEdit = te.getEditText();
        phoneEdit.setText(model.getPhone().getValue());

        doButton = rootView.findViewById(R.id.doButton);
        doButton.setOnClickListener(view -> {
            makeCall();
        });

        doButton2 = rootView.findViewById(R.id.doButtonSecond);
        doButton2.setOnClickListener(view -> {
            model.getSmsMode().setValue(!model.getSmsMode().getValue());
            makeCall();
        });

        model.getRequestInProgress().observe(getViewLifecycleOwner(), inProgress -> {
            doButton.setEnabled(!inProgress && canSendRequest);
            doButton2.setEnabled(!inProgress && canSendRequest);

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

        updateText(rootView);

        refreshSendInterval();
        return rootView;
    }

    void refreshSendInterval() {
        SharedPreferences sp = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long nextConnect = sp.getLong(PREF_KEY, 0) + SEND_INTERVAL;
        long ct = (new Date()).getTime();

        if(ct < nextConnect) {
            waitConnect(nextConnect - ct);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(cdTimer != null) {
            cdTimer.cancel();
        }
    }

    private void waitConnect(long interval) {
        canSendRequest = false;

        doButton2.setEnabled(false);
        doButton.setEnabled(false);

        cdTimer = new CountDownTimer(interval, 1000) {
            @Override
            public void onTick(long l) {
                String text = getString(R.string.can_resend_after, l/1000);
                ((Button)doButton).setText(text);
            }

            @Override
            public void onFinish() {
                canSendRequest = true;
                boolean v = canSendRequest & (!model.getRequestInProgress().getValue());
                doButton2.setEnabled(v);
                doButton.setEnabled(v);

                updateText(rootView);
            }
        };
        cdTimer.start();
    }

    private void putLastConnect() {
        SharedPreferences.Editor e = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        e.putLong(PREF_KEY, new Date().getTime() );
        e.commit();

        waitConnect(SEND_INTERVAL);
    }

    void updateText(View v) {
        boolean smsMode = model.getSmsMode().getValue();
        Button b = v.findViewById(R.id.doButton);
        b.setText(smsMode ? R.string.get_code_by_sms : R.string.get_code_by_phone);

        TextView tv = v.findViewById(R.id.doButtonSecond);
        tv.setText(smsMode ? R.string.get_code_by_phone_u : R.string.get_code_by_sms_u);
    }

    void makeCall() {
        if(model.getRequestInProgress().getValue()) {
            return;
        }

        String phone = phoneEdit.getText().toString();
        if(phone.length() < 10) {
            Toast.makeText(getContext(), R.string.phone_number_incorrect, Toast.LENGTH_SHORT).show();
            return;
        }

        model.getPhone().setValue(phone);
        model.ackCode(getActivity());
        putLastConnect();
    }
}
