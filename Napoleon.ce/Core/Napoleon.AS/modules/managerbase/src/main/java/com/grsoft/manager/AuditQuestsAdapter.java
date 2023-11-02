package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MAnswer;
import com.grsoft.dataobjects.impl.MOrgImpl;
import com.grsoft.dataobjects.impl.ManagerAgentImpl;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.util.Util;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AuditQuestsAdapter extends BaseAdapter {
	private List<MAnswer> data = new ArrayList<MAnswer>();
	private Context context;
	private String questid;
	private ManagerAgentImpl agent = new ManagerAgentImpl();
	private MOrgImpl org = new MOrgImpl();
	
	public AuditQuestsAdapter(Context context, String questid) {
		this.context = context;
		this.questid = questid;
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
			view = View.inflate(context, R.layout.auditquest_row, null);
		
		MAnswer a = (MAnswer) getItem(position);
		
		TextView tv = (TextView) view.findViewById(R.id.tvAgent);
		
		if (agent.read("id", a.agentid))
			tv.setText(agent.getData().name);
		else
			tv.setText("");
		
		String on = "";
		String oa = "";
		
		if (org.read("id", a.id)) {
			on = org.getData().name;
			oa = org.getData().address;
		}
		
		tv = (TextView) view.findViewById(R.id.tvOrgName);
		
		if ((on + oa).length() > 0)
			tv.setText(Html.fromHtml("<b>" + on + "</b><br><i>" + oa + "</i>"));
		else
			tv.setText("");
		
		tv = (TextView) view.findViewById(R.id.tvDate);
		tv.setText(Util.simpleDateFormat.format(a.created));
		
		ImageView iv = (ImageView) view.findViewById(R.id.ivSended);
		iv.setImageResource(DocumentUtils.isExported(a.params) ? R.drawable.btn_check_on : R.drawable.btn_check_off);
		
		return view;
	}

	public void reload() {
		data.clear();
		String where = String.format("question = \"%s\"", questid);
		DataTraveler.travel(MAnswer.class, new DataTraveler.Travel<MAnswer>(true) {

			@Override
			public boolean travel(DataTraveler<MAnswer> item) {
				data.add(item.data);
				return true;
			}}, where);
		
		Collections.sort(data, new Comparator<MAnswer>() {

			@Override
			public int compare(MAnswer lhs, MAnswer rhs) {
				return rhs.created.compareTo(lhs.created);
			}
		});
	}
}
