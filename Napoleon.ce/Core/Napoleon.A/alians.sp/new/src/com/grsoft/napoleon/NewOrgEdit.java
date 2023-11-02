package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.ClassTT;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delay;
import com.grsoft.dataobjects.DeliveryRoute;
import com.grsoft.dataobjects.Dutie;
import com.grsoft.dataobjects.NewOrg;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.NewOrgImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class NewOrgEdit extends OrgEditActivity implements OnClickListener{
	private EditText edName;
	private EditText edRegion;
	private EditText edCity;
	private EditText edPunkt;
	private EditText edStreet;
	private EditText edDom;
	private EditText edKvartira;
	private Button btnClassTT;
	private Button btnVisitDay;
	private Button btnPrevOrg;
	private Button btnDeliveryRoute;
	private Button btnTimeIN;
	private Button btnTimeOUT;
	private EditText edDiscount;
	private Button btnDelay;
	private EditText edCName;
	private EditText edCName2;
	private EditText edCName3;
	private EditText edPhone;
	private Button btnDutie;
	
	private Map<String, ClassTT> classtt = new HashMap<String, ClassTT>();
	private Map<String, Map<String, OrgFolderItemEx>> routes = new HashMap<String, Map<String, OrgFolderItemEx>>();
	private Map<String, DeliveryRoute> drvs = new HashMap<String, DeliveryRoute>();
	private Map<String, Delay> delays = new HashMap<String, Delay>();
	private Map<String, Dutie> duties = new HashMap<String, Dutie>();

	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, NewOrgEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		edName = (EditText) findViewById(R.id.edName);
		edRegion = (EditText) findViewById(R.id.edRegion);
		edCity = (EditText) findViewById(R.id.edCity);
		edPunkt = (EditText) findViewById(R.id.edPunkt);
		edStreet = (EditText) findViewById(R.id.edStreet);
		edDom = (EditText) findViewById(R.id.edDom);
		edKvartira = (EditText) findViewById(R.id.edKvartira);
		btnClassTT = (Button) findViewById(R.id.btnClassTT);
		btnVisitDay = (Button) findViewById(R.id.btnVisitDay);
		btnPrevOrg = (Button) findViewById(R.id.btnPrevOrg);
		btnDeliveryRoute = (Button) findViewById(R.id.btnDeliveryRoute);
		btnTimeIN = (Button) findViewById(R.id.btnTimeIN);
		btnTimeOUT = (Button) findViewById(R.id.btnTimeOut);
		edDiscount = (EditText) findViewById(R.id.edDiscount);
		btnDelay = (Button) findViewById(R.id.btnDelay);
		edCName = (EditText) findViewById(R.id.edCName);
		edCName2 = (EditText) findViewById(R.id.edCName2);
		edCName3 = (EditText) findViewById(R.id.edCName3);
		edPhone = (EditText) findViewById(R.id.edPhone);
		btnDutie = (Button) findViewById(R.id.btnDutie);
		
		if (doc.isEditable()) {
			btnClassTT.setOnClickListener(this);
			btnVisitDay.setOnClickListener(this);
			btnPrevOrg.setOnClickListener(this);
			btnDeliveryRoute.setOnClickListener(this);
			btnTimeIN.setOnClickListener(this);
			btnTimeOUT.setOnClickListener(this);
			btnDelay.setOnClickListener(this);
			btnDutie.setOnClickListener(this);
		}
		
		DataTraveler.travel(ClassTT.class, new DataTraveler.Travel<ClassTT>(true) {

			@Override
			public boolean travel(DataTraveler<ClassTT> item) {
				classtt.put(item.data.id, item.data);
				return true;
			}
		}, null);

		DataTraveler.travel(OrgFolders.class, new DataTraveler.Travel<OrgFolders>(true) {

			@Override
			public boolean travel(DataTraveler<OrgFolders> item) {
				String name = item.data.name.trim();

				if (name.length() > 0) {
					if (Character.isDigit(name.charAt(0)))
						name = name.substring(1);

					if (!routes.containsKey(name))
						routes.put(name, new HashMap<String, OrgFolderItemEx>());

					Map<String, OrgFolderItemEx> items = routes.get(name);

					for (OrgFolderItem i : item.data.items)
						if (!items.containsKey(i.name))
							items.put(i.name, (OrgFolderItemEx) i);
				}

				return true;
			}
		}, null);
		
		DataTraveler.travel(DeliveryRoute.class, new DataTraveler.Travel<DeliveryRoute>(true) {

			@Override
			public boolean travel(DataTraveler<DeliveryRoute> item) {
				drvs.put(item.data.id, item.data);
				return true;
			}
		}, null);
		
		DataTraveler.travel(Delay.class, new DataTraveler.Travel<Delay>(true) {

			@Override
			public boolean travel(DataTraveler<Delay> item) {
				delays.put(item.data.id, item.data);
				return true;
			}
		}, null);

		DataTraveler.travel(Dutie.class, new DataTraveler.Travel<Dutie>(true) {

			@Override
			public boolean travel(DataTraveler<Dutie> item) {
				duties.put(item.data.id, item.data);
				return true;
			}
		}, null);

		NewOrg c = (NewOrg) doc.getData();
		
		edName.setText(c.name);
		edRegion.setText(c.region);
		edCity.setText(c.city);
		edPunkt.setText(c.punkt);
		edStreet.setText(c.street);
		edDom.setText(c.dom);
		edKvartira.setText(c.kvartira);

		String ct = c.classTT;
		if (classtt.containsKey(c.classTT))
			ct = classtt.get(c.classTT).name;

		btnClassTT.setText(ct);

		btnVisitDay.setText(c.visitDay);
		
		OrgImpl org = new OrgImpl();
		org.read("id", c.prevOrg);
		btnPrevOrg.setText(org.getData().name);
		
		String dr = c.deliveryRoute;
		
		if(drvs.containsKey(dr))
			dr = drvs.get(dr).name;
		
		btnDeliveryRoute.setText(dr);
		edDiscount.setText(Util.IntToScaleStr(c.discount, Consts.SUM_SCALE));
		
		String dl = c.delay;
		
		if (delays.containsKey(dl))
			dl = delays.get(dl).name;

		btnDelay.setText(dl);
		
		edCName.setText(c.contactName);
		edCName2.setText(c.contactName2);
		edCName3.setText(c.contactName3);
		edPhone.setText(c.phone);
		
		String dt = c.dutie;
		
		if (duties.containsKey(dt))
			dt = duties.get(dt).name;
		
		btnDutie.setText(dt);
		
		btnTimeIN.setText(Integer.toString(c.timeIn));
		btnTimeOUT.setText(Integer.toString(c.timeOut));
		
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (doc.isEditable()) {
			NewOrg c = (NewOrg) doc.getData();
			
			c.name = edName.getText().toString().trim();
			c.region = edRegion.getText().toString().trim();
			c.city = edCity.getText().toString().trim();
			c.punkt = edPunkt.getText().toString().trim();
			c.street = edStreet.getText().toString().trim();
			c.dom = edDom.getText().toString().trim();
			c.kvartira = edKvartira.getText().toString().trim();
			c.discount = Util.StrToScale(edDiscount.getText().toString().trim(), Consts.SUM_SCALE);
			c.contactName = edCName.getText().toString().trim();
			c.contactName2 = edCName2.getText().toString().trim();
			c.contactName3 = edCName3.getText().toString().trim();
			c.phone = edPhone.getText().toString().trim();
			
			doc.write();
			doc.close();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		int id = v.getId();
		if (id == R.id.btnForma)
			showDialog(R.id.forma_dlg);
		else if (id == R.id.btnClassTT)
			showDialog(R.id.classtt_dlg);
		else if (id == R.id.btnVisitDay)
			showDialog(R.id.visit_day_dlg);
		else if (id == R.id.btnPrevOrg) 
			selectPrevOrg();
		else if (id == R.id.btnDeliveryRoute)
			showDialog(R.id.dlv_route_dlg);
		else if (id == R.id.btnTimeIN)
			showDialog(R.id.time_in_dlg);
		else if (id == R.id.btnTimeOut)
			showDialog(R.id.time_out_dlg);
		else if (id == R.id.btnDelay)
			showDialog(R.id.delay_dlg);
		else if (id == R.id.btnDutie)
			showDialog(R.id.dutie_dlg);
		
	}

	private void selectPrevOrg() {
		NewOrg org = (NewOrg) doc.getData();
		
		if (routes.containsKey(org.visitDay.trim()))
			showDialog(R.id.prev_org_dlg);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.classtt_dlg)
			return createClassTTDlg();
		else if (id == R.id.visit_day_dlg)
			return createVisitDayDlg();
		else if (id == R.id.prev_org_dlg)
			return createPrevOrgDlg();
		else if (id == R.id .dlv_route_dlg)
			return createDlvRouteDlg();
		else if (id == R.id.time_in_dlg)
			return createTimeINDlg();
		else if (id == R.id.time_out_dlg)
			return createTimeOUTDlg();
		else if (id == R.id.delay_dlg)
			return createDelayDlg();
		else if (id == R.id.dutie_dlg)
			return createDutieDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog createDutieDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<Dutie> list = new ArrayList<Dutie>();
		list.addAll(duties.values());
		Collections.sort(list, new Comparator<Dutie>() {

			@Override
			public int compare(Dutie lhs, Dutie rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});

		list.add(0, new Dutie());

		ArrayAdapter<Dutie> aa = new ArrayAdapter<Dutie>(this, R.layout.simple_spinner_layout, list);

		builder.setAdapter(aa, dutieSelect);
		return builder.create();
	}

	private Dialog createDelayDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<Delay> list = new ArrayList<Delay>();
		list.addAll(delays.values());
		Collections.sort(list, new Comparator<Delay>() {

			@Override
			public int compare(Delay lhs, Delay rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});

		list.add(0, new Delay());

		ArrayAdapter<Delay> aa = new ArrayAdapter<Delay>(this, R.layout.simple_spinner_layout, list);

		builder.setAdapter(aa, delaySelect);
		return builder.create();
	}

	private Dialog createTimeOUTDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<String> list = new ArrayList<String>();

		final int SV = 2;
		final int EV = 60;
		
		for (int i = SV; i <= EV; i++)
			list.add(Integer.toString(i));

		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, list);
		builder.setAdapter(aa, timeOUTSelect);
		return builder.create();
	}

	private Dialog createTimeINDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<String> list = new ArrayList<String>();

		final int SV = 2;
		final int EV = 60;
		
		for (int i = SV; i <= EV; i++)
			list.add(Integer.toString(i));

		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, list);
		builder.setAdapter(aa, timeINSelect);
		return builder.create();
	}

	private Dialog createDlvRouteDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<DeliveryRoute> list = new ArrayList<DeliveryRoute>();
		list.addAll(drvs.values());
		Collections.sort(list, new Comparator<DeliveryRoute>() {

			@Override
			public int compare(DeliveryRoute lhs, DeliveryRoute rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});

		list.add(0, new DeliveryRoute());

		ArrayAdapter<DeliveryRoute> aa = new ArrayAdapter<DeliveryRoute>(this, R.layout.simple_spinner_layout, list);

		builder.setAdapter(aa, dlvRouteSelect);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == R.id.prev_org_dlg)
			preparePrevOrgDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	private void preparePrevOrgDlg(Dialog dialog) {
		ArrayAdapter<Org> aa = (ArrayAdapter<Org>) ((AlertDialog) dialog).getListView().getAdapter();
		aa.clear();
		
		NewOrg org = (NewOrg) doc.getData();
		
		Map<String, OrgFolderItemEx> map = routes.get(org.visitDay); 
		Set<String> ids = map.keySet();
		List<OrgFolderItemEx> items = new ArrayList<OrgFolderItemEx>(map.values());
		
		if (ids != null && ids.size() > 0)
		{
			final Map<String, Org> data = new HashMap<String, Org>();
			StringBuilder sb = new StringBuilder();
			
			for(String id : ids) {
				if(sb.length() > 0)
					sb.append(",");
				sb.append("'").append(id).append("'");
			}
				
			String where = String.format("id in (%s)", sb.toString());
			
			DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>(true) {

				@Override
				public boolean travel(DataTraveler<Org> item) {
					if (!data.containsKey(item.data.id))
						data.put(item.data.id, item.data);
					return true;
				}
			}, where);
			
			Collections.sort(items, new OrgFolderItemCmp());
			
			List<Org> list = new ArrayList<Org>();
			
			for(OrgFolderItemEx i : items) {
				if (data.containsKey(i.name))
					list.add(data.get(i.name));
			}
			
			aa.addAll(list);
		}
	}

	private Dialog createPrevOrgDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<Org> list = new ArrayList<Org>();
		list.add(0, new Org());

		ArrayAdapter<Org> aa = new ArrayAdapter<Org>(this, R.layout.simple_spinner_layout, list);

		builder.setAdapter(aa, orgSelect);
		return builder.create();
	}

	private Dialog createVisitDayDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<String> list = new ArrayList<String>();

		for (String s : routes.keySet())
			if (s.trim().length() > 0)
				list.add(s);

		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				WeekDay wkObject1 = WeekDay.getWeekDay(lhs);
				WeekDay wkObject2 = WeekDay.getWeekDay(rhs);

				if (wkObject1 != null && wkObject2 != null)
					return WeekDay.compare(wkObject1, wkObject2);

				return 0;
			}
		});

		list.add(0, "");

		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, list);

		builder.setAdapter(aa, visitDaySelect);
		return builder.create();
	}

	private Dialog createClassTTDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		List<ClassTT> list = new ArrayList<ClassTT>();
		list.addAll(classtt.values());
		Collections.sort(list, new Comparator<ClassTT>() {

			@Override
			public int compare(ClassTT lhs, ClassTT rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});

		list.add(0, new ClassTT());

		ArrayAdapter<ClassTT> aa = new ArrayAdapter<ClassTT>(this, R.layout.simple_spinner_layout, list);
		builder.setAdapter(aa, classttSelect);
		
		return builder.create();
	}

	DialogInterface.OnClickListener dutieSelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Dutie d = (Dutie) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.dutie = d.id;
			doc.write();
			doc.write();
			
			btnDutie.setText(d.name);
		}
	};
	
	DialogInterface.OnClickListener delaySelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Delay d = (Delay) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.delay = d.id;
			doc.write();
			doc.close();
			
			btnDelay.setText(d.name);
		}
	};
	
	DialogInterface.OnClickListener timeINSelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String f = (String) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.timeIn = Integer.parseInt(f);
			doc.write();
			doc.close();
			
			btnTimeIN.setText(f);
		}
	};
	
	DialogInterface.OnClickListener timeOUTSelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String f = (String) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.timeOut = Integer.parseInt(f);
			doc.write();
			doc.close();
			
			btnTimeOUT.setText(f);
		}
	};
	
	DialogInterface.OnClickListener dlvRouteSelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			DeliveryRoute d = (DeliveryRoute) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.deliveryRoute = d.id;
			doc.write();
			doc.close();
			
			btnDeliveryRoute.setText(d.name);
		}
	};
	
	DialogInterface.OnClickListener orgSelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Org o = (Org) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.prevOrg = o.id;
			doc.write();
			doc.close();
			
			btnPrevOrg.setText(o.name);
		}
	};

	DialogInterface.OnClickListener classttSelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			ClassTT f = (ClassTT) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.classTT = f.id;
			doc.write();
			doc.close();
			
			btnClassTT.setText(f.name);
		}
	};

	DialogInterface.OnClickListener visitDaySelect = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String d = (String) ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
			NewOrg org = (NewOrg) doc.getData();
			org.visitDay = d;
			doc.write();
			doc.close();

			btnVisitDay.setText(d);
		}
	};

	@Override
	CreatableDocument<?> createDocument() {
		return new NewOrgImpl();
	}

	@Override
	int getLayoutId() {
		return R.layout.neworgedit;
	}

}
