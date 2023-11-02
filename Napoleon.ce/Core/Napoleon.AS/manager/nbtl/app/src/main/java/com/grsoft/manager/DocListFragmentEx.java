package com.grsoft.manager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptItem;

public class DocListFragmentEx extends DocListFragmentNew{
    @NonNull
    @Override
    protected DocListAdapter createDocListAdapter(Context ctx) {
        return new DocListAdapter(ctx, (SelParam) ctx){
            @Override
            public void setView(int pos, View view, DocRow row) {
                super.setView(pos, view, row);

                DocDataObject item = row.getDocument().getData();

                TextView tv = view.findViewById(R.id.tvSum);
                tv.setText("");

                if (item instanceof Script){
                    for (ScriptItem i : ((Script)item).items)
                        if (i.state == ScriptItem.DOC_INITED)
                            tv.setText(sdf.format(i.date));
                }
            }
        };
    }
}
