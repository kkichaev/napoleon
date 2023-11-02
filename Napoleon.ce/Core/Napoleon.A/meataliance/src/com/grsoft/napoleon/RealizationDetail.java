/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Форма для отображения содержания накладной
 *
 * kki   02/03/2011   creating
 */
package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RealizationImpl;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.ListViewRefresher;
import com.grsoft.view.RegDurationActivity;

public class RealizationDetail extends RegDurationActivity
{
	private PriceImpl priceImpl;
	private ListView lvItems;
	private RealizationImpl realization;
	LinesCountController linesController;
	private static final String TAG = "DeliveryDetail";
	
	public static Class<? extends Activity> activity = RealizationDetail.class;
	
	static public void open(Context context, DbObject<?> doc) {
		Log.d(TAG, "open");
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		realization.close();
		priceImpl.close();
	}
	
	protected int getContentViewId() { return R.layout.orderdetail; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());
	
//		View v = findViewById(R.id.TextView03);
//		
//		if(v != null)
//			v.setVisibility(View.GONE);
		
		priceImpl = new PriceImpl();
		realization = new RealizationImpl();
		
		long delivRowId = ExtrasConst.INVALID_ID;
		
		if (savedInstanceState == null)
			delivRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			delivRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		
		OrgImpl orgImpl = new OrgImpl();
		if (delivRowId != ExtrasConst.INVALID_ID) {
			realization.read(delivRowId);
			orgImpl.getData().id = realization.getData().id;
			orgImpl.read();	
			orgImpl.close();
		}
		else
			Log.d("Error", "delivRowId wasn't set");
		
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(orgImpl.getData().name);
		
		ImageButton btnEditOrder = (ImageButton) findViewById(R.id.btnEditOrder);
		btnEditOrder.setVisibility(View.GONE);
		
		ImageButton btnAddItems = (ImageButton) findViewById(R.id.btnAddItems);
		btnAddItems.setVisibility(View.GONE);
		
		lvItems = (ListView) findViewById(R.id.lvItems);
		ImageButton btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setVisibility(View.GONE);
		
		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				lvItems, btnLines, this);
		linesController = linesOnClickListener.getController();
//		btnLines.setOnClickListener(linesOnClickListener);
		
		updateTotalSum(realization.sum(), realization.weight());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lvItems.setAdapter(new DeliveryItemsAdapter());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, realization.getRowid());
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		ListViewRefresher.refresh(lvItems);
	}
	
	class DeliveryItemsAdapter extends BaseAdapter
	{		
		@Override
		public int getCount()
		{
			return realization.getData().items.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			return realization.getData().items.get(arg0);
		}

		@Override
		public long getItemId(int arg0)
		{
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			DeliveryItem item = (DeliveryItem) getItem(arg0);
			
			View view = arg1; 
			
			if (view == null)
				view = View.inflate(RealizationDetail.this, R.layout.orderdetail_list_row, null);
			
			view.setTag(item);
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
			
			linesController.prepareTextView(tvName);
			linesController.prepareTextView(tvName);

			Price p = priceImpl.getData();
			p.id = item.id;
			String name;
			if( priceImpl.read() )
				name = p.name;
			else
				name = "< '" + getString(R.string.id) + " " + item.id + "' >";
			
			tvName.setText(name);			
			tvName.setTag(p.id);

			
			final int qtyScale = DataObjectInfo.getInstance().getScale(DeliveryItem.class, "qty");
			final int sumScale = DataObjectInfo.getInstance().getScale(DeliveryItem.class, "sum");
			
			tvSum.setText(Util.IntToScaleWStr(item.sum, sumScale, Consts.PRICE_DEC_WIDTH, false));
			//tvSum.setVisibility(View.GONE);
			tvQty.setText(Util.IntToScaleStr(item.qty, qtyScale));

			return view;
		}
	}
}
