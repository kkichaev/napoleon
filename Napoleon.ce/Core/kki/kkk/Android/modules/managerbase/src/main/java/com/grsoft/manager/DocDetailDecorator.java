package com.grsoft.manager;

import java.io.Serializable;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DocDetailDecorator {
	public static final String DOCTYPE = "doctype";
	public static final String ROWID = "rowid";
	
	
	private TextView tvName;
	private TextView tvSum;
	private TextView tvQty;
	private TextView tvComment;
	private ListView list;
	private Document<?> document;
	
	public int getLayoutID(){ return R.layout.docitems; }
	
	public void initView(Activity activity) {
		tvName = (TextView) activity.findViewById(R.id.tvName);
		tvQty = (TextView) activity.findViewById(R.id.tvQty);
		tvSum = (TextView) activity.findViewById(R.id.tvSum);
		tvComment = (TextView) activity.findViewById(R.id.tvComment);
		list = (ListView) activity.findViewById(R.id.list);
	}
	
	public boolean initDoc(Intent intent){
		boolean result = false;
		Serializable s = intent.getSerializableExtra(DOCTYPE);
		long rowid = intent.getLongExtra(ROWID, ExtrasConst.INVALID_ROWID);
		
		try{
			document = (Document<?>)((Class<?>) s).newInstance();
			result = document.read(rowid);
			document.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public void init(final DocDetail dd) {
		DocDataObject data = document.getData();
		tvQty.setText(Util.IntToScaleStr(document.qty(), Consts.QTY_SCALE));
		tvSum.setText(Util.IntToScaleStr(document.sum(), Consts.SUM_SCALE));
		
		if(data instanceof CreateDocDataObject){
			CreateDocDataObject exdata = (CreateDocDataObject)data;
			tvName.setText(dd.getTitle(exdata));
			tvComment.setText(exdata.remark);
			
			if(exdata.remark.trim().length() > 0)
				tvComment.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) { dd.showRemark(v); }
				});
		}
		
		list.setDividerHeight(0);
		list.setAdapter(dd.createAdapter());
	}

	public Document<?> getDocument() {return document; }
}
