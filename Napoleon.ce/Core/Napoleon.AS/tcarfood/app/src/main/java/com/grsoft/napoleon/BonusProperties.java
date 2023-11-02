package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.Adapter.Data;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

@SuppressLint("SimpleDateFormat")
public class BonusProperties extends CreateOrder {
	Spinner spOrder;
	private static final int DIALOG_DATE_PICKER_ID = 0;

	public static void open(Context context,
			OrderImplBase<? extends Order> order, boolean editOldOrder) {
		Intent i = new Intent(context, BonusProperties.class);

		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

		context.startActivity(i);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public int getLayoutid() {
		return R.layout.bonusprop;
	}

	protected OrderImplBase<? extends Order> createDocument() {
		return new BonusImpl();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		spOrder = (Spinner) findViewById(R.id.spOrder);

		Date start = Util.getDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date finish = cal.getTime();

		StringBuilder where = new StringBuilder();
		where.append("created > ").append(start.getTime())
				.append(" and created < ").append(finish.getTime())
				.append(" and id='").append(order.getId())
				.append("' and ((params & 1) = 1)");

		Cursor<Order> cursor = new Cursor<Order>(new OrderImpl(),
				where.toString(), "created");

		ArrayList<Data> array = new ArrayList<Data>();

		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

		while (cursor.moveNext()) {
			sb.setLength(0);
			OrderImpl o = (OrderImpl) cursor.current();
			sb.append(sdf.format(o.getData().created)).append(" (")
					.append(Util.IntToScaleStr(o.sum(), Consts.SUM_SCALE))
					.append(")");

			Data d = new Data(o.getData().created, sb.toString(), (int) o.sum());
			array.add(d);
		}

		cursor.close();
		spOrder.setAdapter(new Adapter(this, array));

		btnOK.setOnClickListener(new OKClickListener() {
			@Override
			public void onClick(View v) {
				Adapter.Data data = (Data) spOrder.getSelectedItem();

				if (data != null)
					super.onClick(v);
				else
					Toast.makeText(v.getContext(), R.string.order_missed,
							Toast.LENGTH_SHORT).show();
			}

			@Override
			protected void okDone(boolean updateSumType) {
				Bonus bonus = (Bonus) order.getData();
				Adapter.Data data = (Data) spOrder.getSelectedItem();
				bonus.order = data.created;
				bonus.ordersum = data.sum;
				super.okDone(updateSumType);
			}
		});

		findViewById(R.id.tvDate).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(BonusProperties.this,
								CalendarActivity.class);
						i.putExtra(ExtrasConst.DATE_TAG, order.getDate()
								.getTime());
						startActivityForResult(i, DIALOG_DATE_PICKER_ID);
					}
				});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			order.getData().date = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));		
	}
}

class Adapter implements SpinnerAdapter {
	private ArrayList<Data> data;
	private Context context;

	static public class Data {
		public Date created;
		public String caption;
		public int sum;

		public Data(Date created, String caption, int sum) {
			this.created = created;
			this.caption = caption;
			this.sum = sum;
		}
	}

	public Adapter(Context context, ArrayList<Data> data) {
		this.data = data;
		this.context = context;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.simple_spinner_layout, null);

		TextView tv = (TextView) view.findViewById(R.id.tvFirmaName);
		tv.setText(((Data) getItem(position)).caption);

		return view;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}
