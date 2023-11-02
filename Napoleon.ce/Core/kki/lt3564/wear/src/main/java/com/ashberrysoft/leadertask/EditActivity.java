package com.ashberrysoft.leadertask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class EditActivity extends WearableActivity {

    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    private String mText;

    public static Intent newInstance(Context context, String text) {
        final Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EXTRA_TEXT, text);

        return intent;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        //
        if (b != null) {
            mText = b.getString(EXTRA_TEXT);
        } else {
            Intent intent = getIntent();
            mText = intent.getStringExtra(EXTRA_TEXT);
        }
        //
        setContentView(R.layout.activity_edit);

        TextView textView = (TextView) findViewById(R.id.text_task);
        textView.setText(mText);
    }

}
