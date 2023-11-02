package com.ashberrysoft.leadertask.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.views.ContactListItem;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ContactAdapter extends BaseAdapter//
        implements ContactListItem.OnContactListItemListener {

    // VALUE's
    private ArrayList<Contact>  mSelectedContacts  = new ArrayList();
    private List<Contact> mAllContacts;
    private ArrayList mCheckedItems = new ArrayList();

    public ContactAdapter( List<Contact> allContacts, ArrayList<Contact>  selectedContacts) {
        for (Contact contact: selectedContacts) {
            mSelectedContacts.add(contact);
        }
        mAllContacts = allContacts;
        if(mSelectedContacts != null) {
            int i = 0;
            for (Contact contact : mAllContacts) {
                for (Contact checkedContact : mSelectedContacts) {
                    if (contact.getUid().equals(checkedContact.getUid())) {
                        mCheckedItems.add(i);
                    }
                }
                i++;
            }
        }
    }

    public ArrayList<Contact> getSelectedContacts() {
        return mSelectedContacts;
    }

    @Override
    public View getView(int i, View cV, ViewGroup parent) {
        final ContactListItem v = cV == null ? new ContactListItem(parent.getContext(), this)
                : (ContactListItem) cV;

        v.setData( getItem(i));
        boolean check = false;
        for (int m = 0; m < mCheckedItems.size(); m++) {
            if ((""+i).equals(mCheckedItems.get(m).toString())) {
                check = true;
                break;
            }
        }
        v.setChecked(check);

        return v;
    }

    @Override
    public int getCount() {
        return mAllContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mAllContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void onContactChecked(Contact contact, boolean isChecked) {
        if (isChecked) { // выбран
            if (!mSelectedContacts.contains(contact)) {
                mSelectedContacts.add(contact);
                mCheckedItems.add(mAllContacts.indexOf(contact));
            }
        }
        else { // не выбран
            if (mSelectedContacts.contains(contact)) {
                mSelectedContacts.remove(contact);
                if (mAllContacts.indexOf(contact) < mCheckedItems.size()) {
                    mCheckedItems.remove(mAllContacts.indexOf(contact));
                }
            }
        }
    }

    @Override
    public void onContactOpen(Contact contact) {

    }
}