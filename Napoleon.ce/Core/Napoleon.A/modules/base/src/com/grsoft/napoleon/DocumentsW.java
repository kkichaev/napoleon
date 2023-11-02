/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма для отображения документов по организации
 *
 * kki   8/10/2010   creating
 */
package com.grsoft.napoleon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.util.WorkTimeListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.Refreshable;

public class DocumentsW extends DocumentsBase implements Refreshable, WorkTimeListener.ClickHandler
{
	public static Class<? extends Activity> activity = DocumentsW.class;
	
	protected ListView lvDocs;
	protected DocumentsAdapter adapter;
	private WorkTimeListener wtl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lvDocs = (ListView) findViewById(R.id.lvDocs);
		lvDocs.setDividerHeight(0);
		registerForContextMenu(lvDocs);
		
		if (Features.START_STOP) {
			wtl = createWorkTimeListener(this, org.getData().id, (ImageButton) findViewById(R.id.btnStart), btnNewDoc);
			wtl.senOnClickHandler(this);
		}
	}
	
	protected WorkTimeListener createWorkTimeListener(Context context, String orgId, ImageButton startButton, ImageButton newDocButton) {
		return new WorkTimeListener(context, orgId, startButton, newDocButton);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		close();
	}
	
	void close() {
		org.close();
		if( adapter != null )
			adapter.close();
	}
	
	static public void open(Context context, Org org) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.ORG_ID_STR, org.id);
		if( org.isPotencial() )
			i.putExtra(ONLY_VISIT, true);
		
		context.startActivity(i);		
	}
	
	public static void open(Context context, Long orgRowid, boolean onlyVisit){
		Intent i = new Intent(context, activity);
		
		OrgImpl orgImpl = new OrgImpl();
		if (orgImpl.read(orgRowid)){
			
			i.putExtra(ExtrasConst.ORG_ID_STR, orgImpl.getData().id);
			i.putExtra(ONLY_VISIT, onlyVisit);
		
			context.startActivity(i);
		}
		
		orgImpl.close();
	}
	
	public static void open(Context context, OrgImpl orgImpl, boolean onlyVisit){
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgImpl.getData().id);
		i.putExtra(ONLY_VISIT, onlyVisit);
	
		context.startActivity(i);
	}
	
	@Override
	protected void init(Bundle b) {
		super.init(b);
	}
	
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new DocumentsAdapter(this, docType, id, order);
	}

	protected String getOrder(DocType docType) {
		return (docType.isCreatable() == false) ? "date" : "date desc, created desc";
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( adapter == null ) {
			adapter = createAdapter(docType, org.getData().id); //new DocumentsAdapter(this, docType, org.getData().id, null);
			lvDocs.setAdapter(adapter);
			lvDocs.setOnItemClickListener( adapter.clickListner() );
		}
		else {
			adapter.setOrder(getOrder(docType));
			adapter.setDocType(docType);
		}
		
		super.adjustViewForDocType(docType);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if( DocType.getCurDoc().isCreatable() ) {
			getMenuInflater().inflate(getContextMenuId(), menu);
			Class<? extends DocType> ct = DocType.getCurDoc().getClass();
			if( Features.DISABLE_DOC_COPY || QuestionDoc.instance().getClass().isAssignableFrom(ct) )
				menu.removeItem(R.id.itCopy);
		} else
			menu.removeItem(R.id.itCopy);
	}

	protected int getContextMenuId() {
		return R.menu.doc_context_menu;
	}
	
	
	protected void docDelete(CreatableDocument<?> doc) {
		DocDeleteHelper.delete(doc, this);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
		if( doc != null ) 
			onContextAction(item, doc);
		try{
			DocType.getCurDoc().refreshDocSum(org.getData().id);
		}catch (Exception exception){
			exception.printStackTrace();
		}
		
		return super.onContextItemSelected(item);
	}

	protected void onContextAction(MenuItem item, Document<?> doc) {
		if (item.getItemId() == R.id.itDelete) {
			docDelete((CreatableDocument<?>) doc);
		} else if (item.getItemId() == R.id.itEdit) {
			doc.open(this);
		} else if (item.getItemId() == R.id.itCopy) {
			if(canCreateDoc()) {
				CreatableDocument<?> cd = ((CreatableDocument<?>) doc).copy();
				if( cd != null )
					cd.open(this);
				else 
					Toast.makeText(this, R.string.copy_doesnt_allow, 
							Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public void refreshContent() {
		adapter.setDocType((DocType) DocType.getCurDoc());
		refreshTotalSum();
	}
	
	@Override
	protected void refreshTotalSum() {
		if(Features.SHOW_WEIGHT_IN_DOC_LIST  && DocType.getCurDoc() instanceof OrderDoc){
			int weight = 0;
			int sum = 0;
			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += d.sum();
				weight += ((OrderImplBase<?>)d).weight();
			}
			
			updateTotalSum(sum, weight);
		} else
			super.refreshTotalSum();
	}
	
	@Override
	public void onBackPressed() {
		if(wtl != null && wtl.isInWork() )
			return;
		
		super.onBackPressed();
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		boolean result = super.canCreateDoc(docType);
		
		if (wtl != null && result)
			result =  wtl.isInWork();

		return result;
	}

	@Override
	public void onClick(WorkTimeListener owner, boolean inWork) {
		if(!inWork)
			finish();		
	}
}


