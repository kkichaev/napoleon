package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class OrgSelectDialog {
	
	public interface OrgSelect {
		void selected(Org o);
	}
	
	public static Dialog create(Context context, final OrgSelect selector) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View v = View.inflate(context, R.layout.org_list_dlg, null);
		
		ListView list = (ListView) v.findViewById(R.id.lvItems);
		final EditText find = (EditText) v.findViewById(R.id.edFind);
		ImageButton ib = (ImageButton)v.findViewById(R.id.btnClearFind);
		ib.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { find.setText("");}
		});
		
		builder.setView(v);
		OrgListAdapter adapter = new OrgListAdapter(context);
		list.setAdapter(adapter);
		final Dialog result = builder.create();
		
		FindTextWatcher fw = new FindTextWatcher(find, list);
		find.addTextChangedListener(fw);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Org org = (Org)parent.getAdapter().getItem(position);
				if(selector != null)
					selector.selected(org);
				result.dismiss();
			}
		});
		
		return result;		
	}
}

class OrgListAdapter extends BaseAdapter implements FilterAdapter{
	private List<Org> data;
	List<Org> allData = new ArrayList<Org>();
	private Context context;
	
	public OrgListAdapter(Context context) {
		this.context = context;
		String where = "(flags & " + Integer.toString(Org.FL_USER_CREATED) + ") = 0"; 
		final Class<? extends DataObject> orgtype = DbObject.getDataType(Org.class);
		DataTraveler.travel(orgtype, new DataTraveler.Travel<Org>() {

			@Override
			public boolean travel(DataTraveler<Org> item) {
				allData.add(item.data);
				try{
					item.data = (Org) orgtype.newInstance();
				}catch(Exception e){ e.printStackTrace(); }
				return true;
			}}, where, "name");
		
		data = allData;
	}
	
	@Override
	public int getCount() {	return data.size();	}

	@Override
	public Object getItem(int position) { return data.get(position); }

	@Override
	public long getItemId(int position) { return 0;	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.orglist_row, null);
		
		Org org = (Org)getItem(position);
		setBackground(position, org, view);
		TextView tvOrgName = (TextView)view.findViewById(R.id.text);
		StringBuilder sb = new StringBuilder("<b>");
		sb.append(org.name).append("</b><br>").append(org.address);
		tvOrgName.setText(Html.fromHtml(sb.toString()));
		return view;
	}
	
	protected void setBackground(int position, Org org, View view) {
		view.setBackgroundResource(org.isStopList() ? 
				R.drawable.list_grey_selector : position % 2 != 0 ? R.drawable.even_row_selector: R.drawable.list_selector);
	}

	@Override
	public void applyFilter(String value) {

		value = value.toUpperCase();
		
		data = new ArrayList<Org>();
		for(Org o : allData) {
			if( o.srchName.contains(value))
				data.add(o);
		}
		
		notifyDataSetChanged();
	}

	@Override
	public void resetFilter() { data = allData; notifyDataSetChanged(); }
}