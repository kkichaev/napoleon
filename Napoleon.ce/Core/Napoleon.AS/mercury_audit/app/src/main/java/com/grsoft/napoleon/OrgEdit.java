package com.grsoft.napoleon;

import java.util.UUID;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgType;
import com.grsoft.dataobjects.OrgTypeItem;
import com.grsoft.dataobjects.TypeTP;
import com.grsoft.dataobjects.VisitType;
import com.grsoft.dataobjects.impl.OrgImpl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class OrgEdit extends Activity implements OnClickListener{
	public static final String ORG_ID = "org_id";
	public static final String CITY_NAME = "city_name";
	EditText edName;
	EditText edAddr;
	OrgImpl orgImpl = new OrgImpl();
	Spinner spOrgType;
	Spinner spOrgCateg;
	Spinner spVisitType;
	EditText edFreq;
	EditText edNameTP;
	Spinner spTypeTP;
	
	public static void openForResult(Activity context, String id, String name) {
		Intent i = new Intent(context, OrgEdit.class);
		i.putExtra(ORG_ID, id);
		i.putExtra(CITY_NAME, name);
		context.startActivityForResult(i, R.id.org_edit_id);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.org_edit);
		
		edName = (EditText) findViewById(R.id.edName);
		edAddr = (EditText) findViewById(R.id.edAddr);
		spOrgType = (Spinner) findViewById(R.id.spOrgType);
		spOrgCateg = (Spinner) findViewById(R.id.spOrgCateg);
		spVisitType = (Spinner) findViewById(R.id.spVisitType);
		spTypeTP = (Spinner) findViewById(R.id.spTypeTP);
		edFreq = (EditText) findViewById(R.id.edFreq);
		edNameTP = (EditText) findViewById(R.id.edNameTP);
		 
		findViewById(R.id.btnCancel).setOnClickListener(this);
		findViewById(R.id.btnOK).setOnClickListener(this);
		
		String id = getIntent().getStringExtra(ORG_ID);
		
		if(id == null) {
			orgImpl.getData().id = UUID.randomUUID().toString().replace("-", "");
			orgImpl.getData().address = getIntent().getStringExtra(CITY_NAME);
		}else {
			orgImpl.read("id", id);
		}
		
		initOrgType();
		initVisitType();
		initTypeTP();
		
		initView();
	}

	private void initView() {
		OrgEx org = (OrgEx) orgImpl.getData();
		edName.setText(org.name);
		edAddr.setText(org.address);
		
		if(org.typeID.length() > 0) {
			ArrayAdapter<VDataObject> a = (ArrayAdapter<VDataObject>) spOrgType.getAdapter();
			
			for(int i = 0; i < a.getCount(); i++) {
				VDataObject v = a.getItem(i);
				OrgType t = (OrgType) v.getSource();
				
				if(t.id.equals(org.typeID)) {
					spOrgType.setSelection(i, true);
					break;
				}
			}
		}
		
		if(org.visitTypeID.length() > 0) {
			ArrayAdapter<VDataObject> a = (ArrayAdapter<VDataObject>) spVisitType.getAdapter();
			
			for(int i = 0; i < a.getCount(); i++) {
				VDataObject v = a.getItem(i);
				VisitType t = (VisitType) v.getSource();
				
				if(t.id.equals(org.visitTypeID)) {
					spVisitType.setSelection(i, true);
					break;
				}
			}
		}
		
		edFreq.setText(org.freq);
		edNameTP.setText(org.nameTP);
		
		if(org.typeTPID.length() > 0) {
			ArrayAdapter<VDataObject> a = (ArrayAdapter<VDataObject>) spTypeTP.getAdapter();
			
			for(int i = 0; i < a.getCount(); i++) {
				VDataObject v = a.getItem(i);
				TypeTP t = (TypeTP) v.getSource();
				
				if(t.id.equals(org.typeTPID)) {
					spTypeTP.setSelection(i, true);
					break;
				}
			}
		}
	}

	private void initTypeTP() {
		final ArrayAdapter<VDataObject> ota = new ArrayAdapter<VDataObject>(this, R.layout.simple_spinner_layout);
		DataTraveler.travel(TypeTP.class, new DataTraveler.Travel<TypeTP>(true) {

			@Override
			public boolean travel(DataTraveler<TypeTP> item) {
				ota.add(new VDataObject(item.data, "name"));
				return true;
			}
		}, null);
		
		spTypeTP.setAdapter(ota);
	}

	private void initVisitType() {
		final ArrayAdapter<VDataObject> ota = new ArrayAdapter<VDataObject>(this, R.layout.simple_spinner_layout);
		DataTraveler.travel(VisitType.class, new DataTraveler.Travel<VisitType>(true) {

			@Override
			public boolean travel(DataTraveler<VisitType> item) {
				ota.add(new VDataObject(item.data, "name"));
				return true;
			}
		}, null);
		
		spVisitType.setAdapter(ota);
	}

	protected void initOrgType() {
		final ArrayAdapter<VDataObject> ota = new ArrayAdapter<VDataObject>(this, R.layout.simple_spinner_layout);
		DataTraveler.travel(OrgType.class, new DataTraveler.Travel<OrgType>(true) {

			@Override
			public boolean travel(DataTraveler<OrgType> item) {
				ota.add(new VDataObject(item.data, "name"));
				return true;
			}
		}, null);
		
		spOrgType.setAdapter(ota);
		spOrgType.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				spOrgCateg.setAdapter(null);
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				VDataObject vdo = (VDataObject) parent.getItemAtPosition(position);
				OrgType o = (OrgType)vdo.getSource();
				
				ArrayAdapter<VDataObject> a = new ArrayAdapter<VDataObject>(view.getContext(), R.layout.simple_spinner_layout);
				
				int sel = -1;
				
				for(int i = 0; i < o.items.size(); i++) {
					OrgTypeItem ti = o.items.get(i);
					a.add(new VDataObject(ti, "name"));
					
					if (sel == -1 && ti.id.equals(((OrgEx)orgImpl.getData()).categID))
						sel = i;
				}
				
				spOrgCateg.setAdapter(a);
				
				if (sel != -1)
					spOrgCateg.setSelection(sel, true);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) {
			Intent i = new Intent();
			i.putExtra(ORG_ID, editOrg());
			setResult(RESULT_OK, i);
		}else if (v.getId() == R.id.btnCancel)
			setResult(RESULT_CANCELED);
		
		finish();
	}

	private String editOrg() {
		OrgEx org = (OrgEx) orgImpl.getData();
		
		org.flags &= ~Org.FL_EXPORTED;
		org.flags |= Org.FL_USER_CREATED;
		
		org.name = edName.getText().toString().trim();
		org.address = edAddr.getText().toString().trim();
		org.typeID = getOrgType();
		org.categID = getOrgCateg();
		org.visitTypeID = getVisitType();
		org.freq = edFreq.getText().toString().trim();
		org.nameTP = edNameTP.getText().toString().trim();
		org.typeTPID = getTypeTP();
		
		orgImpl.write();
		orgImpl.close();
		
		return org.id;
	}

	private String getTypeTP() {
		String result = "";
		VDataObject v = (VDataObject) spTypeTP.getSelectedItem();
		
		if(v != null)
			result = ((TypeTP)v.getSource()).id;
		
		return result;
	}

	private String getVisitType() {
		String result = "";
		VDataObject v = (VDataObject) spVisitType.getSelectedItem();
		
		if(v != null)
			result = ((VisitType)v.getSource()).id;
		
		return result;
	}

	private String getOrgCateg() {
		String result = "";
		VDataObject v = (VDataObject) spOrgCateg.getSelectedItem();
		
		if(v != null)
			result = ((OrgTypeItem)v.getSource()).id;
		
		return result;
	}

	private String getOrgType() {
		String result = "";
		VDataObject v = (VDataObject) spOrgType.getSelectedItem();
		
		if(v != null)
			result = ((OrgType)v.getSource()).id;
		
		return result;
	}
}
