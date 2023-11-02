package com.ashberrysoft.leadertask.modern.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CategoryAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.CategoriesRootTreeItem;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskCategoriesDialog extends BaseDialog//
        implements OnClickListener {

    public static final int CODE = R.id.category_dialog_request_code;
    private static final String EXTRA_CATEGORIES = "EXTRA_CATEGORIES";

    // VALUE's
    private String mCategories;

    // ADAPTER
    private CategoryAdapter mAdapter;
    private static Fragment mTarget;

    public static TaskCategoriesDialog newInstance(Fragment target, LTask task) {
        final Bundle b = new Bundle(1);
        if (task.getCategories() != null) {
            b.putString(EXTRA_CATEGORIES, task.getCategories());
        }

        final TaskCategoriesDialog d = new TaskCategoriesDialog();
        d.setTargetFragment(target, CODE);
        d.setArguments(b);

        mTarget = target;
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        mCategories = bundle.getString(EXTRA_CATEGORIES);

        final Set<Category> categories;
        if (mCategories == null) {
            categories = null;

        } else {
            final String[] uids = TaskHelper.getCategoriesFromString(mCategories);
            categories = new HashSet<>(uids.length);
            Category category;

            for (String uid : uids) {
                category = new Category();
                category.setId(UUID.fromString(uid));

                categories.add(category);
            }
        }

        final DbHelper db = DbHelper.getInstance(getActivity());
        mAdapter = new CategoryAdapter(getActivity(), new CategoriesRootTreeItem(getActivity(), db, false), categories);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_category_dialog, null);

        final ListView lv = (ListView) v.findViewById(R.id.list_categories);
        lv.setCacheColorHint(0);
        lv.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.unboarding_dialog_footer, null);
        final TextView textView = (TextView) footer.findViewById(R.id.unbord_diag_text);
        if (mAdapter.isEmpty()) {;
            textView.setText(getResources().getString(R.string.unboarding_dialog_categories));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }

        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(false);
        ad.setView(v);
        //

        //////////////////
        ad.setTitle(R.string.task_category);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);
        ad.setNeutralButton(getResources().getString(R.string.btn_add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getDialog() != null){
                    getDialog().cancel();
                    AddCategoryDialog.newInstance(mTarget).showDialog(getFragmentManager());
                }
            }
        });

        return ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(EXTRA_CATEGORIES, getTaskCategories());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            receiveObjects(CODE, getTaskCategories());
        }
    }

    private String getTaskCategories() {
        final Set<Category> categories = mAdapter.getSelectedCategories();

        if (categories == null) {
            return null;

        } else {
            final List<Category> list = new ArrayList<>(categories.size());
            for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext();) {
                list.add(iterator.next());
            }
            Collections.sort(list, Category.COMPARATOR);
            return TaskHelper.getStringFromCategories(list);
        }
    }
}