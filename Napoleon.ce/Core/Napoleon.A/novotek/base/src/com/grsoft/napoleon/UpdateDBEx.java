package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Action;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	private static final String  DATE_LAST_UPDATE = "last_update_date";
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		long d = pref.getLong(DATE_LAST_UPDATE, 0);
		
		TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
		
		if(tvInfo != null && d > 0){
			Date date = new Date(d);
			tvInfo.setText(String.format(getResources().getString(R.string.last_update),
					sdf.format(date)));
			tvInfo.setVisibility(View.VISIBLE);
		}
		
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
		
		CheckBox cb = ((CheckBox) findViewById(R.id.cbRemains));
		cb.setChecked(false);
		cb.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
		editor.putLong(DATE_LAST_UPDATE, Calendar.getInstance().getTime().getTime());
		editor.commit();
		return true;
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if (result == null)
			result = new ArrayList<Hitching>();
		
		CheckBox cbGenData = ((CheckBox)findViewById(R.id.cbGenData)); 
		
		if(cbGenData != null && cbGenData.isChecked()) 
			result.add(new RcvNewHitching(Action.class, "Action"));
		
		return result;
	}
}
