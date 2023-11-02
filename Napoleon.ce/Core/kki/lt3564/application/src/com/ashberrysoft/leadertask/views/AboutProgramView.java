package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.utils.Utils;

public class AboutProgramView extends LinearLayout {

    // VIEW
    private TextView mAbout;



    public AboutProgramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_about_program, this);

        mAbout = (TextView) findViewById(R.id.version);
        final ImageView leadertask_logo = (ImageView) findViewById(R.id.leadertask_logo);

        leadertask_logo.setImageResource(Utils.getLeaderTaskLauncherResource());

        mAbout.setText("v" + ((LTApplication) context.getApplicationContext()).getApplicationBuildVersion());
    }
}