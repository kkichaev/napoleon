package com.ashberrysoft.leadertask.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CategoryAdapter;
import com.ashberrysoft.leadertask.adapters.ContactsGroupAdapter;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroupTreeRoot;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.fragments.LTBaseFragment;
import com.ashberrysoft.leadertask.fragments.PropertiesContactFragment;

import java.io.Serializable;
import java.util.Set;

/**
 * Диалог для установления категории задачи
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SetContactsGroupDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String CLASS_PATH = SetContactsGroupDialog.class.getName();
    private static final String DIALOG_TAG = CLASS_PATH + "DIALOG_TAG";
    private static final String EXTRA_SELECTED_CONTACTS_GROUP = CLASS_PATH + "EXTRA_SELECTED_CONTACTS_GROUP";
    private static final String DETAIL_MESSAGE = "Call this custom method: showDialog(FragmentManager manager)";
    public static final int REQUEST_CODE = R.id.contacts_group_dialog_request_code;

    // VALUE's
    private ContactsGroup mSelectedContactsGroups;

    // ADAPTER
    private ContactsGroupAdapter mAdapter;

    public static SetContactsGroupDialog newInstance(Fragment fragment, ContactsGroup contactsGroups) {
        final Bundle b = new Bundle();
        if (contactsGroups != null) {
            b.putSerializable(EXTRA_SELECTED_CONTACTS_GROUP, (Serializable) contactsGroups);
        }

        final SetContactsGroupDialog d = new SetContactsGroupDialog();
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
        if (bundle.containsKey(EXTRA_SELECTED_CONTACTS_GROUP)) {
            mSelectedContactsGroups = (ContactsGroup) bundle.getSerializable(EXTRA_SELECTED_CONTACTS_GROUP);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final DbHelper db = DbHelper.getInstance(getActivity());
        mAdapter = new ContactsGroupAdapter(getActivity(), new ContactsGroupTreeRoot(getActivity()),
                mSelectedContactsGroups);

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.set_contacts_group_dialog, null);
        final ListView listView = (ListView) v.findViewById(R.id.contacts_groups);
        listView.setCacheColorHint(0);
        listView.setAdapter(mAdapter);

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setTitle(R.string.contact_groups);
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

        final ContactsGroup contactsGroups = mAdapter.getSelectedContactsGroup();
        if (contactsGroups != null) {
            b.putSerializable(EXTRA_SELECTED_CONTACTS_GROUP, (Serializable) contactsGroups);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (getTargetFragment() != null && which == Dialog.BUTTON_POSITIVE) {
                ((PropertiesContactFragment) getTargetFragment()).onFragmentResult(mAdapter.getSelectedContactsGroup(), REQUEST_CODE);
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