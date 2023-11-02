package com.grsoft.ads.view;

import com.grsoft.ads.R;
import com.grsoft.napoleon.dataobjects.TaskQuery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TaskView extends View {
	private Paint backgroud = new Paint();
	private Paint edge = new Paint();
	private TextPaint textPaint = new TextPaint();
	private TaskQuery data;
	private StringBuilder str = new StringBuilder();
	private int left = 0;
	private int right = 0;
	private int top = 0;
	private int bottom = 0;
	
	public void setData(TaskQuery data){ this.data = data; }
	
	public TaskQuery getData(){ return data;	}
	
	public TaskView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init() {
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(getContext().getResources().getDimension(R.dimen.taskFontSize));
		textPaint.setColor(getContext().getResources().getColor(R.color.black));
		setPadding(3, 3, 3, 3);
		edge.setStyle(Style.STROKE);
		edge.setColor(getContext().getResources().getColor(R.color.black));
		edge.setStrokeWidth(2);
	}

	public TaskView(Context context) {
		super(context);
		init();
	}

	private RectF bound = new RectF();
	private int bkgColor;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		backgroud.setColor(bkgColor);
		bound.set(0, 0,getWidth(), getHeight());
		canvas.drawRoundRect(bound, 5, 5, backgroud);
		bound.set(1, 1, getWidth() - 1, getHeight() - 1);
		canvas.drawRoundRect(bound, 5, 5, edge);
		
		str.setLength(0);
		str.append(data.text);
		StaticLayout sl = new StaticLayout(str, textPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
		canvas.translate(5, 2);
		sl.draw(canvas);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getWidth(), getHeight());
	}

	@Override
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);
		left = l;
		right = r;
		top = t;
		bottom = b;
	}
	
	
	public Rect getBound() {
		Rect result = new Rect();
		result.top = top;
		result.bottom = bottom;
		result.left = left;
		result.right = right;
		
		return result;
	}
	
	public int getRightPos(){ return right; }

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		return ((TimeLine)getParent()).onTouchEvent(event);
//	}
//	
//	public int getColor(Context context){
//		switch(solution){
//		case TaskAnswer.RESOLVED:
//			return context.getResources().getColor(R.color.task_resolved);
//		case TaskAnswer.REJECTED:
//			return context.getResources().getColor(R.color.task_rejected);
//		default: 
//			return context.getResources().getColor(R.color.task_new);
//		}
//	}

	public boolean checkid(String id) {
		return data != null && data.id.equals(id);
	}

	public void setBkgColor(int bkgColor) {	this.bkgColor = bkgColor; }

//	public void update(TaskAnswer data2) {
//		solution = data2.solution;
//	}
}
