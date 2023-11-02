package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ModifyOrg;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.ModifyOrgEdit;
import com.grsoft.napoleon.OrgInfo;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;

import android.R;
import android.content.Context;

public class ModifyOrgImpl extends CreatableDocument<ModifyOrg> implements OrgInfo {
	private OrgImpl org = new OrgImpl();
	
	@Override
	public void open(Context context) {
		ModifyOrgEdit.open(context, getRowid());
	}

	@Override
	public String getName() {
		return getOrg().name;
	}

	@Override
	public int getTextColor() {
		return R.color.black;
	}

	@Override
	public String getAddress() {
		return getOrg().address;
	}
	
	private Org getOrg() {
		if (org.getRowid() == ExtrasConst.INVALID_ROWID)
			org.read("id", data.orgid);
		
		return org.getData();
	}	

	@Override
	public void postInit() {
		super.postInit();
		
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {

			@Override
			public boolean travel(DataTraveler<Org> item) {
				data.orgid = item.data.id;
				return false;
			}}, null, "srchName");
	}
}
