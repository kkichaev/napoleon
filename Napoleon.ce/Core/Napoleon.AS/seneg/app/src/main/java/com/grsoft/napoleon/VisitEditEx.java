package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.Visit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.VisitDoc;

public class VisitEditEx extends VisitEditNew {
    @Override
    protected int getContentView() {
        return R.layout.visiteditex;
    }

    @Override
    protected CreatableDocument<? extends Visit> createDocument() {
        if(DocType.getCurDoc() instanceof  VisitDoc)
            return (CreatableDocument<? extends Visit>) DocType.getCurDoc().create();
        return (CreatableDocument<? extends Visit>) VisitDoc.instance().create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(visit.isEmpty()) {
                    showDialog(ASK_TO_DEL_VISIT_MSG);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(visit.isEmpty()) {
            showDialog(ASK_TO_DEL_VISIT_MSG);
        } else {
            finish();
        }
    }
}
