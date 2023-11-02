package com.grsoft.napoleon.documents;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.IDelivery;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public int isum;

	static public DocType instance() {
		if (instance == null)
			instance = new DebtDocEx();
		return instance;
	}

	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";

		if (where != null && where.length() > 0) {
			if (whereStr.length() > 0)
				whereStr += " AND ";
			whereStr += where;
		}

		DocList result = new DebtDocListEx(whereStr, order, LoadDelivery);

		isum = 0;

		for (int i = 0; i < result.getCount(); i++) {
			Document<?> doc = result.get(i); 
			if(doc instanceof IncassImpl){
				IncassImpl incassImpl = (IncassImpl) doc;
				if (incassImpl.isExported())
					isum += incassImpl.sum();
			}
		}

		updateList(result, isum);
		result.close();
		
		return result;
	}

	private void updateList(DocList list, int sum) {
		for (int i = 0; i < list.getCount() && sum > 0; i++) {
			Document<?> d = list.get(i);

			if (d != null && d.getData() instanceof IDelivery) {
				IDelivery dlv = (IDelivery) d.getData();

				if (dlv.getSumD()> 0) {
					sum -= dlv.getSumD();

					if (sum <= 0) {
						dlv.setSumD(Math.abs(sum));
						sum = 0;
					} else {
						dlv.setSumD(0);
					}
				}
				
				d.write();
				d.close();
			} 
		}
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		WindowManager wm = (WindowManager) view.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		int w = metrics.widthPixels / 4;
		TextView tvOther = (TextView) view.findViewById(R.id.tvOther);
		tvOther.setWidth(w);
		TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
		tvDate.setWidth(w);

		TextView tvSumDoc = (TextView) view.findViewById(R.id.tvSumDoc);
		tvSumDoc.setWidth(w);
		tvSumDoc.setGravity(Gravity.RIGHT);
		TextView tvSum = (TextView) view.findViewById(R.id.tvSum);
		tvSum.setWidth(w);

		if (doc instanceof DeliveryImpl) {
			DeliveryImpl dlv = new DeliveryImpl();
			dlv.read(doc.getRowid());
			tvSumDoc.setText("+"
					+ Util.IntToScaleWStr(dlv.sum(), Consts.SUM_SCALE, 2, false));
			tvSum.setText(Util.IntToScaleWStr(((IDelivery)dlv.getData()).getSumD(),
					Consts.SUM_SCALE, 2, false));
			dlv.close();
		}

		if (doc instanceof IncassImpl) {
			IncassImpl dlv = new IncassImpl();
			dlv.read(doc.getRowid());
			tvSumDoc.setText("-"
					+ Util.IntToScaleWStr(dlv.sum(), Consts.SUM_SCALE, 2, false));
			tvSum.setText("");
			dlv.close();
		}

		if (doc instanceof PaymentImpl) {
			PaymentImpl dlv = new PaymentImpl();
			dlv.read(doc.getRowid());
			tvSumDoc.setText(Util.IntToScaleWStr(dlv.sum(), Consts.SUM_SCALE,
					2, false));
			tvSum.setText("");
			dlv.close();
		}
		
		if (doc instanceof OrderImpl) {
			OrderImpl ord = new OrderImpl();
			ord.read(doc.getRowid());
			tvSumDoc.setText(Util.IntToScaleWStr(ord.sum(), Consts.SUM_SCALE, 2, false));
			tvSum.setText(Util.IntToScaleWStr(((IDelivery)ord.getData()).getSumD(),
					Consts.SUM_SCALE, 2, false));
			ord.close();
		}
	}

	public void refreshDocSum() throws RuntimeException {
		DbWriter.checkDBTable(OrgSum.class);
		Map<String, Long> sums = new HashMap<String, Long>();
		DocList list = docList(null, null);
		for (int i = 0; i < list.getCount(); i++) {
			Document<?> d = list.get(i);
			String id = d.getId();

			if (d.getData() instanceof IDelivery) {
				long sum = ((IDelivery) d.getData()).getSumD();
				
				if (sums.containsKey(id))
					sum += sums.get(id);
				
				sums.put(id, sum);
			}
		}
		
		list.close();

		writeSumMap(sums);
	}
	
	public void refreshDocSum(String orgId){
		try{
			DbWriter.checkDBTable(OrgSum.class);
			int sum = 0;
			DocList list = docList(orgId, null);
			
			for( int i=0; i<list.getCount(); i++ ){
				Document<?> d = list.get(i);
				if (d.getData() instanceof IDelivery) 
					sum += ((IDelivery) d.getData()).getSumD();
			}
			
			list.close();
			
			OrgSum os = new OrgSum();
			os.id = orgId;
			os.sum = sum;
			os.type = this.name;
			
			DbWriter w = new DbWriter();
			w.insertRecord(os);
			w.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
