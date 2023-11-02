package com.grsoft.napoleon;

import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;

public class DocumentsEx extends Documents {
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new DocumentsAdapterEx(this, docType, id, null);
	}

	class DocumentsAdapterEx extends DocumentsAdapter {
		public DocumentsAdapterEx(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}

		protected DocumentsAdapterEx(Context context, DocType docType, String orgId, String order, int id) {
			super(context, docType, orgId, order, id);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
			
			if( curDocType == DebtDoc.instance() ) {
				if( doc.getData().getClass() == DeliveryEx.class ) {
					DeliveryEx dd = ((DeliveryEx)doc.getData());
					Date d = new Date(100, 1, 1);
					if( dd.payDate.compareTo(d) > 0 && dd.payDate.compareTo(new Date()) < 0 ) {
						TextView tv = (TextView)view.findViewById(R.id.tvDate);
						tv.setTextColor(Color.RED);

						tv = (TextView)view.findViewById(R.id.tvSum);
						tv.setTextColor(Color.RED);
									
						tv = (TextView)view.findViewById(R.id.tvOther);
						tv.setTextColor(Color.RED);
					}
				}
			}
		}
	}
}
