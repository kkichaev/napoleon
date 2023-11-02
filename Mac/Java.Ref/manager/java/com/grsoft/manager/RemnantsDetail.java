package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.MRemnantsImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class RemnantsDetail extends DocDetail {
public static Class<? extends RemnantsDetail> activity = RemnantsDetail.class;
	
	public static void open(Context context, CreatableDocument<?> doc) {
		Intent intent = new Intent(context, activity);
		
		intent.putExtra(DocDetailDecorator.DOCTYPE, doc.getClass());
		intent.putExtra(DocDetailDecorator.ROWID, doc.getRowid());
		
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.tvCost).setVisibility(View.GONE);
		findViewById(R.id.tvSum).setVisibility(View.GONE);
	}
	
	@Override
	public ListAdapter createAdapter() {
		return new BaseAdapter(){

			@Override
			public int getCount() {	return ((MRemnantsImpl)getDocument()).getData().items.size();}

			@Override
			public Object getItem(int pos) { return ((MRemnantsImpl)getDocument()).getData().items.get(pos); }

			@Override
			public long getItemId(int pos) { return 0; }

			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				RemnantItem item = (RemnantItem)getItem(pos);
				
				if(item != null)
					view = getItemView(view, item);
		
				view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector
						: R.drawable.even_row_selector);
				
				return view;
			}
		};
	}
	
	private View getItemView(View view, RemnantItem item){
		if(view == null)
			view = View.inflate(this, R.layout.docitems_row, null);
		
		((TextView) view.findViewById(R.id.tvName)).setText(priceName(item.id));
		((TextView) view.findViewById(R.id.tvQty)).setText(Util.IntToScaleStr(
				item.qty, Consts.QTY_SCALE));
		
		view.findViewById(R.id.tvCost).setVisibility(View.GONE);
//		((TextView) view.findViewById(R.id.tvCost)).setText(Util.IntToScaleStr(price.getData().cost.get(0).cost, Consts.SUM_SCALE));
		
		return view;
	}
}
