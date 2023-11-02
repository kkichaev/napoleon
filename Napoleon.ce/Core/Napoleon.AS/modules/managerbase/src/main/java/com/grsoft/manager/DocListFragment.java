package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.VisitPreview;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.dataobjects.impl.MScriptImpl;
import com.grsoft.dataobjects.impl.NotVisitedImpl;
import com.grsoft.manager.documents.MDocType;
import com.grsoft.manager.documents.MScriptDoc;
import com.grsoft.manager.documents.MVisitDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocListFragment extends Fragment {
	private ListView list;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.doclistfragment, container, false);

		list = (ListView) result.findViewById(R.id.list);
		list.setDividerHeight(0);
		Context ctx = getActivity();
		list.setAdapter(createDocListAdapter(ctx));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long arg3) {
				DocRow item = (DocRow) adapterView.getAdapter().getItem(pos);
				item.open(getActivity());
			}
		});

		registerForContextMenu(list);
		return result;
	}

	@NonNull
	protected DocListAdapter createDocListAdapter(Context ctx) {
		return new DocListAdapter(ctx, (SelParam) ctx);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(R.string.task_menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if( item.getTitle() == getString(R.string.task_menu) ) {
			AdapterContextMenuInfo cmi =(AdapterContextMenuInfo)item.getMenuInfo();
			DocRow row = (DocRow) list.getItemAtPosition(cmi.position);
			if( row != null )
				openOrgTaskList(row);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	protected void openOrgTaskList(DocRow row) {
		OrgTaskList.open(getActivity(), row.getDocument().getId(), ((SelParam)getActivity()).getUserid());
	}

	public void refresh() {
		DocListAdapter adapter = (DocListAdapter) list.getAdapter();
		adapter.load();
		adapter.notifyDataSetChanged();
	}
}

class DocRow implements Comparable<DocRow> {
	Document<? extends DocDataObject> doc;
	public String docName = null;
	
	public DocRow(Document<? extends DocDataObject> doc) {
		this(doc, null);
	}
	
	public DocRow(Document<? extends DocDataObject> doc, String name) {
		this.doc = doc;
		this.docName = name;
	}

	public void open(Context context) {
		doc.open(context);
	}
	
	public Document<? extends DocDataObject> getDocument() { return doc; }

	@Override
	public int compareTo(DocRow another) {
		DocDataObject docData = doc.getData();
		DocDataObject anth = another.doc.getData();
		boolean isNV = doc instanceof NotVisitedImpl;
		boolean aIsNV = another.doc instanceof NotVisitedImpl;
		
		if( isNV && !aIsNV )
			return 1;
		if( !isNV && aIsNV)
			return -1;
		
		if( docData instanceof CreateDocDataObject && anth instanceof CreateDocDataObject )
			return ((CreateDocDataObject)docData).created.compareTo(((CreateDocDataObject)anth).created);
		return docData.date.compareTo(anth.date);
	}
	
	public int getImage() { return -1; }

	public String getText(Context context) {
		return docName == null ? MDocType.getTitle(context, doc.getData().getClass()) : docName;
	}
}

class ScriptRow extends DocRow {
	
	private static final Object VISIT_DOC_NAME = "Visit";
	DocRow[] scriptDocs;
	DocListAdapter owner;
	boolean expanded = false;
	ScriptDef def;
	
	public ScriptRow(CreatableDocument<? extends CreateDocDataObject> doc, DocListAdapter owner, ScriptDef def) {
		super(doc);
		
		this.owner = owner;
		this.def = def;
		
		scriptDocs = new DocRow[((MScriptImpl)doc).getData().items.size()];
	}
	
	@Override
	public String getText(Context context) {
		if(def != null && def.name.length() > 0)
			return def.name;
		return super.getText(context);
	}
	
	@Override
	public void open(Context context) {
		if( expanded ) {
			owner.removeDocs(this, scriptDocs);
		} else {
			owner.addDocs(this, scriptDocs);
		}
		expanded = !expanded;
	}
	
	public boolean addDoc(String docType, CreatableDocument<? extends CreateDocDataObject> addDoc) {
		boolean ret = false;
		
		Script sdoc = (Script)doc.getData();
		if(sdoc.items != null) {
			int idx = 0;
			for(ScriptItem si : sdoc.items) {
				if( si.type.equals(docType) && si.date.equals(addDoc.getData().created) ) {
					String name = null;
					if(def != null && def.items.size() > idx && docType.equals(VISIT_DOC_NAME) ) {
						String tn = def.items.get(idx).name;
						if(tn.length() > 0)
							name = tn; 
					}
					scriptDocs[idx] = new DocRow(addDoc, name);
					ret = true;
					break;
				}
				idx ++;
			}
		}
		
		return ret;
	}
	
	@Override
	public int getImage() { return expanded ? R.drawable.folder_opened :  R.drawable.folder; } 
}

class DocListAdapter extends BaseAdapter{
	protected List<DocRow> data = null;
	protected Context context = null;
	@SuppressLint("SimpleDateFormat")
	protected static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	private MOrgImpl org = new MOrgImpl();
	private SelParam param;
	
	SparseArray<ScriptDef> scriptDefs = new SparseArray<ScriptDef>();
	
	public DocListAdapter(Context context, SelParam param) {
		this.context = context;
		this.param = param;
		data = new ArrayList<DocRow>();
		load();
	}

	public void addDocs(ScriptRow scriptRow, DocRow[] scriptDocs) {
		if( scriptDocs.length > 0 ) {
			int index = data.indexOf(scriptRow);
			if( index < 0 )
				index = 0;
			index++;
			for(DocRow doc : scriptDocs) {
				if(doc != null)
					data.add(index++, doc);
			}
		}
		notifyDataSetChanged();
	}

	public void removeDocs(ScriptRow scriptRow, DocRow[] scriptDocs) {
		List<DocRow> rmv = new ArrayList<DocRow>();
		for(DocRow dr : scriptDocs) {
			rmv.add(dr);
		}
		
		if( scriptDocs.length > 0) {
			data.removeAll(rmv);
			notifyDataSetChanged();
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		data.clear();
		scriptDefs.clear();
		
		DataTraveler.travel(ScriptDef.class, new DataTraveler.Travel<ScriptDef>(true) {
			@Override
			public boolean travel(DataTraveler<ScriptDef> item) {
				scriptDefs.append(item.data.id, item.data);
				return true;
			}
		}, "");

		List<ScriptRow> scripts = new ArrayList<ScriptRow>();
		for(Document<?> d : MScriptDoc.instance().userDoc(this.param.getUserid(), this.param.getDate())) {
			int sid = ((MScriptImpl)d).getData().scriptId;
			scripts.add(new ScriptRow((CreatableDocument<? extends CreateDocDataObject>) d, this, scriptDefs.get(sid)));
		}

		for(DocTypeBase dt : DocTypeBase.docTypes){
			MDocType mdt = (MDocType)dt;
			if( dt == MScriptDoc.instance() )
				continue;
			
			for(Document<?> doc : mdt.userDoc(this.param.getUserid(), this.param.getDate())) {
				boolean added = false;
				if( doc instanceof CreatableDocument ) {
					for(ScriptRow sc : scripts) {
						String docName = (dt != MVisitDoc.instance()) ? dt.getObjectName() : "Visit";
						if(sc.addDoc(docName, (CreatableDocument<? extends CreateDocDataObject>)doc)) {
							added = true;
							break;
						}
					}
				}
				if( !added )
					data.add(new DocRow(doc));
			}
		}
		
		data.addAll(scripts);
		Collections.sort(data);
	}
	
	@Override
	public int getCount() { return data.size(); }

	@Override
	public Object getItem(int position) { return data.get(position); }

	@Override
	public long getItemId(int position) { return 0;	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.doclistfragment_row, null);

		DocRow dr = (DocRow)getItem(pos);
		if( dr != null )
			setView(pos, view, dr);
	
		ImageView iv = (ImageView) view.findViewById(R.id.imgFolder);
		int imgId = dr.getImage();
		if( imgId < 0 )
			iv.setVisibility(View.INVISIBLE);
		else {
			iv.setVisibility(View.VISIBLE);
			iv.setImageResource(imgId);
		}
		
		int backId = (dr instanceof ScriptRow) ? R.drawable.list_grey_selector :
			(dr.getDocument() instanceof NotVisitedImpl) ? R.drawable.org_missing_back :
			pos % 2 != 0 ? R.drawable.list_selector : 
			R.drawable.even_row_selector;
		
		view.setBackgroundResource(backId);

		return view;
	}

	public void setView(int pos, View view, DocRow row) {
		Document<? extends DocDataObject> item = row.getDocument();
		DocDataObject doc = item.getData();

		String text = row.getText(view.getContext());
		((TextView) view.findViewById(R.id.tvName)).setText(text);
		
		String orgname = String.format("контрагент с кодом <%s>", doc.id);
		String address = "";
		
		final String ID_STR = "id";
		if(org.read(ID_STR, doc.id )){
			orgname = org.getData().name;
			address = org.getData().address;
		}
		
		org.close();
		
		((TextView) view.findViewById(R.id.tvOrgName)).setText(orgname);
		((TextView) view.findViewById(R.id.tvAddress)).setText(address);
		((TextView) view.findViewById(R.id.tvIdx)).setText(Integer.toString(pos + 1));
		
		TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
		TextView tvSum = (TextView) view.findViewById(R.id.tvSum);
		
		tvDate.setText("");
		tvSum.setText("");
		
		if( doc instanceof CreateDocDataObject) {
			tvDate.setText(sdf.format( ((CreateDocDataObject)doc).created));
			
			if (!hideDocSum(doc))
				tvSum.setText(Util.IntToScaleStr(item.sum(), Consts.SUM_SCALE));
		}
	}
	
	private boolean hideDocSum(DocDataObject doc) {
		return doc instanceof Remnants || doc instanceof VisitPreview;
	}
	
}
