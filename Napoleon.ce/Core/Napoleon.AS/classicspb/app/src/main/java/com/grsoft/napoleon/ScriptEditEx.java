package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ScriptDefItemEx;
import com.grsoft.dataobjects.VCRMissing;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitCloudResponse;
import com.grsoft.dataobjects.impl.VisitCloudResponseImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ScriptEditEx extends ScriptEdit implements SendResultListener {
	
	private static final int CHANGE_DLG = 10;

	private static final int ERROR_DIALOG = 20;

	Date cloudVisit = null;

	View dlgView;
	List<String> missingItems = new ArrayList<String>();
	String changesDlgStr = "";
	String errMsg = "";
	
	DocumentSender sender = null;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == ERROR_DIALOG) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Ошибка при обработке");
			b.setNeutralButton(android.R.string.ok, null);
			b.setMessage("");
			return b.create();			
		} else if(id == CHANGE_DLG) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Внимание!");
			dlgView = View.inflate(this, R.layout.missing_items_dlg, null);
			b.setView(dlgView);
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { arg0.dismiss(); }
			});
			
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) { 
					arg0.dismiss();
					
					VisitImpl vi = new VisitImpl();
					Visit v = vi.getData();
					v.created = cloudVisit;
					vi.read();
					v.params = 0;
					vi.write();
					vi.close();
					vi.open(ScriptEditEx.this);
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == ERROR_DIALOG) {
			((AlertDialog)dialog).setMessage(errMsg);
		} else if(id == CHANGE_DLG) {
//			AlertDialog ad = ((AlertDialog)dialog); 
			TextView tv = (TextView)dlgView.findViewById(R.id.tvInfo);
			tv.setText(Html.fromHtml(changesDlgStr));
			
			String text = "Не обнаружено " + Integer.toString(missingItems.size()) + " позиций из матрицы";
			tv = (TextView)dlgView.findViewById(R.id.tvMissing);
			tv.setText(text);
			
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, missingItems);
			ListView lv = (ListView)dlgView.findViewById(R.id.lvItems);
			lv.setAdapter(aa);
			
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		cloudVisit = null;
		
		int index = 0;
		for(ScriptDefItem sdi : def.getData().items) {
			if(((ScriptDefItemEx)sdi).needSend != 0) {
				break;
			}
			index++;
		}
		
		Script sd = doc.getData();
		if(index < sd.items.size()) {
			ScriptItem si = sd.items.get(index);
			if(si.state == ScriptItem.DOC_INITED && si.type.equals(VisitDoc.instance().getObjectName()))
				cloudVisit = si.date;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(isFinishing()) {
			if(sender != null)
				sender.setSendResultListener(null);
		}
	}
		
	@Override
	protected ItemsAdapter createItemsAdapter() {
		return new Adapter();
	}
	
	class Adapter extends ItemsAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View ret = super.getView(position, convertView, parent);
			ScriptDefItemEx sd = (ScriptDefItemEx) getItem(position);
			if(sd.needSend > 0) {
				ImageView iv = (ImageView) ret.findViewById(R.id.ivDocIco);
				iv.setImageResource(R.drawable.send_to_cloud);
			}
			return ret;
		}
	}
	
	@Override
	protected DocumentSender createDocumentSender() {
		List<DocExportListener> docs = doc.getSendedDocuments();
		sender = new DocumentSender(this, findViewById(R.id.btnSend), docs, this);
		return sender;
	}
	
	@Override
	public void postSendExecute(boolean result) {
		sender = null;
		if(!result || cloudVisit == null)
			return;

		
		VisitCloudResponseImpl vi = new VisitCloudResponseImpl();
		VisitCloudResponse vcr = vi.getData();
		vcr.created = cloudVisit;
		boolean readed = vi.read();
		vi.close();
		
		if(readed) {
			if(vcr.code != 0 && vcr.answ.length() != 0) {
				errMsg = vcr.answ;
				showDialog(ERROR_DIALOG);
				return;
			}
			boolean showDlg = ((vcr.missing.size() > 0) || (vcr.changes < 0));
			if(vcr.missing.size() > 0) {
				HashMap<String, PriceEx> price = getBCPrice();
				List<PriceEx> missing = new ArrayList<PriceEx>();
				
	//			changesDlgStr = "Нет товаров:\n";
				for(VCRMissing mi : vcr.missing) {
					PriceEx pe = price.get(mi.barcode);
					if(pe != null)
						missing.add(pe);
	//				if(pe != null) {
	//					changesDlgStr += pe.name + "\n";
	//				} else {
	//					changesDlgStr += mi.barcode + "\n";
	//				}
				}
				missingItems.clear();
				for(int i=0; i<missing.size(); i++)
					missingItems.add(missing.get(i).name);
			}
			if(vcr.changes < 0)
				changesDlgStr = "От предыдуего визита стало на <b>" + Integer.toString(-vcr.changes) + "</b> фейсов меньше, необходимо исправить проблему!\n";
			if(showDlg) {
				try {
					showDialog(CHANGE_DLG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	HashMap<String, PriceEx> getBCPrice() {
		final HashMap<String, PriceEx> ret = new HashMap<String, PriceEx>();
		
		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>(true) {

			@Override
			public boolean travel(DataTraveler<PriceEx> item) {
				ret.put(item.data.barcode, item.data);
				return true;
			}
		}, "barcode <> ''");
		
		return ret;
	}
}
