package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;

import java.util.Date;

public class PartyItemEdit extends Activity {
    public static final String DATA = "data";
    public static final int ADD = 1;
    public static final int EDIT = 2;

    SalesPriceCount.AdapterData adapterData;
    DateHandler dateHandler;

    public static void open(Activity context, SalesPriceCount.AdapterData data){
        Intent i = new Intent(context, PartyItemEdit.class);
        i.putExtra(DATA, data);
        context.startActivityForResult(i, data == null ? ADD : EDIT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.party_item_view);

        adapterData =  getIntent().getParcelableExtra(DATA);

        if (adapterData == null)
            adapterData = new SalesPriceCount.AdapterData();


        EditText ed = findViewById(R.id.edNumber);
        ed.setText(adapterData.number);

        ed = findViewById(R.id.edQty);
        ed.setText(adapterData.qty);
        CheckBox cb = findViewById(R.id.cbPack);
        cb.setChecked(adapterData.pack);

        Date date = null;

        try {
            date = Util.simpleDateFormat.parse(adapterData.date);
        }catch (Exception e){
            e.printStackTrace();
        }

        dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), date, 0);

        findViewById(R.id.btnOK).setOnClickListener((v)->{
            updateData();
            Intent i = new Intent();
            i.putExtra(DATA, adapterData);
            setResult(RESULT_OK, i);
            finish();
        });

        findViewById(R.id.btnCancel).setOnClickListener((v)->finish());
    }

    private void updateData() {
        EditText ed = findViewById(R.id.edNumber);
        adapterData.number = ed.getText().toString().trim();

        Date data = dateHandler.getDate();

        if (data != null)
            adapterData.date = Util.simpleDateFormat.format(data);

        ed = findViewById(R.id.edQty);
        adapterData.qty = ed.getText().toString().trim();
        CheckBox cb = findViewById(R.id.cbPack);
        adapterData.pack = cb.isChecked();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0)
            return dateHandler.createDialog();

        return super.onCreateDialog(id);
    }
}
