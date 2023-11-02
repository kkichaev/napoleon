package com.grsoft.napoleon.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PurchaseItem;
import com.grsoft.dataobjects.Selling;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.AnswerImplEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.IncompleteScriptDlg;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.AnswerDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.napoleon.documents.SellingDoc;
import com.grsoft.napoleon.script_wizard.Scriptable;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentsView extends BaseFragment {
    public final String TAG = DocumentsView.class.toString();
    View titlePanel;
    private Adapter adapter;
    RecyclerView recyclerView;

    @Override
    protected int getLayoutID() {
        return R.layout.documents_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Override
    public int getOptionMenu() {
        return R.menu.documents_view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.findViewById(R.id.btnOK).setOnClickListener(w -> getParentFragmentManager().popBackStack());
        titlePanel = v.findViewById(R.id.title);
        titlePanel.setVisibility(View.GONE);

        Calendar c = Calendar.getInstance();
        c.setTime(Util.resetTime(new Date()));

        recyclerView = v.findViewById(R.id.items);

        if (adapter == null)
            adapter = new Adapter(getContext(), null, this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adjustTitlePanel();

        SearchView sv = v.findViewById(R.id.search);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByOrg = query;
                adapter.filter();
                adjustTitlePanel();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    adapter.filterByOrg = "";
                    adapter.filter();
                    adjustTitlePanel();
                }
                return false;
            }
        });

        model.getRefreshing().observe(getViewLifecycleOwner(), data -> {
            v.findViewById(R.id.waiting).setVisibility(data.refreshing ? View.VISIBLE : View.GONE);
            if (data.error != null && data.error.length() > 0) {
                model.clearRefrheshing();
                SyncErrorDialog dlg = new SyncErrorDialog(data.error);
                dlg.show(getParentFragmentManager(), "");
            } else if (data.traffic > 0) {
                model.clearRefrheshing();
                SuccessExchange dlg = new SuccessExchange();
                dlg.show(getParentFragmentManager(), "");
                getActivity().invalidateOptionsMenu();
            }
        });

        getParentFragmentManager().setFragmentResultListener(IncompleteScriptDlg.KEY,
                getViewLifecycleOwner(), (requestKey, result) -> {
                    long rowid = result.getLong(ExtrasConst.DOC_ROW_ID_STR);

                    ScriptImplEx script = new ScriptImplEx();
                    script.read(rowid);
                    script.close();

                    if (result.getInt(IncompleteScriptDlg.ACTION) == IncompleteScriptDlg.DELETE_ACTION) {
                        script.delete();
                        script.close();

                        adapter = new Adapter(getContext(), null, this);
                        recyclerView.setAdapter(adapter);

                        if (result.getBoolean(IncompleteScriptDlg.SYN_AFTER_DEL))
                            model.refresh(getContext(), (CfgNpl) ConfigManager.getConfig());
                    } else {
                        OrgImpl org = new OrgImpl();
                        org.read("id", script.getId());

                        model.setCurrentOrg((OrgEx) org.getData());
                        model.currentScript = script;
                        int step = script.isSigned() ? 2 : 0;
                        ((MainActivity) getActivity()).openFragment(new ScriptWizard(step), true);
                    }
                });

        return v;
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
        FilterDlg dlg = new FilterDlg();
        dlg.setOnFilterListener((t, d) -> filterByDate(t, d));
        dlg.show(getParentFragmentManager(), "");
    }

    private void filterByDate(DocType type, DatePeriod dp) {
        adapter.filterByType = type;
        adapter.filterByPeriod = dp;
        adapter.filter();

        adjustTitlePanel();
    }

    private void adjustTitlePanel() {
        DocType type = adapter.filterByType;
        if (type != null && (type.equals(PurchaseDoc.instance()) || type.equals(SellingDoc.instance()))) {
            titlePanel.setVisibility(View.VISIBLE);
            int weight = 0;
            long sum = 0;
            long bsum = 0;
            long wsum = 0;
            int qty = 0;
            Map<String, Long> sums = new HashMap();
            Map<String, Long> weights = new HashMap();

            for (Document<?> d : adapter.dataFilter) {
                OrderImplBase<?> b = (OrderImplBase<?>) d;
                sum += b.sum();
                qty += b.qty();

                if(d instanceof SellingImpl) {
                    if(((Selling)d.getData()).bmark == 0) {
                        wsum += b.sum();
                    } else {
                        bsum += b.sum();
                    }
                    continue;
                }
                if (!type.equals(PurchaseDoc.instance())) {
                    continue;
                }

                PurchaseImpl p = (PurchaseImpl) d;
                weight += p.weight();

                if (sums.containsKey(p.getData().payType))
                    sums.put(p.getData().payType, sums.get(p.getData().payType) + p.sum());
                else
                    sums.put(p.getData().payType, p.sum());

                for (OrderItem i : p.getData().items){
                    if (weights.containsKey(i.id))
                        weights.put(i.id, weights.get(i.id)  + ((PurchaseItem)i).weight);
                    else
                        weights.put(i.id, (long)((PurchaseItem)i).weight);
                }
            }

            if (type.equals(PurchaseDoc.instance())) {
                ((TextView) titlePanel.findViewById(R.id.tv1)).setText(String.format(getString(R.string.weight_title), Util.IntToScaleStr(weight, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false)));
                setWeights(weights);
                setSums(sums);
                titlePanel.findViewById(R.id.holder1).setVisibility(View.VISIBLE);
                titlePanel.findViewById(R.id.holder2).setVisibility(View.VISIBLE);
            }else {
                ((TextView) titlePanel.findViewById(R.id.tv1)).setText(String.format(getString(R.string.qty_title), qty));
                titlePanel.findViewById(R.id.holder1).setVisibility(View.GONE);
                titlePanel.findViewById(R.id.holder2).setVisibility(View.GONE);
            }

            String tsum = String.format(getString(R.string.sum_title), Util.IntToScaleStr(sum, Consts.SUM_SCALE));
            ((TextView) titlePanel.findViewById(R.id.tv2)).setText(tsum);
            if(type.equals(SellingDoc.instance())) {
                titlePanel.findViewById(R.id.tblSales).setVisibility(View.VISIBLE);
                ((TextView)titlePanel.findViewById(R.id.b_sum)).setText(Util.IntToScaleStr(bsum, Consts.SUM_SCALE));
                ((TextView)titlePanel.findViewById(R.id.w_sum)).setText(Util.IntToScaleStr(wsum, Consts.SUM_SCALE));
            } else {
                titlePanel.findViewById(R.id.tblSales).setVisibility(View.GONE);
            }
        } else
            titlePanel.setVisibility(View.GONE);
    }

    private void setSums(Map<String, Long> sums) {
        List<Pair<String, Long>> list = new ArrayList<>();
        for(Map.Entry<String, Long> e : sums.entrySet())
            list.add(new Pair<>(e.getKey(), e.getValue()));

        list.sort(Comparator.comparing(x->x.first));
        LinearLayout ll = titlePanel.findViewById(R.id.holder2);
        ll.removeAllViews();

        for(Pair<String, Long> v : list ){
            LinearLayout params = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.params_layout, null);
            TextView tv = params.findViewById(R.id.tvVal1);
            tv.setText(v.first);
            tv = params.findViewById(R.id.tvVal2);
            tv.setText(Util.IntToScaleStr(v.second, Consts.SUM_SCALE));
            ll.addView(params);
        }
    }

    private void setWeights(Map<String, Long> weights) {
        PriceImpl p = new PriceImpl();
        LinearLayout ll = titlePanel.findViewById(R.id.holder1);
        ll.removeAllViews();
        List<Pair<String, String>> list = new ArrayList<>();

        for(Map.Entry<String, Long> e : weights.entrySet()){
            p.read("id", e.getKey());
            list.add(new Pair<>(p.getData().name, Util.IntToScaleStr(e.getValue(), Consts.WEIGHT_SCALE, Util.DEC_DELIM, false)));
        }

        p.close();

        list.sort(Comparator.comparing(x -> x.first));

        for(Pair<String, String> v : list ){
            LinearLayout params = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.params_layout, null);
            TextView tv = params.findViewById(R.id.tvVal1);
            tv.setText(v.first);
            tv = params.findViewById(R.id.tvVal2);
            tv.setText(v.second);
            ll.addView(params);
        }
    }

    public static class DVHolder extends RecyclerView.ViewHolder {
        public Date created;
        public DVHolder(@NonNull View itemView, DocumentsView owner) {
            super(itemView);
            itemView.setOnClickListener(v -> owner.onDocumentClicked(getAdapterPosition()));
            itemView.setOnLongClickListener(view -> owner.onItemMenu(this, view));
        }
    }

    private boolean onItemMenu(DVHolder dvHolder, View view) {
        PopupMenu pm = new PopupMenu(getContext(), view);
        pm.inflate(R.menu.documents_view_context_menu);
        pm.setOnMenuItemClickListener(item1 -> {
            if(item1.getItemId() == R.id.itSync) {
                MainActivity parent = (MainActivity) getActivity();
                ScriptImpl us = parent.getIncompleteScript();
                if (us != null) {
                    IncompleteScriptDlg dlg = new IncompleteScriptDlg(us.getRowid());
                    dlg.show(getParentFragmentManager(), "");
                    return true;
                }

//                ScriptImpl script = null;
//
//                DocList scripts = ScriptDoc.instance().docList(null, "");
//
//                if (scripts != null){
//                    for(Document d : scripts){
//                        if (d == null)
//                            continue;
//
//                        Script s = (Script) d.getData();
//
//                        for(ScriptItem i : s.items){
//                            if (i.date.equals(dvHolder.created)){
//                                script = (ScriptImpl) d;
//                                break;
//                            }
//                        }
//
//                        if (script != null)
//                            break;
//                    }
//                }
//
//                if (script != null){
//                    script.getData().params = 0;
//                    script.write();
//                    script.close();
//
//                    ScriptEx scr = (ScriptEx) script.getData();
//                    VisitImpl refVisit = new VisitImpl();
//                    refVisit.getData().created = scr.visitDoc;
//
//                    if (refVisit.read()){
//                        refVisit.getData().params = 0;
//                        refVisit.write();
//                    }
//
//                    refVisit.close();
//
//                    for(Document d : script.getDocuments()){
//                        if (d == null)
//                            continue;
//
//                        CreateDocDataObject cd = (CreateDocDataObject) d.getData();
//                        cd.params = 0;
//
//                        d.write();
//                        d.close();
//                    }
//
//                    model.refresh(getContext(), (CfgNpl) ConfigManager.getConfig());
//                }
            }

            return true;
        });
        pm.show();
        return true;

    }

    private void onDocumentClicked(int pos) {
        Document d = adapter.dataFilter.get(pos);

        if (d instanceof Scriptable) {
            model.currentDoc = (CreatableDocument) d;

            FragmentTransaction ft = getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment, ((Scriptable) d).getPreview());
            ft.addToBackStack("");
            ft.commit();
        }
    }

    public static class Adapter extends RecyclerView.Adapter<DVHolder> {
        private final DocumentsView owner;
        public DocType filterByType = null;
        public DatePeriod filterByPeriod = new DatePeriod(
                Util.getDayStart(new Date()),
                Util.getDayEnd(new Date()));
        public String filterByOrg = "";

        Map<String, Org> orgs = new HashMap<>();
        List<Document> data = new ArrayList<>();
        public List<Document> dataFilter = new ArrayList<>();
        Context context;
        Map<Class<?>, String> docNames = new HashMap<>();

        public Adapter(Context context, DatePeriod dp, DocumentsView owner) {
            this.context = context;
            this.owner = owner;

            docNames.put(PurchaseImpl.class, context.getString(R.string.purchase));
            docNames.put(SellingImpl.class, context.getString(R.string.selling_title));
            docNames.put(AnswerImplEx.class, context.getString(R.string.quest_doc_title));

            DocList purch = PurchaseDoc.instance().docList(null, "", dp);
            DocList sell = SellingDoc.instance().docList(null, "", dp);
            DocList quest = AnswerDoc.instance().docList(null, "", dp);

            purch.instanceCollections().forEach(d -> {if (((PurchaseImpl)d).count() > 0 ) data.add(d);});
            sell.instanceCollections().forEach(d -> data.add(d));
            quest.instanceCollections().forEach(d -> data.add(d));

            data.sort((o1, o2) ->
                    ((CreatableDocument<?>)o2).getData().created.compareTo(((CreatableDocument<?>)o1).getData().created)
            );

            data.forEach((d) -> {
                if (!orgs.containsKey(d.getId())) {
                    OrgImpl impl = new OrgImpl();
                    impl.read("id", d.getId());
                    orgs.put(impl.getData().id, impl.getData());
                }
            });

            filter();
//            dataFilter.addAll(data);
        }

        @NonNull
        @Override
        public DVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.documents_view_list_item, parent, false);
            return new DVHolder(v, owner);
        }

        @Override
        public void onBindViewHolder(@NonNull DVHolder holder, int position) {
            CreatableDocument<?> d = (CreatableDocument<?>) dataFilter.get(position);
            holder.created = d.getData().created;

            if (orgs.containsKey(d.getId())) {
                Org o = orgs.get(d.getId());
                ((TextView) holder.itemView.findViewById(R.id.tvOrg)).setText(o.name);
                ((TextView) holder.itemView.findViewById(R.id.tvAddress)).setText(o.address);
            }

            String docName = "";

            if (docNames.containsKey(d.getClass()))
                docName = docNames.get(d.getClass());

            String date = Util.simpleDateFormat.format(d.getData().created);
            ((TextView) holder.itemView.findViewById(R.id.tvDoc)).setText(String.format("%s %s", docName, date));
        }

        @Override
        public int getItemCount() {
            return dataFilter.size();
        }

        public void filter() {
            dataFilter.clear();
            List<String> ids = new ArrayList<>();

            orgs.forEach((i, o) -> {
                String str = o.name.toUpperCase() + "|" + o.address.toUpperCase();

                if (filterByOrg.length() > 0 && str.contains(filterByOrg.toUpperCase()) || filterByOrg.length() == 0)
                    ids.add(i);
            });

            for (Document<?> d : data)
                if (ids.contains(d.getId()) &&
                        (filterByType == null || filterByType.equals(getDocType(d))) &&
                        (filterByPeriod == null || isDocIntoPeriod(d)))
                    dataFilter.add(d);

            notifyDataSetChanged();
        }

        private boolean isDocIntoPeriod(Document<?> d) {
            Long cr = ((CreatableDocument<?>) d).getData().created.getTime();

            return cr >= filterByPeriod.begin.getTime() && cr < filterByPeriod.end.getTime();
        }

        private DocType getDocType(Document<?> d) {
            if (d instanceof SellingImpl)
                return SellingDoc.instance();
            else if (d instanceof AnswerImpl)
                return AnswerDoc.instance();
            else if (d instanceof PurchaseImpl)
                return PurchaseDoc.instance();

            return null;
        }
    }

}
