package com.grsoft.napoleon.org_adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;

import java.util.List;

public class OrgAdapter extends BaseOrgAdapter<OrgHolder> {

    public OrgAdapter(MainActivity context) {
        super(context);
    }

    @NonNull
    @Override
    public OrgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.org_row, parent, false);
        return new OrgHolder(this, v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgHolder holder, int position) {
        holder.update(orgs.get(position));
    }

    @Override
    protected List<? extends OrgEx> getOrgs() {
        DbWriter.checkDBTable(OrgEx.class);
        return DbReader.fetch(OrgEx.class, "hidden=0", "name,address");
    }
}
