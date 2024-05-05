package com.grsoft.napoleon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.NewClientImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.new_client.BaseView;
import com.grsoft.napoleon.new_client.Model;
import com.grsoft.napoleon.new_client.Page1;
import com.grsoft.napoleon.new_client.Page2;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NewClientEdit extends AppCompatActivity implements SendResultListener {

    public static void open(Context context, long rowid) {
        Intent i = new Intent(context, NewClientEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        context.startActivity(i);
    }

    View marker;
    SupportMapFragment mapFragment;
    GoogleMap map;

    public final static float DEFAULT_MAP_ZOOM = 17.0f;


    Model model;

    public void loadFragment(BaseView cf, boolean addToBackStack, boolean animation) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (animation)
            ft.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
            );

        ft.replace(R.id.frame, cf, cf.getFragmentTag());

        if(addToBackStack) {
            ft.addToBackStack(cf.getFragmentTag());
            ft.setReorderingAllowed(true);
        }
        ft.commit();
    }

    void askCancel() {
        DialogFragment df = new CancelDialog();
        df.show(getSupportFragmentManager(), "");
    }

    public void removeDoc() {
        model.removeIfEmpty();
        finish();
    }

    public static class CancelDialog extends AppCompatDialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            b.setTitle("Подтвердите действие");
            b.setMessage("Отменить заведение клиента?");
            b.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                dialog.dismiss();
                Activity a = getActivity();
                if(a instanceof NewClientEdit)
                    ((NewClientEdit) a).removeDoc();
            });
            b.setNegativeButton(android.R.string.no, (d, w) -> {
                d.dismiss();
            });
            return b.create();
        }
    }

    void updateButtons(int step) {
        findViewById(R.id.back).setVisibility(step == 1 ? View.INVISIBLE : View.VISIBLE);
        ((TextView)findViewById(R.id.ok)).setText(step == 2 ? android.R.string.ok : R.string.next);
    }

    @Override
    public void onBackPressed() {
        return;
//        FragmentManager fm = getSupportFragmentManager();
//        if(fm.getBackStackEntryCount() == 1) {
//            return;
//        }
//        super.onBackPressed();
    }

    void updateModel() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame);
        if(f instanceof BaseView)
            ((BaseView) f).updateModel();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_client_edit);

        setTitle("Новый клиент");

        model = new ViewModelProvider(this).get(Model.class);
        long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
        model.readDoc(rowid);

        findViewById(R.id.cancel).setOnClickListener(v -> {
            askCancel();
        });

        findViewById(R.id.back).setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() > 1) {
                updateModel();
                fm.popBackStackImmediate();
            }
            updateButtons(fm.getBackStackEntryCount());
        });

        findViewById(R.id.ok).setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            int step = fm.getBackStackEntryCount();
            if(step == 1) {
                updateModel();
                loadFragment(new Page2(), true, true);
                step++;
            } else {
                save();
            }
            updateButtons(step);
        });

        loadFragment(new Page1(), true, true);
        updateButtons(1);
    }


    boolean save() {
        if(!model.isEditable()) {
            return false;
        }

        updateModel();

        Model.Validate v = model.validate();
        if(v == Model.Validate.ok) {
            model.write();

            NewClientImpl doc = model.getDoc();
            new DocumentSender(this, findViewById(R.id.btnOK),
                    NewClientDoc.instance().getObjectName(), doc,
                    doc.getRowid(), this).execute((Void[])null);
            return true;
        }

        String text = "Ошибка ввода";
        switch (v) {
            case noFio:
                text = getString(R.string.input_fio);
                break;
            case noLocation:
                text = "Укажите координаты клиента";
                break;
            case noInn:
                text = getString(R.string.input_inn);
                break;
            case noAddress:
                text = getString(R.string.input_address);
                break;
            case noName:
                text = getString(R.string.input_name);
                break;
            case noSalesChannel:
                text = getString(R.string.select_sales_channel);
                break;
            case noTypeTT:
                text = getString(R.string.select_type_tt);
                break;
            case noProfile:
                text = getString(R.string.select_profile);
                break;
            case noPhone:
                text = getString(R.string.incorrect_phone_number);
                break;
            case noAcceptTime:
                text = getString(R.string.input_time1);
                break;
            case noRestTime:
                text = getString(R.string.input_time2);
                break;
            case wrongTime:
                text = getString(R.string.incorrect_time_format);
                break;
        }

        if(text.length() > 0) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    @Override
    public void postSendExecute(boolean result) {
        finish();
    }
}
