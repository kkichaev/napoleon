package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.widget.TextView;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DymovTask;
import com.grsoft.dataobjects.DymovTaskResult;
import com.grsoft.dataobjects.impl.DymovTaskImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DymovTaskDoc extends DateDocType {
	static DymovTaskDoc instance;
	
	public static DymovTaskDoc instance() {
		if( instance == null )
			instance = new DymovTaskDoc();
	
		return instance;
	}
	
	DymovTaskDoc() {
		super("Задачи", "DymovTask", DymovTaskImpl.class);
	}

	@Override
	public int getResurceId() {
		return R.drawable.taskdoc;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.task_doc_title;
	}

	
	@Override
	public void viewClosed(Activity documentsView) {
		TextView dt = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( dt != null )
			dt.setText(R.string.date);
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		TextView dt = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( dt != null )
			dt.setText(R.string.DateTill);
	}
	
	@Override
	public DocList docList(String orgId, String order, DatePeriod selection) {
		return new DymovTaskDocList(orgId, selection);
	}
}

class DymovTaskDocList extends DocList {
	List<DymovTask> docs = new ArrayList<DymovTask>();
	
	public DymovTaskDocList(String orgId, DatePeriod selection) {
		document = new DymovTaskImpl();
		String idWhere = String.format("id = '%s'", orgId);
		String where = idWhere;
		if( selection == null ) {
			Date begin = Util.getDate();
			Date end = new Date(begin.getTime() + 366 * 24 * 36000000l);
			selection = new DatePeriod(begin, end);
		}
		String.format("date >= %s and start <= %s", orgId,
				Long.toString(selection.begin.getTime()), Long.toString(selection.end.getTime()));
		
		final HashMap<String, DymovTaskResult> done = new HashMap<String, DymovTaskResult>();
		DataTraveler.travel(DymovTaskResult.class, new DataTraveler.Travel<DymovTaskResult>() {

			@Override
			public boolean travel(DataTraveler<DymovTaskResult> item) {
				done.put(item.data.idTask, item.data);
				item.data = new DymovTaskResult();
				return true;
			}
		}, idWhere);
		
		DataTraveler.travel(DymovTask.class, new DataTraveler.Travel<DymovTask>() {
			@Override
			public boolean travel(DataTraveler<DymovTask> item) {
				DymovTaskResult dtr = done.get(item.data.idTask);
				if(dtr != null && (item.data.isPeriod == 0 || Util.isToday(dtr.done)))
					return true;
				docs.add(item.data);
				item.data = new DymovTask();
				return true;
			}
		}, where);
		
		where = idWhere + " and done is null or created > done";
		DataTraveler.travel(DymovTaskResult.class, new DataTraveler.Travel<DymovTaskResult>() {

			@Override
			public boolean travel(DataTraveler<DymovTaskResult> item) {
				DymovTask dt = new DymovTask();
				dt.loadFrom(item.data);
				docs.add(dt);
				return true;
			}
		}, where);
		
		Collections.sort(docs);
		ids = new ArrayList<Long>();
		for(long i=0; i<docs.size(); i++)
			ids.add(i);
	}
	
	@Override
	public Document<?> get(int index) {
		if( index >= 0 && index < docs.size()) {
			((DymovTaskImpl)document).setData(docs.get(index));
			return document;
		}
		return super.get(index);
	}
}
