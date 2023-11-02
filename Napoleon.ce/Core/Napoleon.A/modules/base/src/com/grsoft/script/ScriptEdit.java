package com.grsoft.script;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.DispositionActivity;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.util.ChatterProtect;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.CreateByScriptDef;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScriptEdit extends BaseActivity {
	public static Class<? extends Activity> activity = ScriptEdit.class; 
	private static final String SCRIPT_DEF_ID = "scriptdefid";
	public static final String REFRESH_DOC_ACTION = "com.grsoft.script.ScriptEdit.REFRESH_DOC_ACTION";
	protected ScriptImpl doc = null;
	protected ScriptDefImpl def = new ScriptDefImpl();
	protected long docRowId;
	protected ChatterProtect chatterProtect = new ChatterProtect();
	
	public static void open(Context c, ScriptImpl doc, int defid) {
		Intent i = new Intent(c, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(SCRIPT_DEF_ID, defid);
		c.startActivity(i);
	}
	
	BroadcastReceiver refreshDocReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshDoc();
			if (listView != null && listView.getAdapter() != null)
				((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
		}
	};
	
	protected ListView listView;
	protected boolean inited;
	
	protected void openDoc(int position) { docOpenning(position); }

	protected void docOpenning(int position) {
		if (chatterProtect.check()) {
			if(Features.ORG_DISPOSITION) {
				if(!doc.isDone(position)) {
					String type = doc.itemType(position);
					if (DispositionActivity.isNeedDisposition(this, doc.getId(), type)) {
						DispositionActivity.open(this, doc.getId());
						return;
					}
					
				}
			}
			doc.openDoc(ScriptEdit.this, position, def.getData());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutid());
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		docRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
	
		listView = (ListView)findViewById(R.id.lvItems);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 0 && position < def.getData().items.size())
					openDoc(position);
			}
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		int scriptdefid = b.getInt(SCRIPT_DEF_ID, ExtrasConst.INVALID_ID);
		doc = (ScriptImpl) ScriptDoc.instance().create();
		inited = false;
		initSrciptImpl(scriptdefid);
		registerForContextMenu(listView);
	}

	protected void initSrciptImpl(int scriptdefid) {
		if (scriptdefid != ExtrasConst.INVALID_ID){
			ScriptImpl scriptImpl = new ScriptImpl();
			ScriptDefImpl scriptDefImpl = new ScriptDefImpl();
			scriptDefImpl.getData().id = scriptdefid;
			
			if (scriptImpl.read(docRowId) && scriptDefImpl.read() && scriptImpl.getData().items.size() == 0){
				for(ScriptDefItem item : scriptDefImpl.getData().items) {
					ScriptItem si = doc.createItem();
					
					DocType dt = (DocType) DocType.getDocType(item.curType);
					
					if(si != null && dt != null){
						si.itemID = item.id;
						si.type = item.curType;
						si.pos = item.pos;
						scriptImpl.getData().items.add(si);
						
						if( !inited ) {
							CreatableDocument<?> doc = openFirstItem(scriptImpl, scriptDefImpl.getData(), item, dt);
							
							if(doc != null){
								si.date = doc.getData().created;
								si.state = ScriptItem.DOC_INITED;
								inited = true;
							}
						}
					}
				}
				
				postScriptInit(scriptDefImpl, scriptImpl);
				scriptImpl.write();
			}
			
			scriptImpl.close();
			scriptDefImpl.close();
		}
	}

	protected void postScriptInit(ScriptDefImpl scriptDefImpl, ScriptImpl scriptImpl) {}

	protected CreatableDocument<?> openFirstItem(ScriptImpl scriptImpl, ScriptDef def, ScriptDefItem item, DocType dt) {
		CreatableDocument<?> doc  = null;
		if(Features.DONT_SHOW_FIRST_SCRIPT_DOC) {
			DocType.setCurDoc(dt);
			
			if (dt instanceof CreateByScriptDef)
				doc = (CreatableDocument<?>) ((CreateByScriptDef)dt).create(def, item);
			else 
				doc = (CreatableDocument<?>) dt.create();
			
			if( doc.init(this, scriptImpl.getData().id, new GpsCoord(scriptImpl.getData().latitude, 
					scriptImpl.getData().longitude, scriptImpl.getData().stltime)) )
				doc.open(this);
		}
		return doc;
	}

	protected int getLayoutid() {
		return R.layout.script_edit;
	}
	
	protected boolean tryCompleteDoc() {
		return doc.tryCompleete(def.getData());
	}
	
	public void send() {
		if(Features.CANT_SEND_SCRIPT_PART && !tryCompleteDoc()){
			Toast.makeText(this, R.string.cant_send_part_script, Toast.LENGTH_SHORT).show();
			
			refreshDoc();
			BaseAdapter a = (BaseAdapter) listView.getAdapter();
			
			if (a != null)
				a.notifyDataSetChanged();
		}else{
			DocumentSender ds = createDocumentSender();
			ds.execute((Void[])null);
		}
	}

	protected DocumentSender createDocumentSender() {
		return new DocumentSender(this, findViewById(R.id.btnSend), doc.getSendedDocuments());
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(refreshDocReceiver, new IntentFilter(REFRESH_DOC_ACTION));
		refreshDoc();
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(createItemsAdapter());
		
		TextView tv = (TextView)findViewById(R.id.tvTotalSum);
		tv.setText(Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		OrgImpl oi = new OrgImpl();
		oi.getData().id = doc.getId();
		oi.read();
		oi.close();
		tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(Html.fromHtml(orgInfo(oi.getData())));
	}
	
	/**
	 * Заголовок окна. Можно использовать HTML теги
	 * @param o
	 * @return
	 */
	protected String orgInfo(Org o) {
		return o.name;
	}

	protected ItemsAdapter createItemsAdapter() { return new ItemsAdapter(); }

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(refreshDocReceiver);
		inited = false;
	}
	
	public void refreshDoc() {
		if( doc.read(docRowId) ) {
			def.getData().id = doc.getData().scriptId;
			def.read();
			
			filterDef(def);
		}
		
		doc.refreshDoc();
	}
	
	private void filterDef(ScriptDefImpl def2) {
		List<ScriptDefItem> list = new ArrayList<ScriptDefItem>();
		
		for(ScriptDefItem sdi : def.getData().items){
			DocType dt = (DocType) DocType.getDocType(sdi.curType);
			if(dt != null)
				list.add(sdi);
		}
		
		def.getData().items = list;
	}

	@Override
	public void onBackPressed() {
		DocType.setCurDoc(ScriptDoc.instance());
		
		if(!doc.isContainsItem())
			doc.delete();
		
		if (keyBackPressed())
			super.onBackPressed();
	}
	
	protected boolean keyBackPressed() { return true;}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		doc.close();
		def.close();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;
		Adapter adapter = listView.getAdapter();
		
		if(adapter != null){
			getMenuInflater().inflate(R.menu.scriptedit_context_menu, menu);
			ScriptDefItem sdi = (ScriptDefItem) adapter.getItem(aMenuInfo.position);
			MenuItem itSkip = menu.findItem(R.id.itSkip);
			
			if(itSkip != null)
				itSkip.setVisible(sdi.canSkip() && doc.IsEnabled(aMenuInfo.position, def.getData()));
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itSkip){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			int pos = menuInfo.position;
			skipItem(pos);
			return true;
		}else
			return super.onContextItemSelected(item);
	}

	protected void skipItem(int pos) {
		doc.setSkipped(pos);
		doc.skipItemsTo(pos);
		BaseAdapter adapter = (BaseAdapter)listView.getAdapter();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	public int getItemViewID(){	return R.layout.script_row; }
	
	public class ItemsAdapter extends BaseAdapter {

		@Override
		public int getCount() { return def.getData().items.size(); }

		@Override
		public Object getItem(int position) {
			ScriptDef sd = def.getData();
			return (position < sd.items.size() ) ? sd.items.get(position) : null;
		}

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ScriptDefItem sd = (ScriptDefItem) getItem(position);
			if( sd == null )
				return null;
			
			DocType dt = (DocType) DocType.getDocType(sd.curType);
			if( convertView == null )
				convertView = View.inflate(ScriptEdit.this, getItemViewID(), null);
			
			ImageView iv = (ImageView) convertView.findViewById(R.id.ivDocIco);
			if( dt != null ) {
				iv.setVisibility(View.VISIBLE);
				iv.setImageResource(dt.getResurceId());
			} else
				iv.setVisibility(View.INVISIBLE);
			
			TextView tv = (TextView)convertView.findViewById(R.id.tvItem);
			tv.setText((sd.name.length() > 0) ? sd.name : (dt != null) ? dt.getName() : "?");

			if (doc.isSkipped(position)){
				tv.setTextColor(Color.BLUE);
				convertView.setBackgroundResource(R.drawable.list_selector);
			} else if( doc.IsEnabled(position, def.getData())) {
				tv.setTextColor(Color.BLACK);
				convertView.setBackgroundResource(R.drawable.list_selector);
			} else {
				tv.setTextColor(Color.GRAY);
				convertView.setBackgroundColor(Color.WHITE);
			}
							
			iv = (ImageView)convertView.findViewById(R.id.ivDocCompleete);
			
			int vsb = View.INVISIBLE;
			
			if(!inited) {
				if( def.getData().items.get(position).canSkip() ){
					vsb = View.VISIBLE;
					iv.setImageResource(R.drawable.skip);
				}
				if (doc.isSkipped(position)) {
					vsb = View.VISIBLE;
					iv.setImageResource(R.drawable.skip);
				} else if(doc.isDone(position)) { 
					vsb = View.VISIBLE;
					iv.setImageResource(R.drawable.apply);
				}
			}
			iv.setVisibility(vsb);
		
			return convertView;
		}
		
	}
}
