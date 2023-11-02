package com.novotek.sales.login_views;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.novotek.dataobjects.ws.ReqCodeResult;
import com.novotek.sales.BuildConfig;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

public class CheckCode extends Fragment {
    public static final String RESULT_CODE = "check_code";

    Model model;

    CountDownTimer cdt = null;
    private EditText edCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.check_code_fragment_new, container, false);

        model = new ViewModelProvider(getActivity()).get(Model.class);
        TextView phone = v.findViewById(R.id.phone);
        phone.setText(model.getPhone().getValue());

        if(BuildConfig.DEBUG) {
            ReqCodeResult r = model.getRequestResult().getValue();
            if(r != null) {
                TextInputLayout til = v.findViewById(R.id.code);
                til.setHint(r.code);
            }
        }

        edCode = ((TextInputLayout) v.findViewById(R.id.code)).getEditText();
        edCode.setGravity(Gravity.CENTER_HORIZONTAL);

        final View doButton = v.findViewById(R.id.doButton);
        doButton.setOnClickListener(view -> {
            ReqCodeResult r = model.getRequestResult().getValue();
            if(r != null) {
                try {
                    String inpCode = edCode.getText().toString().toUpperCase();
                    if (inpCode.endsWith(r.code.toUpperCase())) {
                        MainActivity.setAppToken(getContext(), r.token);
                        getParentFragmentManager().setFragmentResult(RESULT_CODE, null);
                    } else {
                        Toast.makeText(getActivity(), R.string.wrong_code, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        final Button doAgain = v.findViewById(R.id.doAgain);
        doAgain.setOnClickListener(view -> {
            model.ackCode(getActivity());
            updateWaiting(doAgain);
        });

        updateWaiting(doAgain);

        Task<Void> task = SmsRetriever.getClient(getActivity()).startSmsUserConsent("6505551212");
        task.addOnSuccessListener((s)->{
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            getActivity().registerReceiver(smsRcv, intentFilter, SmsRetriever.SEND_PERMISSION, null);
        });

        return v;
    }

    private void updateWaiting(Button doAgain) {
        long waitInt = model.getWaitInterval();
        if(waitInt != 0) {
            doAgain.setEnabled(false);
            cdt = new CountDownTimer(waitInt, 1000) {
                @Override
                public void onTick(long l) {
                    String t = getString(R.string.count_down, l/ 1000);
                    doAgain.setText(t);
                }

                @Override
                public void onFinish() {
                    doAgain.setText(R.string.send_again);
                    doAgain.setEnabled(true);
                }
            };
            cdt.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(cdt != null) {
            cdt.cancel();
        }
    }

    MySMSBroadcastReceiver smsRcv = new MySMSBroadcastReceiver();

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
        edCode.setText(message.trim());
    }

    public class MySMSBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch(status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);

                        try {
                            final int SMS_CONSENT_REQUEST = 1;
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }
        }
    }
}
