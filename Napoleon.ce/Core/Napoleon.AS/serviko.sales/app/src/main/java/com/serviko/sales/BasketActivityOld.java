package com.serviko.sales;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.serviko.dataobjects.Basket;
import com.serviko.dataobjects.BasketItem;
import com.serviko.dataobjects.OrderSend;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.ws.ReqCodeParam;
import com.serviko.dataobjects.ws.ReqOrdersParam;
import com.serviko.dataobjects.ws.ReqOrdersResult;
import com.serviko.dataobjects.ws.SendBasketParam;
import com.serviko.dataobjects.ws.WSExchange;
import com.serviko.view.PriceQtyPickerOld;
import com.serviko.view.TextViewCrossOut;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BasketActivityOld extends BaseActivityOld implements PictureHolder.Handler {
    Adapter adapter;

    boolean sending = false;

    public static void open(Context context) {
        Intent i = new Intent(context, BasketActivityOld.class);
        context.startActivity(i);
    }

    @Override protected int getLayoutID() { return R.layout.basket_old; }
    @Override protected int getBottomMenuID() { return R.id.itBasket; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lv = findViewById(R.id.lvItems);
        adapter = new Adapter();
        lv.setAdapter(adapter);

        final MaterialToolbar mb = findViewById(R.id.topAppBar);
        mb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.itSend) {
                    if(sending) {
                        Toast.makeText(BasketActivityOld.this, "Идет отправка заказа", Toast.LENGTH_LONG).show();
                    }  else {
                        askToSend();
                    }
                    return true;
                } else if(id == R.id.itSettings) {
                    editDetails();
                    return true;
                }
                return false;
            }
        });
    }

    void askToSend() {
        final Basket basket = adapter.getBasket();

        MaterialAlertDialogBuilder dlg = new MaterialAlertDialogBuilder(this);
        dlg.setTitle(R.string.confirmSend);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM");
        String message =
                "<H1>Заказ на сумму: <b>" + String.format("%.02f", basket.sum()) +
                        "</b><br/>Дата доставки: <b>" + sdf.format(basket.dlvDate) +
                        "</b><br/><br/>Отправить заказ?</H1>";

        dlg.setMessage(Html.fromHtml(message));
        dlg.setNegativeButton("Нет", null);
        dlg.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                send();
                dialog.dismiss();
            }
        }).show();
    }

    public static class DetailDlg extends AppCompatDialogFragment {
        Basket basket;
        Date dlvDate;
        public DetailDlg(Basket b) {
            basket = b;
            dlvDate = basket.dlvDate;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.order_detail, null);
            final EditText ed = v.findViewById(R.id.edRemark);
            ed.setText(basket.remark);

            final CalendarView cv = v.findViewById(R.id.cvDeliveryDate);
            cv.setDate(dlvDate.getTime());
            long now = (new Date()).getTime();
            cv.setMinDate(now + 24 * 3600 * 1000);
            cv.setMaxDate(now + 14 * 24 * 3600 * 1000);
            cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dlvDate = c.getTime();
                }
            });

            v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { dismiss(); }
            });

            v.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    basket.remark = ed.getText().toString();
                    basket.dlvDate = dlvDate;
                    dismiss();
                }
            });
            return v;
        }
    }

    private void editDetails() {
        (new DetailDlg(adapter.getBasket())).show(getSupportFragmentManager(), "");
    }

    void send() {
        final Basket b = adapter.getBasket();
        if(b.size() > 0) {
            sending = true;
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            OrderSend order = new OrderSend(b);
            SendBasketParam prm = new SendBasketParam();
            ReqCodeParam rp =  MainActivity.getProgParams();
            prm.appId = rp.appId;
            prm.deviceId = rp.deviceId;
            prm.orgId = PartnerList.getCurrent().id;
            prm.orders.add(order);

            WSExchange exch = new WSExchange(this);
            exch.setHandler(new WSExchange.Events() {
                @Override
                public void error(Exception e) {

                }

                @Override
                public void complete(boolean result, Object response, WSExchange exchange) {
                    sending = false;

                    if(!ErrorHandler.handleError(BasketActivityOld.this, result, response)) {
                        WSExchange ws = new WSExchange(BasketActivityOld.this);
                        ReqOrdersParam prm = new ReqOrdersParam();
                        ReqCodeParam rp =  MainActivity.getProgParams();
                        prm.appId = rp.appId;
                        prm.orgId = PartnerList.getCurrent().id;

                        ws.setHandler(new WSExchange.Events() {
                            @Override
                            public void error(Exception e) {

                            }

                            @Override
                            public void complete(boolean result, Object response, WSExchange exchange) {
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                                        b.clear();
                                    }
                                });

                                if(result) {
                                    ReqOrdersResult res = (ReqOrdersResult) response;
                                    if(res.result) {
                                        PartnerList.getCurrent().setOrders(res.orders);
                                        runOnUiThread(new Runnable() {
                                            @Override public void run() { OrderActivity.open(BasketActivityOld.this);  }
                                        });

                                    }
                                }
                            }
                        });
                        ws.reqOrders(prm);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override public void run() { findViewById(R.id.progressBar).setVisibility(View.GONE); }
                        });
                    }
                }
            });

            exch.sendOrder(prm);
        } else {
        }
    }

    public void updateSum() {
        String text = String.format("Итого: %.02f &#x20bd", adapter.basket.sum());
        MaterialToolbar mb = findViewById(R.id.topAppBar);
        mb.setTitle(Html.fromHtml(text));
    }

    @Override
    protected void onPartnerSelect(Partner newPartner) {
        super.onPartnerSelect(newPartner);
        adapter.setBasket(newPartner == null ? new Basket() :  newPartner.basket);
        updateSum();
    }

    @Override
    public void onReceive(final String id, Bitmap img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isVisible = false;
                ListView lvItems = findViewById(R.id.lvItems);
                for(int i=lvItems.getFirstVisiblePosition(); i<= lvItems.getLastVisiblePosition(); i++) {
                    BasketItem item = (BasketItem) adapter.getItem(i);
                    if(item.item.code.equals(id)) {
                        isVisible = true;
                        break;
                    }
                }
                if(isVisible)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    class Adapter extends BaseAdapter {
        Basket basket = new Basket();

        public void setBasket(Basket basket) {
            this.basket = basket;
            basket.setCanRemove(false);
            notifyDataSetChanged();
        }

        public Basket getBasket() { return basket; }

        @Override public int getCount() { return basket.size(); }
        @Override public Object getItem(int position) { return basket.items.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = View.inflate(BasketActivityOld.this, R.layout.basket_row_old, null);
            }
            BasketItem item = (BasketItem) getItem(position);

            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(item.item.name);

            TextViewCrossOut tco = convertView.findViewById(R.id.tvCost);
            tco.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.cost)));

            tv = convertView.findViewById(R.id.tvActCost);
            if(item.discount > 0) {
                tv.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.cost - item.discount)));
                tco.setCrossOut(true);
            } else {
                tv.setText("");
                tco.setCrossOut(false);
            }

            tv = convertView.findViewById(R.id.tvSum);
            tv.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.sum())));

            PictureHolder.setImage((ImageView) convertView.findViewById(R.id.imageView), item.item);

            PriceQtyPickerOld pq = convertView.findViewById(R.id.pqQty);
            pq.setData(item.item, basket);
            return convertView;
        }
    }
}
