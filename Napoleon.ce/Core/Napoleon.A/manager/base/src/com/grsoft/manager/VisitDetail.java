package com.grsoft.manager;

import com.grsoft.dataobjects.VisitInfo;
import com.grsoft.dataobjects.VisitPreviewItem;
import com.grsoft.dataobjects.impl.MVisitImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class VisitDetail extends FragmentActivity{
	public static Class<? extends VisitDetail> activity = VisitDetail.class;
	
	private TextView tvRemark;
	private ImageButton btnSync;
	private GridView gridView;
	
	private MVisitImpl doc;

	private VDAdapter adapter;
	
	public static void open(Context context, Document<?> doc){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		
		context.startActivity(intent);
	}
	
	protected int getLayoutID(){ return R.layout.visitdetail; }
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(getLayoutID());
		initView();
		
		if(initDoc(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID)))
			init();
	}

	private boolean initDoc(long rowid) {
		doc = new MVisitImpl();
		return rowid != ExtrasConst.INVALID_ID && doc.read(rowid);
	}

	private void initView() {
		setTitle(R.string.visit_doc_title);
		tvRemark = (TextView) findViewById(R.id.tvRemark);
		btnSync = (ImageButton) findViewById(R.id.btnSync);
		gridView = (GridView) findViewById(R.id.gridView);
	} 

	private void init() {
		tvRemark.setText(doc.getData().remark);
		btnSync.setOnClickListener(createSyncClick());
		initAdapter();
		gridView.setOnItemClickListener(createGridClick());
		sync();
	}

	private OnItemClickListener createGridClick() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> aview, View arg1, int pos, long arg3) {
				VDAdapter adapter = (VDAdapter) aview.getAdapter();
				openInGallery((VisitPreviewItem) adapter.getItem(pos));
			}};
	}

	public void initAdapter() {
		adapter = (VDAdapter) createAdapter();
		adapter.load(doc);
		gridView.setAdapter(adapter);
	}

	private ListAdapter createAdapter() { return new VDAdapter(this, calcPicSize()); }

	private OnClickListener createSyncClick() {
		return new OnClickListener() { @Override public void onClick(View v) { sync(); } };
	}

	protected void sync() {
		VisitInfo visit = doc.getData();
		SyncPhoto.sync(this, createUpdateHndl(), visit.userid, visit.created);
	}

	public UpdateCtrl createUpdateHndl() {
		return new UpdateCtrl() {
			@Override public void updateCtrl(boolean enabled) { btnSync.setEnabled(enabled);	}
			@Override public void onFinish(boolean result) {
				if( result ) {
					doc.read(doc.getRowid(), false);
					adapter.load(doc); 
					adapter.notifyDataSetChanged();
			}}
		};
	}
	
	private int calcPicSize() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        final int SPACE = 20;
        int col_cnt = 3;
        col_cnt = col_cnt == -1 ? 3 : col_cnt;
        return (displaymetrics.widthPixels - SPACE) / col_cnt; 
	}
	
	public void openInGallery(VisitPreviewItem p) {
		com.grsoft.napoleon.util.Config cfg = ConfigManager.getConfig();
		new LoadPicture(this).execute(cfg.hrefBase() + p.name);
	}
}

class VDAdapter extends BaseAdapter{
	Context context;
	MVisitImpl doc;
	int picsz;
	
	public VDAdapter(Context context, int picsz) {
		this.context = context;
		this.picsz = picsz;
	}

	public void load(MVisitImpl doc) {
		this.doc = doc;
	}
	
	@Override public int getCount() {	return doc.getData().items.size(); }
	
	@Override public Object getItem(int position) { return doc.getData().items.get(position); }
	
	@Override public long getItemId(int position) { return 0; }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null )
			convertView = View.inflate(context, R.layout.visitdetailitem, null);

		VisitPreviewItem item = (VisitPreviewItem) getItem(position);
		int sz = (int) context.getResources().getDimension(R.dimen.story_tape_pic_size);
		Drawable pic = BitmapUtils.createBitmap(context, item.smallPhoto, sz, sz);
		
		ImageView tv = (ImageView)convertView.findViewById(R.id.ivPic);
		tv.setImageDrawable(pic);
		
		return convertView;
	}
}
