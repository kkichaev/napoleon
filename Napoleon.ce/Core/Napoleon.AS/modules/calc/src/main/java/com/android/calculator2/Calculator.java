/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calculator2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Calculator extends Activity {
    EventListener mListener = new EventListener();
    private CalculatorDisplay mDisplay;
    //private Persist mPersist;
    private History mHistory;
    private Logic mLogic;
    private PanelSwitcher mPanelSwitcher;

    private static final int CMD_CLEAR_HISTORY  = 1;
    private static final int CMD_BASIC_PANEL    = 2;
    private static final int CMD_ADVANCED_PANEL = 3;

    //private static final int HVGA_HEIGHT_PIXELS = 480;
    private static final int HVGA_WIDTH_PIXELS  = 320;

    static final int BASIC_PANEL    = 0;
    static final int ADVANCED_PANEL = 1;

    //private static final String LOG_TAG = "Calculator";
    //private static final boolean DEBUG  = false;

    public static String CALCULATOR_RESULT_VALUE = "com.android.calculator2.CALCULATOR_RESULT_VALUE";
    public static String START_CALC_VAL = "com.android.calculator2.START_CALC_VAL";
    public static String CALCULATOR_RESULT_ACTION = "com.android.calculator2.CALCULATOR_RESULT_ACTION";
    public static String BROADCAST_RESULT = "com.android.calculator2.BROADCAST_RESULT";
	private boolean broadcastResult;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.calc);

        //mPersist = new Persist(this);
        mHistory = new History();// mPersist.history;
                
        mDisplay = (CalculatorDisplay) findViewById(R.id.display);
        
        mLogic = new Logic(this, mHistory, mDisplay, (Button) findViewById(R.id.equal));
        
        Intent intent = getIntent();
        
        broadcastResult = false;
        
        if (intent != null){
        	mLogic.setResult(intent.getStringExtra(START_CALC_VAL));
        	broadcastResult = intent.getBooleanExtra(BROADCAST_RESULT, false);
        }
        
        mLogic.setResultListener(new ResultListener() {
			
			@Override
			public void applyResult(String value) {
				if (!broadcastResult){
					Intent data = new Intent();
			        data.putExtra(CALCULATOR_RESULT_VALUE, value);
			        setResult(Activity.RESULT_OK, data);
				} else {
			        Intent bdata = new Intent(CALCULATOR_RESULT_ACTION);
			        bdata.putExtra(CALCULATOR_RESULT_VALUE, value);
			        sendBroadcast(bdata);
				}
		        finish();
			}
		});
        
        HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistory, mLogic);
        mHistory.setObserver(historyAdapter);
        View view;
        mPanelSwitcher = (PanelSwitcher) findViewById(R.id.panelswitch);
                                       
        mListener.setHandler(mLogic, mPanelSwitcher);

        mDisplay.setOnKeyListener(mListener);


        if ((view = findViewById(R.id.del)) != null) {
//            view.setOnClickListener(mListener);
            view.setOnLongClickListener(mListener);
        }
        /*
        if ((view = findViewById(R.id.clear)) != null) {
            view.setOnClickListener(mListener);
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item;
        
        item = menu.add(0, CMD_CLEAR_HISTORY, 0, R.string.clear_history);
        item.setIcon(R.drawable.clear_history);
        
        item = menu.add(0, CMD_ADVANCED_PANEL, 0, R.string.advanced);
        item.setIcon(R.drawable.advanced);
        
        item = menu.add(0, CMD_BASIC_PANEL, 0, R.string.basic);
        item.setIcon(R.drawable.simple);

        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(CMD_BASIC_PANEL).setVisible(mPanelSwitcher != null && 
                          mPanelSwitcher.getCurrentIndex() == ADVANCED_PANEL);
        
        menu.findItem(CMD_ADVANCED_PANEL).setVisible(mPanelSwitcher != null && 
                          mPanelSwitcher.getCurrentIndex() == BASIC_PANEL);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case CMD_CLEAR_HISTORY:
            mHistory.clear();
            break;

        case CMD_BASIC_PANEL:
            if (mPanelSwitcher != null && 
                mPanelSwitcher.getCurrentIndex() == ADVANCED_PANEL) {
                mPanelSwitcher.moveRight();
            }
            break;

        case CMD_ADVANCED_PANEL:
            if (mPanelSwitcher != null && 
                mPanelSwitcher.getCurrentIndex() == BASIC_PANEL) {
                mPanelSwitcher.moveLeft();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        // as work-around for ClassCastException in TextView on restart
        // avoid calling superclass, to keep icicle empty
    }

    @Override
    public void onPause() {
        super.onPause();
        mLogic.updateHistory();
        //mPersist.save();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK 
            && mPanelSwitcher.getCurrentIndex() == ADVANCED_PANEL) {
            mPanelSwitcher.moveRight();
            return true;
        } else {
            return super.onKeyDown(keyCode, keyEvent);
        }
    }

    static void log(String message) {
//        if (LOG_ENABLED) {
//            Log.v(LOG_TAG, message);
//        }
    }

    /**
     * The font sizes in the layout files are specified for a HVGA display.
     * Adjust the font sizes accordingly if we are running on a different
     * display.
     */
    public void adjustFontSize(TextView view) {
        float fontPixelSize = view.getTextSize();
        Display display = getWindowManager().getDefaultDisplay();
        int h = Math.min(display.getWidth(), display.getHeight());
        float ratio = (float)h/HVGA_WIDTH_PIXELS;
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixelSize*ratio);
    }
}
