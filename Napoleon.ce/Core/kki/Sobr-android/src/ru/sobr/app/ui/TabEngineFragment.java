package ru.sobr.app.ui;

import java.util.Arrays;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.telephony.SobrGsm;
import ru.sobr.app.ui.TabSecurityFragment.StartDlg;
import ru.sobr.app.utils.Constants;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragment;

public class TabEngineFragment extends SherlockFragment implements
        OnClickListener {

    // private static final String TAG = "TabEngineFragment";
    // private static final boolean DEBUG = false;

    private String mPhoneStatus = "base_value";
    private String mPinCode = "";
    private String mCmd123 = "disable_value";
    private String mCmd456 = "disable_value";
    private String mCmd789 = "disable_value";
    private String mCmd777 = "disable_value";
    private String mCmd999 = "disable_value";

    private String mCmd123Title = null;
    private String mCmd456Title = null;
    private String mCmd789Title = null;
    private String mCmd777Title = null;
    private String mCmd999Title = null;

    private Button mEngine, mTimer, mSiren, mGate;
    private Button mPreheaterOn, mPreheaterOff;
    private Button mHeaterOn, mHeaterOff, mFindOnParking;

    private Button mExecutionUnitOn, mExecutionUnitOff;

    private Button mUserCmd123, mUserCmd456, mUserCmd789, mUserCmd777,
            mUserCmd999, mCmdCh1, mCmdCh2, mCmdCh3, mCmdCh4;

    private InnerSmsStatusReceiver mSmsStatusReceiver;
    private TextView tvCh1, tvCh2, tvCh3, tvCh4;
    private Button btnAlarm;
	private String systemType = "";
	private static int profileID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String phoneStatus = getArguments().getString(
                MainActivity.PHONESTATUS_KEY);
        if (phoneStatus != null)
            mPhoneStatus = phoneStatus;

        String pinCode = getArguments().getString(MainActivity.PINCODE_KEY);
        if (pinCode != null)
            mPinCode = pinCode;

        String cmd123 = getArguments().getString(MainActivity.CMD123_KEY);
        if (cmd123 != null)
            mCmd123 = cmd123;

        String cmd456 = getArguments().getString(MainActivity.CMD456_KEY);
        if (cmd456 != null)
            mCmd456 = cmd456;

        String cmd789 = getArguments().getString(MainActivity.CMD789_KEY);
        if (cmd789 != null)
            mCmd789 = cmd789;

        String cmd777 = getArguments().getString(MainActivity.CMD777_KEY);
        // Log.d("ENGINE", "cmd777 " + cmd777);
        if (cmd777 != null)
            mCmd777 = cmd777;

        String cmd999 = getArguments().getString(MainActivity.CMD999_KEY);
        if (cmd999 != null)
            mCmd999 = cmd999;

        mCmd123Title = getArguments().getString(MainActivity.CMD123_KEY_TITLE);
        mCmd456Title = getArguments().getString(MainActivity.CMD456_KEY_TITLE);
        mCmd789Title = getArguments().getString(MainActivity.CMD789_KEY_TITLE);
        mCmd777Title = getArguments().getString(MainActivity.CMD777_KEY_TITLE);
        mCmd999Title = getArguments().getString(MainActivity.CMD999_KEY_TITLE);
        systemType = getArguments().getString(MainActivity.SYSTEMTYPE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // if(DEBUG)Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_engine, container,
                false);
        // ActionBar ab = getSherlockActivity().getSupportActionBar();

        mEngine = (Button) layout.findViewById(R.id.device_engine_start_btn);
        mEngine.setOnClickListener(this);
        mTimer = (Button) layout.findViewById(R.id.device_timer_set_btn);
        mTimer.setOnClickListener(this);
        mSiren = (Button) layout.findViewById(R.id.device_siren_on_btn);
        mSiren.setOnClickListener(this);
        mGate = (Button) layout.findViewById(R.id.device_gate_open_close_btn);
        mGate.setOnClickListener(this);

        mPreheaterOn = (Button) layout.findViewById(R.id.device_preheater_on);
        mPreheaterOn.setOnClickListener(this);
        mPreheaterOff = (Button) layout.findViewById(R.id.device_preheater_off);
        mPreheaterOff.setOnClickListener(this);

        mHeaterOn = (Button) layout.findViewById(R.id.device_heater_on_btn);
        mHeaterOn.setOnClickListener(this);
        mHeaterOff = (Button) layout.findViewById(R.id.device_heater_off_btn);
        mHeaterOff.setOnClickListener(this);

        mUserCmd123 = (Button) layout
                .findViewById(R.id.device_cmd_123_user_btn);
        mUserCmd123.setOnClickListener(this);
        mUserCmd456 = (Button) layout
                .findViewById(R.id.device_cmd_456_user_btn);
        mUserCmd456.setOnClickListener(this);
        mUserCmd789 = (Button) layout
                .findViewById(R.id.device_cmd_789_user_btn);
        mUserCmd789.setOnClickListener(this);
        mUserCmd777 = (Button) layout
                .findViewById(R.id.device_cmd_777_user_btn);
        mUserCmd777.setOnClickListener(this);
        mUserCmd999 = (Button) layout
                .findViewById(R.id.device_cmd_999_user_btn);
        mUserCmd999.setOnClickListener(this);
        mCmdCh1 = (Button) layout.findViewById(R.id.ch1_btn);
        mCmdCh1.setOnClickListener(this);
        mCmdCh2 = (Button) layout.findViewById(R.id.ch2_btn);
        mCmdCh2.setOnClickListener(this);
        mCmdCh3 = (Button) layout.findViewById(R.id.ch3_btn);
        mCmdCh3.setOnClickListener(this);
        mCmdCh4 = (Button) layout.findViewById(R.id.ch4_btn);
        mCmdCh4.setOnClickListener(this);

        mExecutionUnitOn = (Button) layout
                .findViewById(R.id.device_executionunit_on_btn);
        mExecutionUnitOn.setOnClickListener(this);
        mExecutionUnitOff = (Button) layout
                .findViewById(R.id.device_executionunit_off_btn);
        mExecutionUnitOff.setOnClickListener(this);
        mFindOnParking = (Button) layout.findViewById(R.id.find_on_parking_btn);
        mFindOnParking.setOnClickListener(this);

        tvCh1 = (TextView) layout.findViewById(R.id.ch1_title);
        tvCh2 = (TextView) layout.findViewById(R.id.ch2_title);
        tvCh3 = (TextView) layout.findViewById(R.id.ch3_title);
        tvCh4 = (TextView) layout.findViewById(R.id.ch4_title);
        layout.findViewById(R.id.startpreheater_btn).setOnClickListener(this);
        btnAlarm = (Button) layout.findViewById(R.id.btnAlarm);
        btnAlarm.setOnClickListener(this);
        
        // TODO set custom command title here
        if (!Arrays.asList(
                getResources().getStringArray(R.array.entryvalues_command_123))
                .contains(mCmd123)) {
            mUserCmd123.setText(mCmd123);
            mUserCmd123.setVisibility(View.VISIBLE);
            TextView title = (TextView) layout
                    .findViewById(R.id.device_cmd_123_user_title);
            title.setText(mCmd123Title);
            title.setVisibility(View.VISIBLE);
        }
        if (!Arrays.asList(
                getResources().getStringArray(R.array.entryvalues_command_456))
                .contains(mCmd456) || "custom_value".equals(mCmd456)) {
            mUserCmd456.setText(R.string.device_cmd_456_btn);
            mUserCmd456.setVisibility(View.VISIBLE);
            TextView title = (TextView) layout
                    .findViewById(R.id.device_cmd_456_user_title);
            title.setText(mCmd456Title);
            title.setVisibility(View.VISIBLE);
        }
        if (!Arrays.asList(
                getResources().getStringArray(R.array.entryvalues_command_789))
                .contains(mCmd789) || "custom_value".equals(mCmd789)) {
            mUserCmd789.setText(R.string.device_cmd_789_btn);
            mUserCmd789.setVisibility(View.VISIBLE);
            TextView title = (TextView) layout
                    .findViewById(R.id.device_cmd_789_user_title);
            title.setText(mCmd789Title);
            title.setVisibility(View.VISIBLE);
        }
        // Log.d("ENGINE", "mCmd777 " + mCmd777);
        if (!Arrays.asList(
                getResources().getStringArray(R.array.entryvalues_command_777))
                .contains(mCmd777)) {
            mUserCmd777.setText(mCmd777);
            mUserCmd777.setVisibility(View.VISIBLE);
            TextView title = (TextView) layout
                    .findViewById(R.id.device_cmd_777_user_title);
            title.setText(mCmd777Title);
            title.setVisibility(View.VISIBLE);
        }
        if (!Arrays.asList(
                getResources().getStringArray(R.array.entryvalues_command_999))
                .contains(mCmd999)) {
            mUserCmd999.setText(mCmd999);
            mUserCmd999.setVisibility(View.VISIBLE);
            TextView title = (TextView) layout
                    .findViewById(R.id.device_cmd_999_user_title);
            title.setText(mCmd999Title);
            title.setVisibility(View.VISIBLE);
        }

        if (!mCmd123.equals("adz_value")) {
            layout.findViewById(R.id.device_engine_title).setVisibility(
                    View.GONE);
            mEngine.setVisibility(View.GONE);
            layout.findViewById(R.id.device_timer_title).setVisibility(
                    View.GONE);
            mTimer.setVisibility(View.GONE);
        }
        if (!mCmd123.equals("siren_value") && !mCmd456.equals("siren_value")) {
            layout.findViewById(R.id.device_siren_on_title).setVisibility(
                    View.GONE);
            mSiren.setVisibility(View.GONE);
        }
        if (!mCmd123.equals("gate_value") && !mCmd456.equals("gate_value")
                && !mCmd777.equals("gate_value")) {
            layout.findViewById(R.id.device_gate_title)
                    .setVisibility(View.GONE);
            mGate.setVisibility(View.GONE);
        }

        if (!mCmd123.equals("ppp_on_value") && !mCmd456.equals("ppp_on_value")) {
            mPreheaterOn.setVisibility(View.GONE);
        }
        if (!mCmd789.equals("ppp_off_value")) {
            mPreheaterOff.setVisibility(View.GONE);
        }
        if (!mCmd123.equals("ppp_on_value") && !mCmd456.equals("ppp_on_value")
                && !mCmd789.equals("ppp_off_value")) {
            layout.findViewById(R.id.device_preheater_title).setVisibility(
                    View.GONE);
        }

        if (!mCmd777.equals("heater_on_value")) {
            mHeaterOn.setVisibility(View.GONE);
        }
        if (!mCmd999.equals("heater_off_value")) {
            mHeaterOff.setVisibility(View.GONE);
        }
        if (!mCmd777.equals("heater_on_value")
                && !mCmd999.equals("heater_off_value")) {
            layout.findViewById(R.id.device_heater_title).setVisibility(
                    View.GONE);
        }

        if (!mCmd456.equals("execution_unit_on_value")) {
            mExecutionUnitOn.setVisibility(View.GONE);
        }
        if (!mCmd789.equals("execution_unit_off_value")) {
            mExecutionUnitOff.setVisibility(View.GONE);
        }
        if (!mCmd456.equals("execution_unit_on_value")
                && !mCmd789.equals("execution_unit_off_value")) {
            layout.findViewById(R.id.device_executionunit_title).setVisibility(
                    View.GONE);
        }

        if(systemType.equals(Constants.SOBR_GSM510)){
        	layout.findViewById(R.id.device_preheater_title).setVisibility(View.GONE);
    		mPreheaterOff.setVisibility(View.GONE);
			mPreheaterOn.setVisibility(View.GONE);	
			layout.findViewById(R.id.device_timer_title).setVisibility(View.GONE);
			mTimer.setVisibility(View.GONE);
			
        	if(getArguments().getString(SobrContract.Profiles.GSM510_WORK_MODE).equals("1")){
        		layout.findViewById(R.id.device_engine_title).setVisibility(View.GONE);
        		layout.findViewById(R.id.device_engine_start_btn).setVisibility(View.GONE);
        	}else{
        		mEngine.setVisibility(View.VISIBLE);
        		layout.findViewById(R.id.find_on_parking_title).setVisibility(View.VISIBLE);
        		mFindOnParking.setVisibility(View.VISIBLE);

        		if(getArguments().getBoolean(SobrContract.Profiles.PREHEATER)){
        			layout.findViewById(R.id.startpreheater_title).setVisibility(View.VISIBLE);
        			layout.findViewById(R.id.startpreheater_btn).setVisibility(View.VISIBLE);
        		}
        		
        		int ch = getArguments().getInt(SobrContract.Profiles.CHANELS);
        		String ch1cmd = getArguments().getString(SobrContract.Profiles.CMD1);
        		String ch2cmd = getArguments().getString(SobrContract.Profiles.CMD2);
        		String ch3cmd = getArguments().getString(SobrContract.Profiles.CMD3);
        		String ch4cmd = getArguments().getString(SobrContract.Profiles.CMD4);
        		String ch1key = getArguments().getString(SobrContract.Profiles.KEY1);
        		String ch2key = getArguments().getString(SobrContract.Profiles.KEY2);
        		String ch3key = getArguments().getString(SobrContract.Profiles.KEY3);
        		String ch4key = getArguments().getString(SobrContract.Profiles.KEY4);
        		
        		tvCh1.setVisibility((ch & 1 ) == 1 ? View.VISIBLE : View.GONE);
        		if(ch1cmd.trim().length() > 0)
        			tvCh1.setText(ch1cmd);
        		mCmdCh1.setVisibility((ch & 1 ) == 1 ? View.VISIBLE : View.GONE);
        		if(ch1key.trim().length() > 0)
        			mCmdCh1.setText(ch1key);
        		
        		tvCh2.setVisibility((ch & 2 ) == 2 ? View.VISIBLE : View.GONE);
        		if(ch2cmd.trim().length() > 0)
        			tvCh2.setText(ch2cmd);
        		mCmdCh2.setVisibility((ch & 2 ) == 2 ? View.VISIBLE : View.GONE);
        		if(ch2key.trim().length() > 0)
        			mCmdCh2.setText(ch2key);
        		
        		tvCh3.setVisibility((ch & 4 ) == 4 ? View.VISIBLE : View.GONE);
        		if(ch3cmd.trim().length() > 0)
        			tvCh3.setText(ch3cmd);
        		mCmdCh3.setVisibility((ch & 4 ) == 4 ? View.VISIBLE : View.GONE);
        		if(ch3key.trim().length() > 0)
        			mCmdCh3.setText(ch3key);
        		
        		tvCh4.setVisibility((ch & 8 ) == 8 ? View.VISIBLE : View.GONE);
        		if(ch4cmd.trim().length() > 0)
        			tvCh4.setText(ch4cmd);
        		mCmdCh4.setVisibility((ch & 8 ) == 8 ? View.VISIBLE : View.GONE);
        		if(ch4key.trim().length() > 0)
        			mCmdCh4.setText(ch4key);
        	}
        }
        
        if(getArguments().getString(MainActivity.SYSTEMTYPE_KEY).equals(Constants.SOBR_GSM)){
        	if(getArguments().getBoolean(SobrContract.Profiles.ALARM))
        	{
        		btnAlarm.setVisibility(View.VISIBLE);
        		layout.findViewById(R.id.tvAlarmTitle).setVisibility(View.VISIBLE);
        	}
        }
        
        profileID = getArguments().getInt(SobrContract.Profiles._ID);
        mSmsStatusReceiver = new InnerSmsStatusReceiver();

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_engine_start_btn:
                SobrGsm.engineStart(getActivity());
                v.setEnabled(false);
                break;

            case R.id.device_timer_set_btn:
                showSetTimerDialog();
                v.setEnabled(false);
                break;

            case R.id.device_siren_on_btn:
                if (mCmd123.equals("siren_value")) {
                    SobrGsm.sirenOn(getActivity(), "123");
                } else {
                    SobrGsm.sirenOn(getActivity(), "456");
                }
                v.setEnabled(false);
                break;

            case R.id.device_gate_open_close_btn:
                StringBuilder msg = new StringBuilder();
                if (mPhoneStatus.equals("not_base_value")) {
                    msg.append(mPinCode);
                }

                if (mCmd123.equals("gate_value")) {
                    msg.append("123");
                } else if (mCmd456.equals("gate_value")) {
                    msg.append("456");
                } else if (mCmd777.equals("gate_value")) {
                    msg.append("777");
                }

                SobrGsm.gateOpenClose(getActivity(), msg.toString());

                v.setEnabled(false);
                break;

            case R.id.device_preheater_on:
                if (mCmd123.equals("ppp_on_value")) {
                    SobrGsm.preheaterOn(getActivity(), "123");
                } else {
                    SobrGsm.preheaterOn(getActivity(), "456");
                }
                v.setEnabled(false);
                break;

            case R.id.device_preheater_off:
                SobrGsm.preheaterOff(getActivity());
                v.setEnabled(false);
                break;

            case R.id.device_heater_on_btn:
                SobrGsm.heater(getActivity(), SobrGsm.SOBR_GSM_HEATER_ON);
                v.setEnabled(false);
                break;

            case R.id.device_heater_off_btn:
                SobrGsm.heater(getActivity(), SobrGsm.SOBR_GSM_HEATER_OFF);
                v.setEnabled(false);
                break;

            case R.id.device_cmd_123_user_btn:
                SobrGsm.userCommand(getActivity(), "123");
                v.setEnabled(false);
                break;

            case R.id.device_cmd_456_user_btn:
                SobrGsm.userCommand(getActivity(), "456");
                v.setEnabled(false);
                break;

            case R.id.device_cmd_789_user_btn:
                SobrGsm.userCommand(getActivity(), "789");
                v.setEnabled(false);
                break;

            case R.id.device_cmd_777_user_btn:
                StringBuilder custom777 = new StringBuilder();
                if (mPhoneStatus.equals("not_base_value")) {
                    custom777.append(mPinCode);
                }
                SobrGsm.gateOpenClose(getActivity(), custom777.append("777")
                        .toString());
                v.setEnabled(false);
                break;

            case R.id.device_cmd_999_user_btn:
                SobrGsm.userCommand(getActivity(), "999");
                v.setEnabled(false);
                break;

            case R.id.device_executionunit_on_btn:
                SobrGsm.executinUnit(getActivity(),
                        SobrGsm.SOBR_GSM_EXECUTIONUNIT_ON);
                v.setEnabled(false);
                break;

            case R.id.device_executionunit_off_btn:
                SobrGsm.executinUnit(getActivity(),
                        SobrGsm.SOBR_GSM_EXECUTIONUNIT_OFF);
                v.setEnabled(false);
                break;
                
            case R.id.find_on_parking_btn:
            	v.setEnabled(SobrGsm.findOnParking(getActivity()));
            	break;
            	
            case R.id.ch1_btn:
            	v.setEnabled(SobrGsm.channelSms(getActivity(), 1));
            	break;
            	
            case R.id.ch2_btn:
            	v.setEnabled(SobrGsm.channelSms(getActivity(), 2));
            	break;
            	
            case R.id.ch3_btn:
            	v.setEnabled(SobrGsm.channelSms(getActivity(), 3));
            	break;
            	
            case R.id.ch4_btn:
            	v.setEnabled(SobrGsm.channelSms(getActivity(), 4));
            	break;
            	
            case R.id.startpreheater_btn:
            	v.setEnabled(SobrGsm.startPreheater(getActivity()));
            	break;
            	
            case R.id.btnAlarm:
            	DialogFragment setTimeDlg = new AlarmDlg();
            	setTimeDlg.show(getActivity().getSupportFragmentManager(), 
            			setTimeDlg.getClass().toString());
            	new PromtpDlg(getString(R.string.alarm_hint)).
    				show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	break;
            default:
                break;
        }
    }
    
    public static class AlarmDlg extends DialogFragment{
    	TimePicker tpTime;
    	final String ALARM = "alarm"; 
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    			Bundle savedInstanceState) {
    		int min = 0;
    		int hour = 0;
    		
    		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
    		StringBuilder sbID = new StringBuilder();
    		sbID.append(ALARM);
    		sbID.append(profileID);
    		String time = pref.getString(sbID.toString(), "");
    		
    		if(time.length() > 0){
    			String[] tarr = time.split(":");
    			
    			if(tarr.length >= 2)
    			try{
    				min = Integer.parseInt(tarr[0]);
    				hour = Integer.parseInt(tarr[1]);
    			}catch(Exception e){}
    		}
    		
    		View result = inflater.inflate(R.layout.set_time_dlg, container);
    		tpTime = (TimePicker) result.findViewById(R.id.tpTime);
    		tpTime.setIs24HourView(true);
    		tpTime.setCurrentHour(hour);
    		tpTime.setCurrentMinute(min);
    		
    		getDialog().setTitle(R.string.sec_set_time_title);
    		result.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Editor ed = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
					StringBuilder sb = new StringBuilder();
					sb.append(tpTime.getCurrentMinute());
					sb.append(":");
					sb.append(tpTime.getCurrentHour());
					
					StringBuilder sbID = new StringBuilder();
					sbID.append(ALARM);
					sbID.append(profileID);
					
					ed.putString(sbID.toString(), sb.toString());
					ed.commit();
					
					StringBuilder val = new StringBuilder();
					val.append(intToStr(tpTime.getCurrentHour()))
						.append(intToStr(tpTime.getCurrentMinute()));
					
					SobrGsm.secSetAlarm(getActivity(), val.toString());
					dismiss();
				}
			});
    		
    		result.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
    		
    		getDialog().setTitle(R.string.start);
    		return result;
    	}
    	
    	private String intToStr(int val){
    		StringBuilder result = new StringBuilder();
    		if(val < 10)
    			result.append("0");
    		result.append(val);
    		return result.toString();
    	}
    }

    @Override
    public void onResume() {
        super.onResume();
        // if (DEBUG)Log.d(TAG, "onResume");
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
        mEngine.setEnabled(true);
        mTimer.setEnabled(true);
        mSiren.setEnabled(true);
        mGate.setEnabled(true);
        mPreheaterOn.setEnabled(true);
        mPreheaterOff.setEnabled(true);
        mHeaterOn.setEnabled(true);
        mHeaterOff.setEnabled(true);
        mExecutionUnitOn.setEnabled(true);
        mExecutionUnitOff.setEnabled(true);

        mUserCmd123.setEnabled(true);
        mUserCmd456.setEnabled(true);
        mUserCmd789.setEnabled(true);
        mUserCmd777.setEnabled(true);
        mUserCmd999.setEnabled(true);
    }

    private void showSetTimerDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.device_timer_set_btn);
        builder.setCancelable(true);
        builder.setSingleChoiceItems(R.array.engine_timer_entries, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hours[] = getResources().getStringArray(
                                R.array.engine_timer_entryvalues);
                        SobrGsm.setTimer(getActivity(), hours[which]);
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mTimer.setEnabled(true);
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

	/*
     * public String toUpperCase(String str) { return
	 * Character.toUpperCase(str.charAt(0)) + str.substring(1); }
	 */

}