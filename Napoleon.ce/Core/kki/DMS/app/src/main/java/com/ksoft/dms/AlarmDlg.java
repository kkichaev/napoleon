package com.ksoft.dms;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmDlg extends DialogFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String MESSAGE = "message";

    public Date dateNotify;
    private Button btnTime;
    private Button btnDate;
    public EditText edText;
    private DialogButtonListener dbl;

    public interface DialogButtonListener {
        void positiveFinish(AlarmDlg dialog);
    }

    public AlarmDlg(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 3);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        dateNotify = c.getTime();
    }
    public void setIAlarmDlg(DialogButtonListener alarm){
        this.dbl = alarm;
    }

    public DialogButtonListener getIAlarmDlg(){
        return dbl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getTitle());
        View view = View.inflate(getContext(), getLayout(), null);

        edText = view.findViewById(R.id.edText);

        btnTime = view.findViewById(R.id.btnTime);
        btnTime.findViewById(R.id.btnTime).setOnClickListener((v)->{
            Calendar c = Calendar.getInstance();
            c.setTime(dateNotify);
            new TimePickerDialog(getContext(), AlarmDlg.this, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
        });

        btnDate = view.findViewById(R.id.btnDate);

        btnDate.setOnClickListener((v)->{
            Calendar c = Calendar.getInstance();
            c.setTime(dateNotify);
            new DatePickerDialog(getContext(), AlarmDlg.this, c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        builder.setView(view);
        builder.setPositiveButton("OK", (d,w)->{
            if (dbl != null)
                dbl.positiveFinish(AlarmDlg.this);
        });

        builder.setNegativeButton("Cancel", null);

        setDate();
        setTime();

        return builder.create();
    }

    protected String getTitle() {
        return getContext().getString(R.string.create_notify);
    }

    protected int getLayout() {
        return R.layout.notification_dlg;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(dateNotify);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        dateNotify = c.getTime();

        setTime();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.setTime(dateNotify);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        dateNotify = c.getTime();

        setDate();
    }

    private void setDate(){
        btnDate.setText(new SimpleDateFormat("dd:MM:yy").format(dateNotify));
    }

    private void setTime(){
        btnTime.setText(new SimpleDateFormat("HH:mm").format(dateNotify));
    }
}
