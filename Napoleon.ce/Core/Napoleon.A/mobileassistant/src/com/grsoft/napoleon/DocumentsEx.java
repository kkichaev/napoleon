package com.grsoft.napoleon;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.DogovorImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;



public class DocumentsEx extends Documents {
	
	DogovorImpl dog = new DogovorImpl();
	
	protected void onlyVisitInit() {
		btnDocFilter.setOnClickListener(null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dog.close();
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new Adapter(this, docType, id, order);
	}
	
	class Adapter extends DocumentsAdapter {
		public Adapter(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
		
			if( doc instanceof OrderImplEx ) {
				OrderEx oe = (OrderEx)doc.getData();
				Dogovor d = dog.getData();
				
				d.id = oe.dgv;
				dog.read();
				
				String dsc = doc.getDescription(view.getContext());;
				String text = "<b>" + d.name + "</b>";
				if( dsc.length() > 0 )
					text += "<br/>" + dsc;
				
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvOther);
				tv.setText(Html.fromHtml(text));
			}
		}
	}
}
