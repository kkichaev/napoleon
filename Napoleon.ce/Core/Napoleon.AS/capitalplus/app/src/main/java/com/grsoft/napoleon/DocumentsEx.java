package com.grsoft.napoleon;

import android.app.DialogFragment;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.documents.WhOrderDoc;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.Util;

import java.util.ArrayList;


public class DocumentsEx extends Documents {
	public DeliveryInfo deliveryInfo;

	protected void onlyVisitInit() {
//		super.onlyVisitInit();

		if(ScriptDefImpl.canScripting() && ScriptDefImpl.getAvailableScripts(org.getData().id).size() > 0) {
			btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this) {
				{
					filter = new ArrayList<DocTypeBase>();
					filter.add(ScriptDoc.instance());
					filter.add(QuestionDoc.instance());
				}
			});
		} else {
			btnDocFilter.setOnClickListener(new DocFilterOnClickListener(this) {
				{
					filter = new ArrayList<DocTypeBase>();
					filter.add(VisitDoc.instance());
					filter.add(QuestionDoc.instance());
				}
			});
		}
	}

	protected String orgInfo(com.grsoft.dataobjects.Org o) {
		deliveryInfo = DeliveryInfo.collectDelivery(o.id);
		OrgEx oe = (OrgEx) o;
		StringBuilder sb = new StringBuilder(super.orgInfo(o));

		if(oe.pers.length() > 0)
			sb.append("<br>" + oe.pers);

		sb.append("<br>");

		sb.append(getString(R.string.debt_info, Util.IntToScaleStr(oe.limitsum, Consts.SUM_SCALE),oe.delay));

		return sb.toString();
	};

	@Override
	protected void doCreate() {
		if ((DocType.getCurDoc() == OrderDoc.instance() || DocType.getCurDoc() == WhOrderDoc.instance()) &&
				deliveryInfo.hasExceed){
			DialogFragment dlg = new DeliveryExceedDlg();
			dlg.show(getFragmentManager(),"");
		}else
			super.doCreate();
	}

	public ListAdapter getDlvAdapter() {
		return new DlvAdapter(this, deliveryInfo);
	}

	public void deliveryDialogOKClick() {
		if (isNonBlocking()){
			super.doCreate();
		}
	}

	private boolean isNonBlocking() {
		StringBuilder sb = new StringBuilder();
		ConfigImpl config = new ConfigImpl();
		config.getValue(sb, "Áëîê_ÏÄÇ");

		if(sb.toString().equals("1"))
			return false;

		return true;
	}

	public static class DlvAdapter extends BaseAdapter {
		DeliveryInfo info;
		Context context;

		public DlvAdapter(Context context, DeliveryInfo di){
			this.context = context;
			this.info = di;
		}

		@Override
		public int getCount() {
			return info.list.size();
		}

		@Override
		public Object getItem(int position) {
			return info.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(context, R.layout.delivery_exceed_row, null);

			Delivery dlv = (Delivery) getItem(position);

			TextView tv = view.findViewById(R.id.tvNumber);
			tv.setText(dlv.number);

			tv = view.findViewById(R.id.tvData);
			tv.setText(Util.simpleDateFormat.format(dlv.date));

			tv = view.findViewById(R.id.tvPay);
			tv.setText(Util.simpleDateFormat.format(dlv.payDate));

			tv = view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(dlv.sumD, Consts.SUM_SCALE));

			return view;
		}
	}
}
