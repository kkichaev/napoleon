package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.RequestSync;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.util.WorkTimeListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;

public class DocumentsEx extends Documents {
	public static final int CONFIRM_STOP_TIMER = 50;
	static final String SCRIPT_KIND_TAG = "ScriptKind";
	
	WTLEx wtl;
	
	static public void openForScript(Context context, Org org, String scriptKind) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.ORG_ID_STR, org.id);
		if( org.isPotencial() )
			i.putExtra(ONLY_VISIT, true);
		
		i.putExtra(SCRIPT_KIND_TAG, scriptKind);
		context.startActivity(i);		
	}
	
	@Override
	protected WorkTimeListener createWorkTimeListener(Context context, String orgId, ImageButton startButton,
			ImageButton newDocButton) {
		return new WTLEx(context, orgId, startButton, newDocButton);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wtl = new WTLEx((NapoleonApp)getApplication(), org.getData().id, (ImageButton) findViewById(R.id.btnStart), btnNewDoc);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		boolean needSync = RequestSync.needSync();
		
		if(!needSync) {
			Date date = SyncInfo.getLastSync(SyncInfo.GEN_DATA);
			if(date != null) {
				Calendar c = Calendar.getInstance(), c2 = Calendar.getInstance();
				c.setTime(date);
				needSync = c.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR);
			} else {
				needSync = true;
			}
		}
		
		if(needSync) {
			Toast.makeText(this, "Создание документов запрещено- сначала сделайте синхронизацию", Toast.LENGTH_LONG).show();
			UpdateDB.open(this);
		}
	}
	
	@Override
	protected boolean isGpsPosValid() {
		return DocType.getCurDoc() == ScriptDoc.instance() || super.isGpsPosValid();
	}
	
	@Override
	protected void createNewDoc() {
		super.createNewDoc();
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
		
	@Override
	protected void init(Bundle b) {
		String scriptKind = b.getString(SCRIPT_KIND_TAG);
		ScriptDefImpl.resolver = new ScriptResolver(scriptKind);
		super.init(b);
	}
	
	
	@Override
	public void onBackPressed() {
		if( wtl.isInWork() )
			return;
		super.onBackPressed();
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return wtl.isInWork() && super.canCreateDoc(docType);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CONFIRM_STOP_TIMER ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Потдверждение");
			b.setMessage("Завершить работу с точкой?");
			b.setPositiveButton(R.string.no, null);
			b.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					wtl.stopWork(findViewById(R.id.btnStart));
					arg0.dismiss();
					finish();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
		
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id, null);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new Adapter(this, docType, id, order);
	}
	
	class WTLEx extends WorkTimeListener {

		public WTLEx(Context app, String id, ImageButton btnStart, View newDoc) {
			super(app, id, btnStart, newDoc);
		}
		
		public void stopWork(View v) {
			super.onClick(v);
		}
		
		@Override
		public void onClick(View v) {
			if( isInWork() ) {
				showDialog(CONFIRM_STOP_TIMER);
				return;
			}
			super.onClick(v);
		}
	}
	
	class Adapter extends DocumentsAdapter {

		public Adapter(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
			
			if(doc instanceof ScriptImplEx) {
				String scrName = ((ScriptImplEx)doc).scriptName();
				TextView tv = (TextView)view.findViewById(R.id.tvOther);
				tv.setText(Html.fromHtml(scrName + " " + doc.getDescription(view.getContext())));
			}
		}
		
		@Override public OnItemClickListener clickListner() { return new ClickListnerEx(); }
		
		class ClickListnerEx implements AdapterView.OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if(wtl.isInWork()) {
					Document<?> doc = (Document<?>) getItem(pos);
					if( doc != null )
						doc.open(context);
				}
			}
		}
	}
}
