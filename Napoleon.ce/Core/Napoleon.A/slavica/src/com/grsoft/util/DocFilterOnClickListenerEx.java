package com.grsoft.util;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;

public class DocFilterOnClickListenerEx extends DocFilterOnClickListener {

	public DocFilterOnClickListenerEx(Selector selector) {
		super(selector);
	}
	
	protected void initData(boolean creatableFilter) {
		if (data.size() == 0){
			if (filter != null)
				data.addAll(filter);
			else if( Features.SCRIPT_DOC && ScriptDefImpl.canScripting() ) {
				DocType sd = ScriptDoc.instance();
				if(creatableFilter)
					data.add(sd);
				else {
					for( DocTypeBase dt : DocType.docTypes )
						if( !dt.isCreatable() || dt == sd )
							data.add((DocType) dt);
					
					if(!data.contains(IncassDoc.instance()))
						data.add(IncassDoc.instance());
				}
			} else {
				if (creatableFilter){
					for(DocTypeBase dt : DocType.docTypes){
						if (dt.isCreatable())
							data.add((DocType) dt);
					}
				} else 
					data.addAll(DocType.docTypes);
				
				if( Features.SCRIPT_DOC )
					data.remove(ScriptDoc.instance());
			}
		}
	}

}
