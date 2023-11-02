package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.PriceHitchingW;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.RestoreDocProceeded;
import com.grsoft.database.RestoreDocProceededEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.StockOrg;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderCancelImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.view.SimpleMessageBox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	final String STATUS_STR = "STATUS";
	PriceHitchingEx phe = null;
	FullPriceEx fpe = null;
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		if( rcvRemains ) {
			phe = new PriceHitchingEx();
			return phe;
		}
		fpe = new FullPriceEx();
		return fpe;
	}

	@Override protected RestoreDocProceeded getRestoreDocProceeded() { return new RestoreDocProceededEx(); }

	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		boolean showError = false; 
		CheckBox rcvCost = (CheckBox)findViewById(R.id.cbCost);

		if( rcvCost != null && rcvCost.isChecked() ) {
			showError = !((CostManagerImpl)Features.COST_MANAGER).isCostReaded();
		}
		
		if( !showError ) {
			if( phe != null && !phe.getReaded() )
				showError = true;
			if( fpe != null && !fpe.getReaded())
				showError = true;
		}
		
		if( showError ) {
			SimpleMessageBox mb = new SimpleMessageBox("Внимание! Новый прайс не принят, цены или остатки могут быть неактуальными.", this);
			task.onUpdateMessage(mb);
		} else {
			PriceChecker.markPriceRecieved(this);
		}

		return !showError;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> ret = super.getExported();
		
		DocExportListener dl = new DocSendListner("OrderCancel", OrderCancelImpl.class, "params", ParamState.ofExported);
		if( dl.getDocuments().getCount() > 0 )
			ret.add(dl);
		
		return ret;
	}
	
	@Override
	protected List<Hitching> getCostHitching() {
		List<Hitching> result =  super.getCostHitching();
		result.add(new RcvNewHitching(StockOrg.class));
		return result;
	}

	@Override
	protected void closeActivity() {
		orderWarningStatus();
	}

	private void orderWarningStatus() {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();

		if (cfg.getValue(sb, "СтатусВнимание")){
			String[] arr = sb.toString().split(";");
			List<String> statusList = new ArrayList<String>();

			for (int i=0; i < arr.length; i++)
				statusList.add(arr[i]);

			StringBuilder attention = new StringBuilder();

			if (statusList.size() > 0){
				for (String s : CurrentStatusesList.getList())
					if (statusList.contains(s)){
						if (attention.length() > 0)
							attention.append(", ");

						attention.append(s);
					}

				if (attention.length() > 0){
					Bundle args = new Bundle();
					args.putString(STATUS_STR, attention.toString());
					showDialog(R.id.status_info_dlg, args);

					return;
				}
			}
		}

		super.closeActivity();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if (id == R.id.status_info_dlg)
			return createStatusDlg();

		return super.onCreateDialog(id, args);
	}

	private Dialog createStatusDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Внимание!");
		builder.setMessage("XXX");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == R.id.status_info_dlg)
			prepareStatusInfoDlg(dialog, args);
		else
			super.onPrepareDialog(id, dialog, args);
	}

	private void prepareStatusInfoDlg(Dialog dialog, Bundle args) {
		((AlertDialog)dialog).setMessage(String.format("Остались документы со статусом: %s", args.getString(STATUS_STR)));
	}
}

class PriceHitchingEx extends PriceHitching {

	boolean readed = false;
	
	boolean getReaded() { return readed; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		super.onRead(rawObject);
		readed = true;
	}
}

class FullPriceEx extends PriceHitchingW {
	boolean readed = false;
	
	boolean getReaded() { return readed; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		super.onRead(rawObject);
		readed = true;
	}
}
