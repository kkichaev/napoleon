package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;


public class DocumentsEx extends Documents {
	private CreatableDocument<?> selectedDoc; 
	
	@Override
	protected boolean isBlocked() {	return org.getData().isBlocked(); }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		if (id == R.id.itOrgCp){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			selectedDoc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
			showDialog(R.id.id_copy_dlg);
			return true;
		} else 
			return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if(ScriptDefImpl.canScripting() || DocType.getCurDoc() == QuestionDoc.instance()){
			MenuItem i = menu.findItem(R.id.itOrgCp);
			
			if(i != null)
				i.setVisible(false);
		}
	}

	private Dialog createChOrgDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = View.inflate(this, R.layout.org_list_dlg, null);
		
		ListView list = (ListView) v.findViewById(R.id.lvItems);
		final EditText find = (EditText) v.findViewById(R.id.edFind);
		ImageButton ib = (ImageButton)v.findViewById(R.id.btnClearFind);
		ib.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { find.setText("");}
		});
		
		builder.setView(v);
		list.setAdapter(new OrgListAdapter(this));
		final Dialog result = builder.create();
		
		FindTextWatcher fw = new FindTextWatcher(find, list);
		find.addTextChangedListener(fw);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					Org org = (Org) parent.getItemAtPosition(position);
					
					if(org != null && selectedDoc != null ){
						Document<?> newDoc = selectedDoc.copy();
						String oldId = selectedDoc.getData().id;
						String newId = org.id;
						
						if (newDoc != null && !oldId.equals(newId)){
							newDoc.getData().id = newId;
							newDoc.write();
							newDoc.close();
							DocType dt = DocType.getCurDoc();
							dt.refreshDocSum(newId);
							finish();
							open(view.getContext(), org);
							result.dismiss();
						}
					}
				}
		});
		
		return result;
	}
	
	@Override
	protected int getContextMenuId() { return R.menu.doc_context_menuex; }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.id_copy_dlg)
			return createChOrgDlg();
		return super.onCreateDialog(id);
	}

	
	class OrgListAdapter extends BaseAdapter implements FilterAdapter{
		private List<Org> data;
		List<Org> allData = new ArrayList<Org>();
		private Context context;
		
		public OrgListAdapter(Context context) {
			this.context = context;
			final Class<? extends DataObject> orgtype = DbObject.getDataType(Org.class);
			DataTraveler.travel(orgtype, new DataTraveler.Travel<Org>() {

				@Override
				public boolean travel(DataTraveler<Org> item) {
					allData.add(item.data);
					try{
						item.data = (Org) orgtype.newInstance();
					}catch(Exception e){ e.printStackTrace(); }
					return true;
				}}, null, "name");
			
			data = allData;
		}
		
		@Override
		public int getCount() {	return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(context, R.layout.orglist_row, null);
			
			Org org = (Org)getItem(position);
			setBackground(position, org, view);
			TextView tvOrgName = (TextView)view.findViewById(R.id.text);
			StringBuilder sb = new StringBuilder("<b>");
			sb.append(org.name).append("</b><br>").append(org.address);
			tvOrgName.setText(Html.fromHtml(sb.toString()));
			return view;
		}
		
		protected void setBackground(int position, Org org, View view) {
			view.setBackgroundResource(org.isStopList() ? 
					R.drawable.list_grey_selector : position % 2 != 0 ? R.drawable.even_row_selector: R.drawable.list_selector);
		}

		@Override
		public void applyFilter(String value) {

			value = value.toUpperCase();
			
			data = new ArrayList<Org>();
			for(Org o : allData) {
				if( o.srchName.contains(value))
					data.add(o);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() { data = allData; notifyDataSetChanged(); }
	}

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new DocumentsAdapter(this, docType, id, order, R.layout.docs_list_row_ex);
	}
}
