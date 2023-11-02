package com.serviko.sales.main_views;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.actionTree.ActionDef;
import com.serviko.dataobjects.actionTree.KupecAction;
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;

import java.util.ArrayList;
import java.util.List;

public class Actions extends BaseView {

    ListView items;

    public static String TAG = Actions.class.toString();

    @Override
    int getResourceId() {
        return R.layout.actions_view;
    }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ret = super.onCreateView(inflater, container, savedInstanceState);

        items = ret.findViewById(R.id.lvItems);
        items.setOnItemClickListener((adapterView, view, i, l) -> {
            ActionDef ad = (ActionDef) adapterView.getAdapter().getItem(i);
            model.currentAction = ad;
            BaseView cf = ad == KupecAction.get() ? new KupecActionView() : new ActionDetail();
            ((MainActivity)getActivity()).loadFragment(cf, true);
        });
        model.getPartner().observe(getViewLifecycleOwner(), this::onNewPartner);
        return ret;
    }

    void onNewPartner(Partner partner) {
        items.setAdapter(new Adapter(partner, getActionCount()));
    }

    protected int getActionCount() { return 0; }

    class Adapter extends BaseAdapter {
        List<ActionDef> actions = new ArrayList<>();

        public Adapter(Partner p, int count) {
            if(p != null) {
                List<ActionDef> src = p.getActions();
                if(count > 0) {
                    if(count > src.size()) count = src.size();
                    actions.addAll(p.getActions().subList(0, count));
                } else {
                    actions = src;
                }
            }
        }

        @Override
        public int getCount() { return actions.size(); }

        @Override
        public Object getItem(int i) { return actions.get(i); }

        @Override
        public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.action_view, null);
            }

            ActionDef ad = (ActionDef) getItem(i);

            TextView tv = view.findViewById(R.id.tvTitle);
            tv.setText(ad.title());

            tv = view.findViewById(R.id.tvSecondary);
            tv.setText(ad.text());

            ImageView iv = view.findViewById(R.id.ivAction);
            String url = model.makeUrl(ad.getId(), true);

            Bitmap b = model.getPhoto(url);
            if(b != null) {
                iv.setImageBitmap(b);
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
                images.put(url, iv);
            }

            return view;
        }
    }
}
