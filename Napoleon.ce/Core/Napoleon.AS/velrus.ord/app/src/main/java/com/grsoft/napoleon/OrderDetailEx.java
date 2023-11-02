package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	View btnAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btnAction = findViewById(R.id.btnAction);
		btnAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionList.open(v.getContext(), doc.getRowid());
			}
		});
	}

	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }
	
	@Override
	public void onBackPressed() {
		if(((OrderEx)doc.getData()).notcomplete > 0)
			Toast.makeText(this, R.string.required_items_missed, Toast.LENGTH_SHORT).show();
		else{
			if( doc.getData().items.size() == 0 )
				doc.delete();
			keyBackPressed();
			finish();
		}
	}
	
	@Override
	protected void checkFocused() {
		super.checkFocused();
		
			if((Button)findViewById(R.id.btnFocus)!=null) {
				btnSend.setEnabled(true);
			}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		int minSum;
		TextView tvMinSum = (TextView)findViewById(R.id.tvInfo1);
		tvMinSum.setVisibility(View.VISIBLE);
		
		OrgImpl o = new OrgImpl();
		o.getData().id = doc.getId();
		o.read();
		o.close();
		
		OrgEx orgEx = (OrgEx)o.getData();
		minSum = orgEx.minOrder;
		
		if( doc.getData().items != null ) {
			long sum = doc.sum();
			if( sum < minSum ) {
				String text = "Сумма заявки " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. меньше " +
					Util.IntToScaleStr(minSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
				tvMinSum.setText(text);
				tvMinSum.setTextColor(Color.RED);
			} else
				tvMinSum.setVisibility(View.GONE);
				
		} else {
			tvMinSum.setVisibility(View.GONE);
		}

		btnAction.setVisibility(((OrderEx)doc.getData()).bonus.size() > 0 ? View.VISIBLE : View.GONE);
	}
	
	@Override
	public void send() {
		if(((OrderEx)doc.getData()).notcomplete > 0)
			Toast.makeText(this, R.string.required_items_missed, Toast.LENGTH_SHORT).show();
		else
			super.send();
	}
}
