package com.grsoft.manager;

import com.grsoft.dataobjects.AgentManagerMemo;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ManagerMemoDialog extends DialogFragment {
	Action handler;
	AgentManagerMemo data;
	
	public interface Action {
		void accept(AgentManagerMemo data, boolean accept);
	}
	
	public void setHandler(Action handler) { this.handler = handler; }
	
	public void setData(AgentManagerMemo data) {
		Bundle b = new Bundle();
		b.putParcelable("DATA", data);
		setArguments(b);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.memo);
		data = getArguments().getParcelable("DATA");
		
//		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		View v = inflater.inflate(R.layout.memo_edit, container);

//		Map<String, ManagerAgent> agents = ManagerAgent.getAgents();
//		ManagerAgent a = agents.get(data.userid);
		
		TextView tv;
		
		tv = (TextView)v.findViewById(R.id.tvTopic);
		tv.setText(AgentMemo.getTopic(data.topic));
		
		tv = (TextView)v.findViewById(R.id.tvOrgName);
		tv.setText(data.orgName);
		tv.setTextColor(data.dogColor);

		String text = data.dogName + "/" + Integer.toString(data.dogDue) + "к/д /" + 
				Util.IntToScaleStr(data.dogLimit, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		tv = (TextView)v.findViewById(R.id.tvDogovor);
		tv.setText(text);

		text = Util.IntToScaleStr(data.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " + 
				Util.IntToScaleStr(data.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " + 
				Integer.toString(data.overdue);
		tv = (TextView)v.findViewById(R.id.tvDolg);
		tv.setText(text);
		
		tv = (TextView)v.findViewById(R.id.tvMemo);
		tv.setText(data.remark);
		
		text = "Разблокировать до: " + Util.simpleDateFormat.format(data.till); 
		tv = (TextView)v.findViewById(R.id.tvUnlockTill);
		tv.setText(text);
		
		text = "Допустимая сумма: " + Util.IntToScaleStr(data.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false); 
		tv = (TextView)v.findViewById(R.id.tvSum);
		tv.setText(text);
		
		final EditText ed = (EditText)v.findViewById(R.id.edComment);
		ed.setText(data.managerRemark);
		
		v.findViewById(R.id.btnDebt).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DebetDialog dd = new DebetDialog();
				dd.setData(data);
				dd.show(getFragmentManager(), "");
			}
		});

//		InputMethodManager imm = (InputMethodManager)ed.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//		if (imm.isActive())
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
		
		View btnReject = v.findViewById(R.id.btnReject);
		View btnAccept = v.findViewById(R.id.btnAccept);
		if(data.isEditable()) {
			btnReject.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					data.managerRemark = ed.getText().toString();
					if(handler != null)
						handler.accept(data, false);
					dismiss();
				}
			});
			
			btnAccept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					data.managerRemark = ed.getText().toString();
					if(handler != null)
						handler.accept(data, true);
					dismiss();
				}
			});
		} else {
			if(data.isAllowed()) {
				btnAccept.setEnabled(false);
				btnReject.setVisibility(View.INVISIBLE);
			} else {
				btnReject.setEnabled(false);
				btnAccept.setVisibility(View.INVISIBLE);
			}
		}
		
		return v;
	}
}
