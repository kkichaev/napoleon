package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.gps.GPSUtilNew;


public class DocumentsEx extends Documents {
	boolean stopped = false;
	private RFOrgListAdapter orgListAdapter;
	private CreatableDocument<?> selectedDoc; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		orgListAdapter = new RFOrgListAdapter(this);
	}
	protected void onResume() {
		stopped = StopHelper.stopDelivery(org.getData().id);
		super.onResume();
	}
	
	@Override protected boolean isOrgBlocked(Org o, DocType dt) {
		return ((dt == OrderDoc.instance() || dt == ScriptDoc.instance()) && stopped) || super.isOrgBlocked(o, dt);
	}
	
//	@Override protected boolean isBlocked() { return stopped || super.isBlocked(); }
	
	@Override protected int getContextMenuId() { return R.menu.doc_context_menuex; }
	
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
		list.setAdapter(orgListAdapter);
		final Dialog result = builder.create();
		
		FindTextWatcher fw = new FindTextWatcher(find, list);
		find.addTextChangedListener(fw);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (orgListAdapter != null){
					Org org = (Org) orgListAdapter.getItem(position);
					String oldId = selectedDoc.getData().id;
					String newId = org.id;
					
					if (!oldId.equals(newId)){
						selectedDoc.getData().id = newId;
						selectedDoc.write();
						selectedDoc.close();
						DocType dt = DocType.getCurDoc();
						dt.refreshDocSum(oldId);
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
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.org_change_dlg)
			return createChOrgDlg();
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;		
		Document<?> doc = (Document<?>) adapter.getItem(aMenuInfo.position);

		getMenuInflater().inflate(getContextMenuId(), menu);
		DocType ct = DocType.getCurDoc();
		if( !ct.isCreatable() )
		{
			menu.removeItem(R.id.itCopy);
			menu.removeItem(R.id.itDelete);
			menu.removeItem(R.id.itEdit);
		}
		
		if(ct != OrderDoc.instance()) {
			MenuItem item = menu.findItem(R.id.itOrgChange);
			if( item != null )
				item.setVisible(false);
			
			if(  !(doc instanceof DeliveryImpl) ) {
				item = menu.findItem(R.id.itMakePKO);
				if( item != null )
					item.setVisible(false);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		if (id == R.id.itOrgChange){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			selectedDoc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
			showDialog(R.id.org_change_dlg);
			return true;
		} else if( id == R.id.itMakePKO ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			makeIncass((Document<?>) adapter.getItem(menuInfo.position));
			return true;
		} else 
			return super.onContextItemSelected(item);
	}

	private void makeIncass(Document<?> selectedDoc) {
		long sum = selectedDoc.sum();
		if( sum == 0 )
			return;
		
		DocType.setCurDoc(IncassDoc.instance());
		
		IncassImpl ii = (IncassImpl) IncassDoc.instance().create();
		IncassEx i = (IncassEx) ii.getData();
		i.sum = (int) sum;
		if( selectedDoc instanceof DeliveryImpl ) {
			Delivery d = (Delivery)selectedDoc.getData();
			i.dlvNumber = d.number;
			i.dlvDate = d.date;
		}
		if( ii.init(this, org.getData().id, GPSUtilNew.getLastKnownLocation(this))) {
			ii.open(this);
		}
		ii.close();
	}
}

class RFOrgListAdapter extends BaseAdapter implements FilterAdapter{
	private List<Org> data;
	List<Org> allData = new ArrayList<Org>();
	private Context context;
	
	public RFOrgListAdapter(Context context) {
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
