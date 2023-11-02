package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.FBTransfer;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.FBTransferImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.FBTransferDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

import java.util.List;

public class FBTransferProperties extends BaseActivity {
    FBTransferImpl doc = new FBTransferImpl();
    boolean isEdit = true;
    boolean initing = false;

    static public void open(Context context, FBTransferImpl doc, boolean isNew) {
        Intent i = new Intent(context, FBTransferProperties.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        i.putExtra(ExtrasConst.EDIT_MODE_STR, !isNew);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createtransfer);

        Bundle b = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();

        isEdit = b.getBoolean(ExtrasConst.EDIT_MODE_STR, true);
        long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        doc.read(rid);

        DocType.setCurDoc(FBTransferDoc.instance());

        FBTransfer src = doc.getData();
        EditText ed = findViewById(R.id.edRemark);
        ed.setText(src.remark);

        RadioButton rbFromMe = findViewById(R.id.rbFromMe);
        RadioButton rbToMe = findViewById(R.id.rbToMe);

        if(src.direction == FBTransfer.DIRECTION_FROM_ME) {
            rbFromMe.setChecked(true);
        } else {
            rbToMe.setChecked(true);
        }

        refrehSklads(src.whId);
        initing = true;

        View btnOk = findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
                if(!isEdit) {
                    Warehouse.open(FBTransferProperties.this, doc, isEdit);
                }
            }
        });

        btnOk.setEnabled(doc.isEditable());

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                if(!isEdit) {
                    doc.delete();
                }
            }
        });
    }

    private void refrehSklads(String selected) {
        if(initing)
            return;

//        String where = showAgentSklads ? "userid <> '' and agentid <> userid" : "userid = ''";
        String where = "agentid <> userid";
        List<Sklad> sklads = DbReader.fetch(Sklad.class, where, "name");

        int sel = 0;
        for(Sklad s : sklads) {
            if(selected.equals(s.id)) {
                break;
            }
            sel++;
        }

        Spinner s = findViewById(R.id.spSklad);
        ArrayAdapter<Sklad> aa = new ArrayAdapter(this, R.layout.simple_spinner_layout, sklads);
        aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
        s.setAdapter(aa);
        if(sel < sklads.size())
            s.setSelection(sel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initing = false;
    }

    private void save() {
        FBTransfer src = doc.getData();
        src.remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();

        Sklad sel = (Sklad)((Spinner)findViewById(R.id.spSklad)).getSelectedItem();
        if(sel != null) {
            src.whId = sel.id;
            src.agent = sel.userid;
        }

        src.direction = ((RadioButton)findViewById(R.id.rbFromMe)).isChecked() ? FBTransfer.DIRECTION_FROM_ME :
                FBTransfer.DIRECTION_TO_ME;

        doc.write();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doc.close();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        outState.putBoolean(ExtrasConst.EDIT_MODE_STR, isEdit);
    }
}
