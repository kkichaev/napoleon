package com.grsoft.dataobjects.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RejectAct;
import com.grsoft.dataobjects.RejectActItem;
import com.grsoft.napoleon.CreateRejectAct;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.RejectDetail;
import com.grsoft.napoleon.RejectItemEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.RejectActDoc;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.widget.TextView;

public class RejectActImpl extends OrderImplBase<RejectAct> {

	@Override public void open(Context context) { RejectDetail.open(context, this); }
	@Override public void editItem(long itemRowid, Context context) { RejectItemEdit.open(context, this, itemRowid);}
	@Override public void editProperties(Context ctx, boolean isOldOrder) { CreateRejectAct.open(ctx, this, isOldOrder); }

	@SuppressLint("SimpleDateFormat")
	public void setFormText(Activity a) {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = getId();
		oi.read();
		oi.close();

		FirmImpl fi = new FirmImpl();
		FirmEx fe = (FirmEx) fi.getData();
		fe.id = data.firmCode;
		fi.read();
		fi.close();
		
		String text = oe.name + "<br/>" + fe.name + " ";
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM yyyy");
		text += sdf.format(getDate());
		
		TextView tv = (TextView)a.findViewById(R.id.tvOrg);
		tv.setText(Html.fromHtml(text));
	}
	
	@Override
	public long write() {
		RejectActDoc.instance().refreshDocSum(data.id);
		return super.write();
	}

	@Override public CreatableDocument<RejectAct> copy() { return null; }
	@Override protected boolean checkPriceQty() { return false; }
	@Override public CreatableDocument<RejectAct> createInstance() { return new RejectActImpl(); }
	
	public int count(String itemId) {
		int count = 0;
		
		for(OrderItem ri : data.items) {
			if(ri.id.equals(itemId)) {
				count += ri.qty;
			}
		}
		
		return count;
	}

	public boolean isEmpty() {
		return data.items.size() == 0;
	}

	public RejectActItem findItem(String id, String number, Date date, String party) {
		for(OrderItem oi : data.items) {
			RejectActItem ri = (RejectActItem)oi;
			if(ri.id.equals(id) && ri.number.equals(number) && ri.date.equals(date) && ri.party.equals(party))
				return ri;
		}
		return null;
	}
}
