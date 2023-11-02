package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.BaseSimpleAdapter;


public class EdRouteAdapter extends BaseSimpleAdapter{
	private Context ctx;
	private OrgFoldersImpl folder; 
	View.OnClickListener commentEdit;
	
	public EdRouteAdapter(Context ctx, OrgFoldersImpl folder, View.OnClickListener commentEdit){
		this.ctx = ctx;
		this.folder = folder;
		this.commentEdit = commentEdit;
	}
	
	@Override
	public int getCount() {	return folder.getData().items.size(); }

	@Override
	public Object getItem(int position) { return folder.getData().items.get(position); }

	@Override
	public long getItemId(int position) { return 0;	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null)
			view = View.inflate(ctx, R.layout.edrouterow, null);
	
		OrgFolderItemEx i = (OrgFolderItemEx) getItem(position);
		OrgImpl org = new OrgImpl();
		org.read("id", i.name);
		Org o = org.getData();
		TextView tv = (TextView) view.findViewById(R.id.tvPos);
		tv.setText(Integer.toString(position + 1));
		
		tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(o.name);
		
		tv = (TextView) view.findViewById(R.id.tvAddress);
		tv.setText(o.address);
		
		tv = (TextView) view.findViewById(R.id.tvComment);
		tv.setText(i.comment);

		View v = view.findViewById(R.id.ivComment);
		v.setTag(i);
		v.setOnClickListener(commentEdit);
		
		return super.getView(position, view, parent);
	}
}
