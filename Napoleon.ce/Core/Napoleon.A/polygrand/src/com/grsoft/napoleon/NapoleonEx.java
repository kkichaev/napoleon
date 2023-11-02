package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.util.Log;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ManagerRoute;
import com.grsoft.dataobjects.ManagerRouteItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ManagerRouteImpl;
import com.grsoft.napoleon.util.OrgFoldersCmp;
import com.grsoft.napoleon.util.OrgFoldersTree;
import com.grsoft.napoleon.util.WeekDay;

public class NapoleonEx extends Napoleon {

	private Map<String, OrgFolders> managerRoute = new HashMap<String, OrgFolders>();

	@Override
	protected void onResume() {
		super.onResume();

		String inwokr = ((NapoleonApp) getApplication()).getInWork();

		if (inwokr.length() > 0) {
			Org org = new Org();
			org.id = inwokr;
			DocumentsEx.open(this, org);
		}
	}

	protected void updateManagerRoute() {
		DbWriter.checkDBTable(ManagerRoute.class);
		managerRoute.clear();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date start = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date finish = calendar.getTime();

		Log.d(getClass().toString(), start + " " + finish);
		StringBuilder sb = new StringBuilder();
		sb.append("date >=").append(start.getTime()).append(" and date<").append(finish.getTime());
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(ManagerRoute.class), sb.toString(), null);

		ManagerRouteImpl mr = new ManagerRouteImpl();

		for (Long rowid : ids) {
			mr.read(rowid);
			mr.close();

			calendar.setTime(mr.getData().date);
			int dw = calendar.get(Calendar.DAY_OF_WEEK);
			WeekDay wd = WeekDay.getDayBySystemId(dw);

			OrgFolders of = new OrgFolders();
			of.name = wd.getCaption();

			for (ManagerRouteItem i : mr.getData().items) {
				OrgFolderItem ofi = new OrgFolderItem();
				ofi.name = i.id;
				ofi.pos = i.pos;

				of.items.add(ofi);
			}

			managerRoute.put(of.name, of);
		}
	}

	@Override
	protected OrgFoldersAdapter getOrgFoldersAdapter() {
		return new OrgFoldersAdapter() {

			@Override
			protected OrgFoldersTree createOrgFoldersTree() {
				return new OrgFoldersTree() {

					@Override
					protected void loadData(final Date onDate) {
						int weekIndex = GetWeekIndex(onDate);
						
						updateManagerRoute();
						orgFolders.clear();

						DbWriter.checkDBTable(DbObject.getDataType(OrgFolders.class));
						String table = DataObjectInfo.getInstance().getTableName(OrgFolders.class);
						DbReader r = new DbReader();
						OrgFolders of = new OrgFolders();
						boolean bdo = r.select(of, table, null);
						while (bdo) {
							if (of.name.length() > 0) {
								char sym = of.name.charAt(0);
								if (Character.isDigit(sym)) {
									// случай если нет начала цикла, пусть будет
									// первая неделя
									if (weekIndex == 0)
										weekIndex = 1;
									int cw = Character.digit(sym, 10);
									if (weekIndex != cw) {
										bdo = r.selectNext(of);
										continue;
									}

									// remove week index
									of.name = of.name.substring(1);
								}
							} else
								of.name = "";

							// if (Features.SQL_ORG_ROUTE)
							Collections.sort(of.items, new Comparator<OrgFolderItem>() {

								@Override
								public int compare(OrgFolderItem lhs, OrgFolderItem rhs) {
									return lhs.pos - rhs.pos;
								}

							});

							if (managerRoute.containsKey(of.name)) {
								orgFolders.add(managerRoute.get(of.name));
								managerRoute.remove(managerRoute.get(of.name));
							} else
								orgFolders.add(of);

							of = new OrgFolders();
							bdo = r.selectNext(of);
						}

						r.close();

						Iterator<OrgFolders> iter = managerRoute.values().iterator();

						while (iter.hasNext())
							orgFolders.add(iter.next());

						Collections.sort(orgFolders, new OrgFoldersCmp());
					}
				};
			}
		};

	}
}
