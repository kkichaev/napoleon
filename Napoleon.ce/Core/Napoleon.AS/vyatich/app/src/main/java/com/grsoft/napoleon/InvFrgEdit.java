package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.dataobjects.InvFrg;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.dataobjects.impl.InvFrgImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.InvFrgDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;

import java.util.UUID;

public class InvFrgEdit extends Activity implements SendResultListener {
    private ListView list;
    private InvFrgImpl doc = new InvFrgImpl();
    private final static String FRGID = "idfrg";
    private View btnAddItem;
    private String selId = "";
    private EditText edRemark;
    private CheckBox cbTenant;
    private View btnSend;

    public static void open(Context context, long rowid) {
        Intent i = new Intent(context, InvFrgEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invfrgedit);

        list = findViewById(R.id.list);
        btnAddItem = findViewById(R.id.btnAddItem);
        edRemark = findViewById(R.id.edRemark);
        cbTenant = findViewById(R.id.cbTenant);
        btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnScan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        if( Features.CANT_SEND_SCRIPT_PART ) {
            if(ScriptImpl.containsDocument(InvFrgDoc.instance().getObjectName(), doc.getData().created, doc.getId()) != null)
                btnSend.setVisibility(View.GONE);
        }

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        btnAddItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
        doc.close();

        list.setAdapter(new Adapter());

        OrgImpl org = new OrgImpl();
        org.read("id", doc.getId());

        TextView tv = (TextView) findViewById(R.id.tvOrg);
        tv.setText(org.getData().name);

        InvFrg d = doc.getData();
        cbTenant.setChecked(d.tenant == 1);

        ((CheckBox)findViewById(R.id.cbRetEuip)).setChecked(d.retEquip == 1);
        edRemark.setText(doc.getData().remark);

        registerForContextMenu(list);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.invfrgmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itClear) {
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            InvFrgItem i = (InvFrgItem) list.getAdapter().getItem(menuInfo.position);
            clear(i);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void send() {
        save();
        if(!doc.isEmpty()) {
            new DocumentSender(InvFrgEdit.this, findViewById(R.id.btnSend),
                    InvFrgDoc.OBJ_NAME, doc, doc.getRowid(), this).execute((Void[]) null);
        }
    }

    private void clear(InvFrgItem f) {
        if (f.isnew == 1)
            doc.getData().items.remove(f);
        else
            f.exist = 0;

        doc.write();
        doc.close();

        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
    }

    private void scan() {
        if (doc.isEditable()) {
            IntentIntegrator ii = new IntentIntegrator(InvFrgEdit.this);
            ii.initiateScan();
        }
    }

    protected void addItem() {
        if (doc.isEditable())
            showDialog(R.id.new_item_dlg);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        if (id == R.id.new_item_dlg)
            return createNewItemDlg();
        else
            return super.onCreateDialog(id);
    }

    private Dialog createNewItemDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_frg_item);
        builder.setView(View.inflate(this, R.layout.frgitemedit, null));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                appendNewItem((Dialog) dialog);
                ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    protected void appendNewItem(Dialog dialog) {
        InvFrgItem item = new InvFrgItem();
        item.isnew = 1;
        item.exist = 1;
        item.id = UUID.randomUUID().toString().replace("-", "");
        EditText ed = dialog.findViewById(R.id.edInvNum);
        item.number = ed.getText().toString().trim();

        ed = dialog.findViewById(R.id.edName);

        String name = ed.getText().toString().trim();

        if (name.length() == 0)
            name = getString(R.string.new_equip);

        item.name = name;

        ed = dialog.findViewById(R.id.edBarcode);
        item.barcode = ed.getText().toString().trim();

        doc.getData().items.add(item);
        doc.write();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (id == R.id.new_item_dlg)
            prepareNewItemDlg(dialog, args);
        super.onPrepareDialog(id, dialog);
    }

    private void prepareNewItemDlg(Dialog dialog, Bundle args) {
        ((EditText)dialog.findViewById(R.id.edInvNum)).setText("");
        ((EditText)dialog.findViewById(R.id.edName)).setText("");
        ((EditText)dialog.findViewById(R.id.edBarcode)).setText("");
    }

    @Override
    public void postSendExecute(boolean result) {
        finish();
    }

    private class Adapter extends BaseAdapter{
        @Override
        public int getCount() {
            return doc.getData().items.size();
        }

        @Override
        public Object getItem(int position) {
            return doc.getData().items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = View.inflate(InvFrgEdit.this, R.layout.invfrgeditrow, null);

            InvFrgItem i = (InvFrgItem) getItem(position);

            TextView tv = (TextView) view.findViewById(R.id.tvNumber);
            tv.setText(i.number);

            tv = (TextView) view.findViewById(R.id.tvName);
            tv.setText(i.name);

            String bc = getDocBarcode(i.id);
            tv = (TextView) view.findViewById(R.id.tvBarcode);

            String bc_val = bc.length() > 0 ? getString(R.string.barcode_val, bc) : "";
            tv.setText(bc_val);

            view.setBackgroundDrawable(getResources().getDrawable(
                    i.isnew == 1 ? R.drawable.red_row :
                    i.exist > 0 ? R.drawable.gray_row : R.drawable.list_selector));

            return view;
        }
    }

    public String getDocBarcode(String id) {
        String res = "";
        InvFrgItem i = doc.getItem(id);

        if (i != null)
            res = i.barcode;

        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String bc = scanResult.getContents();

            if (bc != null) {
                boolean f = false;

                for(InvFrgItem i : doc.getData().items)
                    if (i.barcode.equals(bc)) {
                        i.exist = 1;

                        doc.write();
                        ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                        f = true;
                        break;
                    }

                if (!f){
                    InvFrgItem item = new InvFrgItem();
                    item.isnew = 1;
                    item.exist = 1;
                    item.name = getString(R.string.new_equip);
                    item.barcode = bc;

                    doc.getData().items.add(item);

                    doc.write();
                    ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
                }

                Toast.makeText(this, "Ўрихкод: " + bc, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(doc.isEmpty())
            doc.delete();
        else
            save();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        doc.close();
        super.onStop();
    }

    void save() {
        if(doc.isEditable()) {
            InvFrg d = doc.getData();
            d.tenant = cbTenant.isChecked() ? 1 : 0;
            d.remark = edRemark.getText().toString().trim();
            d.retEquip = ((CheckBox)findViewById(R.id.cbRetEuip)).isChecked() ? 1 : 0;
            doc.write();
        }
    }
}
