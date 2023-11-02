package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.PicStore;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.util.BitmapUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestImage extends QuestControl implements OnClickListener, OnLongClickListener {
	private static final String PREF = "QuestImage.prefrences";
	private static final String COUNTER = "counter";
	private ViewGroup preview;
	private Context context;
	private List<String> list = new ArrayList<String>();
	private View btnPhoto;
	
	public QuestImage(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();
		
		for(String id : list) {
			AnswerItem a = createAnwerItem();
			a.answer = id;
			
			result.add(a);
		}
		
		return result;
	}

	@Override
	public void setValue(List<AnswerItem> value) {
		list.clear();
		
		for(AnswerItem a : value)
			list.add(a.answer);
		
		initPreview(context);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getContext() instanceof QuestPhoto)
			((QuestPhoto)v.getContext()).doPhoto(this);
	}

	public void addImage(Context context, String id) {
		list.add(id);
		initPreview(context);
	}
	
	public void delImage(Context context, String id) {
		list.remove(id);
		initPreview(context);
	}
	
	protected void initPreview(Context context) {
		int w = (int) context.getResources().getDimension(R.dimen.previewPhotoWidth);
		int h = (int) context.getResources().getDimension(R.dimen.previewPhotoHight);
		
		int space = (int) context.getResources().getDimension(R.dimen.previewPhotoSpace);
		
		preview.removeAllViews();
		
		DbReader db = new DbReader();
		
		for(int i = 0; i < list.size(); i++){
			String id  = list.get(i);
			
			PicStore picStore = new PicStore();
			DbWriter.checkDBTable(picStore.getClass());
			
			if (db.select(picStore, DataObjectInfo.getInstance().getTableName(picStore.getClass()), 
					String.format("id='%s'", id))){
				String p = new String(picStore.picture);
				TextView t = new TextView(context);
				t.setCompoundDrawablesWithIntrinsicBounds(null, BitmapUtils.createBitmap(context, p, w, h), null, null);
				t.setOnLongClickListener(this);
				t.setTag(id);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 0, space, 0);
				lp.gravity = Gravity.CENTER_VERTICAL;
				t.setLayoutParams(lp);
				preview.addView(t);
			}
		}
		
		db.close();
//		btnPhoto.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
		
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getContext() instanceof QuestPhoto)
			((QuestPhoto)v.getContext()).longClick(this, v.getTag().toString());
		
		return true;
	}

	@Override
	void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		this.context = context;
		View view  = View.inflate(context, R.layout.quest_image_layout, null);
		btnPhoto = view.findViewById(R.id.btnPhoto);
		preview = (ViewGroup) view.findViewById(R.id.preview);
		btnPhoto.setOnClickListener(this);
		container.addView(view);
	}
}
