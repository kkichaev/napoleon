package com.grsoft.napoleon.dostavka;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RoutePointEx;
import com.grsoft.dataobjects.ShipmentItem;
import com.grsoft.dataobjects.Waybill;
import com.grsoft.dataobjects.WaybillItem;
import com.grsoft.dataobjects.impl.DShipmentImpl;
import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.DShipmentDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class DShipmentEditEx extends DShipmentEdit implements SendResultListener {
    TextView tvTitle;

    @Override
    protected int getLayoutID() {
        return R.layout.dshipmenteditex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTitle = findViewById(R.id.tvTitle);

        DispatchImpl dsp = DispatchImpl.create();
        dsp.read(doc.getData().dispatch.getTime());

        for (DispatchItem i : dsp.getData().items)
            if (i.number.equals(doc.getData().number))
                tvTitle.setText(i.title);

        findViewById(R.id.btnScan).setOnClickListener((v) -> {
            doc.write();
            ShipmentScan.openScan(DShipmentEditEx.this, (DShipmentImpl) doc);
        });
        findViewById(R.id.btnClose).setOnClickListener((v) -> saveAndExit());
        findViewById(R.id.btnSend).setOnClickListener((v) -> saveAndSend());
    }

    @Override
    public void postSendExecute(boolean result) {
        if (result)
            finish();
    }

    interface ICompleteError {
        void showErr(String msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doc.read();
        adapter.reload();
    }

    private void updateConfig() {
        Config config = ConfigManager.getConfig();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        config.address = pref.getString(getString(R.string.ip1_pref), "");
        config.address2 = pref.getString(getString(R.string.ip2_pref), "");
        config.port = Integer.parseInt(pref.getString(getString(R.string.port_pref), getString(R.string.def_port_val)));
        config.login = pref.getString(getString(R.string.login_pref), "");
        config.passw = pref.getString(getString(R.string.pass_pref), "");

        ConfigManager.save();
    }

    private void saveError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    boolean isItemsScanned() {
        PriceImpl pi = new PriceImpl();
        PriceEx pe = (PriceEx) pi.getData();
        for(DWaybillDocumentItem di : doc.getData().items) {
            pe.id = di.id;
            pi.read();
            if(pe.barcode.length() == 0)
                continue;
            if(!((ShipmentItem)di).isScanned()) {
                pi.close();
                return false;
            }
        }
        pi.close();
        return true;
    }

    private boolean checkDocument(ICompleteError errorHndl) {
        RoutePointImpl ri = new RoutePointImpl();
        ri.read("id", doc.getId());
        if(((RoutePointEx)ri.getData()).checkMark > 0 && !isItemsScanned()) {
            doc.write();
            ShipmentScan.openScan(this, (DShipmentImpl) doc);
            errorHndl.showErr("Необходимо отсканировать товар");
            return false;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < doc.getData().items.size(); i++) {
            DWaybillDocumentItem d = doc.getData().items.get(i);

            if (d.inqty != d.outqty && d.cause.trim().length() == 0) {
                if (sb.length() > 0)
                    sb.append(",");
                sb.append(i + 1);
            }
        }

        if (sb.length() > 0) {
            String msg = String.format("В строках %s не указана причина", sb.toString());
            errorHndl.showErr(msg);
        }

        return sb.length() == 0;
    }

    public int getItemLayout() {
        return R.layout.dlvitemrowex;
    }

    @NonNull
    @Override
    public Adapter createAdapter() {
        return new Adapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View res = super.getView(position, convertView, parent);

                DWaybillDocumentItem i = (DWaybillDocumentItem) getItem(position);
                TextView tv = (TextView) res.findViewById(R.id.tvQty);
                tv.setText(Util.IntToScaleStr(i.inqty, Consts.QTY_SCALE));

                tv = (TextView) res.findViewById(R.id.tvSum);
                tv.setText(Util.IntToScaleStr(i.outqty, Consts.QTY_SCALE));


                res.setBackground(i.inqty == i.outqty ?
                        getResources().getDrawable(R.drawable.green_selector) :
                        getResources().getDrawable(R.drawable.list_selector));
                return res;
            }

            @Override
            public int getTextColor(DWaybillDocumentItem i) {
                return getResources().getColor(R.color.black);
            }
        };
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (id == R.id.not_barcode_dlg)
            BarcodeDlgHelper.prepareNotBarcodeDlg(dialog, args);

        super.onPrepareDialog(id, dialog, args);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.not_barcode_dlg)
            return BarcodeDlgHelper.createNotBarcodeDlg(this);
        return super.onCreateDialog(id);
    }

    private boolean tryOpenItem(String barcode) {
        Waybill sr = new Waybill();
        DbReader r = new DbReader();

        String number = ((DShipmentImpl) doc).getData().number;

        if (r.select(sr, sr.getTableName(), String.format("number='%s'", number))) {
            for (DeliveryItem di : sr.items) {
                if (((WaybillItem) di).barcode.equals(barcode)) {
                    openInputDlg(di.id);
                    return true;
                }
            }
        }
        return false;
    }

    private void openInputDlg(String id) {
        for (int i = 0; i < adapter.getCount(); i++) {
            DWaybillDocumentItem item = (DWaybillDocumentItem) adapter.getItem(i);
            final int idx = i;

            if (item.id.equals(id))
                runOnUiThread(() -> {
                    list.performItemClick(null, idx, 0);
                });
        }
    }

    void saveAndExit() {
        if (checkDocument((msg) -> saveError(msg))) {
            doc.write();
            finish();
        }
    }

    private void saveAndSend() {
        if (checkDocument((msg) -> saveError(msg))) {
            doc.setReadyToSend();
            doc.write();

            updateConfig();
            new DocumentSender(this, findViewById(com.grsoft.napoleon.R.id.btnSend),
                    DShipmentDoc.OBJECT_NAME, doc, doc.getRowid(), this).execute();
        }
    }


    @Override
    protected void changeItemQty(final DWaybillDocumentItem item) {
        final QtyDecorator decorator = new QtyDecorator(item);

        InputNumberDlg.open(DShipmentEditEx.this, new InputNumber() {

            @Override
            public boolean isValid(int value, Object... params) {
                if (value > item.inqty) {
                    Toast.makeText(DShipmentEditEx.this, getString(R.string.qty_more_than_delivery), Toast.LENGTH_LONG).show();
                    return true;
                }
                if (value < item.inqty && decorator.selectedValue().length() == 0) {
                    Toast.makeText(DShipmentEditEx.this, getString(R.string.select_cause), Toast.LENGTH_LONG).show();
                    return false;
                }
                return super.isValid(value, params);
            }

            @Override
            public void applayInput(int value, Object... params) {
                if (value >= item.inqty) {
                    item.outqty = item.inqty;
                    adapter.notifyDataSetChanged();
                    return;
                }
                if (value < item.inqty) {
                    item.outqty = value;
                    item.cause = item.outqty == item.inqty ? "" : decorator.selectedValue();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public int getValue() {
                return item == null ? 0 : item.outqty;
            }
        }, Consts.QTY_SCALE, true, "Ввести количество принятого товара", false, decorator);
    }

    public void updateDocOnBack() {
        boolean del = true;

        for(DWaybillDocumentItem i : doc.getData().items) {
            if (i.outqty > 0) {
                del = false;
                break;
            }
        }

        if (del) {
            doc.delete();
            doc.close();
        }
    }
}
