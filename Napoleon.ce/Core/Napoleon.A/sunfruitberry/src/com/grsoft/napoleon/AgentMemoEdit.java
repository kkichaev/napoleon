package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.grsoft.dataobjects.AgentMemo;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AgentMemoEdit extends BaseActivity implements SendResultListener {
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	AgentMemoImpl doc;
	String ido = "";
	int selDog;
	Map<String, DogovorBalanceData> dogData = new HashMap<String, DogovorBalanceData>();
	
	public static void open(Context context, AgentMemoImpl doc) {
		Intent i = new Intent(context, AgentMemoEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.agent_memo_edit);
		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		doc = new AgentMemoImpl();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rid);
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();
		ido = oe.ido;

		String ret = oe.name;
		if(Features.SHOW_ORG_ADDRESS && oe.address.length() > 0 ) {
			ret += "<br><i>" + oe.address + "</i>";
		}	
		TextView tv = (TextView) findViewById(R.id.tvOrgName); 
		tv.setText(Html.fromHtml(ret));
		
		final AgentMemo am = doc.getData();
		EditText ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(am.remark);
		
		if(doc.isEditable()) {
			am.orgColor = BalanceHelper.getOrgColor(oe.ido);
		}
		
		ed = (EditText)findViewById(R.id.edEmail);
		ed.setText(am.email.length() == 0 ? oe.email : am.email);
		
		tv.setTextColor(am.orgColor);
		
		ConfigImpl ci = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spTopic);
		
		List<KeyValue> topics = new ArrayList<KeyValue>();
		Config c = ci.getData();
		c.key = "ТемыСлужебныхЗаписок";
		ci.read();
		int selected = DialogHelper.makeListWithKey(c.value, topics, am.topic);
		if(am.sendInvoice())
			sp.setEnabled(false);
		else {
			for(KeyValue kv : topics) {
				if(kv.key.equals(AgentMemo.SEND_INVOICE_ID)) {
					topics.remove(kv);
					break;
				}
			}
		}
		
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, topics);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( selected >= 0 )
			sp.setSelection(selected);
		ci.close();
		
		
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { checkTopic(); }
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
				
		findViewById(R.id.tvUnblockTill).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(AgentMemoEdit.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, am.till.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { UpdateDBEx.makeDebtSync(AgentMemoEdit.this);}
		});
		
		findViewById(R.id.tvOrderInfo).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				OrderImpl ord = new OrderImpl();
				if(ord.read("created", doc.getData().till)) {
					ord.open(arg0.getContext());
				}
			}
		});
		
		
		findViewById(R.id.tvSum).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				InputNumberDlg.open(AgentMemoEdit.this, new InputNumber() {
					
					@Override public int getValue() { return (int)doc.getData().sum; }
					
					@Override
					public void applayInput(int value, Object... params) {
						if(doc.isEditable()) {
							doc.getData().sum = value;
							refreshSum();
						}
						
					}
				}, Consts.SUM_SCALE, false, "Введите сумму");
			}
		});
		
		refreshDate();
		refreshSum();

		View v;
		v = findViewById(R.id.btnOK);
		v.setEnabled(doc.isEditable());
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				save();
				if(doc.getData().isValid())
					finish();
			}
		});
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				if(doc.isEditable() && (doc.getData().isEmpty() || doc.getData().sendInvoice()) ) 
					doc.delete();
				finish(); 
			}
		});

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	void checkTopic() {
		boolean disable = false;
		KeyValue kv = (KeyValue) ((Spinner)findViewById(R.id.spTopic)).getSelectedItem();
		int visEmail = View.GONE, visUnlock = View.VISIBLE;
		int visLinkOrder = View.GONE;
		if(kv == null || kv.key.length() == 0) {
			visUnlock = View.INVISIBLE;			
		} else if(kv.key.toString().equals(AgentMemo.UNLOCK_TOPIC_ID)) {
			Date dt = SyncInfo.getLastSync(SyncInfo.DEBT);
			disable = (dt != null && ((new Date()).getTime() - dt.getTime()) > 24 * 3600 * 1000);
		} else {
			if(kv.key.toString().equals(AgentMemo.SEND_INVOICE_ID)) {
				visLinkOrder = View.VISIBLE;
				
				OrderImpl ord = new OrderImpl();
				String text = "";
				if(ord.read("created", doc.getData().till)) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
					text = "<font color='blue'><u>Заявка от " + sdf.format(ord.getData().created) + " сумма: " + 
							Util.IntToScaleStr(ord.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.";
					text += "</u></font>";
				}
				((TextView)findViewById(R.id.tvOrderInfo)).setText(Html.fromHtml(text));
			}
			visEmail = View.VISIBLE;
			visUnlock = View.GONE;
		}
		
		findViewById(R.id.llEmail).setVisibility(visEmail);
		findViewById(R.id.llUnlock).setVisibility(visUnlock);
		findViewById(R.id.llOrder).setVisibility(visLinkOrder);
		
		findViewById(R.id.llSync).setVisibility(disable ? View.VISIBLE : View.GONE);
		findViewById(R.id.btnSend).setEnabled(!disable);
		findViewById(R.id.btnOK).setEnabled(!disable);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AgentMemo am = doc.getData();
		KeyValue sel = (KeyValue) ((Spinner)findViewById(R.id.spDogovor)).getSelectedItem();
		if(sel != null)
			am.idDog = sel.key.toString();

		loadDogovors(ido, am.id, am.idDog);
		checkTopic();
	}
	
	private void refreshSum() {
		String text = "<u>" + Util.IntToScaleStr(doc.getData().sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</u>";
		((TextView)findViewById(R.id.tvSum)).setText(Html.fromHtml(text));		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = Util.getDate();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			if(ct < curDate.getTime())
				ct = curDate.getTime();
			Date newDate = new Date(ct);
			
			doc.getData().till = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		String text = "<u>" + sd.format(doc.getData().till) + "</u>";
		((TextView)findViewById(R.id.tvUnblockTill)).setText(Html.fromHtml(text));		
	}

	void loadDogovors(String ido, String id, final String selected) {
		final Date dueDate = Util.getDayEnd(Util.getDate());
		dogData.clear();
		DataTraveler.travel(OrgBalanceData.class, new DataTraveler.Travel<OrgBalanceData>() {

			@Override
			public boolean travel(DataTraveler<OrgBalanceData> item) {
				DogovorBalanceData data = dogData.get(item.data.idDog);
				if(data == null) {
					data = new DogovorBalanceData();
					dogData.put(item.data.idDog, data);
				}
				data.add(item.data, dueDate);
				return true;
			}
		}, "id in (select id from Org where ido='" + ido + "') or ido='" + ido +"'");
		
		Spinner sp = (Spinner)findViewById(R.id.spDogovor);
		final List<KeyValue> values = new ArrayList<KeyValue>();
		
		values.add(new KeyValue("", ""));
		selDog = -1;
		DataTraveler.travel(OrgBalance.class, new DataTraveler.Travel<OrgBalance>() {

			@Override
			public boolean travel(DataTraveler<OrgBalance> item) {
				if(item.data.idDog.equals(selected))
					selDog = values.size();
				
				DogovorBalanceData data = dogData.get(item.data.idDog);
				String value = item.data.name + " " + Integer.toString(item.data.dueDays) + "к/д/ " + 
						Util.IntToScaleStr(item.data.balance, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				if(data != null) {
					value += "\nДолг: " + Util.IntToScaleStr(data.sumD, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " +
							Util.IntToScaleStr(data.overdueSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " / " +
							Integer.toString(data.overdue);
				}
				values.add(new KeyValue(item.data.idDog, value));
				return true;
			}
		}, "id='" + ido + "'");
		
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( selDog >= 0)
			sp.setSelection(selDog);
	}
	
	protected void send() {
		save();
		
		if(!doc.getData().isValid()) {
			Toast.makeText(this, "Не заполнены обязательные поля", Toast.LENGTH_SHORT).show();
		} else {
			List<DocExportListener> tosend = new ArrayList<DocExportListener>();
			tosend.add(new DocSendListner(AgentMemoDoc.instance().getObjectName(), doc));
			
			if(doc.getData().sendInvoice()) {
				OrderImpl oi = (OrderImpl) OrderDoc.instance().create();
				Order o = oi.getData();
				o.created = doc.getData().till;
				if(oi.read() && !oi.isExported())
					tosend.add(new DocSendListner(OrderDoc.instance().getObjectName(), oi));
				oi.close();
			}
			DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), tosend, this);
			ds.execute((Void[])null);
		}
	}
	
	@Override
	protected void onStop() {
		doc.close();
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.getData().isEmpty()) 
			doc.delete();
		super.onBackPressed();
	}
	
	protected void save() {
		if(!doc.isEditable())
			return;
		
		AgentMemo am = doc.getData();
		EditText ed = (EditText)findViewById(R.id.edRemark);
		am.remark = ed.getText().toString();
		
		ed = (EditText)findViewById(R.id.edEmail);
		am.email = ed.getText().toString();
		
		Spinner sp = (Spinner)findViewById(R.id.spTopic);
		KeyValue sel = (KeyValue) sp.getSelectedItem(); 
		if(sel != null) {
			am.topic = sel.key.toString();
			am.topicName = sel.value.toString();
		}
		
		sp = (Spinner)findViewById(R.id.spDogovor);
		if(sp.getSelectedItem() != null) {
			am.idDog = ((KeyValue)sp.getSelectedItem()).key.toString();
			DogovorBalanceData dd = dogData.get(am.idDog);
			if(dd != null) {
				am.sumD = dd.sumD;
				am.overdue = dd.overdue;
				am.overdueSum = dd.overdueSum;
				am.dogColor = BalanceHelper.getColorFromDueDays(dd.overdue);
			} else {
				am.sumD = 0;
				am.overdue = 0;
				am.overdueSum = 0;
				am.dogColor = 0;
			}
		}
		if(!am.isValid()) {
			Toast.makeText(this, "Не заполнены обязательные поля", Toast.LENGTH_SHORT).show();
		} else {
			doc.write();
			AgentMemoDoc.instance().refreshDocSum(doc.getId());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			doc.read(doc.getRowid(), false);
			findViewById(R.id.btnOK).setEnabled(doc.isEditable());
		}
	}
}

class DogovorBalanceData {
	public long sumD = 0;
	public long overdueSum = 0;
	public int overdue = 0;
	
	public DogovorBalanceData() {
	}
	
	public void add(OrgBalanceData data, Date dueDate) {
		sumD += data.sumD;
		if(data.payDate.compareTo(dueDate) < 0) {
			overdueSum += data.sumD;
			int cd = (int)((dueDate.getTime() - data.payDate.getTime()) / (1000 * 3600 * 24));
			if(cd > overdue)
				overdue = cd;
		}
	}
}
