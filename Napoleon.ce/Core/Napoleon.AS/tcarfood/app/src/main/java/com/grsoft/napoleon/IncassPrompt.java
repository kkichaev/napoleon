package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class IncassPrompt extends IncassDebDistrEdit{
    public static String PDUE = "PDUE";
    public int pdue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pdue = getIntent().getIntExtra(PDUE, 0);
        showDialog(R.id.pdue_dlg);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == R.id.pdue_dlg)
            return createPdueDlg();

        return super.onCreateDialog(id);
    }

    @SuppressLint("StringFormatMatches")
    private Dialog createPdueDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert);

        OrgImpl org = new OrgImpl();
        org.getData().id = doc.getId();
        org.read();
        org.close();

        int due = ((OrgEx)org.getData()).due;
        int postdue = pdue;
        int percent = 0;

        if(postdue != 0)
            percent = (int)(((float)postdue / (float)due) * 100);

        builder.setMessage(getString(R.string.alert_due_msg,
                Util.IntToScaleStr(due, Consts.SUM_SCALE),
                Util.IntToScaleStr(postdue, Consts.SUM_SCALE),
                percent));

        builder.setPositiveButton(R.string.ok, (d,w)->{});

        return builder.create();

    }

    @Override
    public void postSendExecute(boolean result) {
        super.postSendExecute(result);

        if (result) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        doc.delete();
        doc.write();
    }

    @Override
    protected void send() {
        if (getSum() < pdue)
            Toast.makeText(this, getString(R.string.prompt_incass_msg, Util.IntToScaleStr(pdue, Consts.SUM_SCALE)), Toast.LENGTH_SHORT).show();
        else
            super.send();
    }
}
