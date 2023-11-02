package com.grsoft.napoleon;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgRemnants;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class MtxRemnants extends BaseActivity implements DataSetNotify {
	public static Class<? extends Activity> activity = MtxRemnants.class;
	public Document<?> doc;
	private ListView list; 
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mtx_remnants);
		list = (ListView) findViewById(R.id.list);
		
		doc = DocType.getCurDoc().create();
		if(doc.read(getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID))){
			
			OrgImpl orgImpl = new OrgImpl();
			orgImpl.getData().id = doc.getId();
			
			if(orgImpl.read()){
				list.setAdapter(new OrgMtxAdapter(
						this, ((OrgEx)orgImpl.getData()).remnants,
						(RemnantsImpl)doc));
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						OrgRemnants item = (OrgRemnants) parent.getAdapter().getItem(position);
						PriceImpl p = new PriceImpl();
						p.getData().id = item.id;
						
						if(p.read())
							((Itemsable)doc).editItem(p.getRowid(), view.getContext());
						p.close();
						
					}
				});
				
				list.setDividerHeight(0);
			}
			
			orgImpl.close();
		}
		
		doc.close();
	}

	@Override
	public void notifyDataSetChanged() {
		Adapter adapter = list.getAdapter();
		
		if(adapter != null){
			((BaseAdapter)adapter).notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if(doc.getRowid() != ExtrasConst.INVALID_ID )
				doc.open(this);
		}
		
		return super.onKeyDown(keyCode, event);
	}
}

class OrgMtxAdapter extends BaseAdapter{
	private List<OrgRemnants> items;
	private Context context;
	private PriceImpl price = new PriceImpl();
	private RemnantsImpl doc;
	
	public OrgMtxAdapter(Context context, List<OrgRemnants> items, RemnantsImpl doc){
		this.items = items;
		this.context = context; 
		this.doc = doc;
	}
	
	@Override
	public int getCount() { return items.size(); }

	@Override
	public Object getItem(int position) { return items.get(position); }

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null)
			view = View.inflate(context, R.layout.mtx_remnants_row, null);
		
		OrgRemnants rmn = (OrgRemnants) getItem(position);
		
		if(rmn != null){
			price.getData().id = rmn.id;
			price.read();
			price.close();
			
			Price p = price.getData();
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(p.name);
			
			tv = (TextView) view.findViewById(R.id.tvQty);
			StringBuilder sb = new StringBuilder();
			sb.append(rmn.qty).append("/").append(
							Util.simpleDateFormat.format(rmn.date));
			tv.setText(sb.toString());
			
			RemnantItem item = (RemnantItem) doc.findItem(rmn.id);
			tv = (TextView) view.findViewById(R.id.tvFace);
			
			if(item != null)
				tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			else
				tv.setText("0");
		}
		
		view.setBackgroundResource(position % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector);
		
		return view;
	}
	
}
