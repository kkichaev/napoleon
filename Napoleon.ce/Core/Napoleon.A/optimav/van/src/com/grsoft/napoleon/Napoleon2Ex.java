package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.util.DocStausReciever;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;

public class Napoleon2Ex extends NapoleonEx{
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> result = super.createDocMenuList();
		
		result.add(new MenuHandler(getString(R.string.movement_list), new Runnable() {			
			@Override public void run() { MovementList.open(Napoleon2Ex.this); }
		}));
		
		return result;
	}
	
	protected java.util.ArrayList<MenuHandler> createMainMenuList() {
		ArrayList<MenuHandler> result = super.createMainMenuList();
		
		result.add(0,new MenuHandler("Обновить статусы докуменов",
				new Runnable() { 
					@Override public void run() { updateDocStatus(); }
				}));
		
		return result;
	};
	
	DocStausReciever docStatusReceiver = null;
	
	void updateDocStatus() {
		if( docStatusReceiver == null ) {
			docStatusReceiver = new DocStausReciever(this, new DocStausReciever.TaskDoneHandler() {
				@Override public void taskDone(NetworkAsyncTask task) {
					if( docStatusReceiver == task )
						docStatusReceiver = null;
				}
			});
			
			docStatusReceiver.execute((Void[])null);
		}
	}
	

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if(docType instanceof MovementDoc)
			docType = SalesDoc.instance();
		
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Documents2Ex.currentDocType = null;
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		ArrayList<DocTypeBase> list = new ArrayList<DocTypeBase>();
		for( DocTypeBase dt : DocType.docTypes )
			if( !(dt instanceof MovementDoc) )
				list.add((DocType) dt);
		
		return new DocFilterOnClickListener(this, false, false, list);
	}
}
