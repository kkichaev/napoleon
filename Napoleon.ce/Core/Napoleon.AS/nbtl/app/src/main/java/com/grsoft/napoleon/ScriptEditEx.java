package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Target;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.dataobjects.impl.TargetImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TargetDoc;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;


public class ScriptEditEx extends ScriptEdit {
	private long targetRowID = -1;
	private boolean targetMessageIsShowed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.btnPrice).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), BarcodePrice.class);
				i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
				v.getContext().startActivity(i);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.target_view_dlg)
			return createTargetViewDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createTargetViewDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.target_dialog_title);
		TargetImpl tg = new TargetImpl();
		tg.read(targetRowID);
		tg.close();
		builder.setMessage(tg.getData().remark);
		builder.setPositiveButton(R.string.ok, null);
		builder.setCancelable(false);
		return builder.create();
	}

	protected void onResume() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		Editor e = p.edit();
		e.putLong(ScriptImplEx.CURRENT_SCRIPT_ROW_ID, ExtrasConst.INVALID_ROWID);
		e.commit();
		
		super.onResume();
		
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
		
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Target.class), 
				String.format("id = '%s' and scriptCreated < %d order by scriptCreated",
						doc.getId(), doc.getData().created.getTime()), null);
// 03.03.2020 Кабанов велел убрать
//		
//		if (!targetMessageIsShowed && !hasTarget() && ids.size() > 0) {
//			targetMessageIsShowed = true;
//			targetRowID = ids.get(ids.size() -1);
//			showDialog(R.id.target_view_dlg);
//		}
	};

	protected int getLayoutid() {
		return R.layout.script_editex;
	}
	
	protected CreatableDocument<?> openFirstItem(ScriptImpl scriptImpl, ScriptDefItem item, DocType dt) {return null;}
	
	@Override
	public void refreshDoc() {
		if( doc.read(docRowId, false) ) {
			def.getData().id = doc.getData().scriptId;
			def.read();
		}
		
		doc.refreshDoc();
	}
	
	@Override
	public void onBackPressed() {
		if(new ScriptExitControl().allowExit() || !doc.isContainsItem() || doc.isComplete())
			super.onBackPressed();
		else 
			Toast.makeText(this, R.string.exit_script_warning, Toast.LENGTH_SHORT).show();
		
//		if (doc.isComplete() && !hasTarget()) 
//			createTarget();
	}

	protected void createTarget() {
		TargetImpl result = (TargetImpl) TargetDoc.instance().create();
		result.init(this, doc.getId(), GPSUtilNew.getLastKnownLocation());
		result.getData().defid = def.getData().id;
		result.getData().scriptCreated = doc.getData().created;
		result.write();
		result.open(this);
		result.close();
	}
	
//	@Override
//	protected boolean tryCompleteDoc() {
//		return super.tryCompleteDoc() && hasTarget();
//	}

	private boolean hasTarget() {
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Target.class), 
				String.format("scriptCreated = " + doc.getData().created.getTime()), null);
		
		return ids.size() > 0;
	}
	
	/*@Override
	public void send() {
		if (doc.isComplete() && !hasTarget()) 
			createTarget();
		else
			super.send();
	}*/
}
