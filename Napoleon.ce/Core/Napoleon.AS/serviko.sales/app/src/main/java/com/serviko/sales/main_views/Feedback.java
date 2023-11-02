package com.serviko.sales.main_views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.dataobjects.Agent;
import com.serviko.dataobjects.Contract;
import com.serviko.dataobjects.PartnerList;
import com.serviko.sales.R;

import java.util.List;

public class Feedback extends BaseView {

    Adapter adapter;

    public static String TAG = Profile.class.toString();

    @Override
    int getResourceId() {
        return R.layout.feedback_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());
        ListView items = v.findViewById(R.id.lvItems);
        items.setDividerHeight(0);

        items.setOnItemClickListener((adapterView, view, i, l) -> {
            Agent a = (Agent) adapter.getItem(i);
            if(a.phone.length() > 0) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + a.phone));
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.cand_dial, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        adapter = new Adapter();
        items.setAdapter(adapter);

        model.getPartner().observe(getViewLifecycleOwner(), p -> adapter.refresh());
        return v;
    }

    class Adapter extends BaseAdapter {
        List<Agent> agents;
        public Adapter() {
            refresh();
        }

        public void refresh() {
            agents = model.getPartner().getValue().agents;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() { return agents.size(); }

        @Override
        public Object getItem(int i) { return agents.get(i); }

        @Override
        public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.feedback_row_view, null);
            }
            Context context = getContext();
            Agent a = (Agent) getItem(i);
            TextView tv;
            String text;
            Contract c = PartnerList.contracts.get(a.contract);
            text = c == null ? a.contract : c.name;
            text = context.getString(R.string.contract_row, text);
            tv = view.findViewById(R.id.tvContract);
            tv.setText(text);

            text = context.getString(R.string.name_row, a.name);
            tv = view.findViewById(R.id.tvName);
            tv.setText(text);

            text = context.getString(R.string.phone_row, a.phone);
            tv = view.findViewById(R.id.tvPhone);
            tv.setText(text);

            return view;
        }
    }
}
