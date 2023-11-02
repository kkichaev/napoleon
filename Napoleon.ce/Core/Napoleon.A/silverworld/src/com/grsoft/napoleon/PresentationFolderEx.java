package com.grsoft.napoleon;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.ExtrasConst;

public class PresentationFolderEx extends PresentationFolder {
	LinearLayout llPic;
	
	@Override
	protected int getLayoutId() { return R.layout.presentationfolderex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		llPic = (LinearLayout)findViewById(R.id.llPic);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK 
				&& DocType.getCurDoc() == OrderDoc.instance() && docRowId != ExtrasConst.INVALID_ID) {
			OrderImpl order = new OrderImpl();
			boolean r = order.read(docRowId);
			order.close();
			
			if(r)
				order.open(this);
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		onCreate(getIntent().getExtras());
		createDocItems();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		createDocItems();
	}

	protected void createDocItems() {
		Document<?> doc = DocType.getCurDoc().create();
		DbWriter.checkDBTable(Present.class);
		SQLiteStatement st = DataBaseManager
				.getDataBase().compileStatement("select photoPath from presentation where id=?");
		
		if(doc instanceof OrderImpl && doc.read(docRowId) ){
			PriceImpl p = new PriceImpl();
			llPic.removeAllViews();
			OrderImpl order = (OrderImpl) doc;
			
			for(OrderItem item : order.getData().items){
				st.bindString(1, item.id);
				try{
					String path = st.simpleQueryForString();
					TextView tv = new TextView(this);
					tv.setCompoundDrawablesWithIntrinsicBounds(null, 
							BitmapUtils.createBitmap(path, picSize), null, null);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					final int space = 20;
					lp.setMargins(space / 2, 0, space / 2, 0);
					tv.setLayoutParams(lp);
					tv.setTag(item.id);
					tv.setOnClickListener(itemClick);
					
					p.getData().id = item.id;
					p.read();
					p.close();
					tv.setText(((PriceEx)p.getData()).art);
					tv.setTextColor(getResources().getColor(R.color.black));
					
					llPic.addView(tv);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			p.close();
		}
		
		st.close();
		doc.close();
	}
	
	OnClickListener itemClick = new OnClickListener(){
		@Override
		public void onClick(View v) {
			PriceImpl p = new PriceImpl();
			p.getData().id = (String) v.getTag();
			p.read();
			p.close();
			
			Document<?> doc = DocType.getCurDoc().create();
			doc.read(docRowId);
			doc.close();
			
			if(p.getRowid() != ExtrasConst.INVALID_ID){
				PriceCount.open(v.getContext(), p.getRowid(), doc);
			}
		}
	};
}
