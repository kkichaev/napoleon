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
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.ContactAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.ContactTreeRoot;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;

public class TaskContactsDialog extends BaseDialog//
        implements OnClickListener {

    public static final int CODE = R.id.contacts_dialog_request_code;
    private static final String EXTRA_CONTACTS = "EXTRA_CONTACTS";

    // VALUE's
    private String mContacts;

    // ADAPTER
    private ContactAdapter mAdapter;

    public static TaskContactsDialog newInstance(Fragment target, LTask task) {
        final Bundle b = new Bundle(1);
        if (task.getContacts() != null) {
            b.putString(EXTRA_CONTACTS, task.getContacts());
        }

        final TaskContactsDialog d = new TaskContactsDialog();
        d.setTargetFragment(target, CODE);
        d.setArguments(b);

        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        setHasOptionsMenu(true);
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        mContacts = bundle.getString(EXTRA_CONTACTS);

        ArrayList<Contact> contacts = new ArrayList<>();
        if (mContacts == null) {
            contacts = new ArrayList<>();

        } else {
            final String[] uids = TaskHelper.getContactsFromString(mContacts);
            Contact contact;

            for (String uid : uids) {
                contact = new Contact();
                contact.setUid(UUID.fromString(uid));

                contacts.add(contact);
            }
        }
        List<Contact> allContacts = DbHelper.getInstance(getActivity()).getAllContacts();
        if (allContacts == null) {
            allContacts = new ArrayList<>();
        }
        mAdapter = new ContactAdapter(allContacts ,contacts);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_contact_dialog, null);

        final ListView lv = (ListView) v.findViewById(R.id.contacts);
        lv.setCacheColorHint(0);
        lv.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.task_contact);
        ad.setPositiveButton(R.string.btn_ok, this);
        ad.setNegativeButton(R.string.btn_cancel, null);

        return ad.show();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(EXTRA_CONTACTS, getTaskContacts());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            receiveObjects(CODE, getTaskContacts());
        }
    }

    private String getTaskContacts() {
        final ArrayList<Contact> contacts = mAdapter.getSelectedContacts();

        if (contacts == null) {
            return null;

        } else {
            final List<Contact> list = new ArrayList<>(contacts.size());
            for (Iterator<Contact> iterator = contacts.iterator(); iterator.hasNext();) {
                list.add(iterator.next());
            }
            Collections.sort(list, Contact.COMPARATOR);
            return TaskHelper.getStringFromContacts(list);
        }
    }
}