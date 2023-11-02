package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
		@Override
		protected String orgInfo(Org o) {
			OrgEx oe = (OrgEx)o;
			String orgText = super.orgInfo(o);
			
			if( oe.balance != 0 )
				orgText += "\nДолг: " 
						+ Util.IntToScaleStr(oe.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false) 
						+ " руб.";

			return orgText;
		}
		
		protected DocumentsAdapter createAdapter(DocType docType, String id) {
			return new DebtDocDocumentsAdapter(this, docType, id);
		}
		
		class DebtDocDocumentsAdapter extends DocumentsAdapter{

			public DebtDocDocumentsAdapter(Context context, DocType docType,
					String orgId) {
				super(context, docType, orgId, "date", docType instanceof DebtDoc ? 
						R.layout.debt_list_row : R.layout.docs_list_row);
			}
			
			@Override
			public void setDocType(DocType docType) {
				if(docType instanceof DebtDoc)
					viewId = R.layout.debt_list_row;
				else 
					viewId = R.layout.docs_list_row;
				
				super.setDocType(docType);
			}
			
		}
}
