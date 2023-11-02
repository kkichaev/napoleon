package com.grsoft.napoleon.org_adapter;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.main.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BaseOrgAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    static int SELECTED_COLOR;
    MainActivity main;
    Model model;

    String filter;

    List<OrgEx> orgs;
    List<? extends OrgEx> allOrgs;
    public BaseOrgAdapter(MainActivity context) {
        SELECTED_COLOR = context.getColor(R.color.cur_org_color);
        model = new ViewModelProvider(context).get(Model.class);
        allOrgs = getOrgs();
        orgs = (List<OrgEx>) allOrgs;
        this.main = context;
    }

    public void refresh() {
        allOrgs = getOrgs();
        filter(filter);
    }

    public void filter(String text) {
        this.filter = text;
        if(text == null || text.length() == 0) {
            orgs = (List<OrgEx>) allOrgs;
        } else {
            String[] srch = text.toUpperCase(Locale.ROOT).split(" ");

            orgs = new ArrayList<>();
            for (OrgEx o : allOrgs) {
                boolean contains = true;
                for (String si : srch) {
                    if (!o.name.toUpperCase(Locale.ROOT).contains(si) && !o.address.toUpperCase(Locale.ROOT).contains(si)) {
                        contains = false;
                        break;
                    }
                }
                if (contains) {
                    orgs.add(o);
                }
            }
        }
        notifyDataSetChanged();
    }

    protected abstract List<? extends OrgEx> getOrgs();

    @Override
    public int getItemCount() {
        return orgs.size();
    }

    public int indexOf(OrgEx o) { return orgs.indexOf(o); }

    public void selectOrg(OrgEx o) {
        model.setCurrentOrg(o);
        notifyDataSetChanged();
    }

    public void selectOrg(int pos) {
        model.setCurrentOrg(orgs.get(pos));
        notifyDataSetChanged();
    }

    public void openSchedule(Org o){
       main.openSchedule(o.id);
    }

    public OrgEx getSelectedOrg() {
        return model.getCurrentOrg().getValue();
    }

    public int getItemPosition(OrgEx value) {
        for(int i = 0; i < orgs.size(); i++)
            if (value.id.equals(orgs.get(i).id))
                return i;
        return -1;
    }
}
