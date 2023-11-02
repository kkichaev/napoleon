package com.grsoft.napoleon;

import java.util.List;
import android.widget.CheckBox;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.ObjectListener;


public class UpdateDBEx extends UpdateDB {
	
	//Убирем из списка документов для отправки Инкассацию
	@Override
	protected List<DocExportListener> getExportedDocs(boolean docs, boolean visit) {
		List<DocExportListener> result = super.getExportedDocs(docs, visit);
		
		for(DocExportListener del : result){
			if(del instanceof DocSendListner){
				DocSendListner dsl = (DocSendListner)del;
				
				if(dsl.getObjectName().equals(IncassDoc.instance().getObjectName())){
					result.remove(del);
					break;
				}
			}
		}
		return result;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = super.getExported();
		
		CheckBox cbinc = (CheckBox) findViewById(R.id.cbIncass);
		
		if(cbinc.isChecked()){
			DocExportListener del = IncassDoc.instance().getDirtyDocuments();
			
			if(del != null && del.getDocuments().getCount() > 0)
				result.add(del);
		}
		
		return result;
	}
	
	@Override
	protected int getContentView() { return R.layout.updatedbex; }
}
