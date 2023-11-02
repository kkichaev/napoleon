package com.grsoft.napoleon;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.DogovorImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;

public class DocListEx extends DocList {
	DogovorImpl dog = new DogovorImpl();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dog.close();
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);
		
		if( doc instanceof OrderImplEx ) {
			OrderEx oe = (OrderEx)doc.getData();
			Dogovor d = dog.getData();
			
			d.id = oe.dgv;
			dog.read();
			
			String text = d.name;
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(text));
		}
	}
}
