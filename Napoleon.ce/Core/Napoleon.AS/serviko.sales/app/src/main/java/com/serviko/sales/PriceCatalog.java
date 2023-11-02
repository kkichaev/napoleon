package com.serviko.sales;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.appbar.MaterialToolbar;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.priceTree.Folder;
import com.serviko.dataobjects.priceTree.TreeElement;
import com.serviko.view.PriceQtyPickerOld;
import com.serviko.view.TextViewCrossOut;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PriceCatalog extends BaseActivityOld implements PictureHolder.Handler {
    static String SEARCH_PRICE_ACTION = "com.serviko.sales.srch_price";
    static String FOLDER_TAG = "folder";

    Adapter adapter;
    SearchView searchView;
    String openFolderId;

    @Override protected int getLayoutID() { return R.layout.price_catalog; }
    @Override protected int getBottomMenuID() { return 0; } //R.id.itOrder; }

    List<Folder> folders = new ArrayList<>();

    public static void open(Context context) {
        Intent i = new Intent(context, PriceCatalog.class);
        context.startActivity(i);
    }

    public static void open(Context context, Folder curFolder) {
        Intent i = new Intent(context, PriceCatalog.class);
        i.putExtra(FOLDER_TAG, curFolder.item.id);
        context.startActivity(i);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MaterialToolbar mb = findViewById(R.id.topAppBar);
        mb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFolder();
                if(folders.size() == 1) {
                    mb.setNavigationIcon(null);
                    mb.setTitle(R.string.catalog);
                } else {
                    mb.setTitle(folders.get(folders.size()-1).item.name);
                }
            }
        });

        openFolderId = getIntent().getStringExtra(FOLDER_TAG);

        initSearch(mb);
        initPriceList(mb);
    }

    private void initPriceList(final MaterialToolbar mb) {
        ListView lv = findViewById(R.id.lvItems);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = adapter.getItem(position);
                if(obj instanceof Folder) {
                    moveToFolder((Folder)obj, mb);
                } else {
                    GoodsView.open(PriceCatalog.this, ((TreeElement)obj).item);
                }
            }
        });
    }

    void moveToFolder(Folder f, MaterialToolbar mb) {
        folders.add(f);

        mb.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp);
        mb.setTitle(f.item.name);

        adapter.setFolder(f);
    }

    private void initSearch(MaterialToolbar mb ) {
        Menu menu = mb.getMenu();
        searchView = (SearchView) menu.findItem(R.id.itFind).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchableInfo si = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(si);
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override public void onViewAttachedToWindow(View v) { }
            @Override public void onViewDetachedFromWindow(View v) { adapter.setFolder(getCurRoot()); }
        });
    }

    Folder getCurRoot() { return folders.size() == 0 ? null : folders.get(folders.size() - 1); }

    void loadPrice(Partner cp) {
        if(adapter == null) {
            adapter = new Adapter();
            ListView lv = findViewById(R.id.lvItems);
            lv.setAdapter(adapter);
        }

        if(cp.getPrice().size() == 0) {
        } else {
            Folder f = cp.getPrice().root();
            folders.clear();
            folders.add(f);
            if(openFolderId != null) {
                f = cp.getPrice().findFolder(openFolderId);
                openFolderId = null;
                if(f != null)
                    moveToFolder(f, (MaterialToolbar) findViewById(R.id.topAppBar));
            } else {
                adapter.setFolder(f);
            }
        }
    }

    @Override
    protected void refreshPartner() {
        if(adapter == null) {
            super.refreshPartner();
        } else {
            // don't loadPrice on resume to activity - searched price disappear
            super.onPartnerSelect(PartnerList.getCurrent());
            adapter.notifyDataSetChanged(); // just refresh
        }
    }

    @Override
    protected void onPartnerSelect(Partner newPartner) {
        super.onPartnerSelect(newPartner);
        loadPrice(newPartner);
    }

    boolean upFolder() {
        if(folders.size() <= 1) {
            return false;
        }
        folders.remove(folders.size() - 1);
        adapter.setFolder(getCurRoot());
        return true;
    }


    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()) {
            MaterialToolbar mb = findViewById(R.id.topAppBar);
            Menu menu = mb.getMenu();
            menu.findItem(R.id.itFind).collapseActionView();
            return;
        }

        if(!upFolder()) {
            super.onBackPressed();
            return;
        }
    }

    void doSearch(String query) {
        Folder srch = new Folder();
        Pattern pattern = Pattern.compile(query.replace(" ", "(.* .*)"), Pattern.CASE_INSENSITIVE);
        getCurRoot().findItems(srch.items, pattern);
        adapter.setFolder(srch);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PictureHolder.addHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PictureHolder.removeHandler(this);
    }

    @Override
    public void onReceive(final String id, Bitmap img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isVisible = false;
                ListView lvItems = findViewById(R.id.lvItems);
                for(int i=lvItems.getFirstVisiblePosition(); i<= lvItems.getLastVisiblePosition(); i++) {
                    TreeElement item = (TreeElement) adapter.getItem(i);
                    if(item.item.code.equals(id)) {
                        isVisible = true;
                        break;
                    }
                }
                if(isVisible)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    class Adapter extends BaseAdapter {
        Folder current = null;
        public void setFolder(Folder f) {
            current = f;
            notifyDataSetChanged();
        }

        public Folder getCurrent() { return current; }

        @Override public int getCount() { return current == null ? 0 : current.size(); }
        @Override public Object getItem(int position) { return current.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override public int getViewTypeCount() { return 2; }

        @Override
        public int getItemViewType(int position) {
            TreeElement item = (TreeElement) getItem(position);
            return item.item.isFolder ? 1 : 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            TreeElement item = (TreeElement) getItem(position);

            if(item.item.isFolder)
                return setFolderView((Folder)item, view);
            return setPriceView(item, view);
        }

        private View setPriceView(final TreeElement item, View view) {
            if(view == null) {
                view = View.inflate(PriceCatalog.this, R.layout.price_row, null);
            }
            View ret = view;
            TextView tv = ret.findViewById(R.id.tvName);
            tv.setText(item.item.name);

            TextViewCrossOut tco = ret.findViewById(R.id.tvCost);
            tco.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.item.cost)));

            tv = ret.findViewById(R.id.tvActCost);
            if(item.item.discount > 0) {
                tv.setText(Html.fromHtml(String.format("%.02f &#x20bd", item.item.cost - item.item.discount)));
                tco.setCrossOut(true);
            } else {
                tv.setText("");
                tco.setCrossOut(false);
            }

            PriceQtyPickerOld pq = view.findViewById(R.id.pqQty);
            pq.setData(item.item, PartnerList.getCurrent().basket);

            PictureHolder.setImage((ImageView) view.findViewById(R.id.imageView), item.item);

            View v = view.findViewById(R.id.btnAction);
            if(item.item.action != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) { ActionRules.open(v.getContext(), item.item.action.getId()); }
                });
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
                v.setOnClickListener(null);
            }

            return ret;
        }

        private View setFolderView(Folder item, View view) {
            if(view == null) {
                view = View.inflate(PriceCatalog.this, R.layout.folder_row, null);
            }

            View ret = view;
            TextView tv = ret.findViewById(R.id.tvName);
            tv.setText(item.item.name);
            return ret;
        }
    }
}
