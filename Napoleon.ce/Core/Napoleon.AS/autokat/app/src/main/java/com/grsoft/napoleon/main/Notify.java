package com.grsoft.napoleon.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.MessageNew;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.IncompleteScriptDlg;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.util.ExtrasConst;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Notify extends BaseFragment {

    private Adapter adapter;

    @Override
    protected int getLayoutID() {
        return R.layout.notify_view;
    }

    @Override
    public String TAG() {
        return Notify.class.toString();
    }

    @Override
    public int getOptionMenu() {
        return R.menu.notify;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.btnOK).setOnClickListener(w -> getParentFragmentManager().popBackStack());
        RecyclerView rv = v.findViewById(R.id.items);
        adapter = new Adapter(getContext(), this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        getParentFragmentManager().setFragmentResultListener(FilterMsgDlg.KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    int w = result.getInt(FilterMsgDlg.FILTER);
                    adapter.filter(w);
                    adapter.notifyDataSetChanged();
                });

        return v;
    }

    public static class DVHolder extends RecyclerView.ViewHolder {

        public DVHolder(@NonNull View itemView, Notify owner) {
            super(itemView);
            itemView.setOnClickListener(v -> owner.onMessageClicked(getAdapterPosition()));
        }
    }

    private void onMessageClicked(int pos) {
        MessageNew msg =  adapter.data.get(pos);
        msg.read = msg.read == 0 ? 1 : 0;
        adapter.notifyDataSetChanged();

        DbWriter writer = new DbWriter();
        writer.updateRecord(msg, msg.date.getTime());
        writer.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            showFilterDlg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDlg() {
        FilterMsgDlg dlg = new FilterMsgDlg();
        dlg.show(getParentFragmentManager(), "");
    }


    public static class Adapter extends RecyclerView.Adapter<DVHolder> {
        private final Notify owner;
        List<MessageNew> data = new ArrayList<>();
        public List<MessageNew> dataFilter = new ArrayList<>();
        Context context;

        public Adapter(Context context, Notify owner) {
            this.context = context;
            this.owner = owner;

            data.addAll(DbReader.fetch(MessageNew.class, "", "date DESC"));
            dataFilter.addAll(data);
        }

        @NonNull
        @Override
        public DVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_view_item, parent, false);
            return new DVHolder(v, owner);
        }

        @Override
        public void onBindViewHolder(@NonNull DVHolder holder, int position) {
            MessageNew msg = dataFilter.get(position);

            ((TextView)holder.itemView.findViewById(R.id.text)).setText(msg.message);
            ((ImageView)holder.itemView.findViewById(R.id.status)).setImageResource(getImage(msg.read));
        }

        private int getImage(int read) {
            return read == 0 ? R.drawable.ic_unread_msg : R.drawable.ic_read_msg;
        }

        @Override
        public int getItemCount() {
            return dataFilter.size();
        }

        public void filter(int w) {
            dataFilter.clear();

            if (w != R.id.all){
                data.forEach(m->{
                    if ((w == R.id.read && m.read == 1) ||  (w == R.id.unread && m.read == 0))
                        dataFilter.add(m);
                });
            }else
                dataFilter.addAll(data);

            notifyDataSetChanged();
        }
    }
}
