package com.grsoft.napoleon;

import java.lang.reflect.Method;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public class AskToSendDlg extends DialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.asktosenddlg, container, false);
		view.findViewById(R.id.btnSend).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object send = getActivity();
				try{
					Method m = send.getClass().getMethod("send", (Class[])null);
					m.invoke(send, (Object[])null);
				}catch(Exception e){
					e.printStackTrace();
				}
				dismiss();
			}
		});
		
		view.findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				dismiss();
			}
		});
		
		getDialog().setTitle(R.string.question);
		
		return view;
	}

}
