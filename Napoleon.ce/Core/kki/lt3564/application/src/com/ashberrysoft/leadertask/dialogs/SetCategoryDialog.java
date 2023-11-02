package com.ashberrysoft.leadertask.dialogs;

import java.io.Serializable;
import java.util.Set;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CategoryAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.CategoriesRootTreeItem;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;

/**
 * Диалог для установления категории задачи
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SetCategoryDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String CLASS_PATH = SetCategoryDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_SELECTED_CATEGORIES = CLASS_PATH + "EXTRA_SELECTED_CATEGORIES";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.category_dialog_request_code;

    // VALUE's
    private Set<Category> mSelectedCategories;

    // ADAPTER
    private CategoryAdapter mAdapter;

    public static SetCategoryDialog newInstance(Fragment fragment, Set<Category> categories) {
        final Bundle b = new Bundle();
        if (categories != null) {
            b.putSerializable(EXTRA_SELECTED_CATEGORIES, (Serializable) categories);
        }

        final SetCategoryDialog d = new SetCategoryDialog();
        d.setTargetFragment(fragment, REQUEST_CODE);
        d.setArguments(b);

        return d;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        if (bundle.containsKey(EXTRA_SELECTED_CATEGORIES)) {
            mSelectedCategories = (Set<Category>) bundle.getSerializable(EXTRA_SELECTED_CATEGORIES);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DbHelper db = DbHelper.getInstance(getActivity());
        mAdapter = new CategoryAdapter(getActivity(), new CategoriesRootTreeItem(getActivity(), db, false),
                mSelectedCategories);

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_category_dialog, null);
        final ListView listView = (ListView) v.findViewById(android.R.id.list);
        listView.setCacheColorHint(0);
        listView.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.task_category);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, this);

        return ad.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        final Set<Category> categories = mAdapter.getSelectedCategories();
        if (categories != null) {
            b.putSerializable(EXTRA_SELECTED_CATEGORIES, (Serializable) categories);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == Dialog.BUTTON_POSITIVE) {
            if (getTargetFragment() instanceof LTBaseFragment) {
                ((LTBaseFragment) getTargetFragment()).onFragmentResult(mAdapter.getSelectedCategories(), REQUEST_CODE);
            }
        }

        dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        throw new NoSuchMethodError(DETAIL_MESSAGE);
    }

    public void showDialog(FragmentManager manager) {
        if (manager.findFragmentByTag(DIALOG_TAG) == null) {
            super.show(manager, DIALOG_TAG);
        }
    }

    public static void setTargetFragment(Fragment target, FragmentManager manager) {
        final Fragment fragment = manager.findFragmentByTag(DIALOG_TAG);
        if (fragment != null && fragment instanceof DialogFragment) {
            fragment.setTargetFragment(target, REQUEST_CODE);
        }
    }
}