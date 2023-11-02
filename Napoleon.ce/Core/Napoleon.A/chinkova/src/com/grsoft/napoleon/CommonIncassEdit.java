package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.CommonIncassImpl;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;
import com.grsoft.napoleon.dialogs.SelectDialog;
import com.grsoft.util.ExtrasConst;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CommonIncassEdit extends CommonIncassEditBase {
	private List<OrgEx> orgList = new ArrayList<OrgEx>();

	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, CommonIncassEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected CommonIncassImplBase<? extends CommonIncass> createDocument() { return new CommonIncassImpl(); }
	
	@Override
	protected String sendObjectName() { return "CommonIncass"; }


	@Override
	protected void initData() {
		super.initData();
		updateOrgList();
	}
	
	@Override
	protected void initUI() {
		super.initUI();
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DialogFragment df = new SelectOrgDlg();
				df.show(getSupportFragmentManager(), df.getClass().toString());
			}
		});
	}

	protected void updateOrgList() {
		DbReader reader = new DbReader();
		OrgEx data = new OrgEx();
		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), null);
		
		while (bdo) {
			orgList.add(data);
			data = new OrgEx();
			bdo = reader.selectNext(data);
		}
		
		Collections.sort(orgList, new Comparator<OrgEx>() {
			@Override
			public int compare(OrgEx lhs, OrgEx rhs) {
				int result = lhs.orgid.compareTo(rhs.orgid);
				
				if(result == 0)
					result = lhs.name.compareTo(rhs.name);
				
				return result; 
			}
		});

		List<OrgEx> dl = new ArrayList<OrgEx>();
		String orgid = null;
		
		for(OrgEx o :orgList){
			if(orgid != null && !orgid.equals(o.orgid))
				dl.add(new OrgDelimiter());
			
			orgid = o.orgid;
			dl.add(o);	
		}
		
		orgList = dl;
	}

	@SuppressLint("ValidFragment")
	class SelectOrgDlg extends SelectDialog {

		final boolean selitems[] = new boolean[orgList.size()];
		private ListView listView;

		@Override
		public void onOKButtonPressed(View result) {
			for (int i = 0; i < selitems.length; i++) {
				Org org = ((Org) listView.getItemAtPosition(i));
				CommonIncassItem item = doc.findItem(org.id);
				if (selitems[i]) {
					if (item == null) {
						CommonIncassItem cii = new CommonIncassItem();
						cii.id = org.id;
						doc.getData().items.add(cii);
					}
				} else
					doc.getData().items.remove(item);
			}
			((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
		}

		@Override
		public int getViewId() {
			return R.layout.select_org;
		}

		@Override
		public void prepareView(View view) {
			for (int i = 0; i < orgList.size(); i++) {
				Org org = orgList.get(i);
				selitems[i] = doc.findItem(org.id) != null;
			}

			listView = (ListView) view.findViewById(R.id.list);
			listView.setDividerHeight(0);
			listView.setAdapter(new OrgListAdapter());
		}

		@Override
		public int getTitle() {
			return R.string.select_orgs;
		}

		class OrgListAdapter extends BaseAdapter {
			@Override
			public int getCount() {
				return orgList.size();
			}

			@Override
			public Object getItem(int position) {
				return orgList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return getItem(position) instanceof OrgDelimiter ? DEL_ROW : DATA_ROW;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				long itemId = getItemId(position);
				int id = (itemId == DEL_ROW) ? R.layout.row_delimiter : R.layout.select_org_row;
				if( convertView == null || ((Integer)convertView.getTag()) != id ) {
					convertView = View.inflate(CommonIncassEdit.this, id, null);
					convertView.setTag(id);
				}
				
				if(getItemId(position) != DEL_ROW){
					Org org = (Org) getItem(position);
					TextView tv = (TextView) convertView.findViewById(R.id.tvName);
					tv.setText(org.name);
					tv = (TextView) convertView.findViewById(R.id.tvAddress);
					tv.setText(org.address);
					CheckBox cb = (CheckBox) convertView.findViewById(R.id.cbSelected);
					cb.setTag(position);
					
					cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
	
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							selitems[(Integer) buttonView.getTag()] = isChecked;
						}
					});
					
					cb.setChecked(selitems[position]);
					convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
				}
				
				return convertView;
			}
		}
	}
}
