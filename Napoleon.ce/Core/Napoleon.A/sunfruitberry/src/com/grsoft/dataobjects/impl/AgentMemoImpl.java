package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.AgentMemo;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.AgentMemoEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.annotation.SuppressLint;
import android.content.Context;

public class AgentMemoImpl extends CreatableDocument<AgentMemo> {

	public static final String APPROVE_STR = "œŒƒ“¬≈–∆ƒ≈ÕŒ";
	public static final String REJECT_STR = "Œ“ ¿«¿ÕŒ";
	
	@Override
	public void open(Context context) {
		AgentMemoEdit.open(context, this);
	}
	
	@SuppressLint("DefaultLocale")
	public boolean isAppoved() {
		return (data.podRemark.toUpperCase().startsWith(APPROVE_STR));
	}

	@SuppressLint("DefaultLocale")
	public boolean isRejected() {
		return (data.podRemark.toUpperCase().startsWith(REJECT_STR));
	}
	
	@Override
	public String getDescription(Context context) {
		String ret = super.getDescription(context);
		ret = topic() + "<br/>" + ret; 
		return ret;
	}
	
	public String topic() {
		if(data.topicName.length() == 0) {
			ConfigImpl ci = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			ci.getValue(sb, "“ÂÏ˚—ÎÛÊÂ·Ì˚ı«‡ÔËÒÓÍ");
			List<KeyValue> values = new ArrayList<KeyValue>();
			int sel = DialogHelper.makeListWithKey(sb.toString(), values, data.topic);
			if(sel >= 0) {
				data.topicName = values.get(sel).value.toString();
				write();
			}
		}
		return data.topicName;
	}

	public static AgentMemoImpl createSendInvoice(Context context, OrderImpl doc) {
		AgentMemoImpl ret = new AgentMemoImpl();
		ret.init(context, doc.getId(), GPSUtilNew.getLastKnownLocation());
		ret.data.topic = AgentMemo.SEND_INVOICE_ID;
		ret.data.till = doc.getData().created;
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();

		ret.data.email = oe.email;
		ret.write();
		return ret;
	}
}
