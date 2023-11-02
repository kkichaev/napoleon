package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.DivisionInfo;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgHelper;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.IncassImplEx;
import com.grsoft.dataobjects.impl.SalesBanImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.ArchIncassDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

public class DocumentsEx extends Documents {
	@Override
	protected String orgInfo(Org o) {
		int ud = OrgHelper.getUnpayDays(org.getData().id);
		int bd = getBlockDayCnt();
		
		StringBuilder sb = new StringBuilder(super.orgInfo(o));
		sb.append("<i><br>");
		sb.append(String.format("Фактическая просрочка: %d", ud));
		sb.append("<br>");
		sb.append(String.format("Допустимая просрочка: %d", bd));
		sb.append("</i>");
		
		return sb.toString();
	}
	private static final int BLOCK_DAY_CNT = 7;
	@Override
	protected void init(Bundle b) {
		super.init(b);
		btnNewDoc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DocType dt = (DocType) DocType.getCurDoc();
				OrgEx o = (OrgEx)org.getData();
				
				if( dt == SalesDoc.instance() ) {
					if(SalesBanImpl.isOrgBanned(o.id) ) {
						Toast.makeText(DocumentsEx.this, R.string.org_blocked, Toast.LENGTH_SHORT).show();
						return;
					} else if (OrgHelper.getUnpayDays(o.id) >= getBlockDayCnt()) {
						Toast.makeText(DocumentsEx.this, R.string.cant_load_org, Toast.LENGTH_SHORT).show();
						return;
					}
				} /*
				else if( dt == IncassDoc.instance()) {
					String dogTable = DataObjectInfo.getInstance().getTableName(OrgDogovor.class);
					String firmTable = DataObjectInfo.getInstance().getTableName(Firm.class);
					String stmt = "select d.name from " + dogTable + " d, " + firmTable + 
							" f where d.supplyercode = f.id and f.tax = 0 and ido = '" + o.ido +  "'";
					
					boolean canCreate = false;
					try {
						Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
						canCreate = c.moveToNext();
						c.close();
					} catch(Exception e){
						e.printStackTrace();
					}
					if( !canCreate ) {
						Toast.makeText(DocumentsEx.this, R.string.no_cash, Toast.LENGTH_SHORT).show();
						return;
					}
				} */
				doCreate();
			}
		});
	}
	
	private int getBlockDayCnt() {
		int res = 0;
		///1 - Отсрочка контрагента
		SalesBanImpl sbi = new SalesBanImpl();
		sbi.read("id", org.getData().id);
		
		if (sbi.getData().delay.trim().length() > 0) {
			try {
				res = Integer.parseInt(sbi.getData().delay);
				
				return res;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		///2 - Отсрочка агента
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		cfg.getValue(sb, "Delay");
		
		if (sb.toString().trim().length() > 0) {
			try {
				res = Integer.parseInt(sb.toString().trim());
				
				return res;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		///3 - Отсрочка подразделения
		String table = DataObjectInfo.getInstance().getTableName(AgentPrefix.class);

		boolean bdo = false;
		DbReader r = new DbReader();
		AgentPrefix p = new AgentPrefix();
		bdo = r.select(p, table, "id=userid");
		
		if (bdo) {
			DivisionInfo d = new DivisionInfo();
			bdo = r.select(d, DataObjectInfo.getInstance().getTableName(DivisionInfo.class), 
					String.format("userid='%s'", p.userid));
			
			if (bdo) {
				if (d.delay.trim().length() == 0) {
					final Map<Integer, DivisionInfo> map = new HashMap<Integer, DivisionInfo>();
					
					DataTraveler.travel(DivisionInfo.class, new DataTraveler.Travel<DivisionInfo>(true) {

						@Override
						public boolean travel(DataTraveler<DivisionInfo> item) {
							map.put(item.data.id, item.data);
							return true;
						}
					}, null);
					
					while (map.containsKey(d.parent) && d.parent != 0)
						d = map.get(d.parent);
				}
				
				try {
					res = Integer.parseInt(d.delay);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return (docType == ArchIncassDoc.instance()) ? false : super.canCreateDoc(docType);
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	@Override
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListener(this){
			@Override
			protected void initData(boolean creatableFilter) {
				super.initData(creatableFilter);
				data.remove(WSOrderDoc.instance());
			}
		};
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if( SalesDoc.instance() == DocType.getCurDoc() ) {
			getMenuInflater().inflate(R.menu.doc_context_menu_print, menu);
			MenuItem item = menu.findItem(R.id.itMakeSale);
			if( item != null )
				item.setVisible(false);
		} else
			super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		GpsCoord location = (allowCreateDocWhithoutGpsPos || GPSUtilNew.isGpsPosValid()) ? 
				GPSUtilNew.getLastKnownLocation() :
				null;
		if( item.getItemId() == R.id.itMakePKO ) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
			Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
			if( doc != null && (doc instanceof SalesImpl || doc instanceof DeliveryImpl)) {
				if (location != null) {
					if( doc instanceof SalesImpl )
						makePKO((SalesImpl) doc, location);
					else if( doc instanceof DeliveryImpl )
						makePKO((DeliveryImpl) doc, location);
				} else
					makeLocationAlert();
			}
			return false;
		}
		return super.onContextItemSelected(item);
	}

	private void makePKO(DeliveryImpl doc, GpsCoord location) {
		IncassImplEx iie = new IncassImplEx();
		IncassEx ie = (IncassEx) iie.getData();
		DeliveryEx de = (DeliveryEx) doc.getData();
		ie.supplyercode = de.supplyercode;
		ie.sum = (int)de.sumD;
		iie.init(this, doc.getId(), location);
		iie.open(this);
	}

	private void makePKO(SalesImpl doc, GpsCoord location) {
		IncassImplEx iie = new IncassImplEx();
		IncassEx ie = (IncassEx) iie.getData();
		SalesEx de = (SalesEx) doc.getData();
		ie.supplyercode = de.supplyercode;
		ie.dogovor = de.dogovor;
		ie.sum = (int)de.sum();
		iie.init(this, doc.getId(), location);
		iie.open(this);
	}
	
}
