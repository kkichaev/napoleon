package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.InvFrg;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Rfrgr;
import com.grsoft.napoleon.InvFrgEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class InvFrgImpl extends CreatableDocument<InvFrg> {

	@Override
	public void open(Context context) {
		InvFrgEdit.open(context, getRowid());
	}

	@Override
	public void postInit() {
		super.postInit();

		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();

		for(Rfrgr r : o.rfrgr) {
			InvFrgItem i = new InvFrgItem();
			i.id = r.id;
			i.name = r.name;
			i.number = r.number;
			i.volume = r.volume;

			data.items.add(i);
		}
	}

	@Override
	public boolean isEmpty() {
		return data.items.isEmpty();
	}

	@Override
	public boolean delete() {
		boolean ret = super.delete();
		if(ret) {
			VisitImplEx vi = new VisitImplEx();
			vi.getData().created = data.visitDoc;
			if(vi.read()) {
				vi.delete();
			}
			vi.close();
		}
		return ret;
	}

    public boolean checkPhoto(VisitImplEx refVisit) {
		for(InvFrgItem i : data.items) {
			if(refVisit.findPhoto(i.id) == null) {
				return false;
			}
		}

		return true;
    }
}
