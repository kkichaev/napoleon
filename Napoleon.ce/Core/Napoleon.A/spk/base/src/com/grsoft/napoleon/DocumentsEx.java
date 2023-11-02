package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderImplEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderToDelImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	private static final int IT_CH_ORG = 1024;
	private static final int CH_ORG_DLG = 1025;
	OrgListAdapter orgListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		try{
			orgListAdapter = new OrgListAdapter(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(orgListAdapter != null)
			orgListAdapter.close();
	}
	
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		String info = ((OrgEx)o).info;
		if(info != null && info.length() > 0)
			ret += "<br/>" + info; 
		return ret;
	}
	
	@Override
	protected void docDelete(CreatableDocument<?> doc) {
		if (doc instanceof OrderImplEx && doc.isExported()){
			SQLiteDatabase database = DataBaseManager.getDataBase(); 
			database.beginTransaction();
			
			try{
				((OrderImplEx)doc).setMarkToDel();
				doc.write();
				doc.close();
				
				OrderToDelImpl orderToDelImpl = new OrderToDelImpl();
				orderToDelImpl.getData().created = ((Order)doc.getData()).created;
				orderToDelImpl.getData().date = Util.getDateTime();
				orderToDelImpl.write();
				orderToDelImpl.close();
				
				database.setTransactionSuccessful();
			}finally{
				database.endTransaction();
			}
			refreshContent();
		} else
			super.docDelete(doc);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if( DocType.getCurDoc().isCreatable() && menu != null){
			
			CreatableDocument<?> doc = (CreatableDocument<?>) adapter
					.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
			
			if (((doc instanceof OrderImplEx) && !(((OrderImplEx)doc).isMarkToDel() || 
					((OrderImplEx)doc).isDeleted())) || !(doc instanceof OrderImplEx))
				menu.add(0, IT_CH_ORG, 0, "Сменить организацию");
		}
			}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == IT_CH_ORG){
			AdapterView.AdapterContextMenuInfo menuInfo = 
					(AdapterContextMenuInfo) item.getMenuInfo();
				
			selectedDoc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
			showDialog(CH_ORG_DLG);
			return true;
		}else
			return super.onContextItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case CH_ORG_DLG:
			return createChOrgDlg();
		default:
			return super.onCreateDialog(id);
		}
		
	}

	private Dialog createChOrgDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ListView list = (ListView)View.inflate(this, R.layout.orglist, null);
		builder.setView(list);
		list.setAdapter(orgListAdapter);
		final Dialog result = builder.create();
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (orgListAdapter != null){
					OrgImpl orgImpl = (OrgImpl) orgListAdapter.getItem(position);
					String oldId = selectedDoc.getData().id;
					String newId = orgImpl.getData().id;
					
					if (!oldId.equals(newId)){
						selectedDoc.getData().id = newId;
						selectedDoc.write();
						selectedDoc.close();
						DocType dt = DocType.getCurDoc();
						dt.refreshDocSum(oldId);
						dt.refreshDocSum(newId);
						finish();
						open(view.getContext(), orgImpl.getData());
						result.dismiss();
					}
				}
			}
		});
		
		return result;
	}
	
	private CreatableDocument<?> selectedDoc; 
	
	class OrgListAdapter extends DataBaseAdapter<Org>
	{
		OrgSumImpl os = new OrgSumImpl();
		public OrgListAdapter(Context context) 
			throws IllegalAccessException, InstantiationException 
		{
			super(context, new OrgImpl(), "", "name");
		}
		
		@Override
		public void close() {
			os.close();
			super.close();
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			OrgImpl orgImpl = (OrgImpl)cursor.get(arg0);
			
			View view = arg1; 
			
			if (view == null)
				view = View.inflate(context, R.layout.main_list_row, null);
			
			setBackground(arg0, orgImpl, view);

			view.setTag(orgImpl.getRowid());

			TextView tvOrgName = (TextView)view.findViewById(R.id.tvOrgName);
			tvOrgName.setLines(2);
			
			tvOrgName.setEllipsize(TruncateAt.END);
			tvOrgName.setHorizontallyScrolling(true);
			
			TextView tvOrgSum = (TextView)view.findViewById(R.id.tvOrgSum);
			tvOrgSum.setVisibility(View.GONE);
			
			Org org = orgImpl.getData();
			
			StringBuilder sb = new StringBuilder("<b>");
			sb.append( org.name).append("</b><br>").append(org.address);
			tvOrgName.setText(Html.fromHtml(sb.toString()));
			
			ImageView ivFolder = (ImageView) view.findViewById(R.id.ivFolder);
			ivFolder.setVisibility(View.GONE);
			
			return view;
		}

		protected void setBackground(int arg0, OrgImpl orgImpl, View view) {
			view.setBackgroundResource(orgImpl.getData().isStopList() ? 
					R.drawable.list_grey_selector :
					arg0 % 2 != 0 ? R.drawable.even_row_selector:  
									R.drawable.list_selector);
		}
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				if(ScriptImplEx.getPlanItem(org.getData().id) != null && data.size() == 0){
					DocType sd = ScriptDoc.instance();
					if(creatableFilter)
						data.add(sd);
					else {
						for( DocTypeBase dt : DocType.docTypes )
							if( !dt.isCreatable() || dt == sd )
								data.add((DocType) dt);
						for( DocTypeBase dt : ScriptDefImpl.docInScript ) {
							if( data.contains(dt) == false )
								data.add(dt);
						}
					}
				}else
					super.initData(creatableFilter);
			}
		};
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		if (ScriptImplEx.getPlanItem(org.getData().id) != null && 
				docType.isCreatable() && docType != ScriptDoc.instance())
			adjustViewForDocType(ScriptDoc.instance());
	}
}
