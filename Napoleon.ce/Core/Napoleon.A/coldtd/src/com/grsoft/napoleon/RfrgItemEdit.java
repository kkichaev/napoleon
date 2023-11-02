package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Rfrg;
import com.grsoft.dataobjects.RfrgAuditItem;
import com.grsoft.dataobjects.impl.RfrgAuditImpl;
import com.grsoft.napoleon.rfid.RfidHelper;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;
import com.senter.support.openapi.StBarcodeScanner;

public class RfrgItemEdit extends BaseActivity {
	
	private static final String DOC_ITEM = "doc_item";

	protected static final int MODEL_LIST = 0;
	private static final int WAIT_SCANNING = 1;
	
	RfrgAuditImpl doc = new RfrgAuditImpl();
	AtomicBoolean isScanning=new AtomicBoolean(false);
	RfrgAuditItem item;
	int itemPos;
	
	Dialog scanDialog;

	static public void open(Context context, RfrgAuditImpl doc, int item) {
		Intent i = new Intent(context, RfrgItemEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(DOC_ITEM, item);
		
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rfrg_item_edit);
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		itemPos = b.getInt(DOC_ITEM, -1);
		
		if( itemPos < 0  ) {
			item = new RfrgAuditItem();
			doc.getData().items.add(item);
		}  if( itemPos >= 0 ) {
			item = doc.getData().items.get(itemPos);
		}
		
		TextView tv = (TextView)findViewById(R.id.tvRFID);
		tv.setText(item.fact_rfid);
		
		EditText ed;
		ed = (EditText)findViewById(R.id.edFactNum);
		ed.setText(item.fact_id);
		ed.setEnabled(item.doc_id.length() == 0 || item.fact_id.length() == 0);

		ed = (EditText)findViewById(R.id.edModel);
		ed.setText(item.model);
		ed.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				showDialog(MODEL_LIST);
				return false;
			}
		});

		ed = (EditText)findViewById(R.id.edDescr);
		ed.setText(item.descr);
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { finish(); }
		});

		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				save();
				finish(); 
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isScanning.set(false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( doc.isEditable() ) {
			switch (keyCode) {
			case 222: {
				if( !RfidHelper.isScanning() ) {
					showDialog(WAIT_SCANNING);
					RfidHelper.startScanning(new RfidHelper.Handler() {
						@Override public void recievedRFID(String rfid) { 
							setNewID(rfid);
							runOnUiThread(new Runnable() {
								@Override  public void run() {
									try {
										scanDialog.dismiss();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}}, this);
				}
				return true;
			}
			case 212:
			case 221:
				if( isScanning.compareAndSet(false, true) ) {
					Thread scanThread = new Thread() {
						public void run() {
							try {
								StBarcodeScanner scanner=StBarcodeScanner.getInstance();
								if (scanner!=null) {
									String id = scanner.scan();
									if( id.length() > 0 )
										setNewID(id);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							isScanning.set(false);
						}
					};
					scanThread.start();
				}
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void setNewID(final String id) {
		item.fact_rfid = id;
		runOnUiThread(new Runnable() {
			@Override  public void run() { ((TextView)findViewById(R.id.tvRFID)).setText(id); }
		});
		
	}

	protected void save() {
		
		if(doc.isEditable()) {
			EditText ed;
			if( item.doc_id.length() == 0 ) {
				ed = (EditText)findViewById(R.id.edFactNum);
				item.fact_id = ed.getText().toString();
			}
			
			ed = (EditText)findViewById(R.id.edModel);
			item.model = ed.getText().toString();
			ed = (EditText)findViewById(R.id.edDescr);
			item.descr = ed.getText().toString();
			
			doc.write();
		}	
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == MODEL_LIST) {
			final HashSet<String> models = new HashSet<String>();
			DataTraveler.travel(Rfrg.class, new DataTraveler.Travel<Rfrg>() {

				@Override
				public boolean travel(DataTraveler<Rfrg> item) {
					models.add(item.data.model);
					return true;
				}
			}, "");
			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите модель");
			
			final CharSequence[] items = new CharSequence[models.size()];
			models.toArray(items);
			b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText ed = (EditText)findViewById(R.id.edModel);
					ed.setText(items[which].toString());
				}
			});
			return b.create();
		} else if( id == WAIT_SCANNING ) {
			scanDialog = RfidHelper.createWaitDialog(this);
			return scanDialog;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		outState.putInt(DOC_ITEM, itemPos);
	}
	
	@Override
	protected void onStop() {
		super.onStop();		
		doc.close();
	}

}
