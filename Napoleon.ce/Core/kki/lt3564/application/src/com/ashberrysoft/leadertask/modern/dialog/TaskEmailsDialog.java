package com.ashberrysoft.leadertask.modern.dialog;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CategoryAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.CategoriesRootTreeItem;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.modern.adapter.EmailsAdapter;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public class TaskEmailsDialog extends BaseDialog//
        implements OnClickListener, AdapterView.OnItemClickListener {

    public static final int CODE = R.id.access;
    private static final String EMAILS = "EMAILS";

    // VALUE's
    private String emails;

    // ADAPTER
    private BaseAdapter adapter;
    private static Fragment mTarget;

    public static TaskEmailsDialog newInstance(Fragment target, LTask task) {
        final Bundle b = new Bundle(1);

        if (task.getEmails() != null) {
            b.putString(EMAILS, task.getEmails());
        }

        final TaskEmailsDialog d = new TaskEmailsDialog();
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
        emails = bundle.getString(EMAILS);

        final DbHelper db = DbHelper.getInstance(getActivity());
        adapter = new EmailsAdapter(getActivity());
        setTaskEmail();
    }

    private void setTaskEmail() {
        if (emails != null && emails.trim().length() > 0 ){
            String[] arr = emails.split(SharedStrings.SPLIT_DOT_DOBLE);

            ((EmailsAdapter) adapter).checked = new HashSet<>(Arrays.asList(arr));
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_category_dialog, null);

        final ListView lv = (ListView) v.findViewById(R.id.list_categories);
        lv.setCacheColorHint(0);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.unboarding_dialog_footer, null);
        footer.findViewById(R.id.unbord_diag_text).setVisibility(View.GONE);

        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(false);
        ad.setView(v);

        ad.setTitle(R.string.task_access);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

        return ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(EMAILS, getTaskEmails());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            receiveObjects(CODE, getTaskEmails());
        }
    }

    private String getTaskEmails() {
        Set<String> emails = ((EmailsAdapter)adapter).checked;

        final StringBuilder sb = new StringBuilder();

        for (String s : emails) {
            if (sb.length() > 0){
                sb.append(SharedStrings.DOT_C);
                sb.append(SharedStrings.DOT_C);
            }

            sb.append(s);
        }

        return sb.toString();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter != null){
            ((EmailsAdapter)adapter).setChecked(position);
            adapter.notifyDataSetChanged();
        }
    }
}