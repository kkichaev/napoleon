package ru.sobr.app.ui;

import java.util.Arrays;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.telephony.SobrGsm;
import ru.sobr.app.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

public class TabSecurityFragment extends SherlockFragment implements OnClickListener {

    //private static final String TAG = "TabSecurityFragment";
    //private static final boolean DEBUG = false;

    private static final int DIALOG_MIC_ID = 0;

    private String mSystemType = Constants.SOBR_GSM;
    private String mCmd666 = "disable_value";
    private String mCmd999 = "disable_value";
    private String mCmd911 = "disable_value";
    private String mCmd911Title;
    private String mShockSensor = "false";

    private Button mSecurityOn, mSecurityOff, mSecurityWo;
    private Button mEngineLock, mEngineUnloch;
    private Button mSearchOn, mSearchOff;
    private Button mDoors;
    private Button mMic;
    private Button mUserCmd911;
    private Button mSecOn, mSecByHour, mSecOff, mSecSetTime, mSecRange;

    private InnerSmsStatusReceiver mSmsStatusReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String systemType = getArguments().getString(MainActivity.SYSTEMTYPE_KEY);
        if (systemType != null) mSystemType = systemType;

        String cmd666 = getArguments().getString(MainActivity.CMD666_KEY);
        if (cmd666 != null) mCmd666 = cmd666;

        String cmd999 = getArguments().getString(MainActivity.CMD999_KEY);
        if (cmd999 != null) mCmd999 = cmd999;

        String cmd911 = getArguments().getString(MainActivity.CMD911_KEY);
        if (cmd911 != null) mCmd911 = cmd911;

        mCmd911Title = getArguments().getString(MainActivity.CMD911_KEY_TITLE);

        String shockSensor = getArguments().getString(MainActivity.SHOCKSENSOR_KEY);
        if (shockSensor != null) mShockSensor = shockSensor;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //if (DEBUG)Log.d(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_security, container, false);

        mSecurityOn = (Button) layout.findViewById(R.id.security_security_on_btn);
        mSecurityOn.setOnClickListener(this);
        mSecurityOff = (Button) layout.findViewById(R.id.security_security_off_btn);
        mSecurityOff.setOnClickListener(this);
        mSecurityWo = (Button) layout.findViewById(R.id.security_security_wo_btn);
        mSecurityWo.setOnClickListener(this);

        mEngineLock = (Button) layout.findViewById(R.id.security_engine_lock_btn);
        mEngineLock.setOnClickListener(this);
        mEngineUnloch = (Button) layout.findViewById(R.id.security_engine_unlock_btn);
        mEngineUnloch.setOnClickListener(this);

        mSearchOn = (Button) layout.findViewById(R.id.security_search_mode_on_btn);
        mSearchOn.setOnClickListener(this);
        mSearchOff = (Button) layout.findViewById(R.id.security_search_mode_off_btn);
        mSearchOff.setOnClickListener(this);

        mDoors = (Button) layout.findViewById(R.id.security_doors_open_btn);
        mDoors.setOnClickListener(this);

        mMic = (Button) layout.findViewById(R.id.security_mic_on_btn);
        mMic.setOnClickListener(this);

        mUserCmd911 = (Button) layout.findViewById(R.id.security_cmd_911_btn);
        mUserCmd911.setOnClickListener(this);
        
        mSecOn = (Button) layout.findViewById(R.id.sec_on_btn);
        mSecOn.setOnClickListener(this);
        mSecByHour = (Button) layout.findViewById(R.id.sec_by_hour_btn);
        mSecByHour.setOnClickListener(this);
        mSecOff = (Button) layout.findViewById(R.id.sec_off_btn);
        mSecOff.setOnClickListener(this);
        mSecSetTime = (Button) layout.findViewById(R.id.sec_set_time_btn);
        mSecSetTime.setOnClickListener(this);
        mSecRange = (Button) layout.findViewById(R.id.sec_range_btn);
        mSecRange.setOnClickListener(this);

        // Manage views visibility
        boolean securityOnOf = false;
        if (mSystemType.equals(Constants.SOBR_CHIP0103) || mSystemType.equals(Constants.SOBR_G0103)) {
            mSecurityOn.setVisibility(View.GONE);
            mSecurityOff.setVisibility(View.GONE);
            securityOnOf = true;

            layout.findViewById(R.id.security_mic_title).setVisibility(View.GONE);
            mMic.setVisibility(View.GONE);
        }
        if (mShockSensor.equals("false")) {
            mSecurityWo.setVisibility(View.GONE);
        }
        if (securityOnOf && mShockSensor.equals("false")) {
            layout.findViewById(R.id.security_security_title).setVisibility(View.GONE);
        }

        if (!mCmd666.equals("blocking_value")) {
            mEngineLock.setVisibility(View.GONE);
        }
        if (!mCmd999.equals("blocking_off_value")) {
            mEngineUnloch.setVisibility(View.GONE);
        }
        if (!mCmd666.equals("blocking_value") && !mCmd999.equals("blocking_off_value")) {
            layout.findViewById(R.id.security_engine_title).setVisibility(View.GONE);
        }

        if (!mCmd666.equals("search_mode_on_value")) {
            mSearchOn.setVisibility(View.GONE);
        }
        if (!mCmd999.equals("search_mode_off_value")) {
            mSearchOff.setVisibility(View.GONE);
        }
        if (mSystemType.equals(Constants.SOBR_CHIP0103) || mSystemType.equals(Constants.SOBR_G0103)) {
            mSearchOff.setVisibility(View.VISIBLE);
        }else if (!mCmd666.equals("search_mode_on_value") && !mCmd999.equals("search_mode_off_value")) {
            layout.findViewById(R.id.security_search_mode_title).setVisibility(View.GONE);
        }

        if (!mCmd911.equals("doors_unlock_value")) {
            layout.findViewById(R.id.security_doors_open_title).setVisibility(View.GONE);
            mDoors.setVisibility(View.GONE);
        }

        if (!Arrays.asList(getResources().getStringArray(R.array.entryvalues_command_911)).contains(mCmd911)) {
            mUserCmd911.setText(mCmd911);
            mUserCmd911.setVisibility(View.VISIBLE);
            if(mCmd911Title!=null){
                TextView title = (TextView) layout.findViewById(R.id.security_cmd_911_title);
                title.setText(mCmd911Title);
                title.setVisibility(View.VISIBLE);
            }
        }

        if(mSystemType.equals(Constants.SOBR_GSM510)){
        	layout.findViewById(R.id.security_security_wo_btn).setVisibility(View.GONE);
        	layout.findViewById(R.id.security_search_mode_title).setVisibility(View.GONE);
        	layout.findViewById(R.id.security_search_mode_on_btn).setVisibility(View.GONE);
        	layout.findViewById(R.id.security_search_mode_off_btn).setVisibility(View.GONE);
        	
        	if(getArguments().getString(SobrContract.Profiles.GSM510_WORK_MODE).equals("1")){
        		layout.findViewById(R.id.security_engine_title).setVisibility(View.GONE);
        		mEngineLock.setVisibility(View.VISIBLE);
        		mEngineLock.setText(R.string.on);
        		mEngineUnloch.setVisibility(View.VISIBLE);
        		mEngineUnloch.setText(R.string.off);
        		mSecurityOn.setVisibility(View.GONE);
        		mSecurityOff.setVisibility(View.GONE);
        		layout.findViewById(R.id.security_security_title).setVisibility(View.GONE);
        		layout.findViewById(R.id.security_search_mode_title).setVisibility(View.GONE);
        		
        		TextView tv = (TextView) layout.findViewById(R.id.security_engine_title); 
        		tv.setVisibility(View.VISIBLE);
        		tv.setText(R.string.state_search_mode_title);
        		
        	}else{
        		layout.findViewById(R.id.security_security_title).setVisibility(View.VISIBLE);
        		mEngineLock.setVisibility(View.VISIBLE);
        		mEngineUnloch.setVisibility(View.VISIBLE);
        		layout.findViewById(R.id.security_engine_title).setVisibility(View.VISIBLE);
        	}
        }
        
        if(mSystemType.equals(Constants.SOBR_CHIP111213)){
        	layout.findViewById(R.id.security_search_mode_title).setVisibility(View.VISIBLE);
        	mSecurityWo.setVisibility(View.GONE);
        	layout.findViewById(R.id.security_security_title).setVisibility(View.GONE);
        	mSearchOn.setVisibility(View.VISIBLE);
    		mSearchOff.setVisibility(View.VISIBLE);
    		mSecurityOn.setVisibility(View.GONE);
    		mSecurityOff.setVisibility(View.GONE);
    		layout.findViewById(R.id.security_mic_title).setVisibility(View.GONE);
    		mMic.setVisibility(View.GONE);
    		layout.findViewById(R.id.sec_by_move_title).setVisibility(View.VISIBLE);
    		mSecOn.setVisibility(View.VISIBLE);
    		mSecByHour.setVisibility(View.VISIBLE);
    		mSecOff.setVisibility(View.VISIBLE); 
    		mSecSetTime.setVisibility(View.VISIBLE); 
    		mSecRange.setVisibility(View.VISIBLE);
//    		layout.findViewById(R.id.sec_by_move_on_prompt).setVisibility(View.VISIBLE);
//    		layout.findViewById(R.id.sec_by_move_off_prompt).setVisibility(View.VISIBLE);
//    		layout.findViewById(R.id.sec_by_hour_prompt).setVisibility(View.VISIBLE);
//    		layout.findViewById(R.id.sec_set_time_prompt).setVisibility(View.VISIBLE);
//    		layout.findViewById(R.id.sec_set_range_prompt).setVisibility(View.VISIBLE);
        }
        
        mSmsStatusReceiver = new InnerSmsStatusReceiver();
        
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAllButtons();
        getActivity().registerReceiver(mSmsStatusReceiver, new IntentFilter(SobrGsm.ACTION_SMS_SENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mSmsStatusReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.security_security_on_btn:
                SobrGsm.security(getActivity(), SobrGsm.SOBR_GSM_SECURITY_ON);
                v.setEnabled(false);
                break;

            case R.id.security_security_off_btn:
                SobrGsm.security(getActivity(), SobrGsm.SOBR_GSM_SECURITY_OFF);
                v.setEnabled(false);
                break;

            case R.id.security_security_wo_btn:
                SobrGsm.securityWo(getActivity());
                v.setEnabled(false);
                break;

            case R.id.security_engine_lock_btn:
            	if(mSystemType.equals(Constants.SOBR_GSM510) &&
            			getArguments().getString(SobrContract.Profiles.GSM510_WORK_MODE).equals("1"))
            		new PromtpDlg(getString(R.string.searh_on_prompt)).
            		show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	
                SobrGsm.engineLock(getActivity());
                v.setEnabled(false);
                break;

            case R.id.security_engine_unlock_btn:
                SobrGsm.engineUnlock(getActivity());
                v.setEnabled(false);
                break;

            case R.id.security_search_mode_on_btn:
            	new PromtpDlg(getString(R.string.searh_on_prompt)).
            		show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
                if (mSystemType.equals(Constants.SOBR_CHIP0103) || mSystemType.equals(Constants.SOBR_G0103)) {
                    SobrGsm.searchModeForType3(getActivity(), SobrGsm.SOBR_GSM_SEARCH_MODE_ON);
                } else {
                    SobrGsm.searchMode(getActivity(), SobrGsm.SOBR_GSM_SEARCH_MODE_ON);
                }
                v.setEnabled(false);
                break;

            case R.id.security_search_mode_off_btn:
                if (mSystemType.equals(Constants.SOBR_CHIP0103) || mSystemType.equals(Constants.SOBR_G0103)) {
                    SobrGsm.searchModeForType3(getActivity(), SobrGsm.SOBR_GSM_SEARCH_MODE_OFF);
                } else {
                    SobrGsm.searchMode(getActivity(), SobrGsm.SOBR_GSM_SEARCH_MODE_OFF);
                }
                v.setEnabled(false);
                break;

            case R.id.security_doors_open_btn:
                SobrGsm.doorsOpen(getActivity());
                v.setEnabled(false);
                break;

            case R.id.security_mic_on_btn:
                onCreateDialog(DIALOG_MIC_ID);
                v.setEnabled(false);
                break;

            case R.id.security_cmd_911_btn:
                SobrGsm.userCommand(getActivity(), "911");
                v.setEnabled(false);
                break;
                
            case R.id.sec_on_btn:
            	new PromtpDlg(getString(R.string.sec_by_move_on_prompt)).
        			show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	v.setEnabled(SobrGsm.secByMoveOn(getActivity()));
            	break;
            	
            case R.id.sec_by_hour_btn:
            	new PromtpDlg(getString(R.string.sec_by_hour_prompt)).
    				show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	v.setEnabled(SobrGsm.secByHour(getActivity()));
            	break;
            	
            case R.id.sec_off_btn:
            	new PromtpDlg(getString(R.string.sec_by_move_off_prompt)).
					show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	v.setEnabled(SobrGsm.secByMoveOff(getActivity()));
            	break;
            	
            case R.id.sec_set_time_btn:
            	DialogFragment setTimeDlg = new StartDlg();
            	setTimeDlg.show(getActivity().getSupportFragmentManager(), 
            			setTimeDlg.getClass().toString());
            	
            	new PromtpDlg(getString(R.string.sec_set_time_prompt)).
				show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	break;
            	
            case R.id.sec_range_btn:
            	DialogFragment rangeDlg = new RangeDlg();
            	rangeDlg.show(getActivity().getSupportFragmentManager(), rangeDlg.getClass().toString());
            	new PromtpDlg(getString(R.string.sec_set_range_prompt)).
					show(getActivity().getSupportFragmentManager(), PromtpDlg.class.toString());
            	break;
            	
            default:
                break;
        }
    }
    
    class RangeDlg extends DialogFragment{
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle(R.string.sec_range_title);
    		builder.setItems(R.array.time_range_entries, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String val = (String) ((AlertDialog)dialog).getListView().getAdapter().getItem(which);
					SobrGsm.secSetRange(getActivity(), val);
				}
			});
    		
    		return builder.create();
    	}
    }
    
    public static class FinishDlg extends StartDlg{
    	public int h = 0;
    	public int m = 0;
    	
    	@Override
    	protected void okDone() {
    		StringBuilder sb = new StringBuilder();
			adjustIntegerVal(h, sb);
			adjustIntegerVal(m, sb);
			String start = sb.toString();
			sb.setLength(0);
			adjustIntegerVal(tpTime.getCurrentHour(), sb);
			adjustIntegerVal(tpTime.getCurrentMinute(), sb);
			String finish = sb.toString();
			SobrGsm.secSetByHour(getActivity(), start, finish);
			dismiss();
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    			Bundle savedInstanceState) {
    		
    		View result = super.onCreateView(inflater, container, savedInstanceState);
    		getDialog().setTitle(R.string.finish);
    		return result;
    	}
    }
    
    public static class StartDlg extends DialogFragment{
    	TimePicker tpTime;
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    			Bundle savedInstanceState) {
    		View result = inflater.inflate(R.layout.set_time_dlg, container);
    		tpTime = (TimePicker) result.findViewById(R.id.tpTime);
    		tpTime.setIs24HourView(true);
    		tpTime.setCurrentHour(0);
    		tpTime.setCurrentMinute(0);
    		
    		getDialog().setTitle(R.string.sec_set_time_title);
    		result.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					okDone();
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
    	
    	protected void adjustIntegerVal(int val, StringBuilder sb){
    		if(val < 10)
    			sb.append("0");
    		sb.append(val);
    	}
    	
    	protected void okDone(){
    		FinishDlg finishDlg = new FinishDlg();
    		finishDlg.h = tpTime.getCurrentHour();
    		finishDlg.m = tpTime.getCurrentMinute();
    		finishDlg.show(getActivity().getSupportFragmentManager(), 
    				finishDlg.getClass().toString());
    		dismiss();
    	}
    }

    protected void enableAllButtons() {
        mSecurityOn.setEnabled(true);
        mSecurityOff.setEnabled(true);
        mSecurityWo.setEnabled(true);
        mEngineLock.setEnabled(true);
        mEngineUnloch.setEnabled(true);
        mSearchOn.setEnabled(true);
        mSearchOff.setEnabled(true);
        mDoors.setEnabled(true);
        mMic.setEnabled(true);
        mUserCmd911.setEnabled(true);
    }

    protected Dialog onCreateDialog(int id) {

        Dialog dialog;
        switch (id) {
            case DIALOG_MIC_ID: {


                String text = getResources().getString(R.string.security_mic_dialog_message);
                StringBuilder messageText = new StringBuilder(text).append(" ");
                if (getArguments().getString(MainActivity.PHONESTATUS_KEY).equals("not_base_value")) {
                    messageText.append(getArguments().getString(MainActivity.PINCODE_KEY));
                }
                messageText.append(getResources().getString(R.string.security_mic_dialog_message_end));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.security_mic);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setMessage(messageText.toString());
                builder.setCancelable(true);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SobrGsm.callMic(getActivity());
                        dialog.dismiss();
                        mMic.setEnabled(true);
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mMic.setEnabled(true);
                    }
                });

                dialog = builder.create();
                dialog.show();
                break;
            }
            default: {
                dialog = null;
            }
        }

        return dialog;

    }

    private class InnerSmsStatusReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null
                    && intent.getAction().equals(SobrGsm.ACTION_SMS_SENT)) {
                enableAllButtons();
            }
        }
    }

    /*public String toUpperCase(String str) {
    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }*/

}