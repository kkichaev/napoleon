package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	
	@Override
	@SuppressWarnings("unchecked")
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if( doc instanceof OrderImplBase ) {
			if( ((OrderImplBase<Order>)doc).getData().number.length() > 0 )
				return R.drawable.sent;
		}
		return super.getDocStatusResource(doc);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DocType dt = DocType.getCurDoc();
		btnSend.setEnabled(dt != OrderDoc.instance() && dt
				!= IncassDoc.instance());
	}
	
	@Override
	public void selectedType(DocType newDocType) {
		super.selectedType(newDocType);
		
		btnSend.setEnabled(newDocType != OrderDoc.instance() && newDocType != IncassDoc.instance());
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		if( doc != null ) {
			Org o = org.getData();
			o.id = doc.getId();
			org.read();
			
			ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
			ivStatus.setImageResource(getDocStatusResource((CreatableDocument<?>)doc));
			
			if (!Features.CANT_CHANGE_SEND_FLAG && doc instanceof CreatableDocument<?>){
				ivStatus.setOnClickListener(sendStatusClickListener);
				ivStatus.setTag(position);
			}
			
			String name = o.name;
			if( doc instanceof OrderImplBase ) {
				@SuppressWarnings("unchecked")
				Order ord = ((OrderImplBase<Order>)doc).getData();
				if( ord.number.length() > 0 )
					name += "<br><i>" + ord.number + "</i>";  
			}
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText(Html.fromHtml(name));
			
			TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
			tvDate.setText(Util.simpleDateFormat.format(doc.getDate()));
			
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
			int costScale = DataObjectInfo.getInstance().getScale(OrderItem.class, "cost");
			tvSum.setText(Util.IntToScaleWStr(getDocSum(doc), costScale, 2, false));
		}
	}
}
