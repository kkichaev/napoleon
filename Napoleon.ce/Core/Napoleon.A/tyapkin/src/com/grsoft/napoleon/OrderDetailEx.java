package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected boolean haveUnsettedFocusedGroups() {
		List<FocusedGroupItem> fi = FocusedGroupImpl.getUnsettedGroups(doc);

		HashSet<Integer> folders = new HashSet<Integer>(); 
		PriceImpl pi = new PriceImpl();
		Price prc = pi.getData();
		
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = doc.getId();
		oi.read();
		oi.close();
		if (org.matrixName != null && org.matrixName.size() > 0) {
			MatrixImpl mti = new MatrixImpl();
			Matrix m = mti.getData();
			m.name = org.matrixName.get(0).name;
			if( mti.read()) {
				for(MatrixItem mi : m.items){
					prc.id = mi.id;
					pi.read();
					
					folders.add(prc.folderID);
				}
			}
			mti.close();
			pi.close();
		}

		if( folders.size() > 0 ) {
			List<FocusedGroupItem> removed = new ArrayList<FocusedGroupItem>();
			for(FocusedGroupItem fgi : fi) {
				if( folders.contains(fgi.folderID) == false )
					removed.add(fgi);
			}
			
			fi.removeAll(removed);
		}
		
		return fi.size() > 0;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (doc.isEditable() && haveUnsettedFocusedGroups())
			showDialog(R.id.need_focus_items_dlg);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.need_focus_items_dlg)
			return pleaseByFocusDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog pleaseByFocusDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(R.string.need_focus_items);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openFocusItemEditor();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
}
