package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.impl.TargetImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.TargetDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;

public class DocumentsEx extends Documents{
    private int pendingTaskCount;
    TargetImpl target;

    @Override
    protected void onResume() {
        super.onResume();

        pendingTaskCount = pendingTaskCount(org.getData().id);

        if (pendingTaskCount > 0)
            showDialog(R.id.pending_task_dlg);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.pending_task_dlg)
            return createPendingTaskDlg();

        return super.onCreateDialog(id);
    }

    private Dialog createPendingTaskDlg() {
        AlertDialog.Builder sb = new AlertDialog.Builder(this);
        sb.setTitle("Невыполненные задачи!");
        sb.setMessage(String.format("%d - невыполенных задач", pendingTaskCount));
        sb.setNegativeButton("Просмотр", (d,w)->{
            DocType.setCurDoc(TaskDoneDoc.instance());
            adjustViewForDocType(TaskDoneDoc.instance());
        });
        sb.setPositiveButton(R.string.ok, null);
        sb.setCancelable(true);
        return sb.create();
    }

    @Override
    protected void adjustViewForDocType(DocType docType) {
        if (docType.equals(TargetDoc.instance()) && !DocType.getCurDoc().equals(TargetDoc.instance()) ||
                !docType.equals(TargetDoc.instance()) && DocType.getCurDoc().equals(TargetDoc.instance()))
            adapter = null;

        super.adjustViewForDocType(docType);
    }

    @Override
    protected DocumentsAdapter createAdapter(DocType docType, String id) {
        String order = getOrder(docType);
        return new DocumentsAdapter(this, docType, id, order){
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (curDocType == TargetDoc.instance()){
                    if (view == null)
                        view = View.inflate(DocumentsEx.this, R.layout.target_list_row, null);

                    TargetImpl doc = (TargetImpl) getItem(position);
                    TextView tv = view.findViewById(R.id.tvOther);
                    tv.setText(doc.getData().remark);

                    tv = view.findViewById(R.id.tvDate);
                    tv.setText(curDocType.getDateDocText(doc));

                    CheckBox cb = view.findViewById(R.id.cbClose);
                    cb.setChecked(doc.isClosed());
                    cb.setTag(position);

                    cb.setOnClickListener((v)->{
                        TargetImpl d = (TargetImpl) getItem((int)v.getTag());
                        if (d.isClosed())
                            ((CheckBox)v).setChecked(true);
                        else
                            docClose(d);
                    });

                    return view;
                }else
                    return super.getView(position, view, parent);
            }
        };
    }

    private void docClose(TargetImpl target) {
        this.target = target;

        showDialog(R.id.ask_for_close_dlg);
    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        if (id == R.id.ask_for_close_dlg)
            return crateAskForCloseDlg();
        return super.onCreateDialog(id, args);
    }

    private Dialog crateAskForCloseDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.question);
        builder.setMessage("Задание не актуально?");
        builder.setPositiveButton(R.string.ok, (d,w)->closeTask());
        builder.setNegativeButton(R.string.cancel, (d,w)->adapter.notifyDataSetChanged());

        return  builder.create();
    }

    private void closeTask() {
        target.getData().closed = 1;
        target.getData().params = 0;
        target.write();
        target.close();
        ((BaseAdapter)lvDocs.getAdapter()).notifyDataSetChanged();
    }
}
