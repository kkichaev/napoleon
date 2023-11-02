package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewGroup that arranges child views in a similar way to text, with them laid out one line at a time and "wrapping" to
 * the next line as needed.
 * 
 * Code licensed under CC-by-SA
 * 
 * @author Henrik Gustafsson
 * @see http://stackoverflow.com/questions/549451/line-breaking-widget-layout-for-android
 * @license http://creativecommons.org/licenses/by-sa/2.5/
 * 
 */
public class PredicateLayout extends ViewGroup {

    private int line_height;// maximum line height
    private int min_line_height;// minimum line height
    private boolean isForLabels;// is PredicateLayout used to locate task labels

    public PredicateLayout(Context context) {
        super(context);
    }

    public PredicateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec);

        // The next line is WRONG!!! Doesn't take into account requested MeasureSpec mode!
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;
        int min_line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                /*
                 * if PredicateLayout not used to locate task labels, then each child can has width and height at most
                 * parent width and height
                 */
                //if (!isForLabels)
                    child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
                //else {
                    /*
                     * if PredicateLayout used to locate task labels and child width less then available parent width,
                     * then each child can has width and height at most parent width and height, otherwise if child
                     * width greater then available parent width, each child can has exactly available width and height
                     * that at most parent height
                     */
                    /*if (xpos + child.getMeasuredWidth() <= width)
                        child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                                MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
                    else {*/
                        /*
                         * if we entire fill available parent width at the previous iteration,
                         * then child width may be at most parent width
                         */
                        /*if (xpos > width)
                            child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
                        else
                            child.measure(MeasureSpec.makeMeasureSpec(width - xpos, MeasureSpec.EXACTLY),
                                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
                    }
                }*/

                /*
                 * if we work with first child view, then store its height in order to use in Math.min method to
                 * determine minimum line height of child views
                 */
                if (i == 0)
                    min_line_height = child.getMeasuredHeight() + lp.height;
                final int childw = child.getMeasuredWidth();
                // determine maximum line height
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.height);
                // determine minimum line height
                min_line_height = Math.min(min_line_height, child.getMeasuredHeight() + lp.height);

                // if child width greater then available width
                if (xpos + childw > width) {
                    // move x position to top of line
                    xpos = getPaddingLeft();
                    ypos += child.getMeasuredHeight() + lp.height;// line_height;
                }
                xpos += childw + lp.width;
            }
        }
        // store maximum and minimum line height values respectively as class fields
        this.line_height = line_height;
        this.min_line_height = min_line_height;

        /*
         * if PredicateLayout used to locate task labels then PredicateLayout height equals to current y position value
         * plus minimum line height, otherwise - equals to current y position value plus maximum line height
         */
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            if (!isForLabels)
                height = ypos + line_height;
            else
                height = ypos + min_line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (!isForLabels) {
                if (ypos + line_height < height)
                    height = ypos + line_height;
            } else {
                if (ypos + min_line_height < height)
                    height = ypos + min_line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(5, 5); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return (p instanceof LayoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    /*
                     * if PredicateLayout used to locate task labels, then use minimum line height value, otherwise
                     * maximum line height value
                     */
                    if (!isForLabels)
                        ypos += line_height;
                    else
                        ypos += min_line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.width;
            }
        }
    }

    // set "isForLabels" flag value
    public void setIsForLabels(boolean flag) {
        this.isForLabels = flag;
    }
}
