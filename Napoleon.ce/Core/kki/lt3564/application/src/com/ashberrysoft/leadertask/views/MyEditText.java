package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;

import static android.content.ContentValues.TAG;

public class MyEditText extends EditText {

    OnKeyPreImeListener onKeyPreImeListener;

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(onKeyPreImeListener != null)
                onKeyPreImeListener.onBackPressed();
            Log.d(TAG, "HIDING KEYBOARD");
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public interface OnKeyPreImeListener {
        void onBackPressed();
    }
}
