package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.util.ExtrasConst;

public class CreateMerch extends Activity {
    public static void open(Context context, long rowid){
        Intent intent = new Intent(context, CreateMerch.class);
        intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(intent);
    }

    MerchImpl doc = new MerchImpl();
    EditText edRemark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createmerch);

        edRemark = findViewById(R.id.edRemark);
        findViewById(R.id.btnOK).setOnClickListener((v)->doOK());
        findViewById(R.id.btnCancel).setOnClickListener((v)->finish());

        doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
        doc.close();
    }

    private void doOK() {
        doc.getData().remark = edRemark.getText().toString().trim();
        doc.write();
        doc.close();

        Warehouse.open(this, doc, false);
        finish();
    }
}
