package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.AgentManagerMemo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DebetDialog extends DialogFragment {

	AgentManagerMemo data;
	Adapter adapter;
	
	public void setData(AgentManagerMemo data) {
		Bundle b = new Bundle();
		b.putParcelable("DATA", data);
		setArguments(b);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.debet);
		data = getArguments().getParcelable("DATA");
		View v = inflater.inflate(R.layout.debet_view, container);
		
		v.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { dismiss(); }
		});
		
		adapter = new Adapter();
		ListView lv = (ListView)v.findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		
		TextView tv = (TextView)v.findViewById(R.id.tvInfo);
		String text = data.dogName + "/" + Integer.toString(data.dogDue) + "ê/ä /" + 
				Util.IntToScaleStr(data.dogLimit, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
		text +=  "  <b>" + Util.IntToScaleStr(data.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " + 
				Util.IntToScaleStr(data.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " + 
				Integer.toString(data.overdue) + "</b>";
		tv.setText(Html.fromHtml(text));
		
		refresh();
		
		return v;
	}

	protected void refresh() {
		final String where = "idDog='" + data.idDog + "'"; 
		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new HitchOnSelect(OrgBalanceData.class, "OrgBalanceData", where) {
			@Override
			public void prepareReading() {
				super.prepareReading();
				String sql = "delete from " + (new OrgBalanceData()).getTableName() + " where " + where;
				try {
					DataBaseManager.getDataBase().execSQL(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		UpdateProcess upp = new UpdateProcess(getActivity(), new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) {}
			@Override public void onFinish(boolean success) {
				if( success ) { 
					adapter.refresh(data.idDog); 
				}
			}
		}, ret);
		upp.execute((Void[]) null);
		
	}

	static final int[] colors = new int[] {
			Color.RED,
			Color.BLUE,
			-16751616, //Color.GREEN,
	};
	
	public static int getColorFromDueDays(int overdueDays) {
		if(overdueDays >= 26)
			return colors[0];
		if(overdueDays >= 11)
			return colors[1];
		if(overdueDays >= 1)
			return colors[2];
		return Color.BLACK;
	}

	class Adapter extends BaseAdapter {
		
		List<DocData> data = new ArrayList<DocData>();
		
		public void refresh(String idDog) {
			final Date dueDate = Util.getDate();
			DataTraveler.travel(OrgBalanceData.class, new DataTraveler.Travel<OrgBalanceData>() {

				@Override
				public boolean travel(DataTraveler<OrgBalanceData> item) {
					DocData dd = new DocData(item.data, dueDate);
					data.add(dd);
					return true;
				}
			}, "idDog = '" + idDog + "'");
			
			Collections.sort(data);
			notifyDataSetInvalidated();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(getContext(), R.layout.debet_item_row, null);
			}
			DocData dd = (DocData) getItem(arg0);
	
			int color = getColorFromDueDays(dd.overdueDays);
			TextView tv;
			String text = "";

			tv = (TextView)view.findViewById(R.id.tvText);
			tv.setText(Html.fromHtml(dd.number));
			tv.setTextColor(color);

			text = dd.overdueDays > 0 ? Integer.toString(dd.overdueDays) : "";
			tv = (TextView)view.findViewById(R.id.tvDue);
			tv.setText(Html.fromHtml(text));
			tv.setTextColor(color);

			tv = (TextView)view.findViewById(R.id.tvDate);
			text = Util.simpleDateFormat.format(dd.date);
			if( dd.payDate != null )
				text += "<br/>" + Util.simpleDateFormat.format(dd.payDate);
			tv.setText(Html.fromHtml(text));
			tv.setTextColor(color);
		
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(dd.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			tv.setTextColor(color);

			return view;
		}
	}
}

class DocData implements Comparable<DocData> {
	public String number;
	public long sum;
	public Date date;
	public Date payDate;
	public int overdueDays;
	
	public DocData(OrgBalanceData data, Date dueDate) {
		sum = data.sumD;
		number = data.number;
		payDate = data.payDate;
		date = data.date;
		overdueDays = 0;
		
		long pd = payDate.getTime();
		long cd = dueDate.getTime();
		if(pd < cd) {
			overdueDays = (int)((cd - pd) / (24 * 3600 * 1000));
		}
	}


	@Override
	public int compareTo(DocData another) {
		int cmp = another.date.compareTo(date);
		if( cmp != 0 )
			return cmp;
		
		return another.number.compareTo(number);
	}
}