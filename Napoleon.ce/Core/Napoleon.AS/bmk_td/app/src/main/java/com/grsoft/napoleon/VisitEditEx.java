package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.LayoutDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;

import java.util.ArrayList;
import java.util.List;

public class VisitEditEx extends VisitEditNew{
    protected int getContentView() {
        return R.layout.visiteditnewex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.btnFinish).setOnClickListener((v)-> { if (!visit.isEmpty()) showDialog(R.id.finish_work_dlg); });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.finish_work_dlg)
            return  createFinishWorkDlg();
        return super.onCreateDialog(id);
    }

    private Dialog createFinishWorkDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.question);
        builder.setMessage(R.string.ask_to_finish_work);
        builder.setPositiveButton(R.string.ok, (w,e)->finishWork());
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    private void finishWork() {
        ((VisitEx)visit.getData()).inwork = 0;
        visit.write();
        visit.close();
        finish();
    }

    @Override
    protected void send() {
        List<DocExportListener> sends = DocType.getDocuments(true, true);
        new DocumentSender(this, findViewById(R.id.btnSend), sends, this).execute((Void[])null);
    }
}
