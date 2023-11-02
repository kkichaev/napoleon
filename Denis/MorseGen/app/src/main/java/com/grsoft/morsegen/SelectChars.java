package com.grsoft.morsegen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SelectChars extends Activity {
    public static final int SEL_CHARS_ID = 112;
    public static final String SEL_CHAR_TAG = "sel_chars";

    String selected;
    Map<Character, Integer> charToCtrl = new HashMap<>();

    public static void open(Activity context, String selected) {
        Intent i = new Intent(context, SelectChars.class);
        if(selected == null)
            selected = "";
        i.putExtra(SEL_CHAR_TAG, selected);
        context.startActivityForResult(i, SEL_CHARS_ID);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_chars);
        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        selected = b.getString(SEL_CHAR_TAG, "");

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,/?=";
        int[] ids = {R.id.cbA,R.id.cbB,R.id.cbC,R.id.cbD,R.id.cbE,R.id.cbF,R.id.cbG,R.id.cbH,R.id.cbI,
                R.id.cbJ,R.id.cbK,R.id.cbL,R.id.cbM,R.id.cbN,R.id.cbO,R.id.cbP,R.id.cbQ,R.id.cbR,
                R.id.cbS,R.id.cbT,R.id.cbU,R.id.cbV,R.id.cbW,R.id.cbX,R.id.cbY,R.id.cbZ,R.id.cb0,
                R.id.cb1,R.id.cb2,R.id.cb3,R.id.cb4,R.id.cb5,R.id.cb6,R.id.cb7,R.id.cb8,R.id.cb9,R.id.
                cbDot,R.id.cbComma,R.id.cbDiv,R.id.cbQuest,R.id.cbEq};

        int idx = 0;
        for(char sym : chars.toCharArray()) {
            charToCtrl.put(sym, ids[idx]);
            if(selected.indexOf(sym) >= 0) {
                CheckBox cb = findViewById(ids[idx]);
                cb.setChecked(true);
            }
            idx++;
        }

        CheckBox sel = findViewById(R.id.cbSelect);
        sel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   for(Integer id : charToCtrl.values()) {
                       CheckBox cb = findViewById(id);
                       cb.setChecked(isChecked);
                   }
               }
           }
        );

        View ib = findViewById(R.id.ibSave);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = "";
                for(Map.Entry<Character, Integer> kv : charToCtrl.entrySet()) {
                    CheckBox cb = findViewById(kv.getValue());
                    if(cb.isChecked()) {
                        res += kv.getKey();
                    }
                }

                Intent i = new Intent();
                i.putExtra(SEL_CHAR_TAG, res);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEL_CHAR_TAG, selected);
    }
}
