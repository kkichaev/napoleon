package com.grsoft.napoleon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FilterCmp;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrgFoldersTree {
	public ArrayList<OrgFolders> orgFolders = new ArrayList<OrgFolders>();
	public OrgFolders currentOrgFolder = null;
	private OrgSumImpl os = new OrgSumImpl();
	private OrgImpl org = new OrgImpl();
	protected ArrayList<OrgFolderItem> filteredArray;
	protected List<String> orgids = new ArrayList<String>();
	
	public interface SheduleStartResolver {
		// return 0 - no shedule starrt
		long getSheduleStart();
	}
	
	public OrgFoldersTree() {
		resetFilter();
	}

	protected void collectValidOrgid() {
		orgids.clear();
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {
			@Override
			public boolean travel(DataTraveler<Org> item) {
				boolean result = true;
					orgids.add(item.data.id);
					item.data = new Org();
				return result;
			}
		}, getValidWhere());
	}

	protected String getValidWhere() { return null; }

	public void close() {
		os.close();
		org.close();
	}

	static final String SHEDULE_START_KEY = "SheduleStart";
	public static SheduleStartResolver SheduleResolver = new SheduleStartResolver() {
		
		@Override
		public long getSheduleStart() {
			long stTime = 0;
			ConfigImpl ci = new ConfigImpl();

			ci.getData().key = SHEDULE_START_KEY;
			ci.checkDBTable();
			if (ci.read()) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				try {
					Date stDate = df.parse(ci.getData().value);
					Calendar c = Calendar.getInstance(Locale.getDefault());
					c.setTime(stDate);
					while(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
						c.add(Calendar.DAY_OF_YEAR, -1);
					stTime = c.getTime().getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			ci.close();
			return stTime;
		}
	};
	
	

//	private int GetWeekIndex() {
//		return GetWeekIndex(new Date());
//	}

	String weekIndexTrace = "";
	public String getWeekIndexTrace() { return weekIndexTrace; }
	
	private int GetWeekIndex(Date date) {
		int wi = 0;
		
		String dateTrace = "";
		
		long stTime = SheduleResolver.getSheduleStart();
		if (stTime != 0) {
			long cTime = date.getTime();
			if(Features.TRACE_WEEK_INDEX) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
				dateTrace += "<br/>str: <b>" + sdf.format(new Date(stTime)) + "</b> <i>" + Long.toString(stTime) + 
						"</i><br/>cur: <b>" + sdf.format(date) + "</b> <i>" + Long.toString(cTime) + "</i>" ; 
			}
			if (cTime > stTime) {
				final long week = 1000 * 3600 * 24 * 7;
				long diff = cTime - stTime;
				if (diff >= week)
					wi = (int) ((diff / week) % 4 + 1);
				else
					wi = 1;
			}
		}
		
		weekIndexTrace = String.format("wi = %d%s", wi, dateTrace);
		return wi;
	}

//	protected void loadData() {
//		loadData(GetWeekIndex());
//	}
	
	protected void prepareFolder(OrgFolders of) {
		if(of.name.length() > 0 && Character.isDigit(of.name.charAt(0))) {
			// remove week index
			of.name = of.name.substring(1);
		}
	}
	
	protected void loadData(final Date onDate){
		orgFolders.clear();
		org.close();
		
		if(Features.ROUTE_HISTORY) {
			DataTraveler.travel(OrgFolders.class, new DataTraveler.Travel<OrgFolders>(true) {

				@Override
				public boolean travel(DataTraveler<OrgFolders> item) {
					if(item.data.IsActive(onDate)) {						
						List<OrgFolderItem> liveItems = new ArrayList<OrgFolderItem>();						
						for(OrgFolderItem i : item.data.items)
							if(orgids.contains(i.name))
								liveItems.add(i);
						
						Collections.sort(liveItems, new Comparator<OrgFolderItem>() {
							@Override public int compare(OrgFolderItem lhs, OrgFolderItem rhs) { return lhs.pos - rhs.pos; }
						});
						item.data.items = liveItems;
						orgFolders.add(item.data);
					}
					return true;
				}
			}, "");
		} else {
			int weekIndex = GetWeekIndex(onDate);
			
			DbWriter.checkDBTable(DbObject.getDataType(OrgFolders.class));
			String table = DataObjectInfo.getInstance().getTableName(OrgFolders.class);
			DbReader r = new DbReader();
			OrgFolders of = new OrgFolders();
			boolean bdo = r.select(of, table, null);
			while (bdo) {
				if (of.name.length() > 0) {
					char sym = of.name.charAt(0);
					if (Character.isDigit(sym)) {
						// случай если нет начала цикла, пусть будет первая неделя
						if (weekIndex == 0)
							weekIndex = 1;
						int cw = Character.digit(sym, 10);
						if (weekIndex != cw) {
							bdo = r.selectNext(of);
							continue;
						}
	
					}
				} else
					of.name = "";
	
				prepareFolder(of);
				// if (Features.SQL_ORG_ROUTE)
				Collections.sort(of.items, new Comparator<OrgFolderItem>() {
	
					@Override
					public int compare(OrgFolderItem lhs, OrgFolderItem rhs) {
						return lhs.pos - rhs.pos;
					}
	
				});
				
				List<OrgFolderItem> liveItems = new ArrayList<OrgFolderItem>();
				
				for(OrgFolderItem i : of.items)
					if(orgids.contains(i.name))
						liveItems.add(i);
				
				of.items = liveItems;
				orgFolders.add(of);
	
				of = new OrgFolders();
				bdo = r.selectNext(of);
			}
			
			r.close();
		}
		Collections.sort(orgFolders, new OrgFoldersCmp());
	}

	public void resetFilter() {
		filteredArray = null;
		collectValidOrgid();
		loadData(new Date());
	}
	
	public void makeTree(Date date){ loadData(date); }

	public int getCount() {
		if (filteredArray != null)
			return filteredArray.size();
		else if (currentOrgFolder == null)
			return orgFolders.size();
		else if (currentOrgFolder.items != null)
			return currentOrgFolder.items.size();
		else
			return 0;
	}
	
	public void setFrom(OrgFoldersTree src) {
		if(src != null && src.currentOrgFolder != null) {
			for(OrgFolders of : orgFolders) {
				if(of.name.equals(src.currentOrgFolder.name)) {
					currentOrgFolder = of;
					break;
				}
			}
		}
	}

	public boolean isFiltered() {
		return filteredArray != null && filteredArray.size() > 0;
	}

	public Object getItem(int pos) {
		if (pos < 0)
			return null;
		else if (filteredArray != null && pos < filteredArray.size())
			return filteredArray.get(pos);
		else if (currentOrgFolder == null && pos < orgFolders.size())
			return orgFolders.get(pos);
		else if (currentOrgFolder != null && currentOrgFolder.items != null && pos < currentOrgFolder.items.size())
			return currentOrgFolder.items.get(pos);
		else
			return null;
	}

	public boolean isToday(int pos) {
		Object dataObject = getItem(pos);
		if (dataObject instanceof OrgFolders) {
			return (WeekDay.compare(WeekDay.getWeekDay(((OrgFolders) dataObject).name), WeekDay.today()) == 0);
		}

		return false;
	}

	public String getFirstColumnText(int pos) {
		Object dataObject = getItem(pos);

		if (dataObject instanceof OrgFolders)
			return ((OrgFolders) dataObject).name;
		else if (dataObject instanceof OrgFolderItem) {
			org.getData().id = ((OrgFolderItem) dataObject).name;
			if (org.read())
				return org.getData().name;
		}

		return "";
	}

	public String getSecondColumnText(int pos) {
		Object dataObject = getItem(pos);
		long sum = 0;

		if (dataObject instanceof OrgFolders) {
			OrgFolders orgFoldersImpl = (OrgFolders) dataObject;

			for (OrgFolderItem item : orgFoldersImpl.items)
				sum += getOrgSum(os, item);
		} else if (dataObject instanceof OrgFolderItem)
			sum = getOrgSum(os, (OrgFolderItem) dataObject);

		return Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
	}

	public boolean isItemInStopList(int pos) {
		Object dataObject = getItem(pos);

		if (dataObject instanceof OrgFolderItem) {
			org.getData().id = ((OrgFolderItem) dataObject).name;
			if (org.read())
				return org.getData().isStopList();
		}

		return false;
	}

	private long getOrgSum(OrgSumImpl os, OrgFolderItem folderItem) {
		try {
			long result = 0;

			os.getData().id = folderItem.name;
			os.getData().type = DocType.getCurDoc().getName();

			if (os.read())
				result = os.getData().sum;

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public OrgImpl getOrg(int pos) {
		Object dataObject = getItem(pos);
		if (dataObject instanceof OrgFolderItem) {
			org.getData().id = ((OrgFolderItem) dataObject).name;
			if (org.read())
				return org;
		}
		return null;
	}

	public Object makeTag(int pos) {
		Object dataObject = getItem(pos);

		if (dataObject instanceof OrgFolders)
			return dataObject;
		else if (dataObject instanceof OrgFolderItem) {
			org.getData().id = ((OrgFolderItem) dataObject).name;
			if (org.read())
				return org.getRowid();
		}

		return null;
	}

	public void applyFilter(FilterCmp filter, String value) {
		if (value.length() == 0) {
			if (filteredArray != null)
				filteredArray = null;
			return;
		}

		if (filteredArray == null)
			filteredArray = new ArrayList<OrgFolderItem>();
		else
			filteredArray.clear();

		if (currentOrgFolder == null)
			for (int i = 0; i < orgFolders.size(); i++)
				processItems(filter, orgFolders.get(i).items, value);
		else
			processItems(filter, currentOrgFolder.items, value);
	}

	protected void processItems(FilterCmp filter, List<OrgFolderItem> list, String value) {
		for (int y = 0; y < list.size(); y++) {
			OrgFolderItem item = list.get(y);
			if (filter.compareTo(item, value))
				if (!isContain(item))
					filteredArray.add(item);
		}
	}

	private boolean isContain(OrgFolderItem item) {
		if (filteredArray == null)
			return false;

		for (OrgFolderItem ofi : filteredArray)
			if (ofi.name.equals(item.name))
				return true;

		return false;
	}

	public int getTextColor(int pos) {
		Object dataObject = getItem(pos);

		if (dataObject instanceof OrgFolderItem) {
			org.getData().id = ((OrgFolderItem) dataObject).name;
			if (org.read())
				return org.getData().color;
		}

		return 0;
	}

	public List<OrgFolderItem> getTodayItems() {
		List<OrgFolderItem> ret = new ArrayList<OrgFolderItem>();

		WeekDay td = WeekDay.today();
		for(int i=0; i<orgFolders.size(); i++) {
			OrgFolders of = orgFolders.get(i);
			if (WeekDay.compare(WeekDay.getWeekDay(of.name), td) == 0) {
				ret.addAll(of.items);
				break;
			}
		}
		return ret;
	}
}
