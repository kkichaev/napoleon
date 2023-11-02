package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribDef;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindOnClickListener;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.ViewUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("DefaultLocale")
public class DistribEdit extends Activity implements SendResultListener {
	private ListView list;
	private LinearLayout llTitle;
	private TextView tvOrg;
	private View btnSend;
	private View btnFind;
	private EditText edFind;
	private LinearLayout llFind;
	private TextWatcher findWatcher;
	private View btnDelFind;
	
	private DistribImpl document = new DistribImpl();
	private List<DistribDef> defs = new ArrayList<DistribDef>();
	private OrgImpl org = new OrgImpl();
	private Map<String, Map<String, DistribItem>> values = new HashMap<String, Map<String, DistribItem>>();
	private Map<String, DistribDef> defmap = new HashMap<String, DistribDef>();
	Map<String, Map<String, String>> prevValues = new HashMap<String, Map<String, String>>();
	List<String> historyItems = new ArrayList<String>();
	private final int DEFAULT_WEIGHT_VALUE_IN_PERCENT = 10;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, DistribEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	void loadPrevDocValues() {
		Distrib pd = new Distrib();
		String where = "created < " + Long.toString(document.getData().created.getTime()) + " and id = '" + document.getId() + "'";
		DbReader r = new DbReader();
		if(r.select(pd, pd.getTableName(), where, "created desc")) {
			for(DistribItem di : pd.items) {
				Map<String, String> itemMap = prevValues.get(di.id);
				if(itemMap == null) {
					itemMap = new HashMap<String, String>();
					prevValues.put(di.id, itemMap);
				}
				itemMap.put(di.iddef, di.val);
			}
		}
		r.close();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distribedit);
		
		list = (ListView)findViewById(R.id.list);
		llTitle = (LinearLayout) findViewById(R.id.llTitle);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnSend = findViewById(R.id.btnSend);
		btnFind = findViewById(R.id.btnFind);
		edFind = (EditText) findViewById(R.id.edFind);
		llFind = (LinearLayout) findViewById(R.id.llFind);
		btnDelFind = findViewById(R.id.btnDelFind);
		
		document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		document.close();
		
		defs = document.getData().defs;
		Collections.sort(defs, new Comparator<DistribDef>(){ @Override public int compare(DistribDef lhs, DistribDef rhs) { return lhs.pos - rhs.pos; }; });
		
		for(DistribItem i : document.getData().items){
			if(!values.containsKey(i.id))
				values.put(i.id, new HashMap<String, DistribItem>());
			
			Map<String, DistribItem> v = values.get(i.id);
			if(!v.containsKey(i.iddef))
				v.put(i.iddef, i);
		}
		
		Display display = getWindowManager().getDefaultDisplay();
		displaySize = new Point();
		display.getSize(displaySize);
		
		for(DistribDef dd : defs){
			if(!defmap.containsKey(dd.id))
				defmap.put(dd.id, dd);
			int width = (int) ViewUtil.dipToPixels(this, displaySize.x / 100 * calcWeight(dd.weight));
			if(dd.loadPrev != 0) {
				historyItems.add(dd.id);
				width += width / 2;
			}
			
			TextView tv = new TextView(this);
			tv.setWidth(width);
			tv.setText(dd.name);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setBackgroundColor(getResources().getColor(R.color.table_caption));
			llTitle.addView(tv);
		}
		
		if(historyItems.size() > 0)
			loadPrevDocValues();
			
		org.read("id", document.getId());
		tvOrg.setText(org.getData().name);
		
		list.setAdapter(new Adapter());
		list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			    View f = getCurrentFocus();
			    if(f != null) {
			    	f.clearFocus();
			    	imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
			    }
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		btnSend.setOnClickListener(sendClick());
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(DistribDoc.instance().getObjectName(), document.getData().created, document.getId()) != null)
				btnSend.setVisibility(View.GONE);
		}
		
		FindOnClickListener findOnClickListener = new FindOnClickListener(edFind, list, llFind);
		llFind.setVisibility(View.GONE);
		btnFind.setOnClickListener(findOnClickListener);
		findWatcher = new FindTextWatcher(edFind, list);
		edFind.addTextChangedListener(findWatcher);
		btnDelFind.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {	edFind.setText("");	}
		});
		
	}

	protected int calcWeight(int weight) {
		return weight == 0 ? DEFAULT_WEIGHT_VALUE_IN_PERCENT : weight;
	}
	
	private OnClickListener sendClick() { 
		return new OnClickListener() {	
			@Override	public void onClick(View v) { 
				send(); 
			}	
		};}

	protected void send() {
		if (isDocContainsData()) {
			document.write();
			document.close();
			DocumentSender d = new DocumentSender(this, btnSend, DistribDoc.instance().getObjectName(), 
				document, document.getRowid());
			d.setSendResultListener(this);
			d.execute((Void[])null);
		}
		else
			Toast.makeText(this, R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();;
	}

	private void initRowView(View view, int pos){
		RowItemData mi = (RowItemData) list.getAdapter().getItem(pos);
		
		for(DistribDef dd : defs){
			View child = null;
			DistribItem di = findItem(mi.id, dd.id);
			int w = displaySize.x / 100 * calcWeight(dd.weight);
			
			
			if(di != null) {
				if(dd.type.toUpperCase().equals(DistribDef.BOOL_TYPE)){
					child = new CheckBox(this);
					((CheckBox)child).setWidth((int) ViewUtil.dipToPixels(this, w));
				}else if (dd.type.toUpperCase().equals(DistribDef.NUMBER_TYPE)){
					child = new EditText(this);
					((EditText)child).setWidth((int) ViewUtil.dipToPixels(this, w));
					((EditText)child).setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				}
				
				if(child != null ){
					if(historyItems.contains(di.iddef)) {
						TextView tv = new TextView(this);
						tv.setWidth((int) ViewUtil.dipToPixels(this, w) / 2);
						tv.setTag(di.iddef);
						tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
						tv.setGravity(Gravity.RIGHT);
						((LinearLayout) view).addView(tv);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(5, 0, 5, 0);
						lp.gravity = Gravity.CENTER_VERTICAL;
						tv.setLayoutParams(lp);
					}
					((LinearLayout) view).addView(child);
					child.setEnabled(document.isEditable());
					child.setTag(dd.id);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.gravity = Gravity.CENTER;
					child.setLayoutParams(lp);
				}
			}
		}
	}
	
	private static class DsItemTW implements TextWatcher{
		DistribItem item = null;
		
		public DsItemTW(DistribItem i){
			this.item = i;
		}

		@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override public void afterTextChanged(Editable s) {
			if(item != null)
				item.val = s.toString().trim(); 
		}
	}
	
	private Map<View, TextWatcher> textWatchers = new HashMap<View, TextWatcher>();
	
//	private Handler handler = new Handler();
	private Point displaySize;
	
	private void initDefItem(String id, View v, final int position){
		String iddef = (String) v.getTag();
		if(v instanceof TextView && !(v instanceof EditText) && !(v instanceof CheckBox)) {
			Map<String, String> pv = prevValues.get(id);
			String val = pv == null ? "" : pv.get(iddef);
			if(val == null)
				val = "";
			if(val.compareToIgnoreCase("true") == 0)
				val = "да";
			else if(val.compareToIgnoreCase("false") == 0)
				val = "нет";
			((TextView)v).setText(val);
			return;
		}
		DistribItem di = findItem(id, iddef);
		
		if(di != null && defmap.containsKey(di.iddef)){
			DistribDef dd = defmap.get(di.iddef);
			
			if(dd.type.toUpperCase().equals(DistribDef.BOOL_TYPE)){
				boolean val = false;
				try{
					val = Boolean.parseBoolean(di.val);
					CheckBox cb =  ((CheckBox)v);
					cb.setOnCheckedChangeListener(null);
					cb.setChecked(val);
					cb.setOnCheckedChangeListener(new CheckBoxCheckedListener(di));
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if (dd.type.toUpperCase().equals(DistribDef.NUMBER_TYPE)){
				final EditText ed = ((EditText)v); 
				
				if(textWatchers.containsKey(v)) {
					ed.removeTextChangedListener(textWatchers.get(v));
					textWatchers.remove(v);
				}
				
				ed.setText(di.val.equals("0") ? "" : di.val);
				TextWatcher w = new DsItemTW(di);
				ed.addTextChangedListener(w);
				ed.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				
				textWatchers.put(v, w);
			}
		}
	}
	
	class CheckBoxCheckedListener implements OnCheckedChangeListener {
		private DistribItem di;
		
		public CheckBoxCheckedListener(DistribItem di) {
			this.di = di;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(di != null)
				di.val = Boolean.toString(isChecked);
		}
	}
	
	private DistribItem findItem(String id, String def) {
		DistribItem di = null;
		if(values.containsKey(id) && values.get(id).containsKey(def))
			di = values.get(id).get(def);
		
		return di;
	}
	
	class Adapter extends BaseAdapter implements FilterAdapter{
		private List<RowItemData> data;
		private List<RowItemData> filter = new ArrayList<RowItemData>();
		
		public Adapter(){
			this.data = readItems(document.getData().matrix);
			this.filter.addAll(this.data);
		}
		
		@SuppressLint("UseSparseArrays")
		private List<RowItemData> readItems(List<MatrixItem> matrix) {
			
			Map<Integer, RowItemData> folders = new HashMap<Integer, RowItemData>();
			
			FolderImpl fi = new FolderImpl();
			Folder f = fi.getData();
			PriceImpl price = new PriceImpl();
			Price p = price.getData();
			
			for(MatrixItem mi : matrix) {
				p.id = mi.id;
				price.read();
				
				RowItemData pitem = new RowItemData();
				pitem.id = p.id;
				pitem.name = p.name;

				RowItemData fitem = folders.get(p.folderID);
				if(fitem == null) {
					f.id = p.folderID;
					fi.read();
					fitem = new RowItemData();
					fitem.id = f.fid;
					fitem.name = f.name;
					folders.put(p.folderID, fitem);
				}
				fitem.addChild(pitem);
			}
			
			price.close();
			fi.close();
			
			List<RowItemData> fret = new ArrayList<RowItemData>(folders.values());
			Collections.sort(fret);
			
			List<RowItemData> ret = new ArrayList<RowItemData>();
			
			for(RowItemData fitem : fret) {
				ret.add(fitem);
				Collections.sort(fitem.childs);
				ret.addAll(fitem.childs);
			}
			return ret;
		}

		@Override public int getCount() { return filter.size();	}

		@Override public Object getItem(int position) { return filter.get(position); }

		@Override public long getItemId(int position) {	return 0;}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			TextView tv;
			RowItemData item = (RowItemData) getItem(position);
			if(item.childs != null) {
				if(view == null || view.getTag(R.layout.distribedit_folder_row) == null) {
					view = View.inflate(DistribEdit.this, R.layout.distribedit_folder_row, null);
					view.setTag(R.layout.distribedit_folder_row, true);
				}
				
				tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(item.name);
				tv.setBackgroundColor(Color.LTGRAY);
			} else {
				if(view == null || view.getTag(R.layout.distribeditrow) == null ) {
					view = View.inflate(DistribEdit.this, R.layout.distribeditrow, null);
					initRowView(view, position);
					view.setTag(R.layout.distribeditrow, true);
				}
				tv = (TextView) view.findViewById(R.id.tvName);
				tv.setText(item.name);
			
				ViewGroup vg = (ViewGroup)view; 
				
				for(int i = 0; i < vg.getChildCount(); i++){
					View c = vg.getChildAt(i);
					if(c.getId() == R.id.tvName)
						continue;
					initDefItem(item.id, c, position);
				}
			}
			
			return view;
		}

		@Override
		public void applyFilter(String value) {
			filter.clear();
			
			String upV = value.toUpperCase();
			for(RowItemData i : data) {
				if(i.childs == null && i.name.toUpperCase().contains(upV))
					filter.add(i);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			filter.clear();
			filter.addAll(data);
			notifyDataSetChanged();
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing() && document.isEditable()){
			boolean wr = isDocContainsData();
			
			if(!wr)
				document.delete();
			else
				document.write();
			
			document.close();
		}
	}

	private boolean isDocContainsData() {
		boolean result = false;
		
		for(DistribItem di : document.getData().items){
			if(defmap.containsKey(di.iddef)){
				DistribDef dd = defmap.get(di.iddef);
				
				if(dd.type.toUpperCase().equals(DistribDef.NUMBER_TYPE))
					result = !Integer.toString(0).equals(di.val);
				if(dd.type.toUpperCase().equals(DistribDef.BOOL_TYPE))
					result = !Boolean.toString(false).equals(di.val);
			}
			
			if(result)
				break;
		}
		
		return result;
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			finish();
	}
}

class RowItemData implements Comparable<RowItemData> {
	public String id = "";
	public String name = "";
	public List<RowItemData> childs = null;
	
	public void addChild(RowItemData ch) {
		if(childs == null)
			childs = new ArrayList<RowItemData>();
		childs.add(ch);
	}

	@Override
	public int compareTo(RowItemData arg0) {
		return name.compareTo(arg0.name);
	}
}
