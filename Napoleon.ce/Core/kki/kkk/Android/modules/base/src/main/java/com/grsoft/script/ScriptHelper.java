package com.grsoft.script;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.IncassDebDistrEdit;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.script.dataobjects.impl.ScriptImpl;

import java.util.Date;

public class ScriptHelper {
    public static void initView(final Activity activity, String docName, Date created, String id) {
        final ScriptContext ctx = ScriptImpl.containsDocument(docName, created, id);
        if (ctx != null) {
            if (Features.CANT_SEND_SCRIPT_PART) {
                View btnSend = activity.findViewById(R.id.btnSend);
                if (btnSend != null)
                    btnSend.setVisibility(View.GONE);
            }
            if (Features.SCRIPT_GO_NEXT) {
                View btnNext = activity.findViewById(R.id.btnNext);
                if (ctx.getIndex() < ctx.getScript().getData().items.size() - 1) {
                    if (btnNext != null) {
                        btnNext.setVisibility(View.VISIBLE);
                        if (activity instanceof ScriptActivity) {
                            final ScriptActivity sa = (ScriptActivity) activity;
                            btnNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean canDo = sa.closeDocument();
                                    if (ctx.canOpenNext() || canDo) {
                                        activity.finish();
                                        ctx.openNext(activity);
                                    } else
                                        Toast.makeText(activity, R.string.please_complete_doc, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
