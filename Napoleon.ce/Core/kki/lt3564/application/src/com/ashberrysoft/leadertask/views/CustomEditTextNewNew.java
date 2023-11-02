package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditTextNewNew extends EditText {

    public interface BackPressedListener {

        void onClickBack();
    }

    Context context;
    // LISTENER
    private BackPressedListener mListener;

    public CustomEditTextNewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setListener(BackPressedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            mListener.onClickBack();
            //this.setText("");
            return true;

        }
        return super.onKeyPreIme(keyCode, event);

    }
}
