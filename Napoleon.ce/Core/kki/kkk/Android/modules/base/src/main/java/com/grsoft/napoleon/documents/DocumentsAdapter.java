package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.R;
import com.grsoft.util.DatePeriod;

/**
 * 
 * јдаптер дл€ списка документов по типам 
 * 
 * @author 1111
 *
 */
public class DocumentsAdapter extends BaseAdapter {

	protected Context context;
	protected DocList documents;
	protected DocType curDocType;
	protected String orgId;
	protected String order;
	protected DatePeriod datePeriod;
	
	protected int viewId;
	
	public DocumentsAdapter(Context context, DocType docType, String orgId, String order) {
		this(context, docType, orgId, order, R.layout.docs_list_row);
	}
	
	public DocumentsAdapter(Context context, DocType docType, String orgId, String order, int id){
		this(context, docType, orgId, order, id, null);
	}
	/**
	 * 
	 * @param context
	 * @param docType - тип документа
	 * @param orgId - id организации
	 * @param order - строка сортировки
	 * @param id - »спользуем R.layout.docs_list_row или порожденные от него
	 * @param dp - период за который будет выборка 
	 */
	protected DocumentsAdapter(Context context, DocType docType, String orgId, String order, int id, DatePeriod dp) {
		this.order = order;
		this.orgId = orgId;
		this.context = context;
		this.viewId = id;
		this.datePeriod = dp;
		
		curDocType = docType;
		
		documents = new FakeDocList();

		fetchByPeriod(docType, dp, orgId, null, null);		
	}

	public DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
		return docType.docList(orgId, order, dp);
	}
	
	public void close() {
		documents.close();
	}
	
	public void setDocType(DocType docType) {
		documents.close();
		documents = fillDocList(docType, orgId, order, datePeriod);
		curDocType = docType;
		notifyDataSetChanged();				
	}

	public void fetchByPeriod(DocType docType, DatePeriod dp){
		fetchByPeriod(docType, dp, orgId, null, null);
//		documents.close();
//		documents = fillDocList(docType, orgId, order, dp);
//		
//		curDocType = docType;
//		datePeriod = dp;
//		notifyDataSetChanged();
	}
	
	public void fetchByPeriod(DocType docType, DatePeriod dp, String orgId, Price item, HashMap<Long, Integer> values){
		this.orgId = orgId;
		documents.close();
		documents = fillDocList(docType, orgId, order, dp);
		
		if( values != null)
			values.clear();
		
		if (item != null) {
			List<Long> toRemoveIds = new ArrayList<Long>();
			for (Document<?> curDoc : documents) {
				if (curDoc instanceof Itemsable ) {
					int qty = ((Itemsable)curDoc).getItemQty(item);
					if( qty == 0 )
						toRemoveIds.add(curDoc.getRowid());
					else if( values != null )
						values.put(curDoc.getRowid(), qty);
				}
			}
			documents.removeDocuments(toRemoveIds);
		}
		
		curDocType = docType;
		datePeriod = dp;
		
		postFetchByPeriod();
		
		notifyDataSetChanged();
	}
	
	protected void postFetchByPeriod() {}

	@Override
	public int getCount() { return (documents != null) ? documents.getCount() : 0; }

	@Override
	public Object getItem(int position) { return (documents != null) ? documents.get(position) : null; }

	@Override
	public long getItemId(int position) { return (documents != null) ? documents.getId(position) : 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = (convertView == null) ? View.inflate(context, viewId, null) : convertView;

		Document<?> doc = (Document<?>)getItem(position);
		if( doc != null ) {
			// при изменении типа документа нужно отображать view по-другому, из-за этого не храним кэш
			
			if (view != null && doc != null) {
				setData(view, doc, position);
				view.setBackgroundResource((position % 2) != 0 ? R.drawable.even_row_selector
						: R.drawable.list_selector);
			}
		}
		
		return view;
	}
	
	class ClickListner implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
			Document<?> doc = (Document<?>) getItem(pos);
			if( doc != null )
				doc.open(context);
		}
	}
	
	/**
	 * ѕо умолчанию, при нажатии происходит открытие документа
	 * @return
	 */
	public AdapterView.OnItemClickListener clickListner() { return new ClickListner(); }

	protected void setData(View view, Document<?> doc, int position) { 
		curDocType.setView(this, view, doc); 
	}
	
	public Context getContext(){
		return context;
	}
	
	public void setOrder(String order){
		this.order = order;
	}
	
	class FakeDocList extends DocList {
		public FakeDocList() {}
	}
}
