package com.grsoft.napoleon.org_adapter;

import android.graphics.Color;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.R;

public class OrgHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    BaseOrgAdapter<?> owner;

    public OrgHolder(BaseOrgAdapter<?> owner, @NonNull View itemView) {
        super(itemView);
        this.owner = owner;

        itemView.setOnCreateContextMenuListener(this);

        itemView.setOnClickListener(v -> {
            owner.selectOrg(getAdapterPosition());
        });

        itemView.setOnLongClickListener(v -> {
            owner.selectOrg(getAdapterPosition());
            return false;
        });
    }

    public void update(OrgEx o) {
        TextView tv = itemView.findViewById(R.id.name);
        tv.setText(o.name);

        Org sel = owner.getSelectedOrg();
        tv.setTextColor(Color.BLACK);

        if (sel != null && o.id.equals(sel.id))
            tv.setTextColor(BaseOrgAdapter.SELECTED_COLOR);

        tv = itemView.findViewById(R.id.address);
        tv.setText(o.address);

        tv = itemView.findViewById(R.id.remark);
        tv.setText(o.remark);
        tv.setVisibility(o.remark.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater mi = new MenuInflater(v.getContext());
        mi.inflate(R.menu.org_context_menu, menu);

        for(int i = 0; i < menu.size(); i++)
            menu.getItem(i).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        OrgEx sel = owner.getSelectedOrg();

        if (item.getItemId() == R.id.miSchedule){
            owner.openSchedule(sel);
            return  true;
        }

        return false;
    }
}
