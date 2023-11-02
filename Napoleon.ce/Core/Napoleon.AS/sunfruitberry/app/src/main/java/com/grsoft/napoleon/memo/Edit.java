package com.grsoft.napoleon.memo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentMemo;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.MemoType;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.AgentMemoImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.BalanceHelper;
import com.grsoft.napoleon.CalendarActivity;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateDBEx;
import com.grsoft.napoleon.documents.AgentMemoDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

@SuppressLint("SimpleDateFormat")
public class Edit extends AppCompatActivity implements SendResultListener {
	AgentMemoImpl doc;
//	String ido = "";
//	int selDog;
//	Map<String, DogovorBalanceData> dogData = new HashMap<String, DogovorBalanceData>();

	Model model;
	BaseFragment currentFragment = null;
	
	public static void open(Context context, AgentMemoImpl doc) {
		Intent i = new Intent(context, Edit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.agent_memo_edit);
		model = new ViewModelProvider(this).get(Model.class);
		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		doc = new AgentMemoImpl();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rid);

		model.memoTypes = DbReader.fetchDic(MemoType.class, "id");
		
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();

		model.doc = doc.getData();
		model.org = oe;

		String ret = oe.name;
		if(Features.SHOW_ORG_ADDRESS && oe.address.length() > 0 ) {
			ret += "<br><i>" + oe.address + "</i>";
		}	
		TextView tv = (TextView) findViewById(R.id.tvOrgName); 
		tv.setText(Html.fromHtml(ret));

		if(doc.isEditable()) {
			model.doc.orgColor = BalanceHelper.getOrgColor(oe.ido);
		}
		tv.setTextColor(model.doc.orgColor);

		loadMemoTypeSpinner();
		
		View v;
		v = findViewById(R.id.btnOK);
		v.setEnabled(doc.isEditable());
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				save();
				if(model.isValid())
					finish();
			}
		});
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { 
				if(doc.isEditable() && (model.isEmpty() || model.sendInvoice()) ) {
					doc.delete();
				}
				finish(); 
			}
		});

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});

		model.getDisabled().observe(this, disabled -> {
			findViewById(R.id.btnSend).setEnabled(!disabled);
			findViewById(R.id.btnOK).setEnabled(!disabled);
		});

		loadMemoDetails();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	private void loadMemoTypeSpinner() {
		ConfigImpl ci = new ConfigImpl();
		Spinner sp = (Spinner)findViewById(R.id.spTopic);

		List<KeyValue> topics = new ArrayList<KeyValue>();
		Config c = ci.getData();
		c.key = "ТемыСлужебныхЗаписок";
		ci.read();
		int selected = DialogHelper.makeListWithKey(c.value, topics, model.doc.topic);
		MemoType msel = model.getType(model.doc.topic);
		if(msel == null) {
			msel = new MemoType();
		}
		if(msel.sendingInvoice()) {
			sp.setEnabled(false);
		} else {
			for(KeyValue kv : topics) {
				msel = model.getType(kv.key.toString());
				if(msel != null && msel.sendingInvoice()) {
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
			@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) { loadMemoDetails(); }
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	void loadMemoDetails() {
		BaseFragment cf = null;

		KeyValue kv = (KeyValue) ((Spinner)findViewById(R.id.spTopic)).getSelectedItem();

		MemoType mt = model.memoTypes.get(kv.key);
		if(mt == null) {
			mt = new MemoType();
		}
		model.doc.topic = mt.id;

		if(mt.unlock()) {
			Date dt = SyncInfo.getLastSync(SyncInfo.DEBT);
			cf = new Unlock();
		} else {
			cf = new Email();
		}

		if(cf != null && (currentFragment == null || !currentFragment.getClass().equals(cf.getClass()))) {
			currentFragment = cf;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.child, cf)
					.commit();
		}
	}
	
	protected void send() {
		save();
		
		if(!model.isValid()) {
			Toast.makeText(this, "Не заполнены обязательные поля", Toast.LENGTH_SHORT).show();
		} else {
			List<DocExportListener> tosend = new ArrayList<DocExportListener>();
			tosend.add(new DocSendListner(AgentMemoDoc.instance().getObjectName(), doc));
			
			if(model.sendInvoice()) {
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
		if(doc.isEditable() && model.isEmpty())
			doc.delete();
		super.onBackPressed();
	}
	
	protected void save() {
		if(!doc.isEditable())
			return;

		if(currentFragment != null) {
			currentFragment.save();
		}
		AgentMemo am = doc.getData();

		Spinner sp = (Spinner)findViewById(R.id.spTopic);
		KeyValue sel = (KeyValue) sp.getSelectedItem(); 
		if(sel != null) {
			am.topic = sel.key.toString();
			am.topicName = sel.value.toString();
		}
		
		if(!model.isValid()) {
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

