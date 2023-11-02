package ru.sobr.app.ui;

import java.util.Calendar;

import ru.sobr.app.R;
import ru.sobr.app.provider.SobrContract;
import ru.sobr.app.telephony.SobrGsm;
import ru.sobr.app.utils.Constants;
import ru.sobr.app.utils.DefaultProfiles;
import android.R.color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class CustomProfileDetailActivity extends SherlockFragmentActivity
        implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener {

    // public static final String TAG = "CustomProfileDetailActivity";
    // public static final boolean DEBUG = false;

    private final int DAILYMODE_DIALOG_ID = 0;
    private final int WEEKLYMODE_DIALOG_ID = 1;

    private Button mCarRemote, mKeys, mLable1, mLable2, mLable3, mDailyMode,
            mWeeklyMode, mCommTime, mTimeZone, mFreqConn, mWorkSch,
            mFifthNumber, mMilAge, mResendInputSms;

    private Spinner mCommand123, mCommand456, mCommand789, mCommand666,
            mCommand777, mCommand999, mCommand09, mCommand911, mWorkMode;

    private EditText mCustomName123,
            mCustomName777, mCustomName999, mCustomName911, mFifthNumberEd,
            mChCmd1, mChKey1, mChCmd2, mChKey2, mChCmd3, mChKey3, mChCmd4, mChKey4;

    private EditText mCustomTitle123, mCustomTitle456, mCustomTitle789,
            mCustomTitle777, mCustomTitle999, mCustomTitle911;

    private CheckBox mGpsReceiver, mReportOnMove, mShockSensor, mImmobilizer,
    	mPreheater, mCh1, mCh2, mCh3, mCh4, cbAlarm;
    private Button mMilAgeKnow;
    private InnerSmsStatusReceiver mSmsStatusReceiver;
	private static String systemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail_custom);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mSmsStatusReceiver = new InnerSmsStatusReceiver();

        mCarRemote = (Button) findViewById(R.id.profile_program_car_remote);
        mCarRemote.setOnClickListener(this);
        mKeys = (Button) findViewById(R.id.profile_program_keys);
        mKeys.setOnClickListener(this);
        mLable1 = (Button) findViewById(R.id.profile_program_label_1);
        mLable1.setOnClickListener(this);
        mLable2 = (Button) findViewById(R.id.profile_program_label_2);
        mLable2.setOnClickListener(this);
        mLable3 = (Button) findViewById(R.id.profile_program_label_3);
        mLable3.setOnClickListener(this);
        mDailyMode = (Button) findViewById(R.id.profile_daily_mode);
        mDailyMode.setOnClickListener(this);
        mWeeklyMode = (Button) findViewById(R.id.profile_weekly_mode);
        mWeeklyMode.setOnClickListener(this);
        mCommTime = (Button) findViewById(R.id.profile_communication_time);
        mCommTime.setOnClickListener(this);
        mTimeZone = (Button) findViewById(R.id.time_zone_btn);
        mTimeZone.setOnClickListener(this);
        mFreqConn = (Button) findViewById(R.id.freq_conn_btn);
        mFreqConn.setOnClickListener(this);
        mWorkSch = (Button) findViewById(R.id.work_schedule_btn);
        mWorkSch.setOnClickListener(this);
        mFifthNumber = (Button) findViewById(R.id.fifth_phone_number_btn);
        mFifthNumber.setOnClickListener(this);
        mMilAge = (Button) findViewById(R.id.milage_btn);
        mMilAge.setOnClickListener(this);
        mResendInputSms = (Button) findViewById(R.id.resend_input_sms_btn);
        mResendInputSms.setOnClickListener(this);
        
        mCommand123 = (Spinner) findViewById(R.id.command_123_spinner);
        mCommand123.setOnItemSelectedListener(this);
        mCommand456 = (Spinner) findViewById(R.id.command_456_spinner);
        mCommand456.setOnItemSelectedListener(this);
        mCommand789 = (Spinner) findViewById(R.id.command_789_spinner);
        mCommand789.setOnItemSelectedListener(this);
        mCommand666 = (Spinner) findViewById(R.id.command_666_spinner);
        mCommand777 = (Spinner) findViewById(R.id.command_777_spinner);
        mCommand777.setOnItemSelectedListener(this);
        mCommand999 = (Spinner) findViewById(R.id.command_999_spinner);
        mCommand999.setOnItemSelectedListener(this);
        mCommand09 = (Spinner) findViewById(R.id.command_09_spinner);
        mCommand911 = (Spinner) findViewById(R.id.command_911_spinner);
        mCommand911.setOnItemSelectedListener(this);
        mWorkMode = (Spinner) findViewById(R.id.work_mode_spinner);
        mWorkMode.setOnItemSelectedListener(this);

        mCustomName123 = (EditText) findViewById(R.id.command_123_custom);
        mCustomName777 = (EditText) findViewById(R.id.command_777_custom);
        mCustomName999 = (EditText) findViewById(R.id.command_999_custom);
        mCustomName911 = (EditText) findViewById(R.id.command_911_custom);
        mFifthNumberEd = (EditText) findViewById(R.id.fifth_phone_number_text);
        mChCmd1 = (EditText) findViewById(R.id.ch1_cmd_ed);
        mChKey1 = (EditText) findViewById(R.id.ch1_key_ed);
        mChCmd2 = (EditText) findViewById(R.id.ch2_cmd_ed);
        mChKey2 = (EditText) findViewById(R.id.ch2_key_ed);
        mChCmd3 = (EditText) findViewById(R.id.ch3_cmd_ed);
        mChKey3 = (EditText) findViewById(R.id.ch3_key_ed);
        mChCmd4 = (EditText) findViewById(R.id.ch4_cmd_ed);
        mChKey4 = (EditText) findViewById(R.id.ch4_key_ed);

        mCustomTitle123 = (EditText) findViewById(R.id.command_123_custom_title);
        mCustomTitle456 = (EditText) findViewById(R.id.command_456_custom_title);
        mCustomTitle789 = (EditText) findViewById(R.id.command_789_custom_title);
        mCustomTitle777 = (EditText) findViewById(R.id.command_777_custom_title);
        mCustomTitle999 = (EditText) findViewById(R.id.command_999_custom_title);
        mCustomTitle911 = (EditText) findViewById(R.id.command_911_custom_title);

        mGpsReceiver = (CheckBox) findViewById(R.id.ind_profile_gps_receiver_cbx);
        mReportOnMove = (CheckBox) findViewById(R.id.ind_profile_report_on_the_move_cbx);
        mShockSensor = (CheckBox) findViewById(R.id.ind_profile_shock_sensor_cbx);
        mImmobilizer = (CheckBox) findViewById(R.id.ind_profile_presence_of_an_immobilizer_cbx);
        mPreheater = (CheckBox) findViewById(R.id.preheater_cbx);
        mCh1 = (CheckBox) findViewById(R.id.ch1_cbx);
        mCh1.setOnCheckedChangeListener(this);
        mCh2 = (CheckBox) findViewById(R.id.ch2_cbx);
        mCh2.setOnCheckedChangeListener(this);
        mCh3 = (CheckBox) findViewById(R.id.ch3_cbx);
        mCh3.setOnCheckedChangeListener(this);
        mCh4 = (CheckBox) findViewById(R.id.ch4_cbx);
        mCh4.setOnCheckedChangeListener(this);
        mMilAgeKnow = (Button) findViewById(R.id.millage_know_btn);
        mMilAgeKnow.setOnClickListener(this);
        cbAlarm = (CheckBox) findViewById(R.id.cbAlarm);
        cbAlarm.setOnCheckedChangeListener(this);
        
        setViewsVisibility();

        setDefaultViewsData();

        if (getIntent().getExtras().getBoolean(
                ProfileDetailActivity.KEY_PROFILE_EDITMODE)) {
            // if (DEBUG) Log.d(TAG, "EDIT_MODE");
            setViewsData();
        }

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(R.string.profile_user_by_default_btn)
                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        setDefaultViewsData();

                        mCustomName123.setText("");
                        mCustomName777.setText("");
                        mCustomName999.setText("");
                        mCustomName911.setText("");

                        mCustomTitle123.setText("");
                        mCustomTitle456.setText("");
                        mCustomTitle789.setText("");
                        mCustomTitle777.setText("");
                        mCustomTitle999.setText("");
                        mCustomTitle911.setText("");

                        return true;
                    }

                })
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM
                                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add(R.string.profile_user_confirm_btn)
                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Intent resultIntent = getViewsData();
                        if (resultIntent.getExtras() != null) {
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }

                        return true;
                    }
                })
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM
                                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {

        // String systemType =
        // getIntent().getStringExtra(ProfileDetailActivity.KEY_PROFILE_SYSTYPE);
        String systemNumb = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
        String password = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_PASSWORD);
        String pinCode = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_PINCODE);
        switch (v.getId()) {
            case R.id.profile_program_car_remote:
                SobrGsm.profileProgCarRemote(this, systemNumb, password);
                v.setEnabled(false);
                break;

            case R.id.profile_program_keys:
                SobrGsm.profileProgKeys(this, systemNumb, password);
                v.setEnabled(false);
                break;

            case R.id.profile_program_label_1:
                SobrGsm.profileProgLable1(this, systemNumb, password);
                v.setEnabled(false);
                break;

            case R.id.profile_program_label_2:
                SobrGsm.profileProgLable2(this, systemNumb, password);
                v.setEnabled(false);
                break;

            case R.id.profile_program_label_3:
                SobrGsm.profileProgLable3(this, systemNumb, password);
                v.setEnabled(false);
                break;

            case R.id.profile_daily_mode:
                showCommentaryDialog(DAILYMODE_DIALOG_ID);
                break;

            case R.id.profile_weekly_mode:
                showCommentaryDialog(WEEKLYMODE_DIALOG_ID);
                break;

            case R.id.profile_communication_time:
                DialogFragment newFragment = new TimePickerFragment();
                Bundle data = new Bundle();
                data.putString("system_number", systemNumb);
                data.putString("pin_code", pinCode);
                data.putString(ProfileDetailActivity.KEY_PROFILE_PASSWORD, password);
                data.putString(ProfileDetailActivity.KEY_PROFILE_PINCODE, pinCode);
                data.putString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB, systemNumb);
                newFragment.setArguments(data);
                newFragment.show(getSupportFragmentManager(), "timePicker");
                break;
                
            case R.id.time_zone_btn:
            	DialogFragment timeZoneDlg = new TimeZoneDlg();
            	Bundle tzData = new Bundle();
            	tzData.putString(ProfileDetailActivity.KEY_PROFILE_PASSWORD, password);
            	tzData.putString(ProfileDetailActivity.KEY_PROFILE_PINCODE, pinCode);
            	tzData.putString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB, systemNumb);
            	timeZoneDlg.setArguments(tzData);
            	timeZoneDlg.show(getSupportFragmentManager(), timeZoneDlg.getClass().toString());
            	break;
            	
            case R.id.freq_conn_btn:
            	DialogFragment freqConnDlg = new FreqConnDlg();
            	Bundle fcData = new Bundle();
            	fcData.putString(ProfileDetailActivity.KEY_PROFILE_PASSWORD, password);
            	fcData.putString(ProfileDetailActivity.KEY_PROFILE_PINCODE, pinCode);
            	fcData.putString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB, systemNumb);
            	freqConnDlg.setArguments(fcData);
            	freqConnDlg.show(getSupportFragmentManager(), freqConnDlg.getClass().toString());
            	break;
            	
            case R.id.work_schedule_btn:
            	DialogFragment workSchDlg = new WorkSchDlg();
            	Bundle wsData = new Bundle();
            	wsData.putString(ProfileDetailActivity.KEY_PROFILE_PASSWORD, password);
            	wsData.putString(ProfileDetailActivity.KEY_PROFILE_PINCODE, pinCode);
            	wsData.putString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB, systemNumb);
            	workSchDlg.setArguments(wsData);
            	workSchDlg.show(getSupportFragmentManager(), workSchDlg.getClass().toString());
            	break;
            	
            case R.id.fifth_phone_number_btn:
            	SobrGsm.fifthPhoneNumb(this, systemNumb, password, 
            			mFifthNumberEd.getText().toString().trim());
            	break;
            	
            case R.id.milage_btn:
            	String number = mFifthNumberEd.getText().toString().trim();
            	if(number.length() > Constants.LEN_FIFTH_NUMBER){
            		((TextView)findViewById(R.id.fifth_phone_number_title)).setTextColor(
            				getResources().getColor(color.black));
            		number = number.substring(number.length() - Constants.LEN_FIFTH_NUMBER, number.length());
            	
            	    DialogFragment milAgeDlg = new MilAgeDlg();
            		Bundle mlData = new Bundle();
            		mlData.putString(MilAgeDlg.NUMBER, number);
            		mlData.putString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB, systemNumb);
            		milAgeDlg.setArguments(mlData);
            		milAgeDlg.show(getSupportFragmentManager(), milAgeDlg.getClass().toString());
            	}else{
            		((TextView)findViewById(R.id.fifth_phone_number_title)).setTextColor(
            				getResources().getColor(R.color.red));
            		mFifthNumberEd.requestFocus();
            	}
            	break;
            
            case R.id.millage_know_btn:
            	if(mFifthNumberEd.getText().toString().trim().length() > Constants.LEN_FIFTH_NUMBER)
            		v.setEnabled(
            			SobrGsm.millageKnow(this, mFifthNumberEd.getText().toString().trim()));
            	else{
            		((TextView)findViewById(R.id.fifth_phone_number_title)).setTextColor(
            				getResources().getColor(R.color.red));
            		mFifthNumberEd.requestFocus();
            	}
            	break;
            	
            case R.id.resend_input_sms_btn:
            	DialogFragment resendInputDlg = new ResendInputSmsDlg();
            	Bundle riData = new Bundle();
            	riData.putString(ProfileDetailActivity.KEY_PROFILE_PASSWORD, password);
            	riData.putString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB, systemNumb);
            	resendInputDlg.setArguments(riData);
            	resendInputDlg.show(getSupportFragmentManager(), resendInputDlg.getClass().toString());
            	break;
            default:
                break;
        }

    }
    
    public static class ResendInputSmsDlg extends DialogFragment{
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle(R.string.resend_input_sms_title);
    		builder.setItems(R.array.resend_input_sms_entries, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
	            	String password = getArguments().getString(
	                        ProfileDetailActivity.KEY_PROFILE_PASSWORD);
	            	String systemNumb = getArguments().getString(
	                        ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
	            	SobrGsm.resendInputSms(getActivity(), systemNumb, password, which);
				}
			});
    		
    		return builder.create();
    	}
    }
    
    public static class MilAgeDlg extends DialogFragment{
    	public static final String NUMBER = "number";
		private NumberPicker num1;
		private NumberPicker num2;
		private NumberPicker num3;
		private NumberPicker num4;
		private NumberPicker num5;
		private NumberPicker num6;
		
		private void adjustNumberPicker(NumberPicker[] pikers){
    		for(int i = 0; i < pikers.length; i++){
    			pikers[i].setMaxValue(9);
    			pikers[i].setMinValue(0);
    		}
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    			Bundle savedInstanceState) {
    		getDialog().setTitle(R.string.milage_title);
    		View result = inflater.inflate(R.layout.milagedlg, container);
    		num1 = (NumberPicker) result.findViewById(R.id.num1);
    		num2 = (NumberPicker) result.findViewById(R.id.num2);
    		num3 = (NumberPicker) result.findViewById(R.id.num3);
    		num4 = (NumberPicker) result.findViewById(R.id.num4);
    		num5 = (NumberPicker) result.findViewById(R.id.num5);
    		num6 = (NumberPicker) result.findViewById(R.id.num6);
    		
    		adjustNumberPicker(new NumberPicker[]{num1, num2, num3, num4, num5, num6});

    		result.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String number = getArguments().getString(NUMBER);
					String sysNum = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
					StringBuilder sb = new StringBuilder();
					sb.append(num6.getValue()).append(num5.getValue())
						.append(num4.getValue()).append(num3.getValue())
						.append(num2.getValue()).append(num1.getValue());
					
					SobrGsm.profileMillAge(getActivity(), sysNum,  number, sb.toString());
					dismiss();
				}
			});
    		
    		return result;
    	}
    }
    
    public static class WorkSchDlg extends DialogFragment{
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setItems(R.array.work_schedule_entries,
    				new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String phone = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
					if(systemType.equals(Constants.SOBR_CHIP111213)){
						String pincode = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_PINCODE);
						SobrGsm.profileWorkSchedule33(getActivity(), which, phone, pincode);
					}else{
						String pwd = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_PASSWORD);
						SobrGsm.profileWorkSchedule(getActivity(), pwd, which, phone);
					}
					dismiss();
				}
			});
    		
    		Dialog result = builder.create(); 
    		result.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		return result;
    	}
    }
    
    public static class FreqConnDlg extends DialogFragment{
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle(R.string.freq_conn_title);
    		int arr = systemType.equals(Constants.SOBR_CHIP111213) ? R.array.freq_conn_entries_chip111213 :
    			R.array.freq_conn_entries;
    		builder.setItems(arr,
    				new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String number =  getArguments().getString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
					
					if(systemType.equals(Constants.SOBR_CHIP111213)){
						String pin = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_PINCODE);
						SobrGsm.profileFreqConn33(getActivity(), which, number, pin);
					}else{
						String pwd = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_PASSWORD);
						SobrGsm.profileFreqConn(getActivity(), pwd, which, number);
					}
					dismiss();
				}
			});
    		return builder.create();
    	}
    }
    
    public static class TimeZoneDlg extends DialogFragment{
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle(R.string.time_zone_title);
    		builder.setSingleChoiceItems(R.array.time_zone_entries, 4,
    				new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String number = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
					String pincode = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_PINCODE);
					if(systemType.equals(Constants.SOBR_CHIP111213)){
						SobrGsm.profileTimeZone33(getActivity(), Integer.toString(which), number, pincode);
					}else{
						String pwd = getArguments().getString(ProfileDetailActivity.KEY_PROFILE_PASSWORD);
						SobrGsm.profileTimeZone(getActivity(), pwd, Integer.toString(which), number);
					}
					dismiss();
				}
			});
    		return builder.create();
    	}
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        switch (parent.getId()) {
            case R.id.command_123_spinner:
                if (parent.getAdapter().getCount() == pos + 2) {
                    mCustomName123.setVisibility(View.VISIBLE);
                    mCustomTitle123.setVisibility(View.VISIBLE);
                } else {
                    mCustomName123.setVisibility(View.GONE);
                    mCustomName123.setText("");
                    mCustomTitle123.setVisibility(View.GONE);
                    mCustomTitle123.setText("");
                }
                break;
            case R.id.command_456_spinner:
                if (parent.getAdapter().getCount() == pos + 2) {
                    mCustomTitle456.setVisibility(View.VISIBLE);
                } else {
                    mCustomTitle456.setVisibility(View.GONE);
                    mCustomTitle456.setText("");
                }
                break;
            case R.id.command_789_spinner:
                if (parent.getAdapter().getCount() == pos + 2) {
                    mCustomTitle789.setVisibility(View.VISIBLE);
                } else {
                    mCustomTitle789.setVisibility(View.GONE);
                    mCustomTitle789.setText("");
                }
                break;
            case R.id.command_777_spinner:

                if (parent.getAdapter().getCount() == pos + 2) {
                    mCustomName777.setVisibility(View.VISIBLE);
                    mCustomTitle777.setVisibility(View.VISIBLE);
                } else {
                    mCustomName777.setVisibility(View.GONE);
                    mCustomName777.setText("");
                    mCustomTitle777.setVisibility(View.GONE);
                    mCustomTitle777.setText("");
                }

                break;
            case R.id.command_999_spinner:
                if (parent.getAdapter().getCount() == pos + 2) {
                    mCustomName999.setVisibility(View.VISIBLE);
                    mCustomTitle999.setVisibility(View.VISIBLE);
                } else {
                    mCustomName999.setVisibility(View.GONE);
                    mCustomName999.setText("");
                    mCustomTitle999.setVisibility(View.GONE);
                    mCustomTitle999.setText("");
                }
                break;
            case R.id.command_911_spinner:
                if (parent.getAdapter().getCount() == pos + 2) {
                    mCustomName911.setVisibility(View.VISIBLE);
                    mCustomTitle911.setVisibility(View.VISIBLE);
                } else {
                    mCustomName911.setVisibility(View.GONE);
                    mCustomName911.setText("");
                    mCustomTitle911.setVisibility(View.GONE);
                    mCustomTitle911.setText("");
                }
                break;
                
            case R.id.work_mode_spinner:
            	if(systemType.equals(Constants.SOBR_GSM510)){
            		int visible = (pos == 0 && systemType.equals(Constants.SOBR_GSM510))? View.VISIBLE : View.GONE; 
            		mImmobilizer.setVisibility(visible);
            		mPreheater.setVisibility(visible);
            		mCh1.setVisibility(visible);
            		mCh2.setVisibility(visible);
            		mCh3.setVisibility(visible);
            		mCh4.setVisibility(visible);
            	}
            	break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableButtons();
        registerReceiver(mSmsStatusReceiver, new IntentFilter(
                SobrGsm.ACTION_SMS_SENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSmsStatusReceiver);
        
        if(MainActivity.isApplicationSentToBackground(this)){
        	System.exit(0);
        }
    }

    private void setViewsVisibility() {

        systemType = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_SYSTYPE);

        if (systemType.equals(Constants.SOBR_GSM)) {

            findViewById(R.id.profile_program_keys_title).setVisibility(
                    View.GONE);
            mKeys.setVisibility(View.GONE);

            findViewById(R.id.command_777_title).setVisibility(View.GONE);
            mCommand777.setVisibility(View.GONE);
            
            mCommand123.setAdapter(ArrayAdapter.createFromResource(this,
            		R.array.entries_command_123_1, R.layout.sherlock_spinner_dropdown_item));
            mCommand456.setAdapter(ArrayAdapter.createFromResource(this,
            		R.array.entries_command_456_1, R.layout.sherlock_spinner_dropdown_item));
            
            findViewById(R.id.time_zone_title).setVisibility(View.VISIBLE);
            mTimeZone.setVisibility(View.VISIBLE);
            cbAlarm.setVisibility(View.VISIBLE);
        } 
        
        if (systemType.equals(Constants.SOBR_DOMONLINE)) {

            findViewById(R.id.profile_program_label_1_title).setVisibility(
                    View.GONE);
            mLable1.setVisibility(View.GONE);

            findViewById(R.id.profile_program_label_2_title).setVisibility(
                    View.GONE);
            mLable2.setVisibility(View.GONE);

            findViewById(R.id.profile_program_label_3_title).setVisibility(
                    View.GONE);
            mLable3.setVisibility(View.GONE);

            findViewById(R.id.command_666_title).setVisibility(View.GONE);
            mCommand666.setVisibility(View.GONE);

            findViewById(R.id.command_911_title).setVisibility(View.GONE);
            mCommand911.setVisibility(View.GONE);

            mGpsReceiver.setVisibility(View.GONE);

            mReportOnMove.setVisibility(View.GONE);

            mShockSensor.setVisibility(View.GONE);

            mImmobilizer.setVisibility(View.GONE);
            
            mCommand123.setAdapter(ArrayAdapter.createFromResource(this,
            		R.array.entries_command_123_2, R.layout.sherlock_spinner_dropdown_item));
            
            mCommand456.setAdapter(ArrayAdapter.createFromResource(this,
            		R.array.entries_command_456_2, R.layout.sherlock_spinner_dropdown_item));
            
            mCommand789.setAdapter(ArrayAdapter.createFromResource(this,
            		R.array.entries_command_789_1, R.layout.sherlock_spinner_dropdown_item));
            
            mCommand999.setVisibility(View.VISIBLE);
            findViewById(R.id.command_999_title).setVisibility(View.VISIBLE);
            mCommand999.setAdapter(ArrayAdapter.createFromResource(this,
            		R.array.entries_command_999_1, R.layout.sherlock_spinner_dropdown_item));
        }
        
        if(systemType.equals(Constants.SOBR_CHIP0103) 
        		|| systemType.equals(Constants.SOBR_G0103)){
        	mCommand666.setVisibility(View.GONE);
        	findViewById(R.id.command_666_title).setVisibility(View.GONE);
        }
        
        if (systemType.equals(Constants.SOBR_CHIP0103)
                || systemType.equals(Constants.SOBR_G0103)
                || systemType.equals(Constants.SOBR_GSM510)
                || systemType.equals(Constants.SOBR_CHIP111213)) {

            findViewById(R.id.profile_program_car_remote_title).setVisibility(
                    View.GONE);
            mCarRemote.setVisibility(View.GONE);

            findViewById(R.id.profile_program_keys_title).setVisibility(
                    View.GONE);
            mKeys.setVisibility(View.GONE);

            findViewById(R.id.profile_program_label_1_title).setVisibility(
                    View.GONE);
            mLable1.setVisibility(View.GONE);

            findViewById(R.id.profile_program_label_2_title).setVisibility(
                    View.GONE);
            mLable2.setVisibility(View.GONE);

            findViewById(R.id.profile_program_label_3_title).setVisibility(
                    View.GONE);
            mLable3.setVisibility(View.GONE);

            findViewById(R.id.profile_daily_mode_title).setVisibility(
                    View.VISIBLE);
            mDailyMode.setVisibility(View.VISIBLE);

            findViewById(R.id.profile_weekly_mode_title).setVisibility(
                    View.VISIBLE);
            mWeeklyMode.setVisibility(View.VISIBLE);

            findViewById(R.id.command_123_title).setVisibility(View.GONE);
            mCommand123.setVisibility(View.GONE);

            findViewById(R.id.command_456_title).setVisibility(View.GONE);
            mCommand456.setVisibility(View.GONE);

            findViewById(R.id.command_789_title).setVisibility(View.GONE);
            mCommand789.setVisibility(View.GONE);

            findViewById(R.id.command_777_title).setVisibility(View.GONE);
            mCommand777.setVisibility(View.GONE);

            findViewById(R.id.command_999_title).setVisibility(View.GONE);
            mCommand999.setVisibility(View.GONE);

            findViewById(R.id.command_911_title).setVisibility(View.GONE);
            mCommand911.setVisibility(View.GONE);

            mGpsReceiver.setVisibility(View.GONE);

            mReportOnMove.setVisibility(View.GONE);

            mShockSensor.setVisibility(View.GONE);

            mImmobilizer.setVisibility(View.GONE);

        }
        if (systemType.equals(Constants.SOBR_CHIP0103)) {

            findViewById(R.id.command_09_title).setVisibility(View.GONE);
            mCommand09.setVisibility(View.GONE);

        }
        
        if (systemType.equals(Constants.SOBR_G0103)) {

            findViewById(R.id.profile_communication_time_title).setVisibility(
                    View.VISIBLE);
            mCommTime.setVisibility(View.VISIBLE);

            findViewById(R.id.command_09_title).setVisibility(View.GONE);
            mCommand09.setVisibility(View.GONE);

        }
        
        if(systemType.equals(Constants.SOBR_GSM510)){
        	findViewById(R.id.profile_daily_mode_title).setVisibility(
                    View.GONE);
            mDailyMode.setVisibility(View.GONE);
            findViewById(R.id.profile_weekly_mode_title).setVisibility(
                    View.GONE);
            mWeeklyMode.setVisibility(View.GONE);
            findViewById(R.id.command_666_title).setVisibility(View.GONE);
            mCommand666.setVisibility(View.GONE);
            findViewById(R.id.command_09_title).setVisibility(View.GONE);
            mCommand09.setVisibility(View.GONE);
            findViewById(R.id.work_mode_title).setVisibility(View.VISIBLE);
            mWorkMode.setVisibility(View.VISIBLE);
            findViewById(R.id.time_zone_title).setVisibility(View.VISIBLE);
            mTimeZone.setVisibility(View.VISIBLE);
            findViewById(R.id.profile_communication_time_title).setVisibility(
                    View.VISIBLE);
            mCommTime.setVisibility(View.VISIBLE);
            findViewById(R.id.freq_conn_title).setVisibility(
                    View.VISIBLE);
            mFreqConn.setVisibility(View.VISIBLE);
            findViewById(R.id.work_schedule_title).setVisibility(View.VISIBLE);
            mWorkSch.setVisibility(View.VISIBLE);
            findViewById(R.id.fifth_phone_number_title).setVisibility(View.VISIBLE);
            findViewById(R.id.fifth_phone_number_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.milage_title).setVisibility(View.VISIBLE);
            mMilAge.setVisibility(View.VISIBLE);
            findViewById(R.id.resend_input_sms_title).setVisibility(View.VISIBLE);
            mResendInputSms.setVisibility(View.VISIBLE);
            mGpsReceiver.setVisibility(View.GONE);
            mReportOnMove.setVisibility(View.GONE);
            mShockSensor.setVisibility(View.GONE);
            mImmobilizer.setVisibility(View.VISIBLE);
            mPreheater.setVisibility(View.VISIBLE);
            mCh1.setVisibility(View.VISIBLE);
            mCh2.setVisibility(View.VISIBLE);
            mCh3.setVisibility(View.VISIBLE);
            mCh4.setVisibility(View.VISIBLE);
            findViewById(R.id.profile_communication_time_prompt).setVisibility(View.VISIBLE);
            findViewById(R.id.freq_conn_prompt).setVisibility(View.VISIBLE);
            findViewById(R.id.work_schedule_prompt).setVisibility(View.VISIBLE);
            findViewById(R.id.fifth_phone_number_prompt).setVisibility(View.VISIBLE);
            findViewById(R.id.milage_prompt).setVisibility(View.VISIBLE);
            findViewById(R.id.resend_input_sms_prompt).setVisibility(View.VISIBLE);
            mImmobilizer.setText(R.string.immtitlegem510);
            mMilAgeKnow.setVisibility(View.VISIBLE);
        	findViewById(R.id.millage_know_title).setVisibility(View.VISIBLE);
        }
        
        if(systemType.equals(Constants.SOBR_CHIP111213)){
        	 findViewById(R.id.profile_daily_mode_title).setVisibility(
                    View.GONE);
        	 mDailyMode.setVisibility(View.GONE);
             findViewById(R.id.profile_weekly_mode_title).setVisibility(
                     View.GONE);
             mWeeklyMode.setVisibility(View.GONE);
             findViewById(R.id.command_666_title).setVisibility(View.GONE);
             mCommand666.setVisibility(View.GONE);
             findViewById(R.id.command_09_title).setVisibility(View.GONE);
             mCommand09.setVisibility(View.GONE);
        	 findViewById(R.id.work_schedule_title).setVisibility(View.VISIBLE);
             mWorkSch.setVisibility(View.VISIBLE);
             findViewById(R.id.time_zone_title).setVisibility(View.VISIBLE);
             mTimeZone.setVisibility(View.VISIBLE);
             findViewById(R.id.profile_communication_time_title).setVisibility(
                     View.VISIBLE);
             mCommTime.setVisibility(View.VISIBLE);
             findViewById(R.id.freq_conn_title).setVisibility(
                     View.VISIBLE);
             mFreqConn.setVisibility(View.VISIBLE);
             TextView tv = (TextView) findViewById(R.id.profile_communication_time_prompt);
             tv.setVisibility(View.VISIBLE);
             tv.setText(R.string.communication_time_ptompt_type33);
             tv = (TextView) findViewById(R.id.freq_conn_prompt);
             tv.setVisibility(View.VISIBLE);
             tv.setText(R.string.freq_conn_prompt_type_33);
             tv = (TextView) findViewById(R.id.work_schedule_prompt);
             tv.setVisibility(View.VISIBLE);
             tv.setText(R.string.work_schedule_prompt_type_33);
        }

    }

    private void setDefaultViewsData() {
        String systemType = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_SYSTYPE);

        ContentValues defaultValues = new ContentValues();

        if (systemType.equals(Constants.SOBR_GSM)) {

            defaultValues = DefaultProfiles.sobrGsm(defaultValues);

        } else if (systemType.equals(Constants.SOBR_DOMONLINE)) {

            defaultValues = DefaultProfiles.sobrDomonline(defaultValues);

        } else if (systemType.equals(Constants.SOBR_CHIP0103)) {

            defaultValues = DefaultProfiles.sobrChip0103(defaultValues);

        } else if (systemType.equals(Constants.SOBR_G0103) ||
        		systemType.equals(Constants.SOBR_CHIP111213)) {

            defaultValues = DefaultProfiles.sobrG0103(defaultValues);

        } else if (systemType.equals(Constants.SOBR_GSM510)){
        	defaultValues = DefaultProfiles.sobrGsm510(defaultValues);
        } else {
            defaultValues = DefaultProfiles.sobrGsm(defaultValues);
        }
        
        int arr = R.array.entryvalues_command_123;
        
        if(systemType.equals(Constants.SOBR_GSM))
        	arr = R.array.entryvalues_command_123_1;
        else if (systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_123_2;

        mCommand123.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_123),arr));
        
        arr = R.array.entryvalues_command_456;
        
        if(systemType.equals(Constants.SOBR_GSM))
        	arr = R.array.entryvalues_command_456_1;
        else if(systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_456_2;

        mCommand456.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_456), arr));

        arr = R.array.entryvalues_command_789;
        
        if(systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_789_1;
        
        mCommand789.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_789), arr));

        mCommand666.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_666),
                R.array.entryvalues_command_666));

        mCommand777.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_777),
                R.array.entryvalues_command_777));

        arr = R.array.entryvalues_command_999;
        
        if(systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_999_1;
        
        mCommand999.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_999), arr));

        mCommand09.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_09),
                R.array.entryvalues_command_09));

        mCommand911.setSelection(getValuePosition(
                defaultValues.getAsString(SobrContract.Profiles.COMMAND_911),
                R.array.entryvalues_command_911));

        mGpsReceiver.setChecked(Boolean.parseBoolean(defaultValues
                .getAsString(SobrContract.Profiles.GPS_RECEIVER)));

        mReportOnMove.setChecked(Boolean.parseBoolean(defaultValues
                .getAsString(SobrContract.Profiles.REPORT_ON_MOVE)));

        mShockSensor.setChecked(Boolean.parseBoolean(defaultValues
                .getAsString(SobrContract.Profiles.SHOCK_SENSOR)));

        mImmobilizer.setChecked(Boolean.parseBoolean(defaultValues
                .getAsString(SobrContract.Profiles.IMMOBILIZER)));
        
        try{
        	mWorkMode.setSelection(Integer.parseInt(
        		defaultValues.getAsString(SobrContract.Profiles.GSM510_WORK_MODE)), true);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        mFifthNumberEd.setText(defaultValues.getAsString(SobrContract.Profiles.FIFTH_PHONE_NUMBER));
        mPreheater.setChecked(defaultValues.getAsBoolean(SobrContract.Profiles.PREHEATER));
        
        int ch = defaultValues.getAsInteger(SobrContract.Profiles.CHANELS);
        mCh1.setChecked((ch & 1) == 1);
        mCh2.setChecked((ch & 2) == 2);
        mCh3.setChecked((ch & 4) == 4);
        mCh4.setChecked((ch & 8) == 8);
        
        mChCmd1.setText(defaultValues.getAsString(SobrContract.Profiles.CMD1));
        mChKey1.setText(defaultValues.getAsString(SobrContract.Profiles.KEY1));
        mChCmd2.setText(defaultValues.getAsString(SobrContract.Profiles.CMD2));
        mChKey2.setText(defaultValues.getAsString(SobrContract.Profiles.KEY2));
        mChCmd3.setText(defaultValues.getAsString(SobrContract.Profiles.CMD3));
        mChKey3.setText(defaultValues.getAsString(SobrContract.Profiles.KEY3));
        mChCmd4.setText(defaultValues.getAsString(SobrContract.Profiles.CMD4));
        mChKey4.setText(defaultValues.getAsString(SobrContract.Profiles.KEY4));
        
        cbAlarm.setChecked(Boolean.parseBoolean(defaultValues
                .getAsString(SobrContract.Profiles.ALARM)));
        
    }

    private void setViewsData() {
        Bundle mExtras = getIntent().getExtras();

        int arr = R.array.entryvalues_command_123;
        
        if(systemType.equals(Constants.SOBR_GSM))
        	arr = R.array.entryvalues_command_123_1;
        else if (systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_123_2;
        
        mCommand123.setSelection(getValuePosition(
                mExtras.getString("command123"),arr));

        arr = R.array.entryvalues_command_456;
        
        if(systemType.equals(Constants.SOBR_GSM))
        	arr = R.array.entryvalues_command_456_1;
        else if(systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_456_2;
        
        mCommand456.setSelection(getValuePosition(
                mExtras.getString("command456"),arr));

        arr = R.array.entryvalues_command_789;
        
        if(systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_789_1;
        
        mCommand789.setSelection(getValuePosition(
                mExtras.getString("command789"),arr));

        mCommand666.setSelection(getValuePosition(
                mExtras.getString("command666"),
                R.array.entryvalues_command_666));

        mCommand777.setSelection(getValuePosition(
                mExtras.getString("command777"),
                R.array.entryvalues_command_777));

        arr = R.array.entryvalues_command_999;
        
        if(systemType.equals(Constants.SOBR_DOMONLINE))
        	arr = R.array.entryvalues_command_999_1;
        
        mCommand999.setSelection(getValuePosition(
                mExtras.getString("command999"),arr));

        mCommand09
                .setSelection(getValuePosition(mExtras.getString("command09"),
                        R.array.entryvalues_command_09));

        mCommand911.setSelection(getValuePosition(
                mExtras.getString("command911"),
                R.array.entryvalues_command_911));

        if (mCommand123.getSelectedItemPosition() == (mCommand123.getCount() - 2)) {
            mCustomName123.setText(mExtras.getString("command123"));
            mCustomTitle123.setText(mExtras.getString("command123Title"));
        }
        if (mCommand456.getSelectedItemPosition() == (mCommand456.getCount() - 2)) {
            mCustomTitle456.setText(mExtras.getString("command456Title"));
        }
        if (mCommand789.getSelectedItemPosition() == (mCommand789.getCount() - 2)) {
            mCustomTitle789.setText(mExtras.getString("command789Title"));
        }
        if (mCommand777.getSelectedItemPosition() == (mCommand777.getCount() - 2)) {
            mCustomName777.setText(mExtras.getString("command777"));
            mCustomTitle777.setText(mExtras.getString("command777Title"));
        }
        if (mCommand999.getSelectedItemPosition() == (mCommand999.getCount() - 2)) {
            mCustomName999.setText(mExtras.getString("command999"));
            mCustomTitle999.setText(mExtras.getString("command999Title"));
        }
        if (mCommand911.getSelectedItemPosition() == (mCommand911.getCount() - 2)) {
            mCustomName911.setText(mExtras.getString("command911"));
            mCustomTitle911.setText(mExtras.getString("command911Title"));
        }

        mGpsReceiver.setChecked(Boolean.parseBoolean(mExtras
                .getString("gps_receiver")));

        mReportOnMove.setChecked(Boolean.parseBoolean(mExtras
                .getString("report_on_move")));

        mShockSensor.setChecked(Boolean.parseBoolean(mExtras
                .getString("shock_sensor")));

        mImmobilizer.setChecked(Boolean.parseBoolean(mExtras
                .getString("immobilizer")));
        
        mWorkMode.setSelection(Integer.parseInt(
        		mExtras.getString(SobrContract.Profiles.GSM510_WORK_MODE)), true);
        mFifthNumberEd.setText(mExtras.getString(SobrContract.Profiles.FIFTH_PHONE_NUMBER));
        mPreheater.setChecked(mExtras.getBoolean(SobrContract.Profiles.PREHEATER));
        
        int ch = mExtras.getInt(SobrContract.Profiles.CHANELS);
        mCh1.setChecked((ch & 1) == 1);
        mCh2.setChecked((ch & 2) == 2);
        mCh3.setChecked((ch & 4) == 4);
        mCh4.setChecked((ch & 8) == 8);
        
        mChCmd1.setText(mExtras.getString(SobrContract.Profiles.CMD1));
        mChKey1.setText(mExtras.getString(SobrContract.Profiles.KEY1));
        mChCmd2.setText(mExtras.getString(SobrContract.Profiles.CMD2));
        mChKey2.setText(mExtras.getString(SobrContract.Profiles.KEY2));
        mChCmd3.setText(mExtras.getString(SobrContract.Profiles.CMD3));
        mChKey3.setText(mExtras.getString(SobrContract.Profiles.KEY3));
        mChCmd4.setText(mExtras.getString(SobrContract.Profiles.CMD4));
        mChKey4.setText(mExtras.getString(SobrContract.Profiles.KEY4));
        
        cbAlarm.setChecked(mExtras.getBoolean(SobrContract.Profiles.ALARM));
    }

    public Intent getViewsData() {
    	int arr = R.array.entryvalues_command_123;
    	
    	if(systemType.equals(Constants.SOBR_GSM))
    		arr = R.array.entryvalues_command_123_1;
    	else if(systemType.equals(Constants.SOBR_DOMONLINE))
    		arr = R.array.entryvalues_command_123_2;
    	
    	String comm132 = getSelectedValue(mCommand123, mCustomName123,arr);
    	
    	arr = R.array.entryvalues_command_456;
    	
    	if(systemType.equals(Constants.SOBR_GSM))
    		arr = R.array.entryvalues_command_456_1;
    	if(systemType.equals(Constants.SOBR_DOMONLINE))
    		arr = R.array.entryvalues_command_456_2;
    	
    	String comm456 = getSelectedValue(mCommand456, null, arr);
    	
    	arr = R.array.entryvalues_command_789;
    	
    	if(systemType.equals(Constants.SOBR_DOMONLINE))
    		arr = R.array.entryvalues_command_789_1;
    	
        String comm789 = getSelectedValue(mCommand789, null,arr);
        String comm666 = getSelectedValue(mCommand666, null,
                R.array.entryvalues_command_666);
        String comm777 = getSelectedValue(mCommand777, mCustomName777,
                R.array.entryvalues_command_777);
        
        arr = R.array.entryvalues_command_999;
        
        if(systemType.equals(Constants.SOBR_DOMONLINE))
    		arr = R.array.entryvalues_command_999_1;
        
        String comm999 = getSelectedValue(mCommand999, mCustomName999,arr);
        String comm09 = getSelectedValue(mCommand09, null,
                R.array.entryvalues_command_09);
        String comm911 = getSelectedValue(mCommand911, mCustomName911,
                R.array.entryvalues_command_911);

        String comm123CustomTitle = mCustomTitle123.getVisibility() == View.VISIBLE ? mCustomTitle123.getText().toString() : null;
        String comm456CustomTitle = mCustomTitle456.getVisibility() == View.VISIBLE ? mCustomTitle456.getText().toString() : null;
        String comm789CustomTitle = mCustomTitle789.getVisibility() == View.VISIBLE ? mCustomTitle789.getText().toString() : null;
        String comm777CustomTitle = mCustomTitle777.getVisibility() == View.VISIBLE ? mCustomTitle777.getText().toString() : null;
        String comm999CustomTitle = mCustomTitle999.getVisibility() == View.VISIBLE ? mCustomTitle999.getText().toString() : null;
        String comm911CustomTitle = mCustomTitle911.getVisibility() == View.VISIBLE ? mCustomTitle911.getText().toString() : null;

        Intent data = new Intent();

//        if (comm132.length() > 0 && comm456.length() > 0
//                && comm789.length() > 0 && comm666.length() > 0
//                && comm777.length() > 0 && comm999.length() > 0
//                && comm09.length() > 0 && comm911.length() > 0
//                && (comm123CustomTitle == null || comm123CustomTitle.length() > 0)
//                && (comm456CustomTitle == null || comm456CustomTitle.length() > 0)
//                && (comm789CustomTitle == null || comm789CustomTitle.length() > 0)
//                && (comm777CustomTitle == null || comm777CustomTitle.length() > 0)
//                && (comm999CustomTitle == null || comm999CustomTitle.length() > 0)
//                && (comm911CustomTitle == null || comm911CustomTitle.length() > 0)) {
//
//            
//        }
        
        data.putExtra("command123", comm132);
        data.putExtra("command456", comm456);
        data.putExtra("command789", comm789);
        data.putExtra("command666", comm666);
        data.putExtra("command777", comm777);
        data.putExtra("command999", comm999);
        data.putExtra("command09", comm09);
        data.putExtra("command911", comm911);

        data.putExtra("command123Title", comm123CustomTitle);
        data.putExtra("command456Title", comm456CustomTitle);
        data.putExtra("command789Title", comm789CustomTitle);
        data.putExtra("command777Title", comm777CustomTitle);
        data.putExtra("command999Title", comm999CustomTitle);
        data.putExtra("command911Title", comm911CustomTitle);
        data.putExtra("gps_receiver", "" + mGpsReceiver.isChecked());
        data.putExtra("report_on_move", "" + mReportOnMove.isChecked());
        data.putExtra("shock_sensor", "" + mShockSensor.isChecked());
        data.putExtra(SobrContract.Profiles.FIFTH_PHONE_NUMBER, 
        		mFifthNumberEd.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.GSM510_WORK_MODE, 
        		Integer.toString(mWorkMode.getSelectedItemPosition()));
        data.putExtra(SobrContract.Profiles.PREHEATER, mPreheater.isChecked());
        data.putExtra("immobilizer", "" + mImmobilizer.isChecked());
        
        int chanel = mCh1.isChecked() ? 1 : 0; 
        chanel |= mCh2.isChecked() ? 2 : 0;
        chanel |= mCh3.isChecked() ? 4 : 0;
        chanel |= mCh4.isChecked() ? 8 : 0;
        
        data.putExtra(SobrContract.Profiles.CHANELS, chanel);
        data.putExtra(SobrContract.Profiles.CMD1, 
        		mChCmd1.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.KEY1, 
        		mChKey1.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.CMD2, 
        		mChCmd2.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.KEY2, 
        		mChKey2.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.CMD3, 
        		mChCmd3.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.KEY3, 
        		mChKey3.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.CMD4, 
        		mChCmd4.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.KEY4, 
        		mChKey4.getText().toString().trim());
        data.putExtra(SobrContract.Profiles.ALARM, cbAlarm.isChecked());
        
        return data;
    }

    protected int getValuePosition(String commandValue, int commandEntryvalues) {
        String commandValues[] = getResources().getStringArray(
                commandEntryvalues);
        int length = commandValues.length;
        int itemNumb = length - 2;
        for (int i = 0; i < length; i++) {
            if (commandValues[i].equals(commandValue)) {
                itemNumb = i;
                break;
            }
        }
        // if (DEBUG)Log.d(TAG, "itemNumb " + itemNumb);
        return itemNumb;
    }

    protected String getSelectedValue(Spinner command, EditText customCommand,
                                      int commandEntryvalues) {
        String itemData = "";
        if ((command.getSelectedItemPosition() + 2) != command.getCount()) {
            String commandValues[] = getResources().getStringArray(
                    commandEntryvalues);
            itemData = commandValues[command.getSelectedItemPosition()];
        } else {
            if (customCommand != null) {
                itemData = capitalize(customCommand.getText().toString());
            } else {
                String command123Values[] = getResources().getStringArray(
                        commandEntryvalues);
                itemData = command123Values[command.getSelectedItemPosition()];
            }
        }
        // if (DEBUG)Log.d(TAG, "itemData - " + itemData);
        return itemData;
    }

    protected void enableButtons() {

        mCarRemote.setEnabled(true);
        mKeys.setEnabled(true);
        mLable1.setEnabled(true);
        mLable2.setEnabled(true);
        mLable3.setEnabled(true);
        mDailyMode.setEnabled(true);
        mWeeklyMode.setEnabled(true);
        mCommTime.setEnabled(true);
    }

    private void showCommentaryDialog(int dialogId) {

        final int DIALOG_ID = dialogId;

        final String SYSNUMBER = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_SYSNUMB);
        final String SYSTYPE = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_SYSTYPE);
        final String PINCODE = getIntent().getStringExtra(
                ProfileDetailActivity.KEY_PROFILE_PINCODE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);

        switch (DIALOG_ID) {
            case DAILYMODE_DIALOG_ID:
                builder.setTitle(R.string.profile_user_enable_daily_mode_title);
                builder.setMessage(R.string.profile_dialog_dailymode_message);
                break;

            case WEEKLYMODE_DIALOG_ID:
                builder.setTitle(R.string.profile_user_enable_weekly_mode_title);
                builder.setMessage(R.string.profile_dialog_weeklymode_message);
                break;

            default:
                break;
        }

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (DIALOG_ID) {
                            case DAILYMODE_DIALOG_ID:
                                SobrGsm.profileDailyMode(
                                        CustomProfileDetailActivity.this,
                                        SYSNUMBER, SYSTYPE, PINCODE);
                                break;

                            case WEEKLYMODE_DIALOG_ID:
                                SobrGsm.profileWeeklyMode(
                                        CustomProfileDetailActivity.this,
                                        SYSNUMBER, SYSTYPE, PINCODE);
                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    public String capitalize(String str) {
        String[] brokenString = str.split(" ");
        String newString = "";

        for (String s : brokenString) {
            char[] chars = s.toCharArray();
            if (chars.length > 0)
                chars[0] = Character.toUpperCase(chars[0]);
            newString = newString + new String(chars) + " ";
        }

        return newString.trim();
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        private static String systemNumb, pinCode;

        private String hours = null, minutes = null;
        private boolean clickButton;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            systemNumb = getArguments().getString("system_number");
            pinCode = getArguments().getString("pin_code");

            TimePickerDialog tpd = new TimePickerDialog(getActivity(), this,
                    hour, minute, true); // DateFormat.is24HourFormat(getActivity()
            tpd.setTitle(R.string.profile_user_communication_time_title);

            return tpd;
        }

        @Override
        public void onDestroy() {
            // FIX onTimeSet calling twice bug on JellyBean
            if (clickButton && hours != null && minutes != null) {
                // if (DEBUG)Log.d(TAG, "onDestroy " + clickButton + " " + hours
                // + ":" + minutes);
            	if(systemType.equals(Constants.SOBR_GSM510)){
            		String password = getArguments().getString(
	                        ProfileDetailActivity.KEY_PROFILE_PASSWORD);
            		SobrGsm.profileCommunicationTimeGSM510(getActivity(), systemNumb, password,
            				hours, minutes);
            	}else
            		SobrGsm.profileCommunicationTime(getActivity(), systemNumb,
                        pinCode, hours, minutes);
            }
            super.onDestroy();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            clickButton = false;
            super.onCancel(dialog);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            clickButton = true;

            hours = "" + hourOfDay;
            minutes = "" + minute;

            if (hours.length() == 1)
                hours = "0" + hours;
            if (minutes.length() == 1)
                minutes = "0" + minutes;

        }
    }

    private class InnerSmsStatusReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null
                    && intent.getAction().equals(SobrGsm.ACTION_SMS_SENT)) {
                enableButtons();
            }
        }
    }

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean checked) {
		switch(btn.getId()){
		case R.id.ch1_cbx:
			mChCmd1.setVisibility(checked ? View.VISIBLE : View.GONE);
			mChKey1.setVisibility(checked ? View.VISIBLE : View.GONE);
			break;
		case R.id.ch2_cbx:
			mChCmd2.setVisibility(checked ? View.VISIBLE : View.GONE);
			mChKey2.setVisibility(checked ? View.VISIBLE : View.GONE);
			break;
		case R.id.ch3_cbx:
			mChCmd3.setVisibility(checked ? View.VISIBLE : View.GONE);
			mChKey3.setVisibility(checked ? View.VISIBLE : View.GONE);
			break;
		case R.id.ch4_cbx:
			mChCmd4.setVisibility(checked ? View.VISIBLE : View.GONE);
			mChKey4.setVisibility(checked ? View.VISIBLE : View.GONE);
			break;
		}
	}

}
