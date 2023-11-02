package com.grsoft.util.view;
import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.grsoft.aceteam.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class CalendarView extends LinearLayout implements OnTouchListener
{
	private static final String LOG_TAG = "CalendarView";

	// Gesture detections
	private GestureDetector gestureDetector;

	public interface OnCalendarActionListener
	{
		public void onDateChanged(final Date oldDate, final Date newDate);
		public void onOtherDateChanged(final Date currentDate, final Date otherDate);
		public void onCalendarCancelled();
	}

	public interface CalendarHandler {
		boolean isDateEnabled(Date date);
	}
	CalendarHandler handler = null;


	public interface DateMarker {
		public boolean isMarked(long date);
	}

	protected final static int [] DAY_OF_WEEK_IDS =
			new int [] { R.id.dayOfWeek0, R.id.dayOfWeek1, R.id.dayOfWeek2, R.id.dayOfWeek3, R.id.dayOfWeek4, R.id.dayOfWeek5, R.id.dayOfWeek6 };

	protected final static int [] DAY_OF_WEEK_NAMES =
			new int [] {
					R.string.day_of_week_1, R.string.day_of_week_2, R.string.day_of_week_3, R.string.day_of_week_4
					, R.string.day_of_week_5, R.string.day_of_week_6, R.string.day_of_week_7
			};

	protected final static int [] CALENDAR_CELL_IDS =
			new int [] {
					R.id.cell01, R.id.cell02, R.id.cell03, R.id.cell04, R.id.cell05, R.id.cell06, R.id.cell07,
					R.id.cell11, R.id.cell12, R.id.cell13, R.id.cell14, R.id.cell15, R.id.cell16, R.id.cell17,
					R.id.cell21, R.id.cell22, R.id.cell23, R.id.cell24, R.id.cell25, R.id.cell26, R.id.cell27,
					R.id.cell31, R.id.cell32, R.id.cell33, R.id.cell34, R.id.cell35, R.id.cell36, R.id.cell37,
					R.id.cell41, R.id.cell42, R.id.cell43, R.id.cell44, R.id.cell45, R.id.cell46, R.id.cell47,
					R.id.cell51, R.id.cell52, R.id.cell53, R.id.cell54, R.id.cell55, R.id.cell56, R.id.cell57,
			};

	protected TextView monthText;
	protected boolean periodSelectable = false;

	protected ViewGroup [] dayViews = null;

	protected static final SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMMMM yyyy", Locale.getDefault());

	private Calendar calendar = GregorianCalendar.getInstance();
	private Calendar todayDate = GregorianCalendar.getInstance();
	private Calendar currentDate = GregorianCalendar.getInstance();
	private Calendar otherDate = GregorianCalendar.getInstance();
	Calendar markDate = null;

	private static int START_DAY_OF_WEEK = Calendar.MONDAY;

	DateMarker marker;

	public CalendarView(Context context) {
		super(context);

		initComponents();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initComponents();
	}

	public void setMarker(DateMarker newMarker) {
		marker = newMarker;
		refreshDays();
	}

	public void setHandler(CalendarHandler handler) {
		this.handler = handler;
		refreshDays();
	}

	protected static Calendar getFirstDayOfMonth(Calendar cal)
	{
		Date date = new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), 1, 0, 0, 0);
		cal.setTime(date);
		return cal;
	}

	protected static Date getStartOfDay(final Date inDate)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(inDate);
		Date date = new Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return date;
	}

	protected void initComponents()
	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.calendar_view, this);

        /*ViewGroup layoutMain = (ViewGroup) findViewById(R.id.layoutMain);
        if (layoutMain != null)
        	layoutMain.setOnTouchListener(this);*/

		gestureDetector = new GestureDetector(new CalendarViewGestureDetector());

		monthText = (TextView) findViewById(R.id.textCalendarMonth);

		todayDate.setTime(getStartOfDay(new Date()));
		currentDate.setTime(todayDate.getTime());
		calendar.setTime(todayDate.getTime());
		calendar = getFirstDayOfMonth(calendar);

		initDaysOfWeek();

		dayViews = new ViewGroup[CALENDAR_CELL_IDS.length];
		for (int index = 0; index < CALENDAR_CELL_IDS.length; index++)
		{
			dayViews [index] = (ViewGroup) findViewById(CALENDAR_CELL_IDS[index]);
			if (dayViews [index] != null)
				dayViews[index].setOnTouchListener(this);
		}
		refreshDays();

		ImageView btnMonthLeft = (ImageView) findViewById(R.id.btnMonthLeft);
		if (btnMonthLeft != null)
		{
			btnMonthLeft.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					monthLeft();
				}
			});
		}

		ImageView btnMonthRight = (ImageView) findViewById(R.id.btnMonthRight);
		if (btnMonthRight != null)
		{
			btnMonthRight.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					monthRight();
				}
			});
		}

		ImageView btnCalendarCancel = (ImageView) findViewById(R.id.btnCalendarCancel);
		if (btnCalendarCancel != null)
		{
			btnCalendarCancel.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					calendarCancel();
				}
			});
		}

	}

	protected void monthRight()
	{
		calendar.add(Calendar.MONTH, 1);
		Animation animation = AnimationUtils.loadAnimation(CalendarView.this.getContext(), R.anim.slide_in_from_right);
		TableLayout calendarTableLayout = (TableLayout)findViewById(R.id.tableLayoutCalendar);
		if (calendarTableLayout != null)
			calendarTableLayout.startAnimation(animation);
		refreshDays();
	}

	protected void monthLeft()
	{
		calendar.add(Calendar.MONTH, -1);
		Animation animation = AnimationUtils.loadAnimation(CalendarView.this.getContext(), R.anim.slide_in_from_left);
		TableLayout calendarTableLayout = (TableLayout)findViewById(R.id.tableLayoutCalendar);
		if (calendarTableLayout != null)
			calendarTableLayout.startAnimation(animation);
		refreshDays();
	}

	protected void calendarCancel()
	{
		Animation animation = null;

		if (currentDate != null && currentDate.getTime().getTime() < todayDate.getTime().getTime())
			animation = AnimationUtils.loadAnimation(CalendarView.this.getContext(), R.anim.slide_in_from_right);
		else
			animation = AnimationUtils.loadAnimation(CalendarView.this.getContext(), R.anim.slide_in_from_left);

//		Date oldDate = currentDate == null ? null : currentDate.getTime();
		if (currentDate == null)
			currentDate = Calendar.getInstance();
		currentDate.setTime(todayDate.getTime());

		if (otherDate == null)
			otherDate = Calendar.getInstance();
		otherDate.setTime(todayDate.getTime());

		calendar.setTime(todayDate.getTime());
		calendar = getFirstDayOfMonth(calendar);

		TableLayout calendarTableLayout = (TableLayout)findViewById(R.id.tableLayoutCalendar);
		if (calendarTableLayout != null)
			calendarTableLayout.startAnimation(animation);
		refreshDays();

		onCancelled();
		//onDateChanged(oldDate, null);
	}

	protected void initDaysOfWeek()
	{

		for (int i = 0, dayCount = START_DAY_OF_WEEK - 1; i < DAY_OF_WEEK_IDS.length; i++, dayCount++)
		{
			TextView dayText = (TextView)findViewById(DAY_OF_WEEK_IDS[i]);
			if (dayText != null)
			{
				dayText.setText(getResources().getString(DAY_OF_WEEK_NAMES[dayCount % DAY_OF_WEEK_NAMES.length]));
			}
		}
	}

	public void refreshDays()
	{
		int startMonth = calendar.get(Calendar.MONTH);
		int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int daysShift = startDayOfWeek - START_DAY_OF_WEEK;

		if (monthText != null)
			monthText.setText(monthDateFormat.format(calendar.getTime()));

		Calendar cal = new GregorianCalendar();
		cal.setTime(calendar.getTime());
		cal.add(Calendar.DAY_OF_YEAR, -daysShift);

		for (ViewGroup dayView: dayViews)
		{
			setupCalendarCell(dayView, cal, startMonth);
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	protected void setupCalendarCell(ViewGroup view, Calendar cal, int startMonth)
	{
		if (view != null)
		{
			view.setTag(R.id.calendar_day, Long.valueOf(cal.getTime().getTime()));
			boolean isEnabled = handler == null || handler.isDateEnabled(cal.getTime());

			view.setEnabled(isEnabled);
			view.setClickable(isEnabled);

			TextView dayTextView = (TextView) view.findViewById(R.id.textViewCalendarDay);
			if (dayTextView != null)
			{
				dayTextView.setText(Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH)).toString());

				int textColor = 0;
				switch (cal.get(Calendar.DAY_OF_WEEK))
				{
					case Calendar.SATURDAY: textColor = getResources().getColor(R.color.calendar_saturday_text); break;
					case Calendar.SUNDAY: textColor = getResources().getColor(R.color.calendar_sunday_text); break;
					default: textColor = getResources().getColor(R.color.calendar_day_text); break;
				}
				textColor &= 0xFFFFFF;

				if (todayDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
						todayDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
						todayDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
				{
					view.setBackgroundResource(R.drawable.calendar_today_border);
					textColor |= getResources().getColor(R.color.calendar_active_mask);
				}
				else if (cal.get(Calendar.MONTH) == startMonth)
				{
					view.setBackgroundColor(getResources().getColor(R.color.calendar_cell_background));
					textColor |= getResources().getColor(R.color.calendar_active_mask);
				}
				else
				{
					view.setBackgroundColor(getResources().getColor(R.color.calendar_inactive_cell_background));
					textColor |= getResources().getColor(R.color.calendar_inactive_mask);
				}

				boolean marked = false;
				if(marker != null) {
					if(marker.isMarked(cal.getTime().getTime())) {
						view.setBackgroundResource(R.drawable.calendar_mark_border);
						marked = true;
					}
				} else {
					if( markDate != null && cal.getTime().equals(markDate.getTime()) ) {
						view.setBackgroundResource(R.drawable.calendar_mark_border);
						marked = true;
					}
				}
				if (currentDate != null)
				{
					if (!periodSelectable || otherDate == null ||
							otherDate.getTime().getTime() == currentDate.getTime().getTime())
					{
						if (currentDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
								currentDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
								currentDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
						{
							if (todayDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
									todayDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
									todayDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
								view.setBackgroundResource(marked ? R.drawable.calendar_today_marked_border : R.drawable.calendar_today_selected_border);
							else
								view.setBackgroundResource(marked ? R.drawable.calendar_today_marked_border : R.drawable.calendar_selected_border);
						}
					}
					else
					{
						if (cal.getTime().getTime() >= currentDate.getTime().getTime() &&
								cal.getTime().getTime() <= otherDate.getTime().getTime())
						{
							if (todayDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
									todayDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
									todayDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
								view.setBackgroundResource(R.drawable.calendar_today_selected_border);
							else
								view.setBackgroundResource(R.drawable.calendar_selected_border);
						}

					}
				}

				if(!isEnabled) {
					view.setBackgroundColor(getResources().getColor(R.color.calendar_inactive_cell_background));
					textColor |= getResources().getColor(R.color.calendar_inactive_mask);
				}

				dayTextView.setTextColor(textColor);
			}
		}
	}

	public void onDayClick(View v)
	{
		if (v != null)
		{
			Long selDate = (Long)v.getTag(R.id.calendar_day);
			if (selDate != null)
			{
				Date oldDate = currentDate == null ? null : currentDate.getTime();
				Date newDate = new Date(selDate);
				if (currentDate == null)
					currentDate = Calendar.getInstance();
				currentDate.setTime(newDate);

				if (otherDate == null)
					otherDate = Calendar.getInstance();
				otherDate.setTime(newDate);

				refreshDays();

				//if (oldDate.getTime() != newDate.getTime())
				{
					onDateChanged(oldDate, newDate);
				}
			}
		}
	}

	private void swapDatesIfNeeded()
	{
		if (currentDate == null)
			currentDate = Calendar.getInstance();
		if (otherDate == null)
			otherDate = Calendar.getInstance();

		if (otherDate.getTime().getTime() < currentDate.getTime().getTime())
		{
			Date buf = currentDate.getTime();
			currentDate.setTime(otherDate.getTime());
			otherDate.setTime(buf);
		}
	}


	public void onOtherDayClick(View v)
	{
		if (v != null)
		{
			Long selDate = (Long)v.getTag(R.id.calendar_day);
			if (selDate != null)
			{
				Date newDate = new Date(selDate);
				if (otherDate == null)
					otherDate = Calendar.getInstance();
				otherDate.setTime(newDate);

				swapDatesIfNeeded();

				refreshDays();

				//if (oldDate.getTime() != newDate.getTime())
				{
					onOtherDateChanged(currentDate.getTime(), otherDate.getTime());
				}
			}
		}
	}

	/**
	 * Collection of listeners
	 */
	private List<OnCalendarActionListener> listeners = new ArrayList<OnCalendarActionListener>();

	/**
	 * Add data listener
	 * @param taskStateListener
	 */
	public void setCalendarActionListener(final OnCalendarActionListener actionListener)
	{
		listeners.add(actionListener);
	}

	/**
	 * Remove data listener
	 * @param taskStateListener
	 */
	public void removeCalendarActionListener(final OnCalendarActionListener actionListener)
	{
		listeners.remove(actionListener);
	}

	/**
	 * Involkes callbacks raised
	 * @param taskId
	 * @param currentTaskState
	 */
	protected void onDateChanged(final Date oldDate, final Date newDate)
	{
		List<OnCalendarActionListener> failedListeners = new ArrayList<OnCalendarActionListener>();
		for (OnCalendarActionListener listener: listeners)
		{
			try
			{
				if (listener != null)
					listener.onDateChanged(oldDate, newDate);
			}
			catch (Exception ex)
			{
				failedListeners.add(listener);
			}
		}
		if (!failedListeners.isEmpty())
		{
			for (OnCalendarActionListener listener: failedListeners)
			{
				listeners.remove(listener);
			}
		}
	}

	/**
	 * Involkes callbacks raised
	 * @param taskId
	 * @param currentTaskState
	 */
	protected void onOtherDateChanged(final Date currentDate, final Date otherDate)
	{
		List<OnCalendarActionListener> failedListeners = new ArrayList<OnCalendarActionListener>();
		for (OnCalendarActionListener listener: listeners)
		{
			try
			{
				if (listener != null)
					listener.onOtherDateChanged(currentDate, otherDate);
			}
			catch (Exception ex)
			{
				failedListeners.add(listener);
			}
		}
		if (!failedListeners.isEmpty())
		{
			for (OnCalendarActionListener listener: failedListeners)
			{
				listeners.remove(listener);
			}
		}
	}

	protected void onCancelled()
	{
		List<OnCalendarActionListener> failedListeners = new ArrayList<OnCalendarActionListener>();
		for (OnCalendarActionListener listener: listeners)
		{
			try
			{
				if (listener != null)
					listener.onCalendarCancelled();
			}
			catch (Exception ex)
			{
				failedListeners.add(listener);
			}
		}
		if (!failedListeners.isEmpty())
		{
			for (OnCalendarActionListener listener: failedListeners)
			{
				listeners.remove(listener);
			}
		}
	}


	public Date getCurrentDate()
	{
		return currentDate == null ? null : currentDate.getTime();
	}

	public void setCurrentDate(Date newCurrentDate)
	{
		if (newCurrentDate == null)
		{
			this.currentDate = null;
			this.otherDate = null;
		}
		else
		{
			if (this.currentDate == null)
				this.currentDate = Calendar.getInstance();
			this.currentDate.setTime(getStartOfDay(newCurrentDate));
			if (this.otherDate == null)
				this.otherDate = Calendar.getInstance();
			this.otherDate.setTime(getStartOfDay(newCurrentDate));
		}

		swapDatesIfNeeded();

		calendar.setTime(currentDate.getTime());
		calendar = getFirstDayOfMonth(calendar);
		refreshDays();
	}

	public Date getOtherDate()
	{
		return otherDate == null ? null : otherDate.getTime();
	}

	public void setOtherDate(Date newOtherDate)
	{
		if (this.otherDate == null)
			this.otherDate = Calendar.getInstance();

		if (newOtherDate == null)
		{
			if (this.currentDate != null)
				this.otherDate.setTime(this.currentDate.getTime());
		}
		else
		{
			this.otherDate.setTime(getStartOfDay(newOtherDate));
		}

		swapDatesIfNeeded();

		refreshDays();
	}

	public boolean isCancellable()
	{
		boolean result = false;
		ImageView btnCalendarCancel = (ImageView) findViewById(R.id.btnCalendarCancel);
		if (btnCalendarCancel != null && btnCalendarCancel.getVisibility() == View.VISIBLE)
			result = true;
		return result;
	}

	public void setCancellable(boolean cancellable)
	{
		ImageView btnCalendarCancel = (ImageView) findViewById(R.id.btnCalendarCancel);
		if (btnCalendarCancel != null)
			btnCalendarCancel.setVisibility(cancellable ? View.VISIBLE : View.GONE);
	}

	public void setMarkDate(Date date) {
		markDate = Calendar.getInstance();
		markDate.setTime(date);
		refreshDays();
	}

	public boolean isPeriodSelectable()
	{
		return this.periodSelectable;
	}

	public void setPeriodSelectable(boolean periodSelectable)
	{
		this.periodSelectable = periodSelectable;
	}

	private View touchedView  = null;

	@Override
	public boolean onTouch(View v, MotionEvent me)
	{
		Log.d(LOG_TAG, "onTouch " + me.toString());
		touchedView = v;
		gestureDetector.onTouchEvent(me);
		return false;
	}

	final ViewConfiguration vc = ViewConfiguration.get(getContext());
	final int swipeMaxOffPath = vc.getScaledTouchSlop();
	final int swipeMinDistance = vc.getScaledTouchSlop();
	final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
	final int swipeMaxThresholdVelocity = vc.getScaledMaximumFlingVelocity();

	class CalendarViewGestureDetector extends SimpleOnGestureListener
	{
		/*private ViewGroup findDayView(MotionEvent e)
		{
			Rect rect = new Rect();
			Point globalOffset = new Point(CalendarView.this.getLeft(), CalendarView.this.getTop());
			for (ViewGroup view: dayViews)
			{
				if (view != null)
				{
					view.getGlobalVisibleRect(rect);
					if (rect.contains((int)e.getX(), (int)e.getY()))
						return view;
				}
			}
			return null;
		}*/

		@Override
		public void onLongPress(MotionEvent e)
		{
			//ViewGroup view = findDayView(e);
			if (touchedView != null)
			{
				onOtherDayClick(touchedView);
			}
			super.onLongPress(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e)
		{
			//ViewGroup view = findDayView(e);
			if (touchedView != null)
			{
				onDayClick(touchedView);
				return true;
			}
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			try
			{
				// right to left swipe
				if(e1.getX() - e2.getX() > swipeMinDistance /*&& (Math.abs(e1.getY() - e2.getY()) < swipeMaxOffPath)*/
						&& Math.abs(velocityX) > swipeThresholdVelocity &&
						Math.abs(velocityX) < swipeMaxThresholdVelocity)
				{
					monthRight();
				}
				else if (e2.getX() - e1.getX() > swipeMinDistance /*&& (Math.abs(e1.getY() - e2.getY()) < swipeMaxOffPath) */
						&& Math.abs(velocityX) > swipeThresholdVelocity &&
						Math.abs(velocityX) < swipeMaxThresholdVelocity)
				{
					monthLeft();
				}
                /*else if (Math.abs(velocityX) < swipeThresholdVelocity || Math.abs(velocityY) < swipeThresholdVelocity)
                {
                	View view1 = findDayView(e1);
                	View view2 = findDayView(e2);

                	if (view1 != null && view2 != null && !view1.equals(view2))
                	{
                		onDayClick(view1);
                		onOtherDayClick(view2);
                	}
                }*/


                /*else if (e1.getY() - e2.getY() > swipeMinDistance && (Math.abs(e1.getY() - e2.getY()) < swipeMaxOffPath)
                		&& Math.abs(velocityY) > swipeThresholdVelocity &&
                		Math.abs(velocityY) < swipeMaxThresholdVelocity)
                {
					calendarCancel();
                }  */
			}
			catch (Exception e)
			{
				// nothing
			}
			return false;
		}
	}

}