package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.grsoft.database.SalesResultHitching;
import com.grsoft.dataobjects.Actionable;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.dataobjects.impl.SalesBaseImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.GraphicPrinter;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.util.MessageBox;
import com.grsoft.util.gps.GPSUtilNew;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SalesDetailEx extends SalesDetail {

    ActionHelper actionHelper = new ActionHelper();

    @Override
    protected String[] createPrintCaption() {
		SalesEx se = (SalesEx)doc.getData();
        boolean isBlack = se.black != 0;
        return isBlack ? new String[] { "Накладная" } :
                new String[] {
                        NPrinter.TORG_12_CAPTION, NPrinter.SCHET_FACT_CAPTION,
                        NPrinter.UPD_CAPTION,
                        "Удостоверение качества"};
    }

    @Override
    protected void deleteItem(OrderItem orderItem) {
        super.deleteItem(orderItem);
        ((SalesImplEx)doc).removeActions(new HashSet<>());
        updateBonus();
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.salesdetailex);
    }

    public void updateBonus() {
        SalesImplEx b = ((SalesImplEx) doc).getBonus();
        findViewById(R.id.bonus).setVisibility((b != null && !b.isEmpty()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(((SalesEx)doc.getData()).bonus > 0)
            return true;

        if (item.getItemId() == MNU_PKO_ID) {
            PkoImpl pko = PkoImpl.fromSales((SalesBaseImpl<?>)doc, GPSUtilNew.getLastKnownLocation(), this);
            pko.getData().number = doc.getData().number;
            pko.write();
            pko.open(this);

            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void send() {
        List<Long> ids = new ArrayList<>();
        ids.add(doc.getData().created.getTime());
        SalesImplEx b = ((SalesImplEx) doc).getBonus();
        if(b != null && !b.isEmpty()) {
            ids.add((b.getData().created.getTime()));
        }
        DocList dl = new DocList(SalesImplEx.class, ids);
        DocSendListner dsl = new DocSendListner(SalesDoc.instance().getObjectName(), dl);
        new DocumentSender(this, btnSend, dsl, this).execute((Void[])null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnGetNumber).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                SalesResultHitching.result = 0;
                SalesResultHitching.message = "";
                send();
            }
        });

        boolean isBonus = ((SalesEx) doc.getData()).bonus > 0;
        if(isBonus) {
            findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
            findViewById(R.id.tabBtn).setVisibility(View.GONE);
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        } else {
            ((SalesImplEx)doc).setActionHandler(haveBonus -> updateBonus());

            updateBonus();
            findViewById(R.id.bonus).setOnClickListener(v -> {
                ((SalesImplEx) doc).getBonus().open(SalesDetailEx.this);
            });

            if(actionHelper.applyToAll().size() > 0) {
                View ab = findViewById(R.id.btnAction);
                ab.setVisibility(View.VISIBLE);
                ab.setOnClickListener(v -> {
                    actionHelper.showApplyToAll(SalesDetailEx.this, doc);
                });
            }
        }
    }

    void updateButtons() {
        if(BuildConfig.FLAVOR.equals("vanTest")) {
            findViewById(R.id.btnPrint).setEnabled(true);
            findViewById(R.id.btnGetNumber).setEnabled(true);
        } else {
            findViewById(R.id.btnPrint).setEnabled(!doc.isEditable());
            findViewById(R.id.btnGetNumber).setEnabled(doc.isEditable());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBonus();
        updateButtons();
    }

    @Override
    public void postSendExecute(boolean result) {
        if(result) {
            doc.read(doc.getRowid(), false);
            if(doc.getData().number.length() == 0) {
                doc.getData().params = 0;
                doc.write();
            }
            updateButtons();
            if(SalesResultHitching.result == 0) {
                MessageBox.show(this, getString(R.string.error), SalesResultHitching.message);
            }
        }
    }

    @Override
    protected SelectPrinFormDlg createPrintDlg() {
        return new PrintDlg( WAIT_FOR_PRINT_DLG, (SalesImplEx)doc);
    }

    class PrintDlg extends SelectPrintFormDlgNew {

        SalesImplEx doc;
        public PrintDlg(int waitDlgid, SalesImplEx doc) {
            super(waitDlgid);
            this.doc = doc;
        }

        protected void doPrint(DialogInterface dialog) {
            Map<DataSource, List<String>> src = new HashMap<>();

            int copy = 1;
            ListView list = (ListView) ((Dialog)dialog).findViewById(R.id.list);
            if(list != null){
                Adapter a = (Adapter) list.getAdapter();
                SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();

                List<String> forms = new ArrayList<>();
                for(int i=0; i < a.getCount(); i++){
                    Adapter.Data d = (Adapter.Data) a.getItem(i);
                    ed.putInt(d.text, d.val);

                    for(int j = 0; j < d.val; j++) {
                        forms.add(d.text);
                    }
                    if(copy < d.val) copy = d.val;
                }

                ed.commit();

                src.put(dataSource, forms);
                SalesImplEx bonus = doc.getBonus();
                if(bonus != null) {
                    SalesEx bd = (SalesEx) bonus.getData();
                    String form = bd.blackBonus > 0 ? "Накладная" : NPrinter.UPD_CAPTION;
                    try {
                        DataSource bonusSrc = createPrintSource(bd);
                        forms = new ArrayList<>();
                        for (int j = 0; j < copy; j++) {
                            forms.add(form);
                        }
                        src.put(bonusSrc, forms);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SalesDetailEx.doPrint((Activity)context, waitDlgid, new ArrayList<>(src.keySet()), source -> src.get(source));
            }
        }
    }
    public static void doPrint(final Activity activity, final int dialogid, List<DataSource> sources, final GraphicPrinter.SourcePrintList printList) {
        new AsyncTask<GraphicPrinter.SourcePrintList, Void, File>(){
            protected void onPreExecute() { activity.showDialog(dialogid); };

            @Override
            protected File doInBackground(GraphicPrinter.SourcePrintList... params) {
                File result = null;

                try {
                    if (params.length > 0) {
                        GraphicPrinter gp = new GraphicPrinter();
                        result = gp.print(activity, sources, params[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            protected void onPostExecute(File output) {
                activity.dismissDialog(dialogid);
                if(output != null){
                    NPrinter.sendPrintTask(activity, output);
                }
            };
        }.execute(printList);
    }
}
