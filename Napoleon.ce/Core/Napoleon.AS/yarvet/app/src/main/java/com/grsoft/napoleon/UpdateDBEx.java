package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgTaskHitching;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDone;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TaskDoneDoc;

public class UpdateDBEx extends UpdateDB{
    @Override
    protected void closeActivity() {
        int tc = getNewTaskCount();

        if (tc > 0)
            showTaskDlg(tc);
        else
            super.closeActivity();
    }

    private void showTaskDlg(int count) {
        AlertDialog.Builder sb = new AlertDialog.Builder(this);
        sb.setTitle("Новые задачи!");
        sb.setMessage(String.format("У вас %d задач", count));
        sb.setNegativeButton("Просмотр", (d,w)->{
            TaskListView.open(UpdateDBEx.this);
            finish();
        });
        sb.setPositiveButton(R.string.ok, (d,w)->{finish();});
        sb.setCancelable(true);
        sb.show();
    }

    private int getNewTaskCount(){
        int res = 0;

        String sql= "select count(*) from agentorgtask t left join OrgTaskOld o on t.id=o.id where o.id is null";

        Cursor c = null;
        try {
            c = DataBaseManager.getDataBase().rawQuery(sql, null);
            if( c.moveToNext())
                res = c.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( c != null )
                c.close();
        }

        return res;
    }

    @NonNull
    @Override
    public Hitching getOrgTaskHitching() {
        return new OrgTaskHitching();
    }
}
