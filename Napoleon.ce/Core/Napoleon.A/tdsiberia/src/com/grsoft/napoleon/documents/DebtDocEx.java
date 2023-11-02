package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.DocumentsEx;
import com.grsoft.napoleon.Napoleon;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	int dlvSum = 0;
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx(DOC_NAME, Debt.class);
	}
	
	protected DebtDocEx(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}
	
	public void setDlvSum(int newDlvSum) { dlvSum = newDlvSum; }

	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");		
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
	
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма/Долг");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		DeliveryEx d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof DeliveryEx )
			d = (DeliveryEx)dobj;

		if( d == null )
			return;

		String str;
		int color = (d.sumD > 0 && d.payDate.compareTo(Util.getDate()) < 0) ? Color.RED : Color.BLACK;
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);

		String text = Util.IntToScaleStr(d.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		if( d.sumPay > 0 ) {
			text += "<br><i>о:" +  Util.IntToScaleStr(d.sumPay, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i>";
		}
		if( d.sumRet > 0 ) {
			text += "<br><i>в:" +  Util.IntToScaleStr(d.sumRet, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i>";
		}
		text += "<br><b>" + Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setText(Html.fromHtml(text));
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
	
	@Override
	public void updateTotalSum(Activity activity, long sum, int weight, int count, int textViewId) {
		if(activity instanceof DocumentsEx) {
			TextView tvTotalSum = (TextView) activity.findViewById(textViewId);
			if( tvTotalSum != null ) {
				tvTotalSum.setVisibility(View.VISIBLE);
				String sumStr = "<b>" + Util.IntToScaleStr(dlvSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
				sumStr += "<br><i>" + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i>";
				tvTotalSum.setText(Html.fromHtml(sumStr));
			}
		} else if(activity instanceof Napoleon) {
			TextView tvTotalSum = (TextView) activity.findViewById(textViewId);
			if( tvTotalSum != null ) {
				int outSum = 0;
				try {
					String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
					String stmt = "select sum(sumD) from '" + table + "' where sumD > 0 and paydate < " + 
								Long.toString(Util.getDate().getTime());
					SQLiteCursor c =  (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(stmt, null);
					if( c.moveToNext() )
						outSum = c.getInt(0);
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				tvTotalSum.setVisibility(View.VISIBLE);
				String sumStr = "<b>" + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
				sumStr += "<br><i>" + Util.IntToScaleStr(outSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i>";
				tvTotalSum.setText(Html.fromHtml(sumStr));
			}
		} else
			super.updateTotalSum(activity, sum, weight, count, textViewId);
	}
}
