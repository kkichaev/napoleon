package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.impl.TargetImpl;
import com.grsoft.util.ExtrasConst;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TargetEdit extends Activity {
    TextView edRemark;
    TargetImpl doc = new TargetImpl();

    public static void open(Context context, long rowid){
        Intent intent = new Intent(context, TargetEdit.class);
        intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.target_edit);
        edRemark = findViewById(R.id.edRemark);

        doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
        doc.close();

        findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(TargetEdit.this, CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
                startActivityForResult(i, R.id.select_date_dlg);
            }
        });

        View btnOK = findViewById(R.id.btnOK);
        btnOK.setEnabled(doc.isEditable());
        btnOK.setOnClickListener((v)->save());
        findViewById(R.id.btnCancel).setOnClickListener((v)->finish());
        edRemark.setText(doc.getData().remark);

        refreshDate();
    }

    private void save() {
        String text = edRemark.getText().toString().trim();

        if (text.length() > 0) {
            doc.getData().remark = text;
            doc.write();
            doc.close();
        }

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && doc.getData().remark.trim().length() == 0){
            doc.delete();
            doc.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( data != null && requestCode == R.id.select_date_dlg ) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            Date newDate = new Date(ct);
            doc.getData().date = newDate;
            refreshDate();
        }
    }

    private void refreshDate() {
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        ((TextView)findViewById(R.id.tvDate)).setText(sd.format(doc.getDate()));
    }
}
