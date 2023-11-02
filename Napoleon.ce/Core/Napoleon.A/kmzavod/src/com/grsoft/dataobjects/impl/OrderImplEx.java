package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.AssortmentMatrixAdapterEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.QuantHelper;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class OrderImplEx extends OrderImpl {
	PriceImpl price = new PriceImpl();
	QuantHelper quantHelper = new QuantHelper();
	
	public static class DebugInt{
		public String debug = "";
		public int value = 0;
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		OrgImpl org = new OrgImpl();
		org.read("id", data.id);
		data.sumType = org.getData().costype;
		
		if (RemnantsImpl.find(data.id, Calendar.getInstance().getTime()) != -1 &&
				collectRouteIds().contains(data.id))
			postInitV1();
//		else
//			postInitV2();
		
	}
	
	public void avgOrder() {
		postInitV2();
		write();
		close();
	}
	
	private void postInitV2() {
		((OrderEx) data).auto = 1;
		
		Map<String, Integer> mapSVR = SVR();
		Map<String, Integer> mapSVV = SVV();
		Map<String, Integer> mapKR = KR();
		
		
		List<MatrixItem> matrix =  AssortmentMatrixAdapterEx.collect(data.id);
		
		for(MatrixItem i : matrix) {
			if(price.read("id", i.id)) {
				int svr = 0;
				int svv = 0;
				int kr = 0;
				int qty = 0;
				
				if (mapSVR.containsKey(i.id))
					svr = mapSVR.get(i.id);
				
				if (mapSVV.containsKey(i.id))
					svv = mapSVV.get(i.id);
				
				if (mapKR.containsKey(i.id))
					kr = mapKR.get(i.id);
				
				if (kr != 0 && svv < svr)
					qty = (svr - svv) / kr;
				
				if (qty > 0) 
					addOrderItem(i.id, qty);
			}
		}
			
	}

	protected OrderItemEx addOrderItem(String id, int qty) {
		PriceImpl p = new PriceImpl();
		p.read("id", id);
		
		OrderItemEx o = new OrderItemEx();
		o.id = id;
		o.qty = quantHelper.roundToQuant(qty, ((PriceEx)price.getData()).quant);
		o.aqty = o.qty;
		o.cost = CostStrategy.getInstance(this.getClass()).getItemCost(p.getData(), this);
		
		data.items.add(o);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<b>").append(p.getData().name).append("</b><br>")
			.append("количество без приведения к кванту = ").append((double)qty / Consts.QTY_SCALE).append("<br>")
			.append("квант = ").append((double)((PriceEx)price.getData()).quant / Consts.QTY_SCALE).append("<br>")
			.append("итоговое значение = ").append((float)o.qty / Consts.QTY_SCALE).append("<br>");
		o.debug = sb.toString();
		
		return o;
	}

	private void postInitV1() {
		((OrderEx) data).auto = 1;
		
		List<MatrixItem> matrix =  AssortmentMatrixAdapterEx.collect(data.id);
		
		Map<String, DebugInt> mapSDP = SDP(matrix);
		Map<String, Integer> mapTO = TO();
		
		for(MatrixItem i : matrix) {
			if(price.read("id", i.id)) {
				DebugInt dsdp = new DebugInt();
				int sdp = 0;
				DebugInt dkdv = KDV();
				int kdv = dkdv.value;
				int to = 0;
				int qty = 0;
				
				if (mapSDP.containsKey(i.id)) {
					dsdp = mapSDP.get(i.id);
					sdp = dsdp.value;
				}
				
				if (mapTO.containsKey(i.id))
					to = mapTO.get(i.id);
				
				int kv = ((PriceEx)price.getData()).quant;
				int sg = ((PriceEx)price.getData()).godn;
				
				qty = (int) (Math.round((double)sdp / Consts.QTY_SCALE * (double)kdv * 1.5 - (double)to / Consts.QTY_SCALE) * Consts.QTY_SCALE);  ;
				
				if(qty == 0)
					if (to == 0 && sdp > 0 && kv > 0 && sg > 0 && sdp * sg >= kv) 
						qty = kv;
					else 
						qty = 0;
				else
					qty = quantHelper.roundToQuant(qty, kv);
				
				if (qty > 0) {
					StringBuilder sb = new StringBuilder();
					
					sb.append("<br><b>рассчет данных</b><br>");
					sb.append("<i>считаю qty = round(sdp * kdv * 1.5 - to)</i><br>");
					sb.append((double)sdp / Consts.QTY_SCALE ).append(" * ")
					  .append((double)kdv).append(" * 1.5 -")
					  .append((double)to / Consts.QTY_SCALE).append("=")
					  .append((double)qty / Consts.QTY_SCALE).append("<br>");
					
					sb.append(dkdv.debug);
					sb.append(dsdp.debug);
					
					OrderItemEx e = addOrderItem(i.id, qty);
					
					e.debug += sb.toString(); 
				}
			}
		}
	}
	
	public Map<String, DebugInt> SDP(List<MatrixItem> matrix) {
		Map<String, DebugInt> result = new HashMap<String, DebugInt>(); 
		Map<String, Integer> mapOND = OND(matrix);
		Map<String, Integer> mapSVR = SVR();
		Map<String, Integer> mapSVV = SVV();
		Map<String, Integer> mapTO = TO();
		Map<String, Integer> mapKD = KD(matrix);
		
		for(MatrixItem i : matrix) {
			int ond = 0;
			int svr = 0;
			int svv = 0;
			int to = 0;
			int kd = 0;
			
			if (mapOND.containsKey(i.id))
				ond = mapOND.get(i.id);
			
			if (mapSVR.containsKey(i.id))
				svr = mapSVR.get(i.id);
			
			if (mapSVV.containsKey(i.id))
				svv = mapSVV.get(i.id);
			
			if (mapTO.containsKey(i.id))
				to = mapTO.get(i.id);
			
			if (mapKD.containsKey(i.id))
				kd = mapKD.get(i.id);
			
			if(kd == 0)
				kd = 1;
			
			int sdp = (ond + svr - svv - to) / kd;
			
			DebugInt di = new DebugInt();
			di.value = sdp;
			
			StringBuilder sb = new StringBuilder();
			sb.append("<br><b>считаю СДП</b><br>");
			sb.append("<i>sdp = (ond + svr - svv - to) / kd<i><br>");
			sb.append("(").append((float)ond / Consts.QTY_SCALE)
				.append(" + ").append((float)svr / Consts.QTY_SCALE)
				.append(" - ").append((float)svv / Consts.QTY_SCALE)
				.append(" - ").append((float)to / Consts.QTY_SCALE).append(")")
				.append(" / ").append(kd).append(" = ").append((float)sdp / Consts.QTY_SCALE);
			
			di.debug = sb.toString();
			
			result.put(i.id, di);
		}
		
		return result;
	}
	
	private Map<String, Integer> KD(List<MatrixItem> matrix) {
		Map<String, Integer> result = new HashMap<String, Integer>(); 
		Map<String, Date> mapND = ND(matrix);
		Date now = new Date();
		
		for(MatrixItem i : matrix) 
			if (mapND.containsKey(i.id))
				result.put(i.id, (int)DatePeriod.daysDiff(mapND.get(i.id), now));
		
		return result;
	}

	private Map<String, Date> ND(List<MatrixItem> matrix) {
		Map<String, Date> result = new HashMap<String, Date>();
		
		DocList remnants = RemnantsDoc.instance().docList(data.id, "created", getPeriod());
		
		for(Document<?> d : remnants) {
			if (d instanceof RemnantsImpl) {
				RemnantsImpl impl = (RemnantsImpl)d;
				
				for(RemnantItem i : impl.getData().items) 
					if (!result.containsKey(i.id) && i.qty > 0)
						result.put(i.id, impl.getData().created);
			}
		}
		
		DocList deliveries = DebtDoc.instance().docList(data.id, "created", getPeriod());
		for(Document<?> d : deliveries) {
			if (d instanceof DeliveryImpl) {
				DeliveryImpl impl = (DeliveryImpl)d;
				
				for(DeliveryItem i : impl.getData().items) 
					if (i.qty > 0) {
						if (!result.containsKey(i.id))
							result.put(i.id, impl.getData().created);
						else {
							Date t = Util.resetTime(result.get(i.id));
							Date t1 = Util.resetTime(impl.getData().date); 
							
							if (t1.before(t)) {
								result.remove(i.id);
								result.put(i.id, t1);
							}
						}
					}
			}
		}
		
		return result;
	}

	private Map<String, Integer> OND(List<MatrixItem> matrix) {
		Map<String, Date> mapND = ND(matrix);
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		for(MatrixItem i : matrix) {
			int qty = 0;
			
			if (mapND.containsKey(i.id)) {
				Date d = mapND.get(i.id);
				long rowid = RemnantsImpl.find(data.id, d);
				
				if (rowid != ExtrasConst.INVALID_ROWID) {
					RemnantsImpl r = new RemnantsImpl();
					r.read(rowid);
					r.close();
					
					for(RemnantItem ri : r.getData().items) {
						if (i.id.equals(ri.id)) {
							qty = ri.qty;
							break;
						}
					}
				}
			}
			
			result.put(i.id, qty);
		}
		
		return result;
	}

	public DebugInt KDV() {
		final List<OrgFolders> folders = new ArrayList<OrgFolders>();
		DataTraveler.travel(OrgFolders.class, new DataTraveler.Travel<OrgFolders>(true) {
			
			@Override
			public boolean travel(DataTraveler<OrgFolders> item) {
				folders.add(item.data);
				return true;
			}
		}, null);
		
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		
		Date today = c.getTime();
				
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		Date start = c.getTime();
		c.add(Calendar.DATE, 30);
		Date finish = c.getTime(); 
		
		c.setTime(start);
		
		while(c.getTimeInMillis() < finish.getTime()) {
			final WeekDay wd = WeekDay.getDayBySystemId(c.get(Calendar.DAY_OF_WEEK));
	
			if (findInFolder(folders, wd))
				break;
			
			c.add(Calendar.DATE, 1);
		}
		
		DebugInt res = new DebugInt();
		res.value = Math.abs((int)DatePeriod.daysDiff(today, c.getTime()));
		StringBuilder sb = new StringBuilder();
		sb.append("<br><b>считаю КДВ</b><br>");
		sb.append("<i>количество днех от ")
			.append(today.toString()).append(" до ")
			.append(c.getTime().toString()).append(" = ").append(res.value).append("</i><br>");
		
		res.debug = sb.toString();
		return res;
	}

	protected boolean  findInFolder(final List<OrgFolders> folders, final WeekDay wd) {
		boolean res = false;
		for(OrgFolders f : folders) {
			if( WeekDay.compare(WeekDay.getWeekDay(f.name), wd) == 0) {
				for(OrgFolderItem oif : f.items)
					if (oif.name.equals(data.id)) {
						res = true;
						break;
					}
			}
		}
		
		return res;
	}

	private Map<String, Integer> TO() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		long rowid = RemnantsImpl.find(data.id, new Date());
		
		if(rowid != -1) {
			RemnantsImpl rem = new RemnantsImpl();
			rem.read(rowid);
			
			for(RemnantItem i : rem.getData().items)
				result.put(i.id, i.qty);
		}
		
		return result;
	}
	
	private Set<String>collectRouteIds(){
		final Set<String> ids = new HashSet<String>();
		
		DataTraveler.travel(OrgFolders.class, new DataTraveler.Travel<OrgFolders>(){

			@Override
			public boolean travel(DataTraveler<OrgFolders> item) {
				for(OrgFolderItem i : item.data.items)
					ids.add(i.name);
				return true;
			}
			
		}, null);
		
		return ids;
	}
	
	private Map<String, Integer> SVR(){
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		DocList docs = DebtDoc.instance().docList(data.id, null, getPeriod());
		
		for(Document<?> d : docs) {
			if (d instanceof DeliveryImpl) {
				DeliveryImpl impl = (DeliveryImpl)d;
				
				for(DeliveryItem i : impl.getData().items) {
					if (result.containsKey(i.id))
						result.put(i.id, result.get(i.id) + i.qty);
					else
						result.put(i.id, i.qty);
				}
			}
		}
		
		return result;
	}

	protected DatePeriod getPeriod() {
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DATE, 1);
		Date finish = c.getTime();
		c.add(Calendar.DATE, -31);
		return new DatePeriod(c.getTime(), finish);
	}
	
	private Map<String, Integer> SVV(){
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		DatePeriod dp = new DatePeriod(Util.getDate(), c.getTime());
		DocList docs = ReturnDoc.instance().docList(data.id, null, dp);
		
		for(Document<?> d : docs) {
			if (d instanceof ReturnImpl) {
				ReturnImpl impl = (ReturnImpl)d;
				
				for(OrderItem i : impl.getData().items) {
					if (result.containsKey(i.id))
						result.put(i.id, result.get(i.id) + i.qty);
					else
						result.put(i.id, i.qty);
				}
			}
		}
		
		return result;
	}
	
	private Map<String, Integer> KR(){
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		DocList docs = DebtDoc.instance().docList(data.id, null, getPeriod());
		
		for(Document<?> d : docs) {
			if (d instanceof DeliveryImpl) {
				DeliveryImpl impl = (DeliveryImpl)d;
				
				Set<String> ids = new HashSet<String>();
				for(DeliveryItem i : impl.getData().items) {
					ids.add(i.id);
				}
				
				for(String id : ids) {
					if (result.containsKey(id))
						result.put(id, result.get(id) + 1);
					else
						result.put(id, 1);
				}
			}
		}
		
		return result;
	}
	
	@Override
	public int getItemColor() {
		return R.color.item_highlight;
	}
}
