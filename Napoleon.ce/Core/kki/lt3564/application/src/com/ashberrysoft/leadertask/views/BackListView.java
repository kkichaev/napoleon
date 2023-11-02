package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * ListView с возвратом в предыдущий экран
 * 
 * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 */
public class BackListView extends ListView {

    private static final float DISTANCE_X = 35f;
    private static final float DISTANCE_Y = 10f;
    private static final float THRESHOLD_FOR_MOVE_EVENT = 5f;
    private FragmentManager mSupportFragmentManager;
    private float mPositionX;
    private float mPositionY;
    private boolean mIsMoveGesture;// if current gesture is move gesture, especially, with changing x coordinate

    public BackListView(Context context) {
        super(context);
    }

    public BackListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            /*
             * when MotionEvent.ACTION_DOWN is detected, current gesture, by default, is not move gesture
             */
            setIsMoveGesture(false);
            mPositionX = event.getX();
            mPositionY = event.getY();
            break;

        case MotionEvent.ACTION_MOVE:
            /*
             * If x coordinate changed greater then by 5 pixels than make conclusion that current gesture is move
             * gesture, otherwise - current gesture is not move gesture. This code snippet intended for correct
             * processing long click events.
             */
            if (Math.abs(mPositionX - event.getX()) > THRESHOLD_FOR_MOVE_EVENT)
                setIsMoveGesture(true);
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            final float toPositionX = event.getX();
            final float toPositionY = event.getY();

            final float deltaY = mPositionY - toPositionY;
            final float deltaX = Math.abs(mPositionX - toPositionX);

            if (deltaX > Math.abs(deltaY / 2) && deltaY <= DISTANCE_Y) {
                if ((event.getX() - mPositionX) > DISTANCE_X && mSupportFragmentManager != null) {
                    mSupportFragmentManager.popBackStack();
                }
            }
            /*
             * If x coordinate changed greater then by 5 pixels than make conclusion that current gesture is move
             * gesture, otherwise - current gesture is not move gesture. This code snippet intended for correct
             * processing click events.
             */
            if (deltaX > THRESHOLD_FOR_MOVE_EVENT) {
                setIsMoveGesture(true);
            } else {
                setIsMoveGesture(false);
            }
            break;

        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    public void setFragmentManager(FragmentManager supportFragmentManager) {
        mSupportFragmentManager = supportFragmentManager;
    }

    /**
     * set - if current gesture is move gesture, especially, with changing x coordinate
     * 
     * @param isMoveGesture
     *            true - current gesture is move gesture false - current gesture is not move gesture
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    public void setIsMoveGesture(boolean isMoveGesture) {
        mIsMoveGesture = isMoveGesture;
    }

    /**
     * determine if current gesture is move gesture, especially, with changing x coordinate
     * 
     * @return true - current gesture is move gesture false - current gesture is not move gesture
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    public boolean isMoveGesture() {
        return mIsMoveGesture;
    }
}
