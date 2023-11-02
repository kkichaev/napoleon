package com.grsoft.napoleon;

import java.util.Calendar;

import com.grsoft.dataobjects.impl.TaskRemarkImpl;

import android.widget.BaseAdapter;
import android.widget.ListView;

public class TaskDocListEx extends TaskDocList {

	protected void saveTaskRemark(String remark) {
		TaskRemarkImpl rem = new TaskRemarkImpl();

		rem.getData().taskid = editRemark.id;
		
		if (remark.trim().length() > 0 ) {
			rem.getData().params = 0;
			rem.getData().remark = remark;
			rem.getData().date = Calendar.getInstance().getTime();
			
			rem.write();
		}else {
			rem.read();
			rem.delete();
		}
		
		rem.close();
		
		((BaseAdapter)((ListView)findViewById(R.id.lvDocs)).getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public String getRemark(String idtask) {
		String result = "";
		TaskRemarkImpl rem = new TaskRemarkImpl();
			
		if (rem.read("taskid", idtask))
			result = rem.getData().remark;
		
		rem.close();
		
		
		return result;
	}
}	
