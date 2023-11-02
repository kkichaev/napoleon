package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.napoleon.R;
import com.grsoft.script.documents.ScriptDoc;

public class ScriptDocEx extends ScriptDoc {
    public static void init() {
        instance = new ScriptDocEx();
    }

    @Override
    public void updateTotalSum(Activity activity, long sum, int weight, int count) {
        hideFields(activity);
    }

    void hideFields(Activity activity) {
        int[] ids = new int[] {
                R.id.SumColumnTitle,
                R.id.tvMainDocValColTitle,
                R.id.tvSum,
                R.id.tvTotalSum,
        };

        for(int id : ids) {
            View v = activity.findViewById(id);
            if (v != null)
                v.setVisibility(View.GONE);
        }
    }

    @Override
    public void viewOpened(Activity documentsView) {
        super.viewOpened(documentsView);
        hideFields(documentsView);
    }

    @Override
    public void setView(Adapter adapter, View view, Document<?> doc) {
        super.setView(adapter, view, doc);

        TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
        tvSum.setVisibility(View.GONE);
    }

}
