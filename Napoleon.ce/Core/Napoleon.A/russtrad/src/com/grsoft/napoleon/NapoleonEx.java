package com.grsoft.napoleon;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;

public class NapoleonEx extends Napoleon {
	private ImageButton btnFilter;
	private DebtFilteredAdapter debtFilteredAdapter;
	private TextView tvFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		if (docType.isEqual(DebtDoc.instance())){
			btnFilter.setVisibility(View.VISIBLE);
			findViewById(R.id.btnFind).setVisibility(View.GONE);
		}else{
			btnFilter.setVisibility(View.GONE);
			findViewById(R.id.btnFind).setVisibility(View.VISIBLE);
			applayListViewMode();
		}
	}
	
	protected void applayListViewMode() {
		tvFilter.setVisibility(View.GONE);
		switch (listViewMode)
		{
			case ORG_LIST :
				if (lvMainOrgs.getAdapter() != mainOrgsAdapter)
					lvMainOrgs.setAdapter(mainOrgsAdapter); 
				break;
			case ROUTE_LIST:
				if (lvMainOrgs.getAdapter() != orgFoldersAdapter)
					lvMainOrgs.setAdapter(orgFoldersAdapter); 
				break;
		}
	}
	
	@Override
	public void init() {
		super.init();
		btnFilter = (ImageButton)findViewById(R.id.btnDebtFilter);
		
		btnFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				findOnClickListener.resetFilter();
				
				switch(listViewMode){
				case ORG_LIST:
					if (lvMainOrgs.getAdapter() != debtFilteredAdapter){
						lvMainOrgs.setAdapter(debtFilteredAdapter);
						tvFilter.setVisibility(View.VISIBLE);
					}else
						applayListViewMode();
					
					break;
				case ROUTE_LIST:
					((OrgFoldersAdapterEx)orgFoldersAdapter).applyDFA();
					break;
					
				}
			}
		});
		
		debtFilteredAdapter = new DebtFilteredAdapter();
		
		tvFilter = (TextView)findViewById(R.id.tvFilter);
		tvFilter.setVisibility(View.GONE);
	}
	
	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}
	
	@Override
	void close() {
		super.close();
		
		if (debtFilteredAdapter != null)
			debtFilteredAdapter.close();
	}
	
	class DebtFilteredAdapter extends BaseAdapter implements FilterAdapter{
		Cursor cursor;
		OrgImpl orgImpl = new OrgImpl();

		DebtFilteredAdapter(){
			recreateCursor();
		}
		
		private void recreateCursor(){
			try{
				cursor = DataBaseManager.getDataBase().rawQuery("select org.id "+
						"from org, " +
						"org_sums where org.id = org_sums.id and " + 
						"org_sums.type = 'Долги' and org_sums.sum > 0", null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		public int getCount() {
			if (cursor == null || cursor.isClosed())
				recreateCursor();
			
			return cursor.getCount();
		}

		@Override
		public Object getItem(int position) {
			Object result = null;
			
			if (cursor.moveToPosition(position))
			{
				orgImpl.getData().id = cursor.getString(cursor.getColumnIndex("id"));
				
				if(orgImpl.read())
					result = orgImpl;
				orgImpl.close();
			}
				
			return result;	
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OrgImpl orgImpl = (OrgImpl)getItem(position);
			return getMainView(orgImpl, position, convertView, parent);
		}
		
		public void close(){
			if (cursor != null && !cursor.isClosed())
			cursor.close();
		}

		@Override
		public void applyFilter(String value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resetFilter() {
			// TODO Auto-generated method stub
			
		}
	}
	
	@Override
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		return new OrgFoldersAdapterEx();
	}
	
	class DebtTreeFilter extends RouteFilter{
		OrgSumImpl osi = new OrgSumImpl();
		
		@Override
		public boolean compareTo(DataObject dataObject, String filter) {
			OrgFolderItem ofi = (OrgFolderItem) dataObject;
			osi.getData().sum = 0;
			osi.getData().id = ofi.name;
			osi.getData().type = DebtDoc.instance().getName();
			
			osi.read();
			osi.close();
			
			return osi.getData().sum > 0;
		}
		
		@Override
		public void close() {
			super.close();
			osi.close();
		}
	}
	
	class OrgFoldersAdapterEx extends OrgFoldersAdapter{
		DebtTreeFilter dtf = new DebtTreeFilter();
		
		public void applyDFA(){
			if (tree.isFiltered()){
				setOffFilter();
				tvFilter.setVisibility(View.GONE);
			}else{
				tree.applyFilter(dtf, "dummy");
				tvFilter.setVisibility(View.VISIBLE);
			}
			
			notifyDataSetChanged();
		}
		
		@Override
		protected void resetFilterProcess() {}
		
		public void setOffFilter(){
			tree.resetFilter();
		}
	}
	
	class FindOnClickListenerEx extends FindOnClickListener{

		public FindOnClickListenerEx(EditText findField, ListView listView,
				View groupView) {
			super(findField, listView, groupView);
		}
		
		@Override
		public void onClick(View v) {
			if (lvMainOrgs.getAdapter() == debtFilteredAdapter)
				applayListViewMode();
			
			super.onClick(v);
		}
	}
	
	@Override
	protected FindOnClickListener createFindOnClickListener() {
		return new FindOnClickListenerEx(edFind, lvMainOrgs, llFind);
	}
	
	@Override
	public void setGoUpVisibility(boolean visible) {
		super.setGoUpVisibility(visible);
		tvFilter.setVisibility(View.GONE);
		((OrgFoldersAdapterEx)orgFoldersAdapter).setOffFilter();
	}
}
