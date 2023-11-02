package com.grsoft.napoleon;

import android.view.ContextMenu;
import android.widget.Toast;

import java.util.Date;

import com.grsoft.dataobjects.OrgEx2;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Util;


public class DocumentsEx extends DocumentsPrint {
	@Override protected boolean hideMakePko() { return true; }
	
	@Override
	boolean canCreateDoc() {
		if(DocType.getCurDoc() == SalesDoc.instance()) {
			OrgEx2 o = (OrgEx2) org.getData();
			if(o.noSales > 0) {
				Toast.makeText(this, "«апрет продажи с борта дл€ контрагента", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			Date lastSync = SyncInfo.getLastSync(SyncInfo.GEN_DATA);
			Date checkDate = new Date(Util.getDate().getTime() - 24 * 3600 * 1000);
			if(lastSync == null || lastSync.compareTo(checkDate) < 0) {
				UpdateDB.open(this);
				return false;
			}
		}
		return super.canCreateDoc();
	}
	
	@Override
	protected void postOnCreateContextMenu(Document<?> doc, ContextMenu menu) {
		if(doc instanceof CreatableDocument<?>){
			CreatableDocument<?> cd = (CreatableDocument<?>)doc;
			
			if(cd.isExported())
				menu.removeItem(R.id.itDelete);
		}
	}
}
