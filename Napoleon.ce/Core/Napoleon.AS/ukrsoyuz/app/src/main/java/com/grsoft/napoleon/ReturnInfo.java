package com.grsoft.napoleon;

import com.grsoft.dataobjects.ReturnData;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.ReturnDataImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ReturnInfo extends Activity{
	
	public static void open(Context context)
	{
		Intent intent = new Intent(context, ReturnInfo.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.return_info);
		
		Cursor<ReturnData> cursor = new Cursor<ReturnData>(new ReturnDataImpl());
		
		if(cursor.moveNext()){
			ReturnData data = cursor.current().getData();
			((TextView) findViewById(R.id.tvUnloadSum)).setText(Util.IntToScaleStr(data.unloadsum, 
					Consts.SUM_SCALE));
			((TextView) findViewById(R.id.tvReturnSum)).setText(Util.IntToScaleStr(data.returnsum, 
					Consts.SUM_SCALE));
			((TextView) findViewById(R.id.tvPercentSum)).setText(Integer.toString(
					data.returnsum / (data.unloadsum / 100)));
			((TextView) findViewById(R.id.tvUnloadWeight)).setText(Util.IntToScaleStr(data.unloadweight, 
					Consts.WEIGHT_SCALE));
			((TextView) findViewById(R.id.tvReturnWeight)).setText(Util.IntToScaleStr(data.returnweight, 
					Consts.WEIGHT_SCALE));
			((TextView) findViewById(R.id.tvPercentWeight)).setText(Integer.toString(
					data.returnweight / (data.unloadweight / 100)));
		}
		
		cursor.close();
		
	}
}
