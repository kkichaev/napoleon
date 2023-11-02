/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Создать накладную
 *
 * kki   24/11/2010   creating
 */
package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.ConfigHelper;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAddress;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.util.view.dialog_helper.TimeHandler;
import com.grsoft.view.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class CreateOrder extends BaseActivity {
    private OrderImpl order = (OrderImpl) OrderDoc.instance().create();

    private static final int DIALOG_DATE_PICKER_ID = 0;
    private static final int DIALOG_TIME_PICKER_ID = 1;

    private boolean editMode = false;

    private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
    private ArrayList<CharSequence> priceType = new ArrayList<CharSequence>();

    private RadioButton rbOrder, rbPrice;

    //	DateHandler dateHandler;
    TimeHandler timeHandlerFrom;
    TimeHandler timeHandlerTill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createorderex);
        init();
    }

    public static void open(Context context, OrderImpl order) {
        open(context, order, true);
    }

    public static void open(Context context, OrderImpl order, boolean editOldOrder) {
        Intent i = new Intent(context, CreateOrder.class);

        i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldOrder);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());

        context.startActivity(i);
    }

    private void init() {
        editMode = getIntent().getBooleanExtra(ExtrasConst.EDIT_MODE_STR, true);
        long orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);

        order.read(orderRowId);
        OrderEx o = (OrderEx) order.getData();

        OrgImpl oi = new OrgImpl();
        oi.getData().id = o.id;
        oi.read();
        oi.close();

        Org org = (Org) oi.getData();
        String ret = org.name;
        if (Features.SHOW_ORG_ADDRESS && org.address.length() > 0) {
            ret += "<br><i>" + org.address + "</i>";
        }
        ((TextView) findViewById(R.id.tvOrgName)).setText(Html.fromHtml(ret));

        if (!editMode)
            initOrder(o, org);

        ConfigImpl config = new ConfigImpl();

        Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
        DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, o.supplyer);

        Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
        DialogHelper.loadSpinnerFromConfig(config, "ВидЦены", priceType, spPrices, o.sumType);

        View trSklads = findViewById(R.id.trSklads);

        if (Features.WH_QTY) {
            trSklads.setVisibility(View.VISIBLE);
            Spinner spSklads = (Spinner) findViewById(R.id.spSklad);
            DialogHelper.loadSpinnerWithKeyW(config, "Склады", new ArrayList<KeyValue>(), spSklads, o.whCode, false);
            spSklads.setEnabled(o.items.size() == 0);
        } else
            trSklads.setVisibility(View.GONE);

        config.getData().key = "МожноИзменятьЦену";
        try {
            if (config.read() && Integer.parseInt(config.getData().value) == 0)
                spPrices.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.close();

        if (Features.DELIVERY_ADDRESS) {
            View v = findViewById(R.id.ftrAddress);
            if (v != null) {
                Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
                if (spAddress != null) {
                    v.setVisibility(View.VISIBLE);
                    ArrayList<KeyValue> addresses = new ArrayList<KeyValue>();
                    int selected = -1;
                    for (OrgAddress addr : oi.getData().orgAddress) {
                        KeyValue kv = new KeyValue(addr.id, addr.name);
                        if (kv.key.toString().equals(o.adrCode))
                            selected = addresses.size();
                        addresses.add(kv);
                    }
                    ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, addresses);
                    spAddress.setAdapter(aa);
                    if (selected >= 0 && selected < spAddress.getCount())
                        spAddress.setSelection(selected);
                }
            }
        }

        TextView tvDelay = (TextView) findViewById(R.id.tvDelay);
        tvDelay.setOnClickListener(new DelayClickListener());

        EditText remark = (EditText) findViewById(R.id.edCreateOrderNotes);
        remark.setText(o.remark);

        if ((o.params & ParamState.ofCash) != 0)
            ((CheckBox) findViewById(R.id.cbCreateOrderCash)).setChecked(true);

        findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, order.getDate().getTime());
                startActivityForResult(i, DIALOG_DATE_PICKER_ID);
            }
        });

//		dateHandler = new DateHandler((TextView)findViewById(R.id.tvDate), o.date, DIALOG_DATE_PICKER_ID);
//		timeHandler = new TimeHandler((TextView)findViewById(R.id.tvTime), o.date, DIALOG_TIME_PICKER_ID);

        View btnOK = findViewById(R.id.btnOK);
        btnOK.setEnabled(order.isEditable());
        btnOK.setOnClickListener(new OKClickListener());

        findViewById(R.id.btnCancel).setOnClickListener(new CancelClickListener());
        updateDisplayDelay();
        refreshDate();

        rbOrder = findViewById(R.id.rbOrder);
        rbPrice = findViewById(R.id.rbPrice);

        rbOrder.setOnCheckedChangeListener((b, c) -> rbPrice.setChecked(!c));
        rbPrice.setOnCheckedChangeListener((b, c) -> rbOrder.setChecked(!c));

        findViewById(R.id.tvOrderTimeStart).setOnClickListener(v -> showDialog(DIALOG_TIME_PICKER_ID));

        timeHandlerFrom = new TimeHandler(findViewById(R.id.tvOrderTimeStart), o.from, R.id.tvOrderTimeStart){
            public void updateDate() {
                if (date != null) {
                    SimpleDateFormat sd = new SimpleDateFormat(displayFormat(), Locale.getDefault());
                    tv.setText("Время с " + sd.format(date));
                }
            }
        };

        timeHandlerTill = new TimeHandler(findViewById(R.id.tvOrderTimeFinish), o.till, R.id.tvOrderTimeFinish){
            public void updateDate() {
                if (date != null) {
                    SimpleDateFormat sd = new SimpleDateFormat(displayFormat(), Locale.getDefault());
                    tv.setText("по " + sd.format(date));
                }
            }
        };

        findViewById(R.id.tvPriceDateStart).setOnClickListener(v->{
            Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
            i.putExtra(ExtrasConst.DATE_TAG, ((OrderEx)order.getData()).from.getTime());
            startActivityForResult(i, R.id.tvPriceDateStart);
        });

        findViewById(R.id.tvPriceDateFinish).setOnClickListener(v->{
            Intent i = new Intent(CreateOrder.this, CalendarActivity.class);
            i.putExtra(ExtrasConst.DATE_TAG, ((OrderEx)order.getData()).till.getTime());
            startActivityForResult(i, R.id.tvPriceDateFinish);
        });

        refreshDateFrom();
        refreshDateTill();
        refreshTimeFrom();
        refreshTimeTill();

        if (o.dtp == 1)
            rbPrice.setChecked(true);
        else
            rbOrder.setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == DIALOG_DATE_PICKER_ID) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            Date newDate = new Date(ct);
            order.getData().date = newDate;
            refreshDate();
        }else if (data != null && requestCode == R.id.tvPriceDateStart) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            Date newDate = new Date(ct);
            ((OrderEx)order.getData()).from = newDate;
            refreshDateFrom();
        }if (data != null && requestCode == R.id.tvPriceDateFinish) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            Date newDate = new Date(ct);
            ((OrderEx)order.getData()).till = newDate;
            refreshDateTill();
        }
    }

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private void refreshDateTill() {
        ((TextView)findViewById(R.id.tvPriceDateFinish)).setText("по " + Util.simpleDateFormat.format(((OrderEx)order.getData()).till));
    }

    private void refreshDateFrom() {
        ((TextView)findViewById(R.id.tvPriceDateStart)).setText("Дата с " + Util.simpleDateFormat.format(((OrderEx)order.getData()).from));
    }

    private void refreshTimeTill() {
        ((TextView)findViewById(R.id.tvOrderTimeFinish)).setText("по " + timeFormat.format(((OrderEx)order.getData()).till));
    }

    private void refreshTimeFrom() {
        ((TextView)findViewById(R.id.tvOrderTimeStart)).setText("Время с " + timeFormat.format(((OrderEx)order.getData()).from));
    }

    private void refreshDate() {
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        ((TextView) findViewById(R.id.tvDate)).setText(sd.format(order.getDate()));
    }

    /**
     * инициализация дополнительных полей заявки (индивидуально для проекта)
     *
     * @param o
     */
    private void initOrder(Order o, Org org) {
        o.sumType = org.costype;
        Calendar c = Calendar.getInstance();

        ((OrderEx)o).from = Util.resetTime(c.getTime());
        ((OrderEx)o).till = Util.resetTime(c.getTime());

        switch (ConfigHelper.getDateType()) {
            case workday:
                dateworkday(o);
                break;
            case nextday:
                datenextday(o);
                break;
            default:
                break;
        }
    }

    private void datenextday(Order o) {
        Calendar c = Calendar.getInstance();
        c.setTime(o.date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        o.date = c.getTime();
    }

    private void dateworkday(Order o) {
        Calendar c = Calendar.getInstance();
        c.setTime(o.date);
        c.add(Calendar.DAY_OF_MONTH, 1);

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            c.add(Calendar.DAY_OF_MONTH, 1);

        o.date = c.getTime();
    }

    private void updateDisplayDelay() {
        String text = "отсрочка: " + order.getData().delay;
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        ((TextView) findViewById(R.id.tvDelay)).setText(ss);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.tvOrderTimeStart:
                return timeHandlerFrom.createDialog();
            case R.id.tvOrderTimeFinish:
                return timeHandlerTill.createDialog();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onStop() {
        order.close();
        super.onStop();
    }

    class DelayClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Отсрочка");
            View dialogView = View.inflate(v.getContext(), R.layout.counter, null);

            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            Button btnCounterUp = (Button) dialogView.findViewById(R.id.btnCounterUp);
            Button btnCounterDown = (Button) dialogView.findViewById(R.id.btnCounterDown);
            Button btnCounterOK = (Button) dialogView.findViewById(R.id.btnCounterOk);
            Button btnCounterCancel = (Button) dialogView.findViewById(R.id.btnCounterCancel);
            final TextView tvCounter = (TextView) dialogView.findViewById(R.id.edCounter);
            tvCounter.setText(Integer.toString(order.getData().delay));
            tvCounter.setFocusable(false);

            btnCounterUp.setOnClickListener(new OnClickListenerToNotify() {

                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    int val = Integer.parseInt(tvCounter.getText().toString());
                    ++val;
                    tvCounter.setText(Integer.toString(val));
                }
            });

            btnCounterOK.setOnClickListener((x) -> {
            });

            btnCounterDown.setOnClickListener(new OnClickListenerToNotify() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    int val = Integer.parseInt(tvCounter.getText().toString());

                    if (val > 0)
                        --val;

                    tvCounter.setText(Integer.toString(val));
                }
            });

            btnCounterOK.setOnClickListener(new OnClickListenerToNotify() {

                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    order.getData().delay = Integer.parseInt(tvCounter.getText().toString());
                    updateDisplayDelay();
                    dialog.hide();
                }
            });

            btnCounterCancel.setOnClickListener(new OnClickListenerToNotify() {

                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    dialog.hide();
                }
            });

            dialog.show();
        }
    }

    class CancelClickListener extends OnClickListenerToNotify {
        @Override
        public void onClick(View v) {
            super.onClick(v);
            deleteEmptyOrder();
            finish();
        }
    }

    private void deleteEmptyOrder() {
        if (!editMode) {
            if (order.getData().items == null || order.getData().items.size() == 0)
                order.delete();
        }
    }

    class OKClickListener extends OnClickListenerToNotify {
        @Override
        public void onClick(View v) {
            super.onClick(v);

            Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
            int costType = spPrices.getSelectedItemPosition();

            if (editMode && (order.getSumType() != costType && costType >= 0))
                askToApplyNewSumType(v.getContext(), costType);
            else
                okDone(false);
        }

        private void okDone(boolean updateSumType) {
            OrderEx o = (OrderEx) order.getData();

            if (o.created == null)
                o.created = new Date();

            Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
            int suppl = spFirma.getSelectedItemPosition();
            Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
            int costType = spPrices.getSelectedItemPosition();

            if (suppl >= 0)
                o.supplyer = suppl;
            if (costType >= 0)
                o.sumType = costType;

            if (Features.WH_QTY) {
                Spinner spSklads = (Spinner) findViewById(R.id.spSklad);
                KeyValue sel = (KeyValue) spSklads.getSelectedItem();
                if (sel == null || sel.key.length() == 0) {
                    Toast.makeText(CreateOrder.this, "Выберите склад", Toast.LENGTH_LONG).show();
                    return;
                }
                if (sel != null) {
                    o.whCode = sel.key.toString();
                    o.whIndex = spSklads.getSelectedItemPosition();
                }
            }

            CheckBox cash = (CheckBox) findViewById(R.id.cbCreateOrderCash);
            if (cash.isChecked()) o.params |= ParamState.ofCash;
            else o.params &= (~ParamState.ofCash);

            EditText remark = (EditText) findViewById(R.id.edCreateOrderNotes);
            o.remark = remark.getText().toString();

            if (Features.DELIVERY_ADDRESS) {
                Spinner spAddress = (Spinner) findViewById(R.id.spAddress);
                if (spAddress != null) {
                    KeyValue sel = (KeyValue) spAddress.getSelectedItem();
                    if (sel != null)
                        o.adrCode = sel.key.toString();
                }
            }

            if (rbPrice.isChecked())
                o.dtp = 1;
            else
                o.dtp = 0;

            o.from = timeHandlerFrom.adjustTime(o.from);
            o.till = timeHandlerTill.adjustTime(o.till);

            if (updateSumType)
                order.updateItemsCost(o.sumType);
            else
                order.write();

            if (!editMode)
                Warehouse.open(CreateOrder.this, order, false);

            finish();
        }

        private void askToApplyNewSumType(Context context, final int newSumType) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Внимание");
            builder.setMessage("Тип цены был изменен, пересчитать заказ?");

            builder.setPositiveButton("Пересчитать", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    okDone(true);
                }
            });

            builder.setNegativeButton("Оставить", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    okDone(false);
                }
            });

            builder.create().show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            deleteEmptyOrder();
            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }
}
