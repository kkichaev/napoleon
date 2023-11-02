package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ContactEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.script.documents.ScriptDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	private static final String VISIT_INFO = "visit_info";
	private TextView tvAddress;
	private TextView tvVisitInfo;
	private TextView tvVisitStatus;
	private TextView tvContacts;
	private TextView tvSelected;
	private Button btnPrice;
	private Button btnStart;
	private boolean inroute;
	private int exceed;
	public static int answers;
	private long exceedDay;

	// 1 - первый вопрос - да
	// 2 - второй вопрос - полный расчет
	// 4 - второй вопрос - частичный расчет
	// 8 - третий вопрос - да

	public static void open(Context context, String id, VisitInfo vi) {
		DocType.setCurDoc(DebtDoc.instance());
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.ORG_ID_STR, id);
		intent.putExtra(VISIT_INFO, vi);
		context.startActivity(intent);
	}

	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvVisitInfo = (TextView) findViewById(R.id.tvVisitInfo);
		tvVisitStatus = (TextView) findViewById(R.id.tvVisitStatus);
		tvContacts = (TextView) findViewById(R.id.tvContacts);
		btnPrice = (Button) findViewById(R.id.btnPrice);
		btnStart = (Button) findViewById(R.id.btnStart);

		VisitInfo vi = getIntent().getParcelableExtra(VISIT_INFO);
		answers = 0;

		Org o = org.getData();
		tvOrgInfo.setText(getString(R.string.client_fmt, o.name));
		tvAddress.setText(getString(R.string.address_fmt, o.address));

		if (vi != null) {
			tvVisitInfo.setText(getString(R.string.visit_info_fmt, vi.name, vi.time));
			inroute = vi.status(tvVisitStatus);
		} else {
			tvVisitInfo.setVisibility(View.GONE);
			tvVisitStatus.setText(R.string.outroute);
			tvVisitStatus.setTextColor(getResources().getColor(R.color.outroute));
		}

		if (o.contacts.size() == 0)
			tvContacts.setVisibility(View.GONE);
		else {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < o.contacts.size(); i++) {
				ContactEx cex = (ContactEx) o.contacts.get(i);

				if (i > 0)
					sb.append("<br>");

				sb.append(cex.name).append(" тел.: ").append(cex.phone).append(" д.р.: ").append(cex.bday);

			}

			tvContacts.setText(Html.fromHtml(sb.toString()));
		}

		DeliveryEx data = new DeliveryEx();
		DbReader reader = new DbReader();
		StringBuilder where = new StringBuilder();
		where.append("id='").append(o.id).append("'");

		boolean bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(data.getClass()), where.toString());

		Date now = Util.getDate();
		int toPay = 0;
		int totalDebt = 0;
		exceed = 0;
		exceedDay = 0;
		
		while (bdo) {
			long d = DatePeriod.daysDiff(now, data.payDate);

			if (d <= 0)
				toPay += data.sumD;

			totalDebt += data.sumD;

			if (d < 0) {
				exceed += data.sumD;
				
				if (d < exceedDay)
					exceedDay = d;
			}

			bdo = reader.selectNext(data);
		}

		Payment payment = new Payment();
		bdo = reader.select(data, DataObjectInfo.getInstance().getTableName(payment.getClass()), where.toString());

		while (bdo) {
			totalDebt += payment.sum;
			bdo = reader.selectNext(payment);
		}

		reader.close();

		TextView tv = (TextView) findViewById(R.id.tvToPay);
		tv.setText(toPay > 0 ? Util.IntToScaleStr(toPay, Consts.SUM_SCALE) : "");

		tvSelected = (TextView) findViewById(R.id.tvSelected);
		tvSelected.setText("");

		tv = (TextView) findViewById(R.id.tvResultDebt);
		tv.setText(totalDebt > 0 ? Util.IntToScaleStr(totalDebt, Consts.SUM_SCALE) : "");

		tv = (TextView) findViewById(R.id.tvExpired);
		tv.setText(exceed > 0 ? Util.IntToScaleStr(exceed, Consts.SUM_SCALE) : "");

		btnPrice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WarehouseEx.open(v.getContext(), org.getData().id);
			}
		});

		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startWork();
			}
		});

		btnNewDoc.setOnClickListener(new OnClickListenerToNotify() {
			@Override
			public void onClick(View v) {
				super.onClick(v);

				DocType dt = (DocType) DocType.getCurDoc();
				if ((dt == OrderDoc.instance() || dt == ScriptDoc.instance()) && org.getData().isStopList())
					showDialog(DLG_WARNING_IF_ORG_IN_STOP_LIST);
				else if (dt == OrderDoc.instance())
					startWork();
				else
					doCreate();
			}
		});
	}

	protected void startWork() {
		if (!inroute)
			showDialog(R.id.ask_1_dlg);
		else if(exceed > 0 && !hasIncassToday())
			showDialog(R.id.ask_2_dlg);
		else
			doOrderCreate();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.ask_1_dlg:
			return createAsk1Dlg();
		case R.id.ask_2_dlg:
			return createAsk2Dlg();
		case R.id.ask_3_dlg:
			return createAsk3Dlg();
		case R.id.client_reject_dlg:
			return createClientRejectDlg();
		case R.id.raschet_bank_dlg:
			return createRaschetBankDlg();
		case R.id.incass_dlg:
			return createIncassDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createIncassDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.incas_doc_title);
		builder.setView(View.inflate(this, R.layout.incass_dlg, null));
		builder.setNegativeButton(R.string.cancel, null);
		
		builder.setNeutralButton(R.string.skip, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				doOrderCreate();
			}
		});
		
		builder.setPositiveButton(R.string.oplacheno, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				IncassImpl incass =  (IncassImpl) IncassDoc.instance().create();
				if (incass.init(DocumentsEx.this, org.getData().id, GPSUtilNew.getLastKnownLocation(DocumentsEx.this))) {
					IncassEx i = (IncassEx) incass.getData();
					EditText edSum = (EditText)((Dialog)dialog).findViewById(R.id.edSum);
					String sum = edSum.getText().toString().trim();
					i.sum = Util.StrToScale(sum, Consts.SUM_SCALE);
					incass.write();
					doOrderCreate();
				}
			}
		});
		
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.raschet_bank_dlg)
			prepareRaschetBankDlg(dialog);
		else if (id == R.id.incass_dlg)
			prepareIncassDlg(dialog);
		else	
			super.onPrepareDialog(id, dialog);
	}
	
	private void prepareIncassDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edSum);
		ed.setText(Util.IntToScaleStr(exceed, Consts.SUM_SCALE));
		ed.selectAll();
		ed.requestFocus();
		
		AlertDialog a = (AlertDialog) dialog;
		a.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(exceed <= 0 ? View.VISIBLE :  View.GONE);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	private void prepareRaschetBankDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edSum);
		ed.setText(Util.IntToScaleStr(exceed, Consts.SUM_SCALE));
		ed.selectAll();
		ed.requestFocus();
		
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	private Dialog createRaschetBankDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.plateg_poruch);
		builder.setView(View.inflate(this, R.layout.plateg_poruch_dlg, null));
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.oplacheno, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				IncassImpl incass =  (IncassImpl) IncassDoc.instance().create();
				if (incass.init(DocumentsEx.this, org.getData().id, GPSUtilNew.getLastKnownLocation(DocumentsEx.this))) {
					IncassEx i = (IncassEx) incass.getData();
					EditText edSum = (EditText)((Dialog)dialog).findViewById(R.id.edSum);
					String sum = edSum.getText().toString().trim();
					i.sum = Util.StrToScale(sum, Consts.SUM_SCALE);
					i.bank = 1;
					String number = ((EditText)((Dialog)dialog).findViewById(R.id.edNumber)).getText().toString().trim();
					i.number = number;
					
					incass.write();
					
					doOrderCreate();
				}
			}
		});
		
		return builder.create();
	}

	private Dialog createClientRejectDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert);
		builder.setMessage(R.string.client_reject);
		builder.setPositiveButton(R.string.ok, null);
		return builder.create();
	}

	private Dialog createAsk3Dlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.quest_3);
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				showDialog(R.id.client_reject_dlg);
			}
		});

		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				answers |= 8;
				doOrderCreate();
			}
		});

		return builder.create();
	}

	private Dialog createAsk2Dlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.clien_calc);
		StringBuilder sb = new StringBuilder();
		sb.append(getString(R.string.quest_2, Util.IntToScaleStr(exceed, Consts.SUM_SCALE)));
		
		if (exceedDay < 0) {
			sb.append("<br>");
			sb.append(getString(R.string.quest_21, Math.abs(exceedDay)));
		}
		builder.setMessage(Html.fromHtml(sb.toString()));
		builder.setPositiveButton(R.string.incas_doc_title, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				answers |= 2;
				if (hasIncassToday())
					doOrderCreate();
				else
					showDialog(R.id.incass_dlg);
			}
		});

		builder.setNeutralButton(R.string.raschet_bank, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				answers |= 4;
				
				if (hasIncassToday())
					doOrderCreate();
				else
					showDialog(R.id.raschet_bank_dlg);
			}
		});

		builder.setNegativeButton(R.string.cancel, null);

		return builder.create();
	}

	protected boolean hasIncassToday() {
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		Date begin = c.getTime();
		c.add(Calendar.DATE, 1);
		Date end = c.getTime();
		DatePeriod pd = new DatePeriod(begin, end);
		
		return IncassDoc.instance().docList(org.getData().id, null, pd).getCount() > 0;
	}

	private Dialog createAsk1Dlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.quest_1);
		builder.setNegativeButton(R.string.cancel, null);
		
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				answers |= 1;
				
				if(exceed > 0 && !hasIncassToday())
					showDialog(R.id.ask_2_dlg);
				else 
					doOrderCreate();
			}
		});

		return builder.create();
	}

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		if (docType == DebtDocEx.instance())
			return new DebtDocAdapter(this, id);
		return super.createAdapter(docType, id);
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		adapter = null;
		super.adjustViewForDocType(docType);

		if (docType == DebtDocEx.instance())
			findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}

	public void updateSelected(int s) {
		if (s > 0)
			tvSelected.setText(Util.IntToScaleStr(s, Consts.SUM_SCALE));
		else
			tvSelected.setText("");
	}

	private void doOrderCreate() {
		DocType.getCurDoc().viewClosed(this);
		DocType.setCurDoc(OrderDoc.instance());

		doCreate();
	}
}

class VisitInfo implements Parcelable {
	String name = "";
	String time = "";
	boolean everyday;

	public VisitInfo(String name, String time, boolean everyday) {
		this.name = name;
		this.time = time;
		this.everyday = everyday;
	}

	public boolean status(TextView tv) {

		if (everyday) {
			tv.setText(R.string.inroute);
			tv.setTextColor(tv.getContext().getResources().getColor(R.color.inroute));
			return true;
		}

		WeekDay wd = WeekDay.getWeekDay(name);

		if (wd != null && wd.equals(WeekDay.today())) {
			String ts[] = time.split("-");

			if (ts.length == 2) {

				int marginStart = 15;
				int marginFinish = 60;

				ConfigImpl impl = new ConfigImpl();
				StringBuilder sb = new StringBuilder();

				if (impl.getValue(sb, "НачалоПосещения")) {
					try {
						marginStart = Integer.parseInt(sb.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				sb.setLength(0);

				if (impl.getValue(sb, "КонецПосещения")) {
					try {
						marginFinish = Integer.parseInt(sb.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				Date start = timeParcer(ts[0], -marginStart);
				Date finish = timeParcer(ts[1], marginFinish);
				Date now = new Date();

				if (now.getTime() >= start.getTime() && now.getTime() <= finish.getTime()) {
					tv.setText(R.string.inroute);
					tv.setTextColor(tv.getContext().getResources().getColor(R.color.inroute));
					return true;
				} else {
					tv.setText(R.string.outroute);
					tv.setTextColor(tv.getContext().getResources().getColor(R.color.outroute));
				}
			} else {
				tv.setText(R.string.outroute);
				tv.setTextColor(tv.getContext().getResources().getColor(R.color.outroute));
			}
		} else {
			tv.setText(R.string.outroute);
			tv.setTextColor(tv.getContext().getResources().getColor(R.color.outroute));
		}

		return false;
	}

	private Date timeParcer(String time, int margin) {
		String[] ta = time.split(":");
		Calendar c = Calendar.getInstance();

		if (ta.length == 2) {
			try {
				int h = Integer.parseInt(ta[0]);
				int m = Integer.parseInt(ta[1]);
				c.set(Calendar.HOUR_OF_DAY, h);
				c.set(Calendar.MINUTE, m);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				c.add(Calendar.MINUTE, margin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return c.getTime();
	}

	public VisitInfo(Parcel in) {
		String[] arr = new String[3];
		in.readStringArray(arr);
		name = arr[0];
		time = arr[1];
		everyday = Boolean.parseBoolean(arr[2]);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { name, time, Boolean.toString(everyday) });
	}

	public static final Parcelable.Creator<VisitInfo> CREATOR = new Parcelable.Creator<VisitInfo>() {
		public VisitInfo createFromParcel(Parcel in) {
			return new VisitInfo(in);
		}

		public VisitInfo[] newArray(int size) {
			return new VisitInfo[size];
		}
	};
}
