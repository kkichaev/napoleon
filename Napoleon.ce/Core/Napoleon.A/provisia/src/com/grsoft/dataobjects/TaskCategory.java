package com.grsoft.dataobjects;

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.R;

@TableInfo(name="taskCat")
public class TaskCategory extends DataObject {
	public static final String ALL_CATEGORY = "<все>";
	
	public String name;
	
	@Override
	public String toString() {
		return name;
	}
	
	public static void loadSpinner(Spinner sp, boolean addAll, String selected) {
		int sel = -1;
		ArrayList<TaskCategory> values = new ArrayList<TaskCategory>();
		String table = DataObjectInfo.getInstance().getTableName(TaskCategory.class);
		TaskCategory tc = new TaskCategory();
		
		if( addAll ) {
			tc.name = ALL_CATEGORY;
			values.add(tc);
			tc = new TaskCategory();
		}
		
		DbReader r = new DbReader();
		boolean bdo = r.select(tc, table, null, "name");
		while( bdo ) {
			if(selected != null && tc.name.equals(selected))
				sel = values.size();
			values.add(tc);
			tc = new TaskCategory();
			bdo = r.selectNext(tc);
		}
		
		ArrayAdapter<TaskCategory> aa = new ArrayAdapter<TaskCategory>(sp.getContext(), R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( sel >= 0)
			sp.setSelection(sel);
	}
}
