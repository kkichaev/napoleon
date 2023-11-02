package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgInfo;
import com.grsoft.dataobjects.OrgTypeSend;
import com.grsoft.dataobjects.OrgTypes;
import com.grsoft.dataobjects.impl.OrgInfoImpl;
import com.grsoft.dataobjects.impl.OrgTypeSendImpl;
import com.grsoft.dataobjects.impl.OrgTypesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.util.OrgInfoClickListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	
	private static final int SET_ORG_TYPE = 1000;
	ArrayList<OrgTypes> types = new ArrayList<OrgTypes>();

	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		return new DocumentsAdapter(this, docType, id, "date", R.layout.docs_list_rowex);
	}
	
	@Override
	protected String orgInfo(Org o) {
		String title = o.name;
		if( ((OrgEx)o).orgType.length() > 0 ) {
			OrgTypesImpl oti = new OrgTypesImpl();
			OrgTypes ot = oti.getData();
			ot.type = ((OrgEx)o).orgType;
			
			if( oti.read() ) {
				title += "<br>Тип торг.точки: " + ot.name;
			}
			oti.close();
		}
		title += "<br><i>" + o.address + "</i>";
		return title;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == SET_ORG_TYPE ) {
			
			types.clear();
			OrgTypes ot = new OrgTypes();
			String table = DataObjectInfo.getInstance().getTableName(OrgTypes.class);
			DbReader r = new DbReader();
			boolean bdo = r.select(ot, table, null, "name");
			while( bdo ) {
				types.add(ot);
				ot = new OrgTypes();
				bdo = r.selectNext(ot);
			}
			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.set_org_type);
			b.setSingleChoiceItems(new TypesAdapter(), -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					OrgTypes ot = types.get(which);
					OrgEx oe = (OrgEx)org.getData();
					if( !oe.orgType.equals(ot.type) ) {
						
						OrgTypeSendImpl otsi = new OrgTypeSendImpl();
						OrgTypeSend ots = otsi.getData();
						ots.id = oe.id;
						ots.type = ot.type;
						
						otsi.write();
						otsi.close();
						
						oe.orgType = ot.type;
						org.write();
						org.close();
						
						TextView tv = (TextView) findViewById(R.id.tvOrgInfo);
						tv.setText(orgInfo(oe));
					}
					
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void init(Bundle b) {
		super.init(b);
		
		View.OnLongClickListener setOrgType = new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				showDialog(SET_ORG_TYPE);
				return false;
			}
		};
		
		findViewById(R.id.tvOrgInfo).setOnLongClickListener(setOrgType);
		View v = findViewById(R.id.llHeader);
		if( v != null )
			v.setOnLongClickListener(setOrgType);
	}
	
	class TypesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return types.size();
		}

		@Override
		public Object getItem(int position) {
			return (position < types.size()) ? types.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null ) {
				view = View.inflate(DocumentsEx.this, R.layout.org_type_item, null);
			}
			
			OrgTypes ot = (OrgTypes) getItem(position);
			if( ot != null ) {
				TextView tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(ot.name);
			}
			return view;
		}
	}
	
	@Override
	protected OnClickListener createInfoClickListener(){
		return new OrgInfoClickEx(org.getData(), getContactViewid(), this); 
	}
}

class OrgInfoClickEx extends OrgInfoClickListener {

	public OrgInfoClickEx(Org o, int contactViewId, ContactViewChanger changer) {
		super(o, contactViewId, changer);
	}
	
	@Override
	protected int getContentView() {
		return R.layout.org_detail_info_ex;
	}
	
	@Override
	protected void adjustDialogView(View view) {
		super.adjustDialogView(view);
		TextView tv = (TextView)view.findViewById(R.id.tvInfo);
		
		OrgInfoImpl oii = new OrgInfoImpl();
		OrgInfo oi = oii.getData();
		oi.id = o.id;
		if(oii.read()) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml(oi.info));
		} else 
			tv.setVisibility(View.GONE);
	}
}
