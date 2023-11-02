package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.script.documents.ScriptDoc;
import android.database.Cursor;


public class ScriptDocEx extends ScriptDoc {
	private ArrayList<String> visitWithPhotoToday = new ArrayList<String>();
	
	public static void initialize() { instance = new ScriptDocEx(); }

	@Override
	public boolean isHasCreatedToday(String id) {
		return super.isHasCreatedToday(id) && visitWithPhotoToday.contains(id);
	}
	
	@Override
	protected void updateTodayDocs() {
		super.updateTodayDocs();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date now = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		String selectionField = "created";
		String where = String.format(" %s >= %s and %s <= %s", selectionField, Long.toString(now.getTime()), selectionField, Long.toString(calendar.getTime().getTime()));

		visitWithPhotoToday.clear();

		VisitImpl doc = new VisitImpl();
		if (doc != null) {
			String tableName = DataObjectInfo.getInstance().getTableName(doc.getData().getClass());

			if (tableName != null && DbWriter.isTableExists(tableName)) {
				try {
					Cursor c = DataBaseManager.getDataBase().query(
							tableName, new String[] { "id", "items" }, where, null,
							null, null, null);

					while (c.moveToNext())
						if (c.getBlob(c.getColumnIndex("items")).length > 0)
							visitWithPhotoToday.add(c.getString(c.getColumnIndexOrThrow("id")));

					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
