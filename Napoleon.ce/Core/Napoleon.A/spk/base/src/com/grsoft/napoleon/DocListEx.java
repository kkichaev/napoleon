package com.grsoft.napoleon;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderImplEx;
import com.grsoft.dataobjects.ParamStateEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocListEx extends DocList {
	
	@Override
	protected void docDelete(CreatableDocument<?> doc) {
		if (doc instanceof OrderImplEx && doc.isExported()){
			((OrderImplEx)doc).setMarkToDel();
			doc.write();
			doc.close();
		} else
			super.docDelete(doc);
	}
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if(DocType.getCurDoc() instanceof OrderDoc){
			
			int sum = 0;
			int weight = 0;
			int count = 0;

			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += getDocSum(d);
				weight += ((OrderImplBase<?>)d).weight();
				count += ((OrderImplBase<?>)d).count();
			}
			
			DocType.getCurDoc().updateTotalSum(this, sum, weight, count, R.id.tvDocSum);
			
		}else 
			super.refreshTotalSum(useFilter);
			
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocListAdapterEx(this, docType);
	}
	
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		int result = R.drawable.notsend;
		
		if ((doc.getData().params & ParamStateEx.approved) == ParamStateEx.approved)  
			result = R.drawable.approved;
		else if  ((doc.getData().params & ParamStateEx.pending) == ParamStateEx.pending)
			result = R.drawable.pending;
		else if (doc.isExported())
			result = R.drawable.pod;
		
		return result;
	}
	
	class DocListAdapterEx extends DocListAdapter{

		public DocListAdapterEx(Context context, DocType docType) {
			super(context, docType, R.layout.docs_list_row2ex);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
			
			if (view != null){
				TextView tvAddress = (TextView) view.findViewById(R.id.tvAddress);
				
				if (tvAddress != null){
					OrgImpl orgImpl = new OrgImpl();
					orgImpl.getData().id = doc.getId();
					
					if (orgImpl.read())
						tvAddress.setText(
								Html.fromHtml(String.format("<i>%s</i>", 
										orgImpl.getData().address)));
					
					orgImpl.close();
				}
			}
				
		}
	}
	
	class  DocStatusChangeListenerEx extends DocStatusChangeListener{
		@Override
		protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
			return true;
		}
		
		@Override
		protected void changeStatus(CreatableDocument<?> cd) {
			cd.unsetProceeded();
			super.changeStatus(cd);
		}
	}
	
	@Override
	protected DocStatusChangeListener createStatusChangeListener() {
		return new DocStatusChangeListenerEx();
	}
}
