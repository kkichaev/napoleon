package com.serviko.sales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.serviko.dataobjects.Basket;

import java.util.Calendar;
import java.util.Date;

public class BasketDetailDlg extends AppCompatDialogFragment {
    Basket basket;
    Date dlvDate;
    Handler handler;

    public interface Handler {
        void onOkClicked(BasketDetailDlg src);
    }

    public BasketDetailDlg(Basket b, Handler handler) {
        basket = b;
        dlvDate = basket.dlvDate;
        this.handler = handler;
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
                basket.assignDlvDate = true;
                dismiss();
                if(handler != null) {
                    handler.onOkClicked(BasketDetailDlg.this);
                }
            }
        });
        return v;
    }
}