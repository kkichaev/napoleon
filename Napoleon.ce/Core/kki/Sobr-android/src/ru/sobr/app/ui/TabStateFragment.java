package ru.sobr.app.ui;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.telephony.SobrGsm;
import ru.sobr.app.utils.Constants;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;

public class TabStateFragment extends SherlockFragment implements OnClickListener {

    //private static final String TAG = "TabStateFragment";
    //private static final boolean DEBUG = false;

    private String mSystemType = Constants.SOBR_GSM;
    private String mPhoneStatus = "base_value";
    private String mPinCode = "";
    private String mCmd09 = "disable_value";
    private String mGpsReceiver = "false";
    private String mReportOnMove = "false";
    private String mImmobilizer = "false";
    private String mSobrAssistLogin = "";
    private String mSobrAssistPassword = "";

    private Button mBalance, mInfoCall, mInfoSms;
    private Button mMap, mCoord;
    private Button mServiceOn, mServiceOff;
    private Button mMoveReport;
    private Button mSobrAssist;

    private InnerSmsStatusReceiver mSmsStatusReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String systemType = getArguments().getString(MainActivity.SYSTEMTYPE_KEY);
        if (systemType != null) mSystemType = systemType;

        String phoneStatus = getArguments().getString(MainActivity.PHONESTATUS_KEY);
        if (phoneStatus != null) mPhoneStatus = phoneStatus;

        String pinCode = getArguments().getString(MainActivity.PINCODE_KEY);
        if (pinCode != null) mPinCode = pinCode;

        String cmd09 = getArguments().getString(MainActivity.CMD09_KEY);
        if (cmd09 != null) mCmd09 = cmd09;

        String gpsReceiver = getArguments().getString(MainActivity.GPSRECEIVER_KEY);
        if (gpsReceiver != null) mGpsReceiver = gpsReceiver;

        String reportOnMove = getArguments().getString(MainActivity.REPORTONMOVE_KEY);
        if (reportOnMove != null) mReportOnMove = reportOnMove;

        String immobilizer = getArguments().getString(MainActivity.IMMOBILIZER_KEY);
        if (immobilizer != null) mImmobilizer = immobilizer;

        String sobrAssistLogin = getArguments().getString(MainActivity.SOBR_ASSIST_LOGIN);
        if (sobrAssistLogin != null) mSobrAssistLogin = sobrAssistLogin;

        String sobrAssistPassword = getArguments().getString(MainActivity.SOBR_ASSIST_PASSWORD);
        if (sobrAssistPassword != null) mSobrAssistPassword = sobrAssistPassword;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if(DEBUG)Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_state, container, false);

        mBalance = (Button) layout.findViewById(R.id.state_common_balance_btn);
        mBalance.setOnClickListener(this);
        mInfoCall = (Button) layout.findViewById(R.id.state_common_info_btn);
        mInfoCall.setOnClickListener(this);
        mInfoSms = (Button) layout.findViewById(R.id.state_common_info_sms_btn);
        mInfoSms.setOnClickListener(this);

        mMap = (Button) layout.findViewById(R.id.state_location_map_btn);
        mMap.setOnClickListener(this);
        mCoord = (Button) layout.findViewById(R.id.state_location_coordinates_btn);
        mCoord.setOnClickListener(this);

        mServiceOn = (Button) layout.findViewById(R.id.state_service_mode_on_btn);
        mServiceOn.setOnClickListener(this);
        mServiceOff = (Button) layout.findViewById(R.id.state_service_mode_off_btn);
        mServiceOff.setOnClickListener(this);

        mMoveReport = (Button) layout.findViewById(R.id.state_report_move_btn);
        mMoveReport.setOnClickListener(this);

        mSobrAssist = (Button) layout.findViewById(R.id.state_sobr_assist_btn);
        mSobrAssist.setOnClickListener(this);
        
        // Manage views visibility
        if (mSystemType.equals(Constants.SOBR_G0103) ||
        		mSystemType.equals(Constants.SOBR_CHIP0103) ||
        		mSystemType.equals(Constants.SOBR_CHIP111213)) {
            mBalance.setVisibility(View.GONE);
        }
        
        if (mSystemType.equals(Constants.SOBR_CHIP111213))
        	mInfoCall.setVisibility(View.GONE);
        
        if (mCmd09.equals("disable_value")) {
            mInfoCall.setVisibility(View.GONE);
        }
        if (!mCmd09.equals("call_and_sms_value")) {
            mInfoSms.setVisibility(View.GONE);
        }
//	if (mSystemType.equals(Constants.SOBR_G0103)){
//	    mInfoSms.setVisibility(View.VISIBLE);
//	    mInfoCall.setVisibility(View.GONE);
//	}
        if (mSystemType.equals(Constants.SOBR_G0103)) {
            mInfoCall.setVisibility(View.GONE);
            mInfoSms.setVisibility(View.VISIBLE);
        }

        if (mSystemType.equals(Constants.SOBR_GSM) || mSystemType.equals(Constants.SOBR_GSM510)) {
            layout.findViewById(R.id.state_sobr_assist_btn).setVisibility(View.VISIBLE);
        } else {
            layout.findViewById(R.id.state_sobr_assist_btn).setVisibility(View.GONE);
        }

        if (mBalance.getVisibility() != View.VISIBLE && mInfoCall.getVisibility() != View.VISIBLE
                && mInfoSms.getVisibility() != View.VISIBLE) {
            layout.findViewById(R.id.state_common_balance_title).setVisibility(View.GONE);
        }

        if (mGpsReceiver.equals(Constants.FALSE)) {
            layout.findViewById(R.id.state_location_link_to_website).setVisibility(View.GONE);
            mMap.setVisibility(View.GONE);
            mCoord.setVisibility(View.GONE);
        }
        if (mReportOnMove.equals(Constants.FALSE)) {
            layout.findViewById(R.id.state_report_move_btn).setVisibility(View.GONE);
            mMoveReport.setVisibility(View.GONE);
        }
        if (mReportOnMove.equals(Constants.FALSE) && mGpsReceiver.equals(Constants.FALSE)) {
            layout.findViewById(R.id.state_location_map_title).setVisibility(View.GONE);
            mMoveReport.setVisibility(View.GONE);
        }

        if (mImmobilizer.equals(Constants.FALSE) || 
        		(mSystemType.equals(Constants.SOBR_GSM510) && 
                		getArguments().getString(SobrContract.Profiles.GSM510_WORK_MODE).equals("1"))) {
            layout.findViewById(R.id.state_service_mode_title).setVisibility(View.GONE);
            mServiceOn.setVisibility(View.GONE);
            mServiceOff.setVisibility(View.GONE);
        }
        
        mSmsStatusReceiver = new InnerSmsStatusReceiver();

        return layout;
    }

    private void openLocation() {
        String url = String.format("http://location.sobr-a.ru/index.jss?login=%s&pass=%s", mSobrAssistLogin, mSobrAssistPassword);
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.URL, url);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.state_sobr_assist_btn:
                openLocation();
                break;
            case R.id.state_common_balance_btn:
                SobrGsm.balance(getActivity());
                v.setEnabled(false);
                break;

            case R.id.state_common_info_btn:
                showInfoCallDialog();
//	    v.setEnabled(false);
                break;

            case R.id.state_common_info_sms_btn:
                SobrGsm.smsInfo(getActivity());
                v.setEnabled(false);
                break;

            case R.id.state_location_map_btn:
                SobrGsm.map(getActivity());
                v.setEnabled(false);
                break;

            case R.id.state_location_coordinates_btn:
                SobrGsm.coordinates(getActivity());
                v.setEnabled(false);
                break;

            case R.id.state_report_move_btn:
                showReportOnMoveDialog();
                v.setEnabled(false);
                break;

            case R.id.state_service_mode_on_btn:
                SobrGsm.serviceModeOn(getActivity());
                v.setEnabled(false);
                break;

            case R.id.state_service_mode_off_btn:
                SobrGsm.serviceModeOff(getActivity());
                v.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        enableButtons();
        getActivity().registerReceiver(mSmsStatusReceiver,
                new IntentFilter(SobrGsm.ACTION_SMS_SENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mSmsStatusReceiver);
    }

    private void enableButtons() {
        mBalance.setEnabled(true);
//	mInfoCall.setEnabled(true);
        mInfoSms.setEnabled(true);
        mMap.setEnabled(true);
        mCoord.setEnabled(true);
        mServiceOn.setEnabled(true);
        mServiceOff.setEnabled(true);
        mMoveReport.setEnabled(true);
    }

    private void showReportOnMoveDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.state_report_move_btn);
        builder.setCancelable(true);
        int items = mSystemType.equals(Constants.SOBR_GSM510) ? R.array.state_report_on_move_entries_gsm510
        		: R.array.state_report_on_move_entries;
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if(mSystemType.equals(Constants.SOBR_GSM510)){
            		String minutes[] = getResources()
            				.getStringArray(R.array.state_report_on_move_entryvalues_gsm510);
	                SobrGsm.reportOnMoveGSM510(getActivity(), minutes[which]);
            	}else{
	                String minutes[] = getResources().getStringArray(R.array.state_report_on_move_entryvalues);
	                SobrGsm.reportOnMove(getActivity(), minutes[which]);
            	}
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mMoveReport.setEnabled(true);
            }
        });
        
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mMoveReport.setEnabled(true);
			}
		});

        builder.show();

    }

    private void showInfoCallDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.state_common_info);
        if (mPhoneStatus.equals("not_base_value")) {
            StringBuilder msg = new StringBuilder();
            msg.append(getString(R.string.state_infocall_dialog_msg))
            	.append(" ")
            	.append(getString(R.string.state_infocall_dialog_msg_for_nobase_phone))
            	.append(" ").append(mPinCode).append("09*");

            builder.setMessage(msg.toString());
        } else {
            builder.setMessage(R.string.state_infocall_dialog_msg);
        }
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.state_infocall_dialog_posbtn,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SobrGsm.callInfo(getActivity());
                    }
                });

        builder.show();

    }

    private class InnerSmsStatusReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null
                    && intent.getAction().equals(SobrGsm.ACTION_SMS_SENT)) {
                enableButtons();
            }
        }
    }

}