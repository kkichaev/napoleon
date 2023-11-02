/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Изменяет количество строк в списке 
 *
 * kki   29/01/2011   creating
 */
package com.grsoft.util;

import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.view.BaseActivity;

public class LinesOnClickListener extends OnClickListenerToNotify implements OnLongClickListener 
{
	public static final String PREF_NAME = "LineInRow";
	public static final int VARIABLE_LINE_HEIGHT = -1;
	
	LinesController controller;
	
	public LinesOnClickListener(ListView listView, ImageView imageView, BaseActivity activity)
	{
		this.controller = new LinesController(listView, imageView, activity);
		imageView.setOnClickListener(this);
	}
	
	public LinesOnClickListener(ListView listView, ImageView imageView, BaseActivity activity, boolean useLongClick)
	{
		this.controller = new LinesController(listView, imageView, activity);
		imageView.setOnClickListener(this);
		if(useLongClick)
			imageView.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View v) {
		controller.setVariable();
		controller.onChanged();
		return true;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		controller.circle();
		controller.onChanged();
	}
	
	public LinesCountController getController()
	{
		return controller;
	}

	class LinesController implements LinesCountController
	{
		private BaseActivity activity;
		private ListView listView;
		private ImageView imageView;
		private int linesInRow;
		int minLines;
		
		public LinesController(ListView listView, ImageView imageView, BaseActivity activity)
		{
			this.listView = listView;
			this.imageView = imageView;
			this.activity = activity;
			this.minLines = 1;
			
			linesInRow = activity.getPrefValue(PREF_NAME, 2);
			refreshImage();
		}

		@Override
		public void setMinLines(int ml) {
			minLines = ml;
			if(linesInRow < minLines)
				linesInRow = minLines;
		}
		
		@Override 
		public void setVariable() {
			if( linesInRow != VARIABLE_LINE_HEIGHT )
				linesInRow = VARIABLE_LINE_HEIGHT;
			else
				linesInRow = minLines;
			SaveChanges();
			onChanged();
		}
		
		@Override
		public boolean isVariable() {
			return ( linesInRow == VARIABLE_LINE_HEIGHT );
		}

		private void SaveChanges()
		{
			activity.setPrefValue(PREF_NAME, linesInRow);
		}
		
		public void prepareTextView(TextView textView) {
			if( linesInRow == VARIABLE_LINE_HEIGHT ) {
				textView.setLines(1);
				textView.setMaxLines(Integer.MAX_VALUE);
				textView.setEllipsize(null);
				textView.setHorizontallyScrolling(false);
			} else {
				textView.setLines(linesInRow);
				textView.setEllipsize((linesInRow == 1) ? TruncateAt.END : null);
				textView.setHorizontallyScrolling((linesInRow == 1));
			}
		}
		
		public void circle() {
			if( linesInRow == VARIABLE_LINE_HEIGHT )
				return;
			linesInRow ++;
			if( linesInRow > Features.LINES_LIMIT )
				linesInRow = minLines;
			SaveChanges();
		}
		
		public void refreshImage() {
			if( linesInRow == VARIABLE_LINE_HEIGHT ) {
				imageView.setImageResource(R.drawable.line_x);
				return;
			}
			int[] linesImagesSources = {R.drawable.line_1, R.drawable.line_2, R.drawable.line_3, R.drawable.line_4};
			int pos = (linesInRow == Features.LINES_LIMIT) ? 0 : linesInRow;
			if( pos >= linesImagesSources.length )
				pos = 0;
			imageView.setImageResource(linesImagesSources[pos]);
		}

		public void onChanged() {
			refreshImage();
			BaseAdapter adapter = (BaseAdapter)listView.getAdapter();
			
			if(adapter != null)
				adapter.notifyDataSetChanged();
		}

		public boolean isMinLines() {
			return linesInRow == 1;
		}
//
//		public int getLinesCount()
//		{
//			return linesInRow;
//		}

		@Override
		public void setLinesCount(int value) {
			if(value < 0 || value >= minLines )
				linesInRow = value;

			refreshImage();
		}
	}
}
