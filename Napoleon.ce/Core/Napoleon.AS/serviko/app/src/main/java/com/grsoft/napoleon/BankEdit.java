package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Bank;
import com.grsoft.dataobjects.BankItem;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.impl.BankImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.napoleon.documents.BankDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DateHandler;
import com.grsoft.view.KeypadHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BankEdit extends Activity implements SendResultListener {
    public static void open (Context context, long rowid){
        Intent intent = new Intent(context, BankEdit.class);
        intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(intent);
    }

    public static void preview(Context context, long rowid) {
        Intent intent = new Intent(context, BankEdit.class);
        intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        intent.putExtra(PREVIEW, true);
        context.startActivity(intent);
    }

    Spinner spDogovor;
    DateHandler dateHandler;
    BankImpl document = new BankImpl();
    final int DATE_DIALOG = 1;
    ListView list;
    Adapter adapter;
    protected KeypadHelper keyHelper;
    EditText edSum;
    EditText edTerminal;
    RadioButton rbTerminal;
    RadioButton rbNal;
    EditText edRemark;
    private String storePath = "";
    private static final int CAMERA_ACTIVITY = 0x181212; //1;
    ImageView btnPhoto;
    private static final String PREVIEW = "preview";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_edit);

        boolean preview = getIntent().getBooleanExtra(PREVIEW, false);

        spDogovor = findViewById(R.id.spDogovor);
        list = findViewById(R.id.list);
        edSum = findViewById(R.id.edSum);
        edTerminal = findViewById(R.id.edTerminal);
        rbTerminal = findViewById(R.id.rbTerminal);
        rbNal = findViewById(R.id.rbNal);
        edRemark = findViewById(R.id.edRemark);
        btnPhoto = findViewById(R.id.btnPhoto);

        if (!preview)
            btnPhoto.setOnClickListener(this::doPhoto);

        btnPhoto.setOnLongClickListener(this::photoLongClick);

        View btnOk = findViewById(R.id.btnOK);
        btnOk.setOnClickListener(this::doOK);


        document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
        document.close();

        edRemark.setText(document.getData().remark);

        if (!preview) {
            rbTerminal.setOnCheckedChangeListener((v, c) -> {
                if (c) {
//                    edSum.setText("0");
                    edTerminal.setEnabled(true);
                    btnPhoto.setVisibility(View.VISIBLE);

                    keyHelper.setTargetView(edTerminal);
                    edTerminal.post(() -> {
                        edTerminal.requestFocus();
                        edTerminal.selectAll();
                    });
//                    list.setOnItemClickListener(null);
//                    document.getData().items.clear();
//                    adapter.notifyDataSetChanged();
                }
            });

            rbNal.setOnCheckedChangeListener((v, c) -> {
                if (c) {
                    edTerminal.setText("0");
                    edTerminal.setEnabled(false);
                    btnPhoto.setVisibility(View.INVISIBLE);

                    edSum.requestFocus();
                    keyHelper.setTargetID(-1);
//                    list.setOnItemClickListener(selectIncass);

                    if (document.getData().photo.length() > 0) {
                        PicStoreImpl p = new PicStoreImpl();
                        p.read("id", document.getData().photo);
                        p.delete();

                        document.getData().photo = "";
                        document.write();
                        document.close();

                        btnPhoto.setImageResource(R.drawable.photo);
                    }
                }
            });
        }

        keyHelper = createKeypadHelper();
        keyHelper.setTargetID(-1);

        edSum.setInputType(InputType.TYPE_NULL);
        edTerminal.setInputType(InputType.TYPE_NULL);

        if (!preview)
            edTerminal.setOnFocusChangeListener((v,h)->{
                if (h){
                    edTerminal.post(()->edTerminal.selectAll());
                }
            });

        List<Firm> data = new ArrayList<>();
        for (Firm d : DbReader.fetch(Firm.class))
            data.add(d);

        Collections.sort(data, new Comparator<Firm>() {

            @Override
            public int compare(Firm lhs, Firm rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });

        Bank bank = document.getData();
        if(!preview)
            data.add(0, new Firm());
        ArrayAdapter<Firm> filter = new ArrayAdapter<Firm>(this, R.layout.simple_spinner_layout, data);
        spDogovor.setAdapter(filter);

        TextView tvd = findViewById(R.id.tvDate);
        dateHandler = new DateHandler(tvd, bank.date, DATE_DIALOG);
        tvd.setEnabled(!preview);
        dateHandler.setHandler(new DateHandler.Handler() {
            @Override
            public boolean canSetDate(Date newDate) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Util.resetTime(cal.getTime()));

                return newDate.getTime() >= cal.getTimeInMillis();
            }
        });

        adapter = new Adapter(this, document, preview);
        list.setAdapter(adapter);

        if (!preview)
            list.setOnItemClickListener(selectIncass);

        if (((Bank)document.getData()).dogovor.length() > 0){
            for(int i = 1; i < data.size(); i++){
                Firm d = data.get(i);
                if (d.id.equals(((Bank)document.getData()).dogovor)){
                    final int pos = i;
                    spDogovor.post(()->{spDogovor.setSelection(pos, true);});
                    break;
                }
            }
        }


        spDogovor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean inited = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Firm d = (Firm) parent.getItemAtPosition(position);
                enableControls(position != 0);
                adapter.refreshData(d.id);
                document.getData().dogovor = d.id;

                if (inited && !preview)
                    document.getData().items.clear();
                inited = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        if (document.getData().mode == Bank.CASH_MODE){
            int sum = 0;

            for (BankItem i : document.getData().items)
                sum += i.sum;

            edSum.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE));
        }else {
            edTerminal.setText(Util.IntToScaleStr(document.getData().terminal, Consts.SUM_SCALE));
        }

        rbTerminal.setChecked(document.getData().mode == Bank.TERMINAL_MODE);

        btnOk.setEnabled(document.isEditable());

        if (preview) {
            btnOk.setVisibility(View.GONE);
            spDogovor.setEnabled(false);
            updateSum();
            if(document.getData().mode == Bank.TERMINAL_MODE) {
                findViewById(R.id.btnPhoto).setVisibility(View.VISIBLE);
            }
        }

        findViewById(R.id.btnSend).setOnClickListener(this::send);
    }

    private boolean photoLongClick(View view) {
        if (document.getData().photo.length() > 0) {
            ManagePhotoDlg dlg = new ManagePhotoDlg();
            Bundle args = new Bundle();
            args.putString(ManagePhotoDlg.PIC_ID, document.getData().photo);
            args.putBoolean(ManagePhotoDlg.REJECT_DELETE, true);
            dlg.setArguments(args);
            dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
        }
        return true;
    }

    private void send(View v) {
        if(document.isExported())
            return;

        if(!updateDoc()) {
            return;
        }

        if(document.isEmpty()) {
            Toast.makeText(this, R.string.complete_doc_before, Toast.LENGTH_LONG).show();
            return;
        }
        document.write();

        List<DocExportListener> docs = new ArrayList<>();
        docs.add(new DocSendListner(BankDoc.instance().getObjectName(), document, document.getRowid()));
        docs.add(IncassDoc.instance().getDirtyDocuments());

        DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), docs, this);
        ds.execute((Void) null);
    }

    private static final String COUNTER = "counter";

    private void doPhoto(View view) {
        File path = new File(Path.getDataDir());
        path.mkdir();
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int cnt = pref.getInt(COUNTER, 1);
        File file = new File(getExternalFilesDir(null), "bank_" + Integer.toString(cnt) + ".jpg");
        storePath = file.getAbsolutePath();
        SharedPreferences.Editor ed = pref.edit();
        ed.putInt(COUNTER, ++cnt);
        ed.commit();

        CameraPreview.takePhoto(this, storePath, CAMERA_ACTIVITY);
    }

    boolean updateDoc() {
        Bank doc = document.getData();

        if (rbTerminal.isChecked() && doc.photo.length() == 0){
            Toast.makeText(this, R.string.need_photo, Toast.LENGTH_SHORT).show();
            return false;
        }

        doc.dogovor = ((Firm)spDogovor.getSelectedItem()).id;
        doc.mode = rbNal.isChecked() ? Bank.CASH_MODE : Bank.TERMINAL_MODE;
        doc.terminal = (int)Util.StrToScale(edTerminal.getText().toString().trim(), Consts.SUM_SCALE);
        doc.remark = edRemark.getText().toString().trim();
        return true;
    }

    private void doOK(View view) {
        if(!updateDoc()) {
            return;
        }

        if (document.isEmpty())
            document.delete();
        else
            document.write();

        document.close();
        finish();
    }

    AdapterView.OnItemClickListener selectIncass = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            IncassDebDistrEx incass = (IncassDebDistrEx) parent.getItemAtPosition(position);

            boolean contains = false;

            for (BankItem i : document.getData().items){
                if (i.incass.getTime() == incass.created.getTime()){
                    contains = true;
                    document.getData().items.remove(i);
                    break;
                }
            }

            if (!contains) {
                BankItem i = new BankItem();
                i.incass = incass.created;
                i.sum = incass.sum;
                document.getData().items.add(i);
            }

            adapter.notifyDataSetChanged();
            updateSum();
        }
    };

    private void updateSum() {
//        int sum = 0;
//
//        for (BankItem i : document.getData().items)
//            sum += i.sum;
//
        edSum.setText(Util.IntToScaleStr(document.sum(), Consts.SUM_SCALE));
    }

    protected KeypadHelper createKeypadHelper() {
        return new KeypadHelper(this, R.id.edTerminal, false);
    }

    private void enableControls(boolean enabled) {
        for (int id : new int []{R.id.rbTerminal, R.id.rbNal, R.id.tvDate, R.id.edSum, R.id.edTerminal, R.id.edRemark})
            findViewById(id).setEnabled(enabled);

        keyHelper.setEnabled(enabled);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_DIALOG:
                return dateHandler.createDialog();
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (document.isEditable() ) {
            if(document.isEmpty())
                document.delete();
            else
                document.write();
        }
    }

    @Override
    public void postSendExecute(boolean result) {
        if(result) {
            document.read(document.getRowid(), false);
            finish();
        }
    }

    public static class Adapter extends BaseAdapter{
        private final Context context;
        private final BankImpl document;
        OrgImpl org = new OrgImpl();
        List<IncassDebDistrEx> list = new ArrayList<>();
        List<IncassDebDistrEx> data = new ArrayList<>();

        public Adapter(Context context, BankImpl document, boolean preview){
            this.context = context;
            this.document = document;

            Calendar c = Calendar.getInstance();
            c.add(Calendar.WEEK_OF_YEAR, -3);
            DatePeriod dp = new DatePeriod(c.getTime(), new Date());

            long crdate = document.getData().created.getTime();
            Set<Long> toRemove = new HashSet<>();
            if(!preview) {
                for (Document d : BankDoc.instance().docList(null, "created DESC", dp)) {
                    Bank b = (Bank) d.getData();

                    if (b.created.getTime() == crdate)
                        continue;

                    for (BankItem i : b.items)
                        toRemove.add(i.incass.getTime());
                }
            }
            Bank src = document.getData();
            for (IncassDebDistrEx incas :  DbReader.fetch(IncassDebDistrEx.class, String.format("created >= %d and created < %d", c.getTimeInMillis(), new Date().getTime()), "created DESC")){
                if(preview) {
                    if(src.contains(incas))
                        list.add(incas);
                } else {
                    if (!toRemove.contains(incas.created.getTime()))
                        list.add(incas);
                }
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = View.inflate(context, R.layout.bank_edit_row, null);

            IncassDebDistrEx incas = (IncassDebDistrEx) getItem(position);
            org.read("id", incas.id);

            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(org.getData().name);

            tv = view.findViewById(R.id.tvAddress);
            tv.setText(org.getData().address);

            tv = view.findViewById(R.id.tvDate);
            tv.setText(Util.simpleDateFormat.format(incas.created));

            tv = view.findViewById(R.id.tvSum);
            tv.setText(Util.IntToScaleStr(incas.sum, Consts.SUM_SCALE));

            boolean contains = false;

            for(BankItem i : document.getData().items)
                if (i.incass.getTime() == incas.created.getTime()){
                    contains = true;
                    break;
                }

            view.setBackgroundResource(contains ? R.drawable.list_green_selector :
                            (position % 2) != 0 ? R.drawable.even_row_selector
                                    : R.drawable.list_selector);

            return view;
        }

        public void refreshData(String id){
            data.clear();

            for(IncassDebDistrEx i : list) {
                if (i.dogovor.equals(id))
                    data.add(i);
            }

            notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_ACTIVITY && resultCode == Activity.RESULT_OK && storePath.trim().length() > 0) {

            PicStoreImpl picStore = new PicStoreImpl();
            picStore.getData().id = UUID.randomUUID().toString().replace("-", "");
            picStore.getData().picture = storePath.getBytes();
            picStore.getData().date = document.getData().created;
            picStore.getData().created = Util.getDateTime();
            picStore.write();
            picStore.close();

            document.getData().photo = picStore.getData().id;

            storePath = "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (document.getData().photo.length() > 0){
            PicStoreImpl picStore = new PicStoreImpl();

            if (picStore.read("id", document.getData().photo)) {
                btnPhoto.setImageDrawable(
                        BitmapUtils.createBitmap(this, new String(picStore.getData().picture),
                        100, 100));
            }
        }
    }
}
