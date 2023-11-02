package com.grsoft.napoleon.util;

import java.util.Collections;
import java.util.Comparator;

import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import android.content.Context;

public class ImagesAdapter extends ImagesItemsAdapter {
	protected Context context;
	CreatableDocument<? extends Visit> visit;
	
	public ImagesAdapter(Context context, CreatableDocument<? extends Visit> visit) {
		super(context, visit.getData().items);
		this.visit = visit;
	}
	
	@Override
	public void notifyDataSetChanged() {
		data = visit.getData().items;
		
		Collections.sort(data, new Comparator<VisitItem>() {

			@Override
			public int compare(VisitItem lhs, VisitItem rhs) {
				if(rhs.date == null)
					return lhs.date == null ? 0 : -1;
				return (lhs.date == null) ? 1 : rhs.date.compareTo(lhs.date);
			}
		});
		super.notifyDataSetChanged();
	}
}
