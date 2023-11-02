package com.ksoft.ardalarm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TimerView extends Activity {
	private Button btnStart;
	private EditText edHour;
	private EditText edMin;
	private EditText edSec;
	private TextView tvTimer;
	private static final int TICK = 1;
	public static final String TICK_BROADCAST_ACTION = "com.kasoft.ardalarm.TICK";
	public Button btnStopTimer;
	public Button btnStopPlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer);

		btnStart = (Button) findViewById(R.id.btnStart);
		edHour = (EditText) findViewById(R.id.edHour);
		edMin = (EditText) findViewById(R.id.edMin);
		edSec = (EditText) findViewById(R.id.edSec);
		tvTimer = (TextView) findViewById(R.id.tvTimer);
		btnStopTimer = (Button) findViewById(R.id.btnStopTimer);
		btnStopPlay = (Button) findViewById(R.id.btnStopPlay);

		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int hour = 0;

				try {
					hour = Integer.parseInt(edHour.getText().toString().trim());
				} catch (Exception e) {
				}

				int min = 0;

				try {
					min = Integer.parseInt(edMin.getText().toString().trim());
				} catch (Exception e) {
				}

				int sec = 0;

				try {
					sec = Integer.parseInt(edSec.getText().toString().trim());
				} catch (Exception e) {
				}

				Intent intent = new Intent(v.getContext(), TimerSrv.class);
				intent.putExtra(TimerSrv.HOUR, hour);
				intent.putExtra(TimerSrv.MIN, min);
				intent.putExtra(TimerSrv.SEC, sec);
				
				startService(intent);
			}
		});
		
		btnStopTimer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), TimerSrv.class);
				stopService(intent);
				
			}
		});
		
		btnStopPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), SoundPlay.class);
				stopService(intent);
			}
		});
	}

	Handler hndl = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TICK:
				Intent intent = (Intent) msg.obj;
				int h = intent.getIntExtra(TimerSrv.HOUR, 0);
				int m = intent.getIntExtra(TimerSrv.MIN, 0);
				int s = intent.getIntExtra(TimerSrv.SEC, 0);

				StringBuilder sb = new StringBuilder();
				sb.append(h).append(":").append(m).append(":").append(s);
				tvTimer.setText(sb.toString());
				break;
			default:
			}
		};
	};

	BroadcastReceiver rcv = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Message message = new Message();
			message.what = TICK;
			message.obj = intent;
			hndl.sendMessage(message);
		}
	};

	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(TICK_BROADCAST_ACTION);
		registerReceiver(rcv, filter);
	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(rcv);
	}
}
