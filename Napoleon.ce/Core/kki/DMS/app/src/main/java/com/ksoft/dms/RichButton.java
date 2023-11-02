package com.ksoft.dms;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RichButton extends LinearLayout {
    TextView tvMain;
    TextView tvTop;

    public RichButton(Context context) {
        super(context);
        initView();
    }

    public RichButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StateButton,0, 0);

        try {
            tvTop = findViewById(R.id.tvTop);
            tvTop.setText(a.getText(R.styleable.StateButton_topText));
            tvTop.setTag(a.getString(R.styleable.StateButton_topTag));
            tvTop.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.StateButton_topTextSize, tvTop.getTextSize()));
            tvTop.setTextColor(a.getColor(R.styleable.StateButton_topTextColor, context.getColor(R.color.black)));

            tvMain = findViewById(R.id.tvMain);
            tvMain.setText(a.getText(R.styleable.StateButton_mainText));
            tvMain.setTag(a.getString(R.styleable.StateButton_mainTag));
            tvMain.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.StateButton_mainTextSize, tvMain.getTextSize()));
            tvMain.setTextColor(a.getColor(R.styleable.StateButton_mainTextColor, context.getColor(R.color.black)));
        }finally {
            a.recycle();
        }
    }

    private void initView() {
        inflate(getContext(), R.layout.richbutton, this);
    }

    public View getMainView(){
        return  tvMain;
    }

    public View getTopView(){
        return tvTop;
    }
}
