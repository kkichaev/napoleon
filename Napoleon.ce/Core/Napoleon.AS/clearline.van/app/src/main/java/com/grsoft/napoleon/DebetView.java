package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class DebetView extends DocumentsBase {
	
	Adapter adapter;
	
	public static void open(Context ctx, String orgId) {
		Intent i = new Intent(ctx, DebetView.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		ctx.startActivity(i);
	}
	
	@Override protected int getContentViewID() { return R.layout.debet;	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgEx oe = (OrgEx)org.getData();
		
		
		adapter = new Adapter(this, DebtDoc.instance(), oe.id, oe.ido, "date desc");

		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Document<?> doc = (Document<?>) adapter.getItem(arg2);
				if( doc != null )
					doc.open(DebetView.this);
			}
		});
		
		Spinner sp = (Spinner)findViewById(R.id.spDebetType);
		String[] values = getResources().getStringArray(R.array.debet_spinner_values);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.debet_mode_row,values);
		sp.setAdapter(aa);

		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				adapter.refresh(arg2 == 0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Spinner sp = (Spinner)findViewById(R.id.spDebetType);
		adapter.refresh(sp.getSelectedItemPosition()==0);
	}
	
	@Override
	protected void onDestroy() {
		adapter.close();
		super.onDestroy();
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType != DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			Documents.open(this, org.getData());
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	class Adapter extends DocumentsAdapter {
		
		String ido;
		
		public Adapter(Context context, DocType docType, String orgId, String ido, String order) {
			super(context, docType, orgId, order);
		
			this.ido = ido;
		}

		public void refresh(boolean showAllDocs) {
			documents.close();
			if( showAllDocs )
				documents = curDocType.docList(null, order, "ido='" + ido + "'");
			else
				documents = curDocType.docList(null, order, "id='" + orgId + "'");
			notifyDataSetChanged();
		}
	
		
	}
}
