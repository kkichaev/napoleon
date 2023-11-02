package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;

public class NapoleonEx extends Napoleon {
	private static final int ORG_INFO = R.id.org_info;
	private OrgImpl infoOrg = null;

	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList();

		docMenu.add(new MenuHandler(getString(R.string.plans), new Runnable() {
			@Override
			public void run() {
				Plans.open(NapoleonEx.this);
			}
		}));

		return ret;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (id == ORG_INFO)
			setInfo((AlertDialog) dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == ORG_INFO)
			return createInfoDialog();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.information);
		builder.setMessage("");
		builder.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				OrgClick ocl = (OrgClick) lvMainOrgs.getOnItemClickListener();
				if (ocl != null)
					ocl.resume(infoOrg);
			}
		});
		return builder.create();
	}

	private void setInfo(AlertDialog dialog) {
		if (infoOrg == null)
			return;

		OrgEx org = (OrgEx) infoOrg.getData();
		int plan = org.deb;
//		int fact = org.getOutdeb();

		StringBuilder text = new StringBuilder();
		text.append(getString(R.string.dolg)).append(": ");
		text.append(Util.IntToScaleStr(plan, Consts.SUM_SCALE, Util.DEC_DELIM,
				false));
//		text.append("\n").append(getString(R.string.out_dolg)).append(": ");
//		text.append(Util.IntToScaleStr(fact, Consts.SUM_SCALE, Util.DEC_DELIM,
//				false));

		dialog.setMessage(text.toString());
	}

	@Override
	protected OnItemClickListener getItemOnClickListner() {
		return new OrgClick();
	}

	class OrgClick extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			infoOrg = oi;
			clickedOrg = oi;
			showDialog(ORG_INFO);
		}

		public void resume(OrgImpl oi) {
			super.openOrg(oi);
		}
	}
}
