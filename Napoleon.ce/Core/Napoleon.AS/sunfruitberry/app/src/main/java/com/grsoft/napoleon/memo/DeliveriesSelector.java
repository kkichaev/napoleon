package com.grsoft.napoleon.memo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.napoleon.R;
import com.grsoft.napoleon.debet_data.DocData;
import com.grsoft.napoleon.debet_data.DogovorData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DeliveriesSelector extends DialogFragment {

    Model model;
    Adapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(getActivity()).get(Model.class);

        DogovorData dd = model.dogData.get(model.doc.idDog);
        List<DocKey> selected = new ArrayList<>();
        for(String s : model.doc.deliveries.split(";"))
            selected.add(new DocKey(s));

        adapter = new Adapter(dd.documents, selected);

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        RecyclerView lv = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.delivery_selector, null);
        lv.setAdapter(adapter);
        lv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        b.setView(lv);
        b.setTitle(R.string.select_deliveries);
        b.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            String docs = "";
            for(DocKey dk : adapter.selected) {
                docs += dk.toString() + ";";
            }
            model.doc.deliveries = docs.substring(0, docs.length() - 1);
        });
        return b.create();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        List<DocData> docs;
        public List<DocKey> selected;

        public Adapter(List<DocData> docs, List<DocKey> selected) {
            this.docs = docs;
            this.selected = selected;
        }

        void update(DocKey clicked) {
            if(selected.contains(clicked)) {
                selected.remove(clicked);
            } else {
                selected.add(clicked);
            }
        }

        class Holder extends RecyclerView.ViewHolder {

            public Holder(@NonNull View itemView) {
                super(itemView);
            }

            public void update(DocData dd) {
                DocKey dk = new DocKey(dd);

                dd.update(itemView);

                CheckBox cb = itemView.findViewById(R.id.check_doc);
                cb.setChecked(selected.contains(dk));
                cb.setOnClickListener(view -> Adapter.this.update(dk));
                itemView.setOnClickListener(view -> {
                    Adapter.this.update(dk);
                    cb.setChecked(selected.contains(dk));
                });
            }
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.delivery_selector_row, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.update(docs.get(position));
        }

        @Override
        public int getItemCount() {
            return docs.size();
        }
    }

    static class DocKey {
        static SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        public String number = "";
        public Date date = new Date();

        public DocKey(String docData) {
            try {
                date = sdf.parse(docData.substring(0, 6));
                number = docData.substring(6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public DocKey(DocData dd) {
            number = dd.number;
            date = dd.date;
        }

        @Override
        public String toString() {
            return sdf.format(date) + number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DocKey docKey = (DocKey) o;
            return Objects.equals(number, docKey.number) && Objects.equals(date, docKey.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, date);
        }
    }
}
