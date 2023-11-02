package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DocUserStatus;
import com.grsoft.dataobjects.impl.DocUserStatusImpl;
import com.grsoft.view.BaseActivity;


@SuppressLint("UseSparseArrays")
public class StatusEdit extends BaseActivity {
	private ListAdapter picsAdapter;
	private String curdocstatus = "";
	private int curresource;
	private BaseAdapter adapter;
	
	/*id ресурса - список стаутсов*/
	private List<Pair<Integer, String>> data = new ArrayList<Pair<Integer, String>>();
	
	public static void open(Context context) {
		Intent intent = new Intent(context, StatusEdit.class);
		context.startActivity(intent);
	}

	private int selected = -1;
	private OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String d = (String) parent.getItemAtPosition(position);
			if(d != null){
				curdocstatus = d;
				curresource = DocStatus.getImage(d);
				showDialog(R.id.status_dlg);
			}
		}};
		
	private OnItemClickListener onSelectItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selected = position;
			((BaseAdapter)picsAdapter).notifyDataSetChanged();
		}};

	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		adapter = new MyAdapter();
		setContentView(prepareContentView());
		picsAdapter = createPicsAdapter();
		
	}


	protected View prepareContentView() {
		ListView list = new ListView(this);
		list.setBackgroundColor(getResources().getColor(R.color.white));
		list.setAdapter(adapter);
		list.setDividerHeight(0);
		list.setOnItemClickListener(onItemClick );
		return list;
	}
	
	class MyAdapter extends BaseAdapter{
		List<String> data = new ArrayList<String>();
		
		public MyAdapter() {
			DocStatus.collect(data);
		}
		
		@Override
		public int getCount() { return data.size(); }

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(StatusEdit.this, R.layout.statusrow, null);
			
			String item = (String) getItem(position);
			
			if(view != null && item != null){
				TextView v = (TextView)view;
				v.setCompoundDrawablesWithIntrinsicBounds(0, 0, DocStatus.getImage(item), 0);
				v.setText(item);
				
				view.setBackgroundResource((position % 2) != 0 ? 
						R.drawable.even_row_selector : R.drawable.list_selector);
			}
			
			return view;
		}
		
	}
	
	
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.status_dlg)
			return createStatusDlg();
		else 
			return super.onCreateDialog(id);
	
	}
	
	private Dialog createStatusDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.selectpic, null);
		
		if(view != null){
			builder.setView(view);
			
			ListView list = (ListView) view.findViewById(R.id.list);
			if(list != null){
				list.setAdapter(picsAdapter);
				list.setDividerHeight(0);
				list.setOnItemClickListener(onSelectItemClick);
			}
			
			View v = view.findViewById(R.id.btnOK);
			if(v != null)
				v.setOnClickListener(okClick());
			
			v = view.findViewById(R.id.btnCancel);
			if(v != null)
				v.setOnClickListener(cancelClick());
		}
		
		return builder.create();
	}
	
	private ListAdapter createPicsAdapter() {
		return new BaseAdapter() {
			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				if (view == null)
					view = View.inflate(StatusEdit.this, R.layout.selectpicrow, null);
				@SuppressWarnings("unchecked") 
				Pair<Integer, String> item = (Pair<Integer, String>) getItem(pos);
				
				if(view != null && item != null){
					CheckedTextView v = (CheckedTextView)view;
					v.setCompoundDrawablesWithIntrinsicBounds(item.first, 0, 0, 0);
					v.setText(item.second);
					
					view.setBackgroundResource((pos % 2) != 0 ? 
							R.drawable.even_row_selector : R.drawable.list_selector);
					
					v.setChecked(selected == pos);
				}
				
				return view;
			}
			
			@Override
			public Object getItem(int pos) { return data.get(pos); }
			
			@Override
			public int getCount() { return data.size(); }

			@Override
			public long getItemId(int position) { return 0;	}
		};
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.status_dlg)
			prepareStatusDlg(dialog);
		super.onPrepareDialog(id, dialog);
	}

	private void prepareStatusDlg(Dialog dialog) {
		if(dialog != null){
			updateData();
			
			TextView tv = (TextView) dialog.findViewById(R.id.tvTitle);
			if(tv != null)
				tv.setText(Html.fromHtml(getString(R.string.select_pic_for_status, curdocstatus)));
			
			ListView list = (ListView) dialog.findViewById(R.id.list);
			if(list != null){
				Adapter a = list.getAdapter();
				if (a != null)
					for(int i = 0; i < a.getCount(); i++){
						@SuppressWarnings("unchecked")
						Pair<Integer, String> item = (Pair<Integer, String>) a.getItem(i);
						if(item != null && curresource == item.first)
							selected = i;
					}
				
				list.setSelection(selected);
			}
		}
	}
	
	protected void updateData() {
		data.clear();
		/*имя картинки - список статусов */
		final Map<String, List<String>> status = new HashMap<String, List<String>>();
		loadUserStatus(status);
		loadMyStatus(status);
		fillData(status);
	}
	
	protected void fillData(final Map<String, List<String>> status) {
		/*id картинки - список статусов*/
		Map<Integer, Pair<Integer, String>> datamap = createDataMap(status);
		if(datamap != null){
			data.addAll(datamap.values());
			Collections.sort(data, new Comparator<Pair<Integer, String>>() {
				@Override
				public int compare(Pair<Integer, String> lhs, Pair<Integer, String> rhs) {
					return lhs.second.length() - rhs.second.length();
				}});
		}
	}

	private Map<Integer, Pair<Integer, String>> createDataMap(final Map<String, List<String>> status) {
		/*id картинки - список статусов*/
		Map<Integer, Pair<Integer, String>> result = new HashMap<Integer, Pair<Integer,String>>();;
		/*имя картинки - id */
		Map<String, Integer> res = DocStatus.getResources();
		
		if(res != null){
			collectDefIcons(status, result, res);
			collectUndefIcons(result, res);
		}
		
		return result;
	}

	protected void collectUndefIcons(Map<Integer, Pair<Integer, String>> datamap, Map<String, Integer> res) {
		for(int r : res.values())
			if(!datamap.containsKey(r))
				datamap.put(r, new Pair<Integer, String>(r, ""));
	}

	protected void collectDefIcons(final Map<String, List<String>> status, Map<Integer, Pair<Integer, String>> result, Map<String, Integer> res) {
		for(String key : status.keySet()){
			if(res.containsKey(key)){
				String text = collectStatuses(status, key);
				int id = res.get(key);
				result.put(id, new Pair<Integer, String>(id, text));
			}
		}
	}

	protected String collectStatuses(final Map<String, List<String>> status, String key) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : status.get(key)){
			if (s.trim().length() == 0)
				continue;
			
			if(sb.length() > 0 )
				sb.append(", ");
			
			sb.append(s);
		}
		
		return sb.toString();
	}

	protected void loadMyStatus(final Map<String, List<String>> status) {
		/*статус - имя картинки*/
		Set<Entry<String, String>> mystatus = DocStatus.getMyStatuses();
		
		List<String> alluserstatus = new ArrayList<String>();

		for(String s : status.keySet()){
			List<String> ss = status.get(s);

			if(ss != null)
				for(String sss : ss)
					if(!alluserstatus.contains(sss))
						alluserstatus.add(sss);
		}
		
		if(mystatus != null){
			Iterator<Entry<String, String>> statusiter = mystatus.iterator();
			while(statusiter.hasNext()){
				/*статус - имя картинки*/
				Entry<String, String> s = statusiter.next();
				
				if(status.containsKey(s.getValue()))
					continue;
				
				if(!status.containsKey(s.getValue()))
					status.put(s.getValue(), new ArrayList<String>());
				
				if(!alluserstatus.contains(s.getKey())){
					List<String> stlist = status.get(s.getValue());
					stlist.add(s.getKey());
				}
			}
		}
	}

	protected void loadUserStatus(final Map<String, List<String>> status) {
		DbWriter.checkDBTable(DocUserStatus.class);
		DataTraveler.travel(DocUserStatus.class, new DataTraveler.Travel<DocUserStatus>() {

			@Override
			public boolean travel(DataTraveler<DocUserStatus> item) {
				if(!status.containsKey(item.data.pic))
					status.put(item.data.pic, new ArrayList<String>());
				
				List<String> stlist = status.get(item.data.pic);
				stlist.add(item.data.name);
					
				return true;
			}}, null);
	}
	
	private OnClickListener cancelClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {dismissDialog(R.id.status_dlg);}
		};
	}

	private OnClickListener okClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selected >= 0 && selected < data.size()){
					Pair<Integer, String> item = data.get(selected);
					DocUserStatusImpl stimpl = new DocUserStatusImpl();
					DocUserStatus st = stimpl.getData();
					st.name = curdocstatus;
					st.pic = DocStatus.getResName(item.first);
					stimpl.write();
					stimpl.close();
					
					if(adapter != null)
						adapter.notifyDataSetChanged();
					
					dismissDialog(R.id.status_dlg);
				}
			}
		};
	};

}
