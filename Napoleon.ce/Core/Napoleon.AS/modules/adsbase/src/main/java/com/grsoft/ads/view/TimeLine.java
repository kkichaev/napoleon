package com.grsoft.ads.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.grsoft.ads.R;
import com.grsoft.ads.utils.Time;
import com.grsoft.ads.utils.TimeRange;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class TimeLine extends ListView{
	private float scaleFactor = 1.f;
	private Paint background = new Paint();
	private Paint hourPaint = new Paint();
	private Paint minPaint = new Paint();
	private Paint timeBgkPaint = new Paint();
	private Paint timeBgkBorder = new Paint();
	private Paint hourLinePaint = new Paint();
	private Paint minuteLinePaint = new Paint();
	private final int TIMEBGKCOLOR = Color.rgb(239, 206, 209);
	private final int TIMBGKALPHA = 60;
	private static final int HOUR_IN_DAY = 24;
	private boolean drawTimeRect = false;
	private float hourStep;
	private float fontSize;
	private float minutMargin;
	private float lineHourLeftMargin;
	private float leftMargin;
	private static final String hours[] = new String[] {
			"01","02","03","04","05","06","07","08","09","10",
			"11","12","13","14","15","16","17","18","19","20",
			"21","22","23","24"}; 
	
	private static final String MINUTSVALUE = "00";
	
	public TimeLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		hourStep = getResources().getDimension(R.dimen.hourstep);
		fontSize = getResources().getDimension(R.dimen.hourfsz);
		minutMargin = getResources().getDimension(R.dimen.minutmargin);
		lineHourLeftMargin = getResources().getDimension(R.dimen.linehourleftmargin);
		leftMargin = getResources().getDimension(R.dimen.leftmargin);
		
		background.setColor(Color.WHITE);
		hourPaint.setColor(Color.BLACK);
		hourPaint.setTextSize(fontSize);
		minPaint.setColor(Color.BLACK);
		minPaint.setTextSize(getResources().getDimension(R.dimen.minfsz));
		timeBgkPaint.setColor(TIMEBGKCOLOR);
		timeBgkPaint.setAlpha(TIMBGKALPHA);
		timeBgkBorder.setStyle(Style.STROKE);
		timeBgkBorder.setColor(TIMEBGKCOLOR);
		final int BORDERWIDTH = 2;
		timeBgkBorder.setStrokeWidth(BORDERWIDTH);
		minuteLinePaint.setStrokeWidth(1);
		minuteLinePaint.setStyle(Style.STROKE);
		minuteLinePaint.setColor(Color.GRAY);
		hourLinePaint.setStrokeWidth(1);
		hourLinePaint.setStyle(Style.STROKE);
		hourLinePaint.setColor(Color.BLACK);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		for (int i = 0; i < this.getChildCount(); i++) {
			View child = this.getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
		}

		int width = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, (int) ((HOUR_IN_DAY * hourStep)
				* scaleFactor + hourStep));
	}

	public int getOffsetForNow() {
		Calendar cal = Calendar.getInstance();
		Time t = new Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
		return getYPos(t);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		final int WIDTH = getWidth();
		
		canvas.drawRect(new Rect(0, 0, WIDTH, getHeight()), background);
		
		if (drawTimeRect) {
			final float RIGHT_MARGIN = (int) getResources().getDimension(R.dimen.timerightmargin);
			final float LEFT_MARGIN = (int) getResources().getDimension(R.dimen.timeleftmargin);
			int ypos = getOffsetForNow();

			canvas.drawRoundRect(new RectF(LEFT_MARGIN, 0, getWidth()- RIGHT_MARGIN, ypos), RIGHT_MARGIN, RIGHT_MARGIN,	timeBgkPaint);
			canvas.drawRoundRect(new RectF(LEFT_MARGIN, 0, getWidth()- RIGHT_MARGIN, ypos), RIGHT_MARGIN, RIGHT_MARGIN,	timeBgkBorder);
		}

		float scaleStep = hourStep * scaleFactor;
		float y = scaleStep;
		float X_POS = leftMargin;
		final float HOUR_TEXT_TOP_MARGIN = scaleStep / 8;
		final float HALF_HOUR = scaleStep / 2;
		
		for (int i = 0; i < HOUR_IN_DAY; i++) {
			canvas.drawText(hours[i], X_POS, y + HOUR_TEXT_TOP_MARGIN, hourPaint);
			canvas.drawText(MINUTSVALUE, X_POS + minutMargin, y, minPaint);
			
			canvas.drawLine(X_POS, y - HALF_HOUR, WIDTH, y - HALF_HOUR, minuteLinePaint);
			canvas.drawLine(X_POS + lineHourLeftMargin, y, WIDTH, y, hourLinePaint);
			
			y += scaleStep;
		}

		for (int i = 0; i < getChildCount(); i++) {
			View child = this.getChildAt(i);
			drawChild(canvas, child, 0);
		}

		canvas.restore();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		
		float width = getWidth() - getResources().getDimension(R.dimen.taskleftmargin) - getResources().getDimension(R.dimen.taskrightmargin);
		float rightpos = width + getResources().getDimension(R.dimen.taskleftmargin);
		
		List<TimeRange> ranges = collectRanges();
		int cnt = this.getChildCount();
		
		for (int i = 0; i < cnt; i++) {
			TimeRange child = ((TaskAdapter)getAdapter()).getTimeRange(i);
			
			int x = getChildXStart(child, i);
			int ll = x;
			int tt = (int) (getYPos(child.start) * scaleFactor + getResources().getDimension(R.dimen.tasktoppadding));
			int rr = x + (int)getChildWidth(child, i, ranges, width);
			int bb = (int) (getYPos(child.finish) * scaleFactor - getResources().getDimension(R.dimen.tasktoppadding));
			
			if (rr + 10 > rightpos)
				rr = (int) rightpos;
			
			getChildAt(i).layout(ll, tt, rr, bb);
		}
	}

	protected List<TimeRange> collectRanges() {
		List<TimeRange> list = new ArrayList<TimeRange>();
		
		for (int i = 0; i < this.getChildCount(); i++) 
			list.add(((TaskAdapter)getAdapter()).getTimeRange(i));
		
		Collections.sort(list, new Comparator<TimeRange>() { @Override public int compare(TimeRange lhs, TimeRange rhs) { return lhs.start.compareTo(rhs.start);}});
		
		List<TimeRange> ranges = new ArrayList<TimeRange>();
		TimeRange timeRange = null;
		
		for(TimeRange r : list){
			if(timeRange == null){
				timeRange = r;
				ranges.add(timeRange);
				continue;
			}
			
			if (isTaskInPeriod(timeRange.start, timeRange.finish, r)){
				if(timeRange.start.getMinutes() > r.start.getMinutes())
					timeRange.start = r.start;
				if(timeRange.finish.getMinutes() < r.finish.getMinutes())
					timeRange.finish = r.finish;
			}else{
				timeRange = r;
				ranges.add(timeRange);
			}
		}
		return ranges;
	}

	private View getLastChildAtTime(TimeRange child, int pos) {
		View result = null;
		Time s = child.start;
		Time f = child.finish;

		for (int i = pos - 1; i >= 0; i--) {
			TimeRange timeRange = ((TaskAdapter)getAdapter()).getTimeRange(i);
			if (isTaskInPeriod(s, f, timeRange)) {
				result = getChildAt(i);
				break;
			}
		}

		return result;
	}

	protected boolean isTaskInPeriod(Time s, Time f, TimeRange tr) {
		int t1 = s.h * 60 + s.m;
		int t2 = f.h * 60 + f.m;
		int r1 = tr.start.h * 60 + tr.start.m;
		int r2 = tr.finish.h * 60 + tr.finish.m;
		
		return r1 < t2 && r2 > t1;
	}

	private int getChildXStart(TimeRange child, int pos) {
		int result =  (int) getResources().getDimension(R.dimen.taskleftmargin);
		
		TaskView c = (TaskView) getLastChildAtTime(child, pos);

		if (c != null) 
			result = (int) (c.getRightPos() + getResources().getDimension(R.dimen.taskmargin));

		return result;
	}

	private float getChildWidth(TimeRange child, int pos, List<TimeRange> ranges, float w) {
		final float PADDING = getResources().getDimension(R.dimen.taskmargin); 
		
		int cnt = getChildCountAtTime(child, ranges);
		
		if (cnt > 0)
			w = ( w - (cnt - 1) * PADDING ) / cnt;
		
		return w;
	}

	protected int getChildCountAtTime(TimeRange child, List<TimeRange> ranges) {
		int cnt = 0;
		
		TimeRange period =  null;
		
		for(TimeRange r : ranges){
			if(isTaskInPeriod(r.start, r.finish, child)){
				period = r;
				break;
			}
		}

		if(period != null){
			for (int i = 0; i < getChildCount(); i++) {
				TimeRange c = ((TaskAdapter)getAdapter()).getTimeRange(i);
	
				if (isTaskInPeriod(period.start, period.finish, c))
					cnt++;
			}
		}

		return cnt;
	}

	private int getYPos(Time begin) {
		float partOfHour = (float) begin.m / 60.0f * hourStep;
		return (int) (begin.h * hourStep + partOfHour);
	}

	public void drawTimeRect(boolean val) {
		drawTimeRect = val;
	}
}
