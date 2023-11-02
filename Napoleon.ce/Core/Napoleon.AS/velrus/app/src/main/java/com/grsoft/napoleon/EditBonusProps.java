package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SalesBonus;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesBonusImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesBonusDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EditBonusProps extends BaseActivity {
    boolean editMode;
    SalesBonusImpl doc;

    public static void open(Context context, SalesBonusImpl doc, boolean isOldDoc) {
        Intent i = new Intent(context, EditBonusProps.class);
        i.putExtra(ExtrasConst.EDIT_MODE_STR, isOldDoc);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bouns_props);

        DocType.setCurDoc(SalesBonusDoc.instance());

        editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
        long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);

        doc = new SalesBonusImpl();
        doc.read(orderRowId);
        final SalesBonus o = (SalesBonus) doc.getData();

        OrgImpl oi = new OrgImpl();
        oi.getData().id = o.id;
        oi.read();
        oi.close();

        OrgEx org = (OrgEx) oi.getData();
        String ret = org.name;
        if(Features.SHOW_ORG_ADDRESS && org.address.length() > 0 ) {
            ret += "<br><i>" + org.address + "</i>";
        }
        ((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

        loadLinkedDocs(o);

        findViewById(R.id.btnCancel).setOnClickListener(view -> {
            if(doc.isEmpty()) {
                doc.delete();
            }
            finish();
        });

        View ok = findViewById(R.id.btnOK);
        ok.setOnClickListener(view -> {
            if(saveDoc()) {
                finish();
                if(!editMode) {
                    Warehouse.open(EditBonusProps.this, doc, editMode);
                }
            }
        });
        ok.setEnabled(doc.isEditable());

        ((EditText)findViewById(R.id.notes)).setText(o.remark);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(doc.isEditable() && doc.isEmpty()) {
            doc.delete();
        }
    }

    private boolean saveDoc() {
        SalesBonus sb = (SalesBonus) doc.getData();

        sb.remark = ((EditText)findViewById(R.id.notes)).getText().toString();

        DeliveryDoc dd = (DeliveryDoc) ((Spinner)findViewById(R.id.linked_doc)).getSelectedItem();
        if(dd == null) {
            Toast.makeText(this, R.string.need_select_linked_doc, Toast.LENGTH_LONG).show();
            return false;
        }
        sb.docNumber = dd.number;
        sb.docDate = dd.date;

        doc.write();
        SalesBonusDoc.instance().refreshDocSum(sb.id);
        return true;
    }

    private void loadLinkedDocs(SalesBonus o) {
        List<DeliveryDoc> ddocs = new ArrayList<>();
        try {
            String stmt = "select d.number, d.date from Sales d left join SalesBonus b " +
                    " on d.number = b.docNumber and d.date = b.docDate " +
                    " where d.id = '" + o.id + "' and  (b.docNumber is null or b.docNumber = '" + o.docNumber + "')";

            Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
            while(c.moveToNext()) {
                ddocs.add(new DeliveryDoc(c.getString(0), c.getLong(1)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(ddocs);

        Spinner sp = findViewById(R.id.linked_doc);
        ArrayAdapter<DeliveryDoc> aa = new ArrayAdapter<DeliveryDoc>(this, R.layout.simple_spinner_layout, ddocs);
        aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
        sp.setAdapter(aa);
        int selected = ddocs.indexOf(new DeliveryDoc(o.docNumber, o.docDate.getMonth()));
        if( selected >= 0 )
            sp.setSelection(selected);
    }

    @TableInfo(name="Delivery")
    public static class DeliveryDoc extends DataObject implements Comparable<DeliveryDoc> {
        public String number = "";
        public Date date = new Date();

        public DeliveryDoc() {}
        public DeliveryDoc(String n, long d) {
            number = n;
            date = new Date(d);
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            return "№ " + number + " от " + sdf.format(date);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeliveryDoc that = (DeliveryDoc) o;
            return Objects.equals(number, that.number) && Objects.equals(date, that.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, date);
        }

        @Override
        public int compareTo(DeliveryDoc o) {
            int cmp = date.compareTo(o.date);
            return cmp == 0 ? number.compareTo(o.number) : cmp;
        }
    }
}
