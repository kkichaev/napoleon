package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MGpsPos;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import android.annotation.SuppressLint;
import android.content.Context;

public class MapFragmentMapUtil {
	public MapData mapData;
	
	@SuppressLint("SimpleDateFormat")
	private MapData createData(String userid, Date date, int routePointInterval){
		final MapData result = new MapData();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long s = c.getTime().getTime();
		c.add(Calendar.DATE, 1);
		long f = c.getTime().getTime();

		collectGpsPos(userid, result, s, f);
		collectExecuted(userid, result, s, f);
		if(routePointInterval != 0) {
			long ct = 0, diff = routePointInterval * 60 * 1000;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			for(Object o : result.points) {
				MapData.GPSPosMap p = (MapData.GPSPosMap)o;
				if(ct == 0 || p.date.getTime() - ct >= diff) {
					MapData.RoutePoint rp = new MapData.RoutePoint();
					rp.date = p.date;
					rp.latitude = p.latitude;
					rp.longitude = p.longitude;
					rp.idx = result.routePoints.size();
					rp.speed = p.speed;
					rp.title = "время:" + sdf.format(p.date) + "<br/><i>скорость: " + Double.toString(rp.speed + 3.6) + " км/ч</i>";
					result.routePoints.add(rp);
					
					ct = p.date.getTime();
				}
			}
		}
		return result;
	}

	private void collectExecuted(String userid, MapData result, Long start, Long finish) {
		Map<String, MapData.Executed> map = new HashMap<String, MapData.Executed>();

		List<CreateDocDataObject> docs = new ArrayList<CreateDocDataObject>();
		
		for (DocTypeBase dt : DocTypeBase.docTypes)
			 docs.addAll(collectDocs(dt, userid, start, finish));
		
		Collections.sort(docs, new Comparator<CreateDocDataObject>() { @Override public int compare(CreateDocDataObject lhs, CreateDocDataObject rhs) { return lhs.created.compareTo(rhs.created);	}});
		
		int idx = 1;
		
		for(CreateDocDataObject d : docs){
			if (!map.containsKey(d.id) && checkCoord(d)){
				MOrgImpl o = new MOrgImpl();
				o.read("id", d.id);
				
				MapData.Executed e = new MapData.Executed();
				e.idx = idx;
				e.date = d.created;
				e.org = o.getData();
				e.latitude = (double) d.latitude / Consts.GPS_SCALE;
				e.longitude = (double) d.longitude / Consts.GPS_SCALE;
				
				map.put(d.id, e);
						
				idx += 1;
			}
		}
		
		List<MapData.Executed> list = new ArrayList<MapData.Executed>(map.values());
		Collections.sort(list, new Comparator<MapData.Executed>() {	@Override public int compare(MapData.Executed lhs, MapData.Executed rhs) { return lhs.idx - rhs.idx; } });
		
		result.executed.addAll(list);
	}
	
	public List<CreateDocDataObject> collectDocs(DocTypeBase dt, String userid, Long start, Long finish){
		final List<CreateDocDataObject> result = new ArrayList<CreateDocDataObject>();
		Document<?> d = dt.create();
		
		String where = String.format("userid = '%s' and created >= %d and created < %d", userid, start, finish);
		
		if (d instanceof CreatableDocument){
			DataTraveler.travel(d.getData().getClass(), new DataTraveler.Travel<CreateDocDataObject>(true) {
				@Override
				public boolean travel(DataTraveler<CreateDocDataObject> item) {
					result.add(item.data);
					return true;
				}
			}, where);
		}
		
		return result;
	}

	private boolean checkCoord(CreateDocDataObject d) {
		return d.latitude != 0 && d.longitude != 0;
	}

	protected void collectGpsPos(String userid, final MapData result, long start, long finish) {
		String where = String.format("userid = '%s' and date >= %d and date < %d ", userid, start, finish);

		DataTraveler.travel(MGpsPos.class, new DataTraveler.Travel<MGpsPos>(true) {
			@Override
			public boolean travel(DataTraveler<MGpsPos> item) {
				result.points.add(new MapData.GPSPosMap(item.data));
				return true;
			}
		}, where);
	}
	
	public String createHtml(Context context, String userid, Date date, int routePointInterval){
		MapHelper mh = new MapHelper();
		mapData = createData(userid, date, routePointInterval);
		
		CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
		String result = mh.createMap(context, mapData, cfg.maptype);
		
		return result;
	}
}
