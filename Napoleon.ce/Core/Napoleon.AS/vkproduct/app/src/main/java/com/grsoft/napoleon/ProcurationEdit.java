package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Procuration;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.ProcurationImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class ProcurationEdit extends FragmentActivity {
	private EditText edRoute;
	private TextView tvQty;
	private EditText edRemark;
	private EditText edFio;
	private EditText edOrg;
	private EditText edFirm;
	private ImageButton btnSend;
	private TextView tvDate;
	private DateHandler dateHandler;
	private ProcurationImpl procuration = new ProcurationImpl();
	private static final String ROUTE_PRC = "Маршрут"; 
	private static final String COMMENT = "Комментарий";
	private static final String FIO = "ФИО";
	
	private List<CharSequence> routes = new ArrayList<CharSequence>();
	private List<KeyValue> firms = new ArrayList<KeyValue>();
	private List<CharSequence> fio = new ArrayList<CharSequence>();
	private List<CharSequence> cmts = new ArrayList<CharSequence>();
	private List<Org> orgs = new ArrayList<Org>();
 	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, ProcurationEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.procuration_edit);
		inflate();
		record();
		init();
	}

	private void init() {
		Procuration prc = procuration.getData();
		edRoute.setText(prc.route);
		updateQtyText(prc);
		edRemark.setText(prc.remark);
		edFio.setText(prc.fio);
		edOrg.setText(prc.org);
		edFirm.setText(prc.firm);
		
		edRoute.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				RouteDlg dlg = new RouteDlg();
				dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
				return true;
			}
		});
		
		edOrg.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				SelOrgDlg dlg = new SelOrgDlg();
				dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
				return true;
			}
		});
		
		edRemark.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				RemarkDlg dlg = new RemarkDlg();
				dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
				return true;
			}
		});
		
		edFio.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				FioDlg dlg = new FioDlg();
				dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
				return true;
			}
		});
		
		edFirm.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				FirmDlg dlg = new FirmDlg();
				dlg.show(getSupportFragmentManager(), dlg.getClass().toString());
				return true;
			}
		});
		
		tvQty.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputNumberDlg.open(ProcurationEdit.this, new InputQty());
			}
		});
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkInputVal()){
					save();
					new DocumentSender(ProcurationEdit.this, btnSend, 
						DocType.getCurDoc().getObjectName(), procuration, 
						procuration.getRowid()){
						protected void onPostExecute(Boolean result) {
							super.onPostExecute(result);
							finish();
						};
					}.execute((Void[])null);
				}else 
					Toast.makeText(v.getContext(), R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();
			}
		});
		
		tvDate.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { showDialog(R.id.dialog_date_picker_id);	}});
		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), prc.date, R.id.dialog_date_picker_id);
		
		initOrgs();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case R.id.dialog_date_picker_id:
				return dateHandler.createDialog();
			default : return super.onCreateDialog(id);
		}
	}

	protected void initOrgs() {
		DataTraveler.travel(OrgEx.class, new DataTraveler.Travel<OrgEx>(){
			@Override
			public boolean travel(DataTraveler<OrgEx> item) {
				orgs.add(item.data);
				item.data = new OrgEx();
				return true;
			}}, null);
		
		Collections.sort(orgs, new Comparator<Org>() {
			@Override public int compare(Org lhs, Org rhs) {
				return lhs.name.compareTo(rhs.name);
			}});
	}

	protected void updateQtyText(Procuration prc) {
		tvQty.setText(getString(R.string.qty_val, Util.IntToScaleStr(prc.qty, Consts.QTY_SCALE)));
	}
	
	class InputQty extends InputNumber {
		@Override public boolean useComma() {	return !Features.INTEGER_INPUTS_QTY; }
		@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
		@Override public void applayInput(int value, Object... params) { 	
			procuration.getData().qty = value;
			updateQtyText(procuration.getData());
		}
		@Override public int getValue() {	return procuration.getData().qty; }
	}

	private void record() {
		procuration.read(getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR));
		procuration.close();
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		fillList(sb, cfg, ROUTE_PRC, routes, cs);
		fillList(sb, cfg, "Организация", firms, kv);
		fillList(sb, cfg, FIO, fio, cs);
		fillList(sb, cfg, COMMENT, cmts, cs);
	}
	
	private ListMaker cs = new ListMaker(){
		@SuppressWarnings("unchecked")
		@Override public void makeList(String val, List<?> out) { DialogHelper.makeList(val, (List<CharSequence>) out);}};
		
	private ListMaker kv = new ListMaker() {
		@SuppressWarnings("unchecked")
		@Override public void makeList(String val, List<?> out) { DialogHelper.makeListWithKey(val, (List<KeyValue>) out, null);}
	};	
	
	private void fillList(StringBuilder sb, ConfigImpl cfg,  String key, List<?> out, ListMaker st){
		sb.setLength(0);
		if(cfg.getValue(sb, key))
			st.makeList(cfg.getData().value, out);
	}
	
	private interface ListMaker{
		void makeList(String val, List<?> out);
	}

	private void inflate() {
		edRoute = (EditText) findViewById(R.id.edRoute);
		tvQty = (TextView) findViewById(R.id.tvQty);
		edRemark = (EditText) findViewById(R.id.edRemark);
		edFio = (EditText) findViewById(R.id.edFIO);
		edOrg = (EditText) findViewById(R.id.edOrg);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		edFirm = (EditText) findViewById(R.id.edFirm);
		tvDate = (TextView) findViewById(R.id.tvDate);
	}
	

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		if(checkInputVal())
			save();
		else
			procuration.delete();
		
		procuration.close();
		
		super.onBackPressed();
	}
	
	private boolean checkInputVal() {
//		boolean result = edRoute.getText().toString().length() > 0 ||
//				procuration.getData().qty > 0 ||
//				edRemark.getText().toString().length() > 0 ||
//				edFio.getText().toString().length() > 0 ||
//				edOrg.getText().length() > 0;
				
		return true;
	}

	private void save() {
		if(procuration.isExported())
			return;
		
		Procuration prc = procuration.getData();
		prc.route = edRoute.getText().toString().trim();
		prc.remark = edRemark.getText().toString().trim(); 
		prc.fio = edFio.getText().toString().trim();
		prc.org = edOrg.getText().toString().trim();
		prc.firm = edFirm.getText().toString().trim();
		prc.date = dateHandler.getDate();
		
		procuration.write();
	}
	
	abstract class SelectFromListDlg extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getTitleId());
			List<CharSequence> values = getTitles();
			CharSequence titles[] = new CharSequence[values.size()];
			titles = values.toArray(titles);
			
			builder.setItems(titles, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					applayVal(((AlertDialog)dialog).getListView().getItemAtPosition(which).toString());
				}});
			return builder.create();
		}
		
		protected abstract List<CharSequence> getTitles();
		protected abstract void applayVal(String val);
		protected abstract int getTitleId();
	}
	
	class SelOrgDlg extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.org);
			ListView lv = new ListView(getActivity());
			lv.setAdapter(createAdapter());
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					edOrg.setText(((Org)parent.getItemAtPosition(position)).name);
					dismiss();
				}});
			
			builder.setView(lv);
			return builder.create();
		}

		private ListAdapter createAdapter() {
			return new BaseAdapter() {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if(convertView == null)
						convertView = View.inflate(getActivity(), R.layout.select_org_row, null);
					
					Org org = (Org) getItem(position);
					TextView tv = (TextView) convertView.findViewById(R.id.tvName);
					tv.setText(org.name);
					tv = (TextView) convertView.findViewById(R.id.tvAddress);
					tv.setText(org.address);
					convertView.findViewById(R.id.cbSelected).setVisibility(View.GONE);
					
					return convertView;
				}
				
				@Override public long getItemId(int position) { return 0;	}
				@Override public Object getItem(int position) { return orgs.get(position);}
				@Override public int getCount() { return orgs.size();	}
			};
		}
	}
	
	class FirmDlg extends SelectFromListDlg{
		@Override protected List<CharSequence> getTitles() {
			List<CharSequence> result = new ArrayList<CharSequence>();
			
			for(KeyValue kv : firms)
				result.add(kv.value);
			
			Collections.sort(result, new Comparator<CharSequence>() {
				@Override
				public int compare(CharSequence lhs, CharSequence rhs) {
					return lhs.toString().compareTo(rhs.toString());
				}});
			
			return result;	
		}
		@Override protected void applayVal(String val) { edFirm.setText(val); }
		@Override protected int getTitleId() { return R.string.firm_dlg_title;	}		
	}
	
	class FioDlg extends SelectFromListDlg{
		@Override protected List<CharSequence> getTitles() { return fio;	}
		@Override protected void applayVal(String val) { edFio.setText(val); }
		@Override protected int getTitleId() { return R.string.fio_dlg_title;	}		
	}
	
	class RemarkDlg extends SelectFromListDlg{
		@Override protected List<CharSequence> getTitles() { return cmts;	}
		@Override protected void applayVal(String val) { edRemark.setText(val); }
		@Override protected int getTitleId() { return R.string.comment_dlg_title;	}		
	}
	
	class RouteDlg extends SelectFromListDlg{
		@Override protected void applayVal(String val) { edRoute.setText(val);}
		@Override protected List<CharSequence> getTitles() { return routes;	}
		@Override protected int getTitleId() { return R.string.route_dlg_title; }
	}
}