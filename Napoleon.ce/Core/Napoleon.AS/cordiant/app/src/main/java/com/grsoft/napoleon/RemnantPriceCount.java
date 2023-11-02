package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class RemnantPriceCount extends PriceCount {
    DocType svDocType;
    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        Intent i = new Intent(context, RemnantPriceCount.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

        context.startActivity(i);
    }

    @Override protected int getContentViewId() { return R.layout.remnants_price_count; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        svDocType = DocType.getCurDoc();
        DocType.setCurDoc(RemnantsDoc.instance());

        super.onCreate(savedInstanceState);

        EditText ed;
        ed = findViewById(R.id.edFace);
        ed.setInputType(InputType.TYPE_NULL);
        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    EditText ed = (EditText)v;
                    keypadHelper.setTargetView(ed);
                    ed.selectAll();
                }
            }
        });

        ed.requestFocus();
        ed.selectAll();

        ed = findViewById(R.id.edCost);
        ed.setInputType(InputType.TYPE_NULL);
        ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    EditText ed = (EditText)v;
                    keypadHelper.setTargetView(ed);
                    ed.selectAll();
                }
            }
        });

        CheckBox cb = findViewById(R.id.cbMRC);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText ed = findViewById(R.id.edCost);
                ed.setEnabled(!isChecked);

                if (isChecked){
                    String value = Util.IntToScaleStr(getInputCost(price.getData()),
                            Consts.SUM_SCALE, Util.DEC_DELIM, false);
                    ed.setText(value);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DocType.setCurDoc(svDocType);
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(document == null) {
            document = (CreatableDocument<?>) RemnantsDoc.instance().create();
        }

        PriceEx pe = (PriceEx) price.getData();
        RemnantItemEx re = (RemnantItemEx) ((RemnantsImplEx)document).findItem(price.getData().id);
        EditText ed;
        int value;

        ed = findViewById(R.id.edCount);
        value = (re != null) ? re.qty : 0;
        ed.setText(Util.IntToScaleStr(value, Consts.QTY_SCALE));
        edCount.selectAll();

        ed = findViewById(R.id.edFace);
        value = (re != null) ? re.face : 0;
        ed.setText(Util.IntToScaleStr(value, 1));

        ed = findViewById(R.id.edCost);
        value = (re != null) ? re.cost : getInputCost(price.getData());
        ed.setText(Util.IntToScaleStr(value, Consts.SUM_SCALE, Util.DEC_DELIM, false));

        ed.setEnabled(re != null && re.mrcChanged != 0);

        CheckBox cb = findViewById(R.id.cbMRC);
        cb.setChecked(re == null || re.mrcChanged == 0);
    }

    @Override
    protected boolean updateOrder() {
        EditText ed;
        int face, qty, cost;

        ed = findViewById(R.id.edCount);
        qty = Util.StrToScale(ed.getText().toString(), Consts.QTY_SCALE);

        ed = findViewById(R.id.edFace);
        face = Util.StrToScale(ed.getText().toString(), 1);

        ed = findViewById(R.id.edCost);
        cost = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);

        int mrc = ((CheckBox)findViewById(R.id.cbMRC)).isChecked() ? 0 : 1;
        ((RemnantsImplEx)document).update(price.getData().id, cost, face, qty, mrc);
        return false;
    }
}
