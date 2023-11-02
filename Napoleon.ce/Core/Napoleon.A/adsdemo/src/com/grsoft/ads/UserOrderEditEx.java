package com.grsoft.ads;

import java.text.SimpleDateFormat;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.grsoft.ads.dataobjects.UserOrderEx;
import com.grsoft.ads.dataobjects.impl.CertificateImpl;
import com.grsoft.ads.dataobjects.impl.ProtocolImpl;
import com.grsoft.ads.dataobjects.impl.UserOrderImplEx;

public class UserOrderEditEx extends UserOrderEdit {
	
	private Button btnStatus;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	@Override
	public int getLayoutId() {
		return R.layout.user_order_edit_ex;
	}
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		userOrderImpl = new UserOrderImplEx();
		super.onCreate(savedInstanceState);
		
		btnStatus = (Button) findViewById(R.id.btnStatus);
		btnStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserOrderImplEx implEx = (UserOrderImplEx)userOrderImpl;
				if (implEx.isDoing())
					implEx.setDone();
				else
					implEx.setDoing();
				
				updateControl();
			}
		});
	};
	
	@Override
	protected void updateControl() {
		super.updateControl();
		
		UserOrderImplEx implEx = (UserOrderImplEx)userOrderImpl;
		UserOrderEx uoe = (UserOrderEx) implEx.getData();
		
		TextView tvWorkHours = (TextView) findViewById(R.id.tvWorkHours);
		
		if (implEx.isDoing()){
			tvWorkHours.setText(sdf.format(uoe.begin));
			btnStatus.setText(OrderSummary.ORDER_DONE_STR);
		} else if(implEx.isDone()){
			tvWorkHours.setText(String.format("%s - %s", 
					sdf.format(uoe.begin), sdf.format(uoe.end)));
			btnStatus.setText(OrderSummary.ORDER_DONE_STR);
			btnStatus.setEnabled(false);
			
			if (uoe.certificate != null && uoe.certificate.length() > 0){
				CertificateImpl cimpl = new CertificateImpl();
				cimpl.getData().number = uoe.certificate;
				
				if (cimpl.read()){
					cimpl.getData().writeof = 1;
					cimpl.close();
				}
				
				cimpl.close();
			}
			
			if (uoe.protocol != null && uoe.protocol.length() > 0){
				ProtocolImpl pimpl = new ProtocolImpl();
				pimpl.getData().number = uoe.protocol;
				
				if (pimpl.read()){
					pimpl.getData().writeof = 1;
					pimpl.close();
				}
				
				pimpl.close();
			}
		} else {
			tvWorkHours.setText("");
			btnStatus.setText(OrderSummary.ORDER_DOIGN_STR);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateControl();
		
		if (!userOrderImpl.isEditable())
			btnStatus.setEnabled(false);
		
	}
}
