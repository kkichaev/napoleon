package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.LoadedOrders;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocumentsEx extends Documents {
	
	Map<Date, LoadedOrders> ldOrders = new HashMap<Date, LoadedOrders>();
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected String orgInfo(Org o) {
		if( o.isPotencial() )
			return super.orgInfo(o);

		OrgEx oe = (OrgEx) o;
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		return oe.name + "<br/>лицензия до: " + (oe.license == null ? "..." : sd.format(oe.license));
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		ldOrders.clear();
		if(docType == OrderDoc.instance()) {
			String where  = "created in (select created from [" + (new Order()).getTableName() + "] where id='" + 
					org.getData().id + "')";
			
			ldOrders = LoadedOrders.get(where);
		}
	}
	
	
	@Override
	protected void doCreate() {

		if (DocType.getCurDoc() == OrderDoc.instance()) {
			OrgEx oe = (OrgEx) org.getData();
			if (!oe.isPotencial() && oe.license.before(new Date())) {
				Toast.makeText(this, "Лицензия контрагента просрочена",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		super.doCreate();
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new DocumentsAdapterEx(this, docType, id, order);
	}
	
	class DocumentsAdapterEx extends DocumentsAdapter {

		public DocumentsAdapterEx(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
			int color = Color.BLACK;
			if(doc instanceof OrderImplEx) {
				Order o = (Order)doc.getData();
				LoadedOrders ld = ldOrders.get(o.created);
				if(ld != null && !ld.isEqualToOrder(o)) {
					color = Color.RED;
				}
				
				TextView tv;
				int[] ids = new int[] { R.id.tvOther, R.id.tvDate, R.id.tvSum};
				for(int i : ids) {
					tv = (TextView)view.findViewById(i);
					if(tv != null)
						tv.setTextColor(color);
				}
			}
		}
	}
}
