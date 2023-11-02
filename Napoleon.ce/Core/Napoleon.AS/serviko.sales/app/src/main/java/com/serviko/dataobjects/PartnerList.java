package com.serviko.dataobjects;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.serviko.sales.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerList {
    public interface Events {
        void onNewList();
        void onCurrentChanged(Partner newCurrent);
    }

    interface SendEvent {
        void send(Events handler);
    }

    static List<Partner> partners = new ArrayList<>();
    static Partner current = null;

    static List<Events> handlers = new ArrayList<>();

    public static Map<String, Contract> contracts = new HashMap<>();

    public static void addHandler(Events h) {
        if(!handlers.contains(h))
            handlers.add(h);
    }

    public static void removeHandler(Events h) {
        handlers.remove(h);
    }

    public static List<Partner> partners() { return partners;}
    public static Partner getCurrent() { return current; }

    public static  void setPartners(List<Partner> newPartners) {
        partners.clear();
        partners.addAll(newPartners);
        current = null;

//        sendEvent(new SendEvent() {
//            @Override public void send(Events handler) {
//                handler.onNewList();
//                handler.onCurrentChanged(null);
//            }
//        });
    }

    public static void setCurrent(Partner cp) {
        current = cp;
    }

    public static void addContracts(List<Contract> contracts) {
        for(Contract c : contracts) {
            PartnerList.contracts.put(c.id, c);
        }
    }

//    public static void setCurrentOld(final Partner current) {
//        if(PartnerList.current != current) {
//            PartnerList.current = current;
//            sendEvent(new SendEvent() {
//                @Override public void send(Events handler) { handler.onCurrentChanged(current); }
//            });
//        }
//    }
//
//    static void sendEvent(SendEvent sender) {
//        for(Events h : handlers) {
//            sender.send(h);
//        }
//    }

//    public static DialogFragment selectPartnerDialog() {
//        return new SelectPartner();
//    }

//    static public class SelectPartner extends DialogFragment {
//        Dialog dlg;
//
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//            View v = getActivity().getLayoutInflater().inflate(R.layout.select_partner_old, null);
//            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
//
//            b.setTitle(R.string.select_partner);
//            ListView lv = v.findViewById(R.id.lvItems);
//            lv.setAdapter(new PartnerAdapter(partners, getActivity().getLayoutInflater()));
//            b.setView(v);
//            dlg =  b.create();
//
//            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Partner sel = (Partner) parent.getAdapter().getItem(position);
//                    PartnerList.setCurrentOld(sel);
//                    dlg.dismiss();
//                }
//            });
//            return dlg;
//        }
//    }
//
//    static public class PartnerAdapter extends BaseAdapter {
//        List<Partner> partners;
//        LayoutInflater inflater;
//        public PartnerAdapter(List<Partner> partners, LayoutInflater inflater) {
//            this.partners = partners;
//            this.inflater = inflater;
//        }
//
//        @Override public int getCount() { return partners.size(); }
//        @Override public Object getItem(int position) { return partners.get(position); }
//        @Override public long getItemId(int position) { return position; }
//
//        @Override
//        public View getView(int position, View v, ViewGroup parent) {
//            if(v == null) {
//                v = inflater.inflate(R.layout.select_partner_row_old, null);
//            }
//            TextView tv = v.findViewById(R.id.tvName);
//            Partner p = (Partner) getItem(position);
//            tv.setText(Html.fromHtml(p.toText()));
//            return v;
//        }
//    }
}
