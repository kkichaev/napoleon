package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DebetWorkImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebetWorkDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;

public class DebetWorkEdit extends Activity implements SendResultListener {
    DebetWorkImpl doc = new DebetWorkImpl();

    public static void open(Context context, DebetWorkImpl doc) {
        Intent i = new Intent(context, DebetWorkEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debet_wrk);

        OrgImpl oi = new OrgImpl();
        Org o = oi.getData();

        long orderRowId;
        if( savedInstanceState == null )
            orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        else
            orderRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);

        doc.read(orderRowId);
        o.id = doc.getId();
        oi.read();
        oi.close();

        TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
        tvOrg.setText(o.name);

        EditText ed = findViewById(R.id.edRemark);
        ed.setText(doc.getData().remark);
        ed.setEnabled(doc.isEditable());

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDoc();
                if(doc.isEmpty()) {
                    return;
                }
                new DocumentSender(DebetWorkEdit.this, v,
                        DebetWorkDoc.instance().getObjectName(), doc,
                        doc.getRowid(), DebetWorkEdit.this).execute((Void[])null);
            }
        });
    }

    void updateDoc() {
        if(doc.isEditable()) {
            doc.getData().remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
            doc.write();
        }
    }

    @Override
    public void onBackPressed() {
        updateDoc();
        if(doc.isEmpty()) {
            doc.delete();
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doc.close();
    }

    @Override
    public void postSendExecute(boolean result) {
        if(result) {
            doc.read(doc.getRowid(), false);
            findViewById(R.id.edRemark).setEnabled(doc.isEditable());
        }
    }
}
