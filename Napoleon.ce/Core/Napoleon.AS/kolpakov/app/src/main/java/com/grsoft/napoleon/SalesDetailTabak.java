package com.grsoft.napoleon;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.grsoft.camera.BarcodeHandler;
import com.grsoft.camera.CameraActivity;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.Gtin;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;
import com.itextpdf.text.BuildConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SalesDetailTabak extends SalesDetailEx implements SalesImplEx.AddEvents {
    private static final String TAG = "SalesDetailTabak";

    static final int ITEM_COMPLETE = -1;
    static final int ITEM_ADDED = -2;

    static List<Pair<PriceEx, Gtin>>  priceItems;

    BarcodeData barcodeData;
    String barcode;

    BarcodeScannerHelper scanner;
    Adapter adapter;
    Uri failTone;
    Uri goodTone;
    MediaPlayer mediaPlayer = null;
    CameraActivity camera;

    // block repeat fail
    String lastBC = null;

    Timer errorTimer = null;

    static public void open(Context context, SalesImplEx order) {
        Intent i = new Intent(context, SalesDetailTabak.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if(cfg.uriGood.length() > 0)
            goodTone = Uri.parse(cfg.uriGood);
        if(cfg.uriFail.length() > 0)
            failTone = Uri.parse(cfg.uriFail);

        if(BuildConfig.DEBUG) {
            View vt = findViewById(R.id.btnTest);
            vt.setVisibility(View.VISIBLE);
            vt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doTest();
                }
            });
        }


        findViewById(R.id.btnBCScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraActivity.openBCScanner(SalesDetailTabak.this, new BarcodeHandler() {
                    @Override
                    public boolean onReadBarcode(Activity owner, String barcode, int type, long elapsesMs) {
                        camera = (CameraActivity) owner;
                        if(type == Barcode.FORMAT_DATA_MATRIX) {
                            ((SalesImplEx) doc).addBarcode(barcode, SalesDetailTabak.this, owner);
                        }
                        return false;
                    }

                    @Override
                    public void initActivity(Activity owner) {
                        updateCamera(owner, 0);
                    }
                });
            }
        });
    }

    private void updateCamera(final Activity camera, int failReason) {
        TextView tv;
        tv = camera.findViewById(R.id.tvVersion);
        SalesImplEx se = (SalesImplEx)doc;
        String text = String.format("Всего / введено: %s / %s", Util.IntToScaleStr(se.getMarkPlan(), Consts.QTY_SCALE),
                Util.IntToScaleStr(se.getMarkFact(), Consts.QTY_SCALE));

        tv.setText(text);

        tv  = camera.findViewById(com.grsoft.camera.R.id.tvInfo);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        if(errorTimer != null) {
            errorTimer.cancel();
            errorTimer = null;
        }

        text = "";
        if(failReason != 0) {
            String ft = reasonToText(failReason);
            String color = failReason == SalesImplEx.FAIL_ALREADY_HAVE ? "fuchsia" :
                    failReason == ITEM_COMPLETE || failReason == ITEM_ADDED ? "green" :
                    "red";

            text = "<br/><font color='" + color +  "'>" + ft + "</font>";
            errorTimer = new Timer();
            errorTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCamera(camera, 0);
                        }
                    });
                }
            }, 5000);
        } else {
            text = "Наведите рамку сканирования на сканируемый код";
            lastBC = null;
        }
        tv.setText(Html.fromHtml(text));

    }

    int ctr = 0;
    private void doTest() {
        String[] gtins = new String[] {
                "010400639607487421hBkuEI+800518000093t07Z",
                "04006396074867qrGDIg<",
                "04006396074867MJ6ZE",
                "010400639607487421MP>S3X'800519000093OwUa",
                "04006396074867rqVU)tK",
                "04006396074867wD7eruD",
//            "04006396074867tdkfG4YAC<o/wKv",
//            "04006396074867'cEILf)AC<o1Atg",
//            "04006396074867nHqbMMjAC<oHiqJ",
//            "04006396074867xTEdx\"nAC<oEEVT",
        };
        if(ctr >= gtins.length) ctr = 0;
        handler.onRead(gtins[ctr++]);
//        if(ctr == 0) {
//            handler.onRead("00000046233370Yf.mOm4AB\"8Wpl0");
//            ctr++;
//        } else {
//            handler.onRead("00000046233387Yf.mOm4AB\"8Wpl0");
//
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if (doc.isEditable() && cfg.scannerAddress.length() > 0) {
            scanner = new BarcodeScannerHelper();
            scanner.scanning(this, cfg.scannerAddress, handler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scanner != null) {
            scanner.close();
            scanner = null;
        }

        price.close();
    }

    @Override
    protected void doPrint() {
        if(!((SalesImplEx)doc).isComplete()) {
            Toast.makeText(this, "Не все товары просканированы", Toast.LENGTH_LONG).show();
            return;
        }
        super.doPrint();
    }

    BarcodeScannerHelper.Event handler = new BarcodeScannerHelper.Event() {
        @Override
        public void onRead(final String barcode) {
            Log.d(TAG, "Read " + barcode);
            if (!doc.isEditable()) {
                return;
            }
            ((SalesImplEx) doc).addBarcode(barcode, SalesDetailTabak.this, null);
        }
    };

    @Override
    protected void onStop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
        super.onStop();
    }

    void playSound(Uri u) {
        if(u == null) {
            return;
        }

        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }

        mediaPlayer = new MediaPlayer();//.create(this, u);
        try {
            mediaPlayer.setDataSource(this, u);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.stop();
                }
            });
            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    PriceImpl price = new PriceImpl();

    int drawFactQty(int color, OrderItem item, TextView tvQty) {
        SalesItemEx se = (SalesItemEx)item;
        price.read("id", se.id);
        String text = "";
        int qty = se.factQty();
        if(qty > 0)
            text = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
        tvQty.setText(text);
        return qty;
    }

    @Override protected void setAdapter() {
        adapter = new Adapter();
        lvItems.setAdapter(adapter);
    }

    @Override protected void setContentView() {
        setContentView(R.layout.salesdetail_tabak);
    }

    @Override
    public void added(SalesItemEx item, BarcodeData data, String bc, Activity cameraActivity) {
        lastBC = bc;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraActivity != null) {
//                    Toast.makeText(camera, "Прочитано!", Toast.LENGTH_SHORT).show();
                    doc.read(doc.getRowid(), false);
                    if (((SalesEx) doc.getData()).compleete == 1)
                        camera.finish();
                    else
                        updateCamera(camera, item.scanned() ? ITEM_COMPLETE : ITEM_ADDED);
                }

                adapter.notifyDataSetChanged();
                playSound(goodTone);
            }
        });
    }

    @Override
    public void fail(BarcodeData data, String bc, final Activity cameraActivity, int reason) {
        if(lastBC != null && lastBC.equals(bc)) {
            return;
        }
        lastBC = bc;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(cameraActivity == null) {
                    String ft = reason == 0 ? "Нельзя добавить этот GTIN в накладную" : reasonToText(reason);
                    Toast.makeText(SalesDetailTabak.this, ft, Toast.LENGTH_SHORT).show();
                } else {
                    updateCamera(cameraActivity, reason);
                }
                playSound(failTone);
            }
        });
    }

    private String reasonToText(int reason) {
        switch(reason) {
            case ITEM_COMPLETE: return "Позиция подобрана";
            case ITEM_ADDED: return "Товар добавлен";
            case SalesImplEx.FAIL_NO_ITEM: return "Нет такого товара в документе";
            case SalesImplEx.FAIL_ALREADY_HAVE: return "КМ уже добавлен";
            case SalesImplEx.FAIL_ITEM_COMPLETE: return "КМ по строке набраны";
            case SalesImplEx.FAIL_MRC_MISMATCH: return "МРЦ не совпадает";
            case SalesImplEx.FAIL_DOC_FINISHED: return "Документ нельзя менять";
            case SalesImplEx.FAIL_BC_PARSING: return "Ошибка чтения ШК";
            case SalesImplEx.FAIL_QTY_MISMATCH: return "Кол-во в КМ больше доступного";
        }
        return "";
    }

    @Override
    public void needSelect(List<Pair<PriceEx, Gtin>> items, BarcodeData data, String bc, Activity cameraActivity) {
        if (camera != null) {
            Collections.sort(items, new Comparator<Pair<PriceEx, Gtin>>() {
                @Override
                public int compare(Pair<PriceEx, Gtin> p1, Pair<PriceEx, Gtin> p2) {
                    return p1.first.compareTo(p2.first);
                }
            });
            priceItems = items;
            barcodeData = data;
            barcode = bc;

            try {
                ChooseItem dlg = new ChooseItem();
                dlg.doc = (SalesImplEx) doc;
                dlg.barcodeData = barcodeData;
                dlg.barcode = barcode;
                dlg.handler = this;
                dlg.priceItems = priceItems;

                dlg.show(camera.getFragmentManager(), "");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onItemSelect(int index) {
        ((SalesImplEx)doc).tryAddBC(priceItems.get(index).second, barcodeData, barcode, this, null);
    }

    public static class ChooseItem extends DialogFragment {
        public SalesImplEx doc;
        public List<Pair<PriceEx, Gtin>> priceItems;
        public BarcodeData barcodeData;
        public String barcode;
        public SalesImplEx.AddEvents handler;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.choose_item, null);
            ListView lv = v.findViewById(R.id.lvItems);

            List<PriceEx> src = new ArrayList<>();
            for(Pair<PriceEx, Gtin> s : priceItems) {
                src.add(s.first);
            }
            ArrayAdapter<PriceEx> aa = new ArrayAdapter<PriceEx>(inflater.getContext(), R.layout.simple_spinner_layout, src);
            lv.setAdapter(aa);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    doc.tryAddBC(priceItems.get(i).second, barcodeData, barcode, handler, null);
                    dismiss();
                }
            });

            v.findViewById(R.id.btnClose).setOnClickListener(btn->getActivity().finish());

            getDialog().setTitle("Выберите товар");
            return v;
        }
    }

    class Adapter extends OrderItemsAdapter {
        @Override int getResourceID() { return R.layout.salestabak_row; }

        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
            super.drawInternal(view, name, color, item, pos);

            SalesItemEx se = (SalesItemEx) item;
            int factQty = drawFactQty(color, item, (TextView)view.findViewById(R.id.tvQtyFact));

            int t = se.qty != factQty && ((PriceEx)price.getData()).marked != 0 ? Typeface.BOLD : Typeface.NORMAL;

            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View v = vg.getChildAt(i);

                    if (v instanceof TextView) {
                        TextView tv = (TextView)v;
                        tv.setTypeface(null, t);
                    }
                }
            }
        }
    }

    @Override
    protected String[] createPrintCaption() {
        Dogovor dogovor = new Dogovor();
        DbReader reader = new DbReader();
        reader.select(dogovor, dogovor.getTableName(), String.format("id=\"%s\"", ((SalesEx)doc.getData()).contractid));
        reader.close();

        if (dogovor.black == 1)
            return new String[]{"Накладная"};
        else {
            List<String> cap = new ArrayList<>();
            cap.addAll(Arrays.asList(super.createPrintCaption()));
            cap.add("Накладная");
            return cap.toArray(new String[0]);
        }
    }
}
