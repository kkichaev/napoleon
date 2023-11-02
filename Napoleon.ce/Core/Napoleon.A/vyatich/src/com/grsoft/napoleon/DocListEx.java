package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class DocListEx extends DocList {
	
	String selectedOrgId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(DocType.getCurDoc() == OrderDoc.instance()) {
			findViewById(R.id.tvDocSum).setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					selectedOrgId = "";
					showDialog(R.id.order_info_by_top_folders);
					return true;
				}
			});
			
			lvDocs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Document<?> doc = (Document<?>) adapter.getItem(arg2);
					selectedOrgId = doc.getId();
					showDialog(R.id.order_info_by_top_folders);
					return true;
				}
			});
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if(id == R.id.order_info_by_top_folders) {
			return createInfoDialog();
		}
		return super.onCreateDialog(id, args);
	}
	
	private Dialog createInfoDialog() {
		return OrderInfoAdapter.createInfoDialog(this);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.order_info_by_top_folders){
			List<Order> docs = new ArrayList<Order>();
			
			for( int i = 0; i < adapter.getCount(); i++) {
				Document<?> doc = (Document<?>) adapter.getItem(i);
				if(doc instanceof OrderImpl) {
					if(selectedOrgId.length() == 0|| selectedOrgId.equals(doc.getId()))
						docs.add((Order)doc.getData().clone());
				}
			}
			
			OrderInfoAdapter.prepareDialog(this, docs);
		}
		super.onPrepareDialog(id, dialog);
	}
	
	
	private OnCheckedChangeListener onOrgTypeChecked() {
		return new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String text = "";
				
				switch(checkedId){
				case R.id.rbOrg:
					text = getString(R.string.Contragent);
					break;
				case R.id.rbTT:
					text = getString(R.string.TorgTchk);
					break;
				default:
					text = "";
				}
				
				TextView tv = (TextView) ((View)group.getParent()).findViewById(R.id.tvContractor);
				tv.setText(text);
			}
		};
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) { 
		return new DocListAdapterEx(this, docType, saveDatePeriod, R.layout.docs_list_row2);
	}
	
	@Override
	protected int getDocStatusResource(CreatableDocument<?> doc) {
		if( doc instanceof OrderImplEx ) {
			Order o = (Order) doc.getData();
			if( o.podRemark != null && o.podRemark.contains("подтверждение2") && doc.isProceeded() == false )
				return R.drawable.ord_handled;
		}
		return super.getDocStatusResource(doc);
	}
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if(DocType.getCurDoc() == OrderDoc.instance()) {
			
			int sum = 0, dal = 0;
			TextView tv = (TextView)findViewById(R.id.tvDocSum);
			if( tv != null ) {
				for( int i=0; i<adapter.getCount(); i++ ) {
					OrderImplEx d = (OrderImplEx) adapter.getItem(i);
					sum += d.sum();
					dal += d.weight();
				}
				String text = Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				text += "<br><i>" + Util.IntToScaleStr(dal, 10, Util.DEC_DELIM, false) +  " дал</i>";
				tv.setText(Html.fromHtml(text));
			}
		} else
			super.refreshTotalSum(useFilter);
	}
	
	@Override
	protected int getFilterLayout() { return R.layout.date_selectionex;	}
	
	@Override
	protected void postUpdateFilterView(View view) {
		RadioGroup rgOrgType = (RadioGroup) view.findViewById(R.id.rgOrgType);
		rgOrgType.setOnCheckedChangeListener(onOrgTypeChecked());
	}
	
	class DocListAdapterEx extends DocListAdapter{
		protected DocListAdapterEx(Context context, DocType docType, DatePeriod filter, int layoutid) {
			super(context, docType, filter, layoutid);
		}

		@Override
		public void fetchByPeriod(DocType docType, DatePeriod dp, String orgId,
				Price item, HashMap<Long, Integer> values) {
			super.fetchByPeriod(docType, dp, null, item, values);
			
			if(dialogView != null){
				RadioGroup rb = (RadioGroup)dialogView.findViewById(R.id.rgOrgType);
				Spinner sp = (Spinner) dialogView.findViewById(R.id.spOrg);
				
				if(rb != null && sp != null && sp.getSelectedItemPosition() > 0)
				{
					KeyValue kv = (KeyValue) sp.getSelectedItem();
					
					if(kv != null){
						OrgImpl org = new OrgImpl();
						if (org.read("id", kv.key)){
							int ch = rb.getCheckedRadioButtonId();
							String ref = org.getData().id;
							
							if(ch == R.id.rbOrg)
								ref = ((OrgEx)org.getData()).ido;
								
							
							filterBy(org, ch, ref);
						}
					}
				}
			}
		}
		
		public void filterBy(OrgImpl org, int orgtype, String ref ){
			
			List<Long> rmid = new ArrayList<Long>();
			for (Document<?> d : documents) {
				if (orgtype == R.id.rbTT){
					if (!d.getId().equals(ref))
						rmid.add(d.getRowid());
				}else if (org.read("id", d.getId()) && !((OrgEx)org.getData()).ido.equals(ref))
					rmid.add(d.getRowid());
			}
			
			if(rmid.size() > 0)
				documents.removeDocuments(rmid);
		}
	}

}
