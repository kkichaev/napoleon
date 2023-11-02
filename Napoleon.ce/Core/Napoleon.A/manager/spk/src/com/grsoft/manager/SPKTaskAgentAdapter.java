package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.SPKTask;
import com.grsoft.napoleon.documents.DocumentUtils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SPKTaskAgentAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public List<SPKTask> data = new ArrayList<SPKTask>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

	public SPKTaskAgentAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.task_agent_row, null);
		
		SPKTask t = (SPKTask) getItem(position);
		
		TextView tv = (TextView) view.findViewById(R.id.tvStart);
		tv.setText(sdf.format(t.start));
		
		tv = (TextView) view.findViewById(R.id.tvFinish);
		tv.setText(sdf.format(t.finish));
		
		tv = (TextView) view.findViewById(R.id.tvTask);
		tv.setText(t.task);
		
		ImageView iv = (ImageView) view.findViewById(R.id.ivStatus);
		iv.setImageResource(t.status == 0 ? R.drawable.btn_check_off : R.drawable.btn_check_on);
		iv.setTag(t);
		iv.setOnClickListener(this);
		
		tv = (TextView) view.findViewById(R.id.tvSended);
		tv.setVisibility(DocumentUtils.isExported(t.params) ? View.VISIBLE : View.INVISIBLE);
		
		return view;
	}
	
	public void reload(String agent, Date start, Date finish) {
		data.clear();
		String where = buildWhere(agent, start, finish);
		
		DataTraveler.travel(SPKTask.class, new DataTraveler.Travel<SPKTask>(true) {

			@Override
			public boolean travel(DataTraveler<SPKTask> item) {
				data.add(item.data);
				return true;
			}}, where);
		
		Collections.sort(data, new Comparator<SPKTask>() {

			@Override
			public int compare(SPKTask lhs, SPKTask rhs) {
				return lhs.start.compareTo(rhs.start);
			}
		});
	}

	private String buildWhere(String agent, Date start, Date finish) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("agentid=\"").append(agent).append("\"");
		sb.append(" and ");
		sb.append("start <= ").append(finish.getTime());
		sb.append(" and ");
		sb.append("finish >= ").append(start.getTime());
		sb.append(" and not (params = 1 and status = 1) ");
		
		return sb.toString();
	}

	@Override
	public void onClick(View v) {
		SPKTask t = (SPKTask) v.getTag();
		
		t.status = t.status == 0 ? 1 : 0;
		t.params = 0;
		DbWriter w = new DbWriter();
		w.updateRecord(t, t.created.getTime());
		w.close();
		
		notifyDataSetChanged();
	}

}
