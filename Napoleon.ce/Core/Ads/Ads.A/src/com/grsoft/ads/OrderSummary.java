package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.impl.ClientImpl;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.ads.documents.OrderDoc;
import com.grsoft.dataobjects.Contact;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class OrderSummary extends Activity {
	
	private OrderImpl orderImpl = new OrderImpl();
	private EditText edReport;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	private TextView tvWotkTime;
	private final String TIME_FACT_STR = "<br>Время факт: ";
	public static final String TAB_NAME = "order_delait";
	public static final String TAB_CAPTION = "Заявка";
	private long rowid = ExtrasConst.INVALID_ID;
	private TextView tvOrderText;
	private static final int CLIENT_INFO_DLG = 0;
	public static final String ORDER_DONE_STR = "Заявка выполнена";
	public static final String ORDER_DOIGN_STR = "Начать выполнение заявки";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_summary);
		
		Bundle bundle = getIntent().getExtras();
		edReport = (EditText)findViewById(R.id.edReport);
		tvOrderText = (TextView)findViewById(R.id.tvOrderText);
		
		if (bundle != null)
			rowid = bundle.getLong(ExtrasConst.DOC_ROW_ID_STR);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (orderImpl.read(rowid, false)){
			Order order = orderImpl.getData();
			
			tvOrderText.setText(order.text);
			
			StringBuilder remark = new StringBuilder();
			
			if (orderImpl.isRejected())
				remark.append("Причина отказа");
			
			remark.append(orderImpl.getData().remark);
			edReport.setText(remark.toString());
			
			tvWotkTime = (TextView)findViewById(R.id.tvWorkTime);
			StringBuilder wtStr = new StringBuilder("Время план: ");
			
			wtStr.append(sdf.format(order.planbegin)).append(" - ")
				.append(sdf.format(order.planend));
			
			Button btnStatus = (Button)findViewById(R.id.btnStatus);
			
			if (!orderImpl.isDone()){
				if (orderImpl.isDoing()){
					btnStatus.setText(ORDER_DONE_STR);
					btnStatus.setOnClickListener(new OrderSetDoneStatusListener());
					wtStr.append(TIME_FACT_STR).append(sdf.format(orderImpl.getData().factbegin));
				}else{
					btnStatus.setText(ORDER_DOIGN_STR);
					edReport.setVisibility(View.GONE);
					btnStatus.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							orderImpl.setDoing();
							Date dateBegin = Util.getDateTime();
							orderImpl.getData().factbegin = dateBegin;
							GpsCoord coord = GPSUtilNew.getLastKnownLocation();
							orderImpl.getData().longitude = coord.longitude;
							orderImpl.getData().latitude = coord.latitude;
							
							Toast.makeText(v.getContext(), 
									String.format("Вы начали выполнять заявку: время %s", 
											sdf.format(dateBegin)),
									Toast.LENGTH_LONG).show();
							Button button = ((Button)v);
							button.setText(ORDER_DONE_STR);
							button.setOnClickListener(new OrderSetDoneStatusListener());
							edReport.setVisibility(View.VISIBLE);
							StringBuilder sbTime = new StringBuilder(tvWotkTime.getText().toString());
							sbTime.append(TIME_FACT_STR).append(sdf.format(dateBegin));
							tvWotkTime.setText(Html.fromHtml(sbTime.toString()));
						}
					});
				}
			}else{
				btnStatus.setText(ORDER_DONE_STR);
				btnStatus.setEnabled(false);
				edReport.setEnabled(false);
				wtStr.append(TIME_FACT_STR).append(sdf.format(orderImpl.getData().factbegin))
				.append(" - ").append(sdf.format(orderImpl.getData().factend));
			}
			
			tvWotkTime.setText(Html.fromHtml(wtStr.toString()));
			TextView tvClient = (TextView)findViewById(R.id.tvClient);
			tvClient.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDialog(CLIENT_INFO_DLG);
				}
			});
			
			ClientImpl clientImpl = new ClientImpl();
			clientImpl.getData().id = orderImpl.getData().client;
			
			if (clientImpl.read())
				tvClient.setText(Html.fromHtml(clientImpl.getHtmlNameAddress()));
			else
				tvClient.setText(OrderDoc.CLIENT_N_A_STR);
			
			clientImpl.close();
			
			((TextView)findViewById(R.id.tvNumber)).setText(orderImpl.getData().number);
		}
		orderImpl.close();
	}
	
	class OrderSetDoneStatusListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Date dateEnd = Util.getDateTime();
			orderImpl.setDone();
			orderImpl.getData().factend = dateEnd;
			orderImpl.getData().remark = edReport.getText().toString();
			
			Toast.makeText(v.getContext(), 
					String.format("Заявка выполнена: время %s", 
							sdf.format(dateEnd)),
					Toast.LENGTH_LONG).show();
			Button button = ((Button)v); 
			button.setEnabled(false);
			button.setOnClickListener(null);
			StringBuilder wtStr = new StringBuilder("Время план: ");
			
			wtStr.append(sdf.format(orderImpl.getData().planbegin)).append(" - ")
				.append(sdf.format(orderImpl.getData().planend))
				.append(TIME_FACT_STR).append(sdf.format(orderImpl.getData().factbegin))
				.append(" - ").append(sdf.format(orderImpl.getData().factend));
			
			tvWotkTime.setText(Html.fromHtml(wtStr.toString()));
			orderImpl.write();
			
			edReport.setEnabled(false);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(!orderImpl.isDone()){
			orderImpl.getData().remark = edReport.getText().toString();
			orderImpl.write();
			orderImpl.close();
		}
	}
	
	@Override
	public Dialog onCreateDialog(int id){
		switch(id){
		case CLIENT_INFO_DLG:
			return createClientInfoDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createClientInfoDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.org_detail_info, null));
		builder.setTitle("Контакты клиента");
		return builder.create();
	}
	
	@Override
	public void onPrepareDialog(int id, Dialog dialog){
		switch(id){
		case CLIENT_INFO_DLG:
			prepareClientInfoDlg(dialog);
			break;
		}
	}

	private void prepareClientInfoDlg(Dialog dialog) {
		ClientImpl clientImpl = new ClientImpl();
		clientImpl.getData().id = orderImpl.getData().client;
		clientImpl.read();
		clientImpl.close();
		
		((TextView)dialog.findViewById(R.id.tvName)).setText(clientImpl.getData().name);
		((TextView)dialog.findViewById(R.id.tvAddress)).setText(clientImpl.getData().address);
		Contact[] contacts = new Contact[clientImpl.getData().contacts.size()];
		clientImpl.getData().contacts.toArray(contacts);
		
		Arrays.sort(contacts, new Comparator<Contact>() {
			@Override
			public int compare(Contact lhs, Contact rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
		
		ContactListAdapter contactAdapter = new ContactListAdapter(
				this, contacts);
		
		ListView lvContacts = ((ListView)dialog.findViewById(R.id.lvContacts));
		lvContacts.setAdapter(contactAdapter);
		lvContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, 
						Uri.parse(String.format("tel: %s", 
								tvPhone.getText().toString())));
				startActivity(intent);
			}
		});
	}
}

class ContactListAdapter extends BaseAdapter{
	Contact[] data;
	Context context;
	
	public ContactListAdapter(Context context, Contact[] data) {
		this.data = data;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.org_detail_info_row, null);
		
		((TextView)convertView.findViewById(R.id.tvFio)).setText(data[position].name);
		((TextView)convertView.findViewById(R.id.tvPhone)).setText(data[position].phone);
		
		return convertView;
	}
	
}
