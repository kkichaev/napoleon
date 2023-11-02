	package com.grsoft.napoleon;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.AliantaOfferImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PresentImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.AliantaOfferDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.modules.print.BaseDataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.ViewUtil;
import com.grsoft.view.BaseActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AliantaOfferEdit extends BaseActivity implements DataSetNotify, SendResultListener {
	
	protected static final int WAIT_FOR_PRINT_DLG = 1;

	AliantaOfferImpl doc;
	OrgEx org;
	Adapter adapter;
	PriceImpl pi = new PriceImpl();
	PresentImpl prez = new PresentImpl();
	int picSize = 92;
	
	Map<String, WeakReference<BitmapDrawable>> images = new WeakHashMap<String, WeakReference<BitmapDrawable>>();
	
	public static void open(Context context, AliantaOfferImpl doc) {
		Intent i = new Intent(context, AliantaOfferEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.offer_edit);
		doc = new AliantaOfferImpl();
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgImpl oi = new OrgImpl();
		org = (OrgEx)oi.getData();
		
		org.id = doc.getId();
		oi.read();
		oi.close();
		
		((TextView)findViewById(R.id.tvOrg)).setText(org.name);
	
		ListView lv = (ListView) findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				OfferItem oi = (OfferItem) adapter.getItem(arg2);
				pi.getData().id = oi.id;
				pi.read();
				doc.editItem(pi.getRowid(), AliantaOfferEdit.this);
			}
		});
		
		registerForContextMenu(lv);
		
		findViewById(R.id.btnAddItems).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { Warehouse.open(AliantaOfferEdit.this, doc, true);}
		});
		
		findViewById(R.id.btnPreview).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				makePdf(new Runnable() {
					
					@Override
					public void run() {
						try {
							File output = new File(doc.getData().offerDoc);
							if(output.exists()) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(output), "application/pdf");
								intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								startActivity(intent);
							}
						} catch (Exception e) {
							Toast.makeText(AliantaOfferEdit.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(doc.getData().offerDoc.isEmpty()) {
					makePdf(new Runnable() {
						@Override public void run() { makeEmail(); }
					});
				} else {
					makeEmail();
				}
			}
		});
		
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) {
				DiscountInputDlg.open(AliantaOfferEdit.this, new InputNumber() {
					
					@Override public int getValue() { return doc.getData().discount; }
					
					@Override
					public void applayInput(int value, Object... params) {
						doc.applyDiscount(-value);
						refreshDiscount();
						adapter.notifyDataSetChanged();
					}
				}, Consts.SUM_SCALE, false, "Введите скидку", DiscountInputDlg.Type.OnlyDiscount);
				
			}
		});
		
		refreshDiscount();
	}
	
	protected void makeEmail() {
		String offerDoc = doc.getData().offerDoc;
		if(offerDoc.isEmpty()) {
			return;
		}
		
		doc.markSendEmail();
		new DocumentSender(this, null, AliantaOfferDoc.instance().getObjectName(), 
				doc, doc.getRowid(), this).execute((Void[])null);
				
		String[] to = new String[] {org.email};
		
		File f = new File(offerDoc);
		Uri uri = Uri.fromFile(f);
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("vnd.android.cursor.dir/email");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		//emailIntent.putExtra(Intent.EXTRA_CC, new String[] { ""});
		
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Коммерческое предложение");
		try {
			startActivity(Intent.createChooser(emailIntent , "Отправить email..."));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void makePdf(final Runnable postExec) {
		final BaseDataSource ods = new BaseDataSource(new OfferPrintData(doc.getData()));
		NPrinter.IMAGE_PADDING = 25;
		
		new AsyncTask<String, Void, File>(){
			protected void onPreExecute() { showDialog(WAIT_FOR_PRINT_DLG); };
			
			@Override
			protected File doInBackground(String... params) {
				File result = null;
				try {
					result = NPrinter.print(AliantaOfferEdit.this, "offer", ods);
					
					String outName = doc.getData().offerDoc;
					if(outName.isEmpty()) {
						Date date = new Date();
						String fn = result.getAbsolutePath();
						int pos = fn.lastIndexOf("/");
						outName = fn.substring(0, pos+1) + Long.toString(date.getTime()) + ".pdf";
						doc.getData().offerDoc = outName;
						doc.write();
					}
					
					File f = new File(outName);
					if(f.exists())
						f.delete();
					result.renameTo(f);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return result;
			}
			
			protected void onPostExecute(File output) {
				dismissDialog(WAIT_FOR_PRINT_DLG);
				if(postExec != null)
					postExec.run();
			};
		}.execute("");
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.offer_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		OfferItem i = (OfferItem) adapter.getItem(menuInfo.position);
		if(item.getItemId() == R.id.itDel) {
			doc.remove(i);
			adapter.notifyDataSetChanged();
		}
		return super.onContextItemSelected(item);
	}
	
	void refreshDiscount() {
		String text = "<u><font color='blue'>Скидка: " + Util.IntToScaleStr(doc.getData().discount, Consts.SUM_SCALE) + "%</font></u>";
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		tv.setText(Html.fromHtml(text));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		pi.close();
		prez.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(doc.empty() && doc.isEditable())
			doc.delete();
	}
	
	class Adapter extends BaseAdapter {

		@Override public long getItemId(int arg0) { return arg0; }
		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }


		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(AliantaOfferEdit.this, R.layout.offer_edit_row, null);
			}
						
			OfferItem i = (OfferItem) getItem(arg0);
			PriceEx pe = (PriceEx) pi.getData();
			pe.id = i.id;
			pi.read(); 
			
			WeakReference<BitmapDrawable> b = images.get(i.id);
			BitmapDrawable bd = null;
			try {
				if(b == null || b.get() == null) {
					Present pr = prez.getData();
					pr.id = i.id;
					if(prez.read()) {
						bd = BitmapUtils.createBitmap(pr.photoPath, (int) ViewUtil.dipToPixels(AliantaOfferEdit.this, picSize));
			        	images.put(i.id, new WeakReference<BitmapDrawable>(bd));
					}
				} else {
					bd = b.get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			TextView tv;
			String text = "";
			tv = (TextView)view.findViewById(R.id.tvName);
			text = pe.id + " " + pe.name;
			tv.setText(text);
			ImageView iv = (ImageView)view.findViewById(R.id.ivItem);
			if(bd != null) {
				iv.setVisibility(View.VISIBLE);
				iv.setImageBitmap(bd.getBitmap());
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
			
			tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(i.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			
			return view;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();		
	}

	@Override
	public void postSendExecute(boolean result) {
		doc.read(doc.getRowid(), false);
	}
}
