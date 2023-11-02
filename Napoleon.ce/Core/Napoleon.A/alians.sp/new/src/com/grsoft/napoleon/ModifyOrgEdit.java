package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ModifyOrg;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ModifyOrgImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class ModifyOrgEdit extends OrgEditActivity implements OnClickListener {
	private Button btnOrgSelect; 
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, ModifyOrgEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	CreatableDocument<?> createDocument() {
		return new ModifyOrgImpl();
	}

	@Override
	int getLayoutId() {
		return R.layout.modifyorgedit;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (doc.isEditable()) {
			doc.write();
			doc.close();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnOrgSelect = (Button) findViewById(R.id.btnOrgSelect);
		
		if (doc.isEditable()) 
			btnOrgSelect.setOnClickListener(this);
		
		ModifyOrg m = (ModifyOrg) doc.getData();
		
		OrgImpl org = new OrgImpl();
		org.read("id", m.orgid);
		
		btnOrgSelect.setText(org.getData().name);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOrgSelect)
			showDialog(R.id.btnOrgSelect);
		super.onClick(v);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.btnOrgSelect)
			return createOrgSelectDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createOrgSelectDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final List<Org> data = new ArrayList<Org>();
		
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>(true) {

			@Override
			public boolean travel(DataTraveler<Org> item) {
				data.add(item.data);
				return true;
			}}, null, "srchName");
		
		ArrayAdapter<Org> aa = new ArrayAdapter<Org>(this, R.layout.simple_spinner_layout, data);
		
		builder.setAdapter(aa, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Org f = data.get(which);
				((ModifyOrg)doc.getData()).orgid = f.id;
				btnOrgSelect.setText(f.name);
			}
		});
		
		return builder.create();
	}
}
