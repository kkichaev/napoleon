package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog.OnDateSetListener;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.dialogs.SetContactsGroupDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.fragments.PropertiesContactFragment;
import com.ashberrysoft.leadertask.modern.dialog.AddressDialog;
import com.ashberrysoft.leadertask.modern.dialog.GenderDialog;
import com.ashberrysoft.leadertask.modern.dialog.PickDateDialog;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesContactHeaderView extends LinearLayout implements View.OnClickListener {

    // VIEW's
    private final EditText mDisplayAs;
    private final EditText mLastName;
    private final EditText mName;
    private final EditText mSername;
    private final TextView mGroup;
    private final LinearLayout mGroupContainer;
    private final ImageView mGroupImage;
    private final ImageView mImageSex;
    private final ImageView mImageDelBirthday;
    private final EditText mDetails;
    private final EditText mPost;
    private final EditText mOrg;

    //private final TextView mConnection;
    private final TextView mBirthday;
    private final LinearLayout mBirthdayContainer;
    private final TextView tvHomeAddress;
    private final TextView tvWorkAddress;
    private final View mDividerBirthday;
    private final View mDividerHomeAddress;
    private final View mDividerWorkAddress;
    private PropertiesContactFragment mFragment;
    private Contact mContact;
    private Long mDateBirthday;
    // VALUE
    private UUID mLastUUID = null;
    private UUID mUUID = null;
    private int mGender = 4;
    private boolean mNewContact;
    private ArrayList <String> mHomeAddress = new ArrayList<>(5);
    private ArrayList <String> mWorkAddress = new ArrayList<>(5);
    private OnDateSetListener  mListener;
    private String mStringBirthday;

    public PropertiesContactHeaderView(Context context, Contact contact, boolean newContact, PropertiesContactFragment fragment) {
        this(context, fragment);
        setData(contact, newContact);
    }

    public PropertiesContactHeaderView(Context context, final PropertiesContactFragment fragment) {
        super(context);
        mFragment = fragment;
        inflate(getContext(), R.layout.view_header_contact_properties, this);
        this.setOrientation(VERTICAL);

        mStringBirthday = getResources().getString(R.string.contact_birthday)+": ";
        mDisplayAs = (EditText) findViewById(R.id.etDisplayAs);
        mImageSex = (ImageView) findViewById(R.id.ivSex);
        mImageDelBirthday = (ImageView) findViewById(R.id.remove_birthday);
        mLastName = (EditText) findViewById(R.id.etLastName);
        mName = (EditText) findViewById(R.id.etName);
        mSername = (EditText) findViewById(R.id.etSername);
        mGroup = (TextView) findViewById(R.id.tvGroup);
        mGroupContainer = (LinearLayout) findViewById(R.id.group_container);
        mGroupImage = (ImageView) findViewById(R.id.ivGroup);
        mDetails = (EditText) findViewById(R.id.etDetails);
        mPost = (EditText) findViewById(R.id.etPost);
        mOrg = (EditText) findViewById(R.id.etOrg);
        mBirthday = (TextView) findViewById(R.id.tvBirthday);
        mBirthdayContainer = (LinearLayout) findViewById(R.id.birthday_container);
        tvHomeAddress = (TextView) findViewById(R.id.tvHomeAdress);
        tvWorkAddress = (TextView) findViewById(R.id.tvWorkAdress);

        mDividerBirthday = (View) findViewById(R.id.dividerBirthday);
        mDividerHomeAddress = (View) findViewById(R.id.dividerHomeAdress);
        mDividerWorkAddress = (View) findViewById(R.id.dividerWorkAdress);


        final int textColor = LTSettings.getInstance(getContext()).isThemeDark() ? Color.WHITE : Color.BLACK;
        mDisplayAs.setTextColor(textColor);
        mLastName.setTextColor(textColor);

        mGroupContainer.setOnClickListener(this);
        mBirthday.setOnClickListener(this);
        mImageSex.setOnClickListener(this);
        tvHomeAddress.setOnClickListener(this);
        tvWorkAddress.setOnClickListener(this);
        mImageDelBirthday.setOnClickListener(this);
        mListener  = new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Date date = new Date(year, monthOfYear, dayOfMonth);
                setBirthday(date.getTime());
            }
        };
    }

    public void setData(Contact contact, boolean newContact) {
        mNewContact = newContact;
        mContact = contact;
        if (contact.getBirthday() != null) {
            mDateBirthday = contact.getBirthday().getTime();
        }
        else {
            mDateBirthday = null;
        }
        if(!mNewContact) {
            mDisplayAs.setText(contact.getTitle());
            mLastName.setText(contact.getLastName());
            mName.setText(contact.getFirstName());
            mSername.setText(contact.getMiddleName());
            String strGroup = contact.getUidGroup() == null ? "" : getContactGroup(contact.getUidGroup());
            mGroup.setText(strGroup);
            mLastUUID = contact.getUidGroup();
            mUUID = contact.getUidGroup();
            mGender = contact.getGender();
            mDetails.setText(contact.getDetails());
            mPost.setText(contact.getJobTitle());
            mOrg.setText(contact.getCompanyName());
            mBirthday.setText(mStringBirthday+(contact.getBirthday() == null ? "" : "" + getBirthdayString(new Date(contact.getBirthday().getTime()))));

            String strHomeAddress = getResources().getString(R.string.contact_home_adress);
            String strWorkAddress = getResources().getString(R.string.contact_work_adress);

            mHomeAddress.add(contact.getHomeCountry() == null ? "" : contact.getHomeCountry());
            mHomeAddress.add(contact.getHomeRegion() == null ? "" : contact.getHomeRegion());
            mHomeAddress.add(contact.getHomeCity() == null ? "" : contact.getHomeCity());
            mHomeAddress.add(contact.getHomeStreet() == null ? "" : contact.getHomeStreet());
            mHomeAddress.add(contact.getHomeIndex() == null ? "" : contact.getHomeIndex());

            mWorkAddress.add(contact.getWorkCountry() == null ? "" : contact.getWorkCountry());
            mWorkAddress.add(contact.getWorkRegion() == null ? "" : contact.getWorkRegion());
            mWorkAddress.add(contact.getWorkCity() == null ? "" : contact.getWorkCity());
            mWorkAddress.add(contact.getWorkStreet() == null ? "" : contact.getWorkStreet());
            mWorkAddress.add(contact.getWorkIndex() == null ? "" : contact.getWorkIndex());

            setAddress(tvHomeAddress, strHomeAddress, mHomeAddress);
            setAddress(tvWorkAddress, strWorkAddress, mWorkAddress);

            if (mContact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                switch (contact.getGender()) {
                    case 1:
                        mImageSex.setImageResource(R.drawable.c_men);
                        break;
                    case 2:
                        mImageSex.setImageResource(R.drawable.c_women);
                        break;
                    case 3:
                        mImageSex.setImageResource(R.drawable.c_org);
                        break;
                    default:
                        mImageSex.setImageResource(R.drawable.c_nobody);
                        break;
                }
            }
            else {
                switch (contact.getGender()) {
                    case 1:
                        mImageSex.setImageResource(R.drawable.c_men_avaleble);
                        break;
                    case 2:
                        mImageSex.setImageResource(R.drawable.c_women_avaleble);
                        break;
                    case 3:
                        mImageSex.setImageResource(R.drawable.c_org_avaleble);
                        break;
                    default:
                        mImageSex.setImageResource(R.drawable.c_nobody_avaleble);
                        break;
                }
            }
        } else {
            for (int i=0; i<5; i++) {
                mHomeAddress.add("");
                mWorkAddress.add("");
            }
        }
        setVisibleOfDelBirthdayIcon();
        mDisplayAs.requestFocus();
        if(!contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
            mDisplayAs.setEnabled(false);
            mGroupContainer.setClickable(false);
            mBirthday.setClickable(false);
            tvHomeAddress.setClickable(false);
            tvWorkAddress.setClickable(false);
            mLastName.setEnabled(false);
            mName.setEnabled(false);
            mSername.setEnabled(false);
            mGroup.setEnabled(false);
            mDetails.setEnabled(false);
            mPost.setEnabled(false);
            mOrg.setEnabled(false);
            mImageDelBirthday.setVisibility(GONE);
            mImageSex.setEnabled(false);
            //mConnection.setEnabled(false);
            if (contact.getBirthday() == null) {
                mBirthdayContainer.setVisibility(GONE);
                mDividerBirthday.setVisibility(GONE);
            }
            if (contact.getHomeCountry() == null && contact.getHomeRegion() == null && contact.getHomeCity() == null && contact.getHomeStreet() == null && contact.getHomeIndex() == null) {
                tvHomeAddress.setVisibility(GONE);
                mDividerHomeAddress.setVisibility(GONE);
            }
            if (contact.getWorkCountry() == null && contact.getWorkRegion() == null && contact.getWorkCity() == null && contact.getWorkStreet() == null && contact.getWorkIndex() == null) {
                tvWorkAddress.setVisibility(GONE);
                mDividerWorkAddress.setVisibility(GONE);
            }
        }
    }

    public Contact getData(Contact contact) {
            contact.setUsnFieldTitle(contact.getUsnFieldTitle() + 1);
            contact.setTitle(mDisplayAs.getText().toString().trim());
            contact.setUsnFieldLastName(contact.getUsnFieldLastName() + 1);
            contact.setLastName(mLastName.getText().toString().trim());
            contact.setUsnFieldFirstName(contact.getUsnFieldFirstName() + 1);
            contact.setFirstName(mName.getText().toString().trim());
            contact.setUsnFieldMiddleName(contact.getUsnFieldMiddleName() + 1);
            contact.setMiddleName(mSername.getText().toString().trim());
            contact.setUsnFieldUidGroup(contact.getUsnFieldUidGroup() + 1);
            contact.setUidGroup(mUUID);
            contact.setUsnFieldDetails(contact.getUsnFieldDetails() + 1);
            contact.setUsnFieldGender(contact.getUsnFieldGender() + 1);
            contact.setGender(mGender);
            contact.setUsnFieldDetails(contact.getUsnFieldDetails() + 1);
            contact.setDetails(mDetails.getText().toString().trim());
            contact.setUsnFieldJobTitle(contact.getUsnFieldJobTitle() + 1);
            contact.setJobTitle(mPost.getText().toString().trim());
            contact.setUsnFieldCompanyName(contact.getUsnFieldCompanyName() + 1);
            contact.setCompanyName(mOrg.getText().toString().trim());
            contact.setUsnFieldBirthday(contact.getUsnFieldBirthday() + 1);
            contact.setBirthday(mDateBirthday != null ? new Date(mDateBirthday) : null);
            contact.setHomeCountry(mHomeAddress.get(0));
            contact.setUsnFieldHomeCountry(contact.getUsnFieldHomeCountry() + 1);
            contact.setHomeRegion(mHomeAddress.get(1));
            contact.setUsnFieldHomeRegion(contact.getUsnFieldHomeRegion() + 1);
            contact.setHomeCity(mHomeAddress.get(2));
            contact.setUsnFieldHomeCity(contact.getUsnFieldHomeCity() + 1);
            contact.setHomeStreet(mHomeAddress.get(3));
            contact.setUsnFieldHomeStreet(contact.getUsnFieldHomeStreet() + 1);
            contact.setHomeIndex(mHomeAddress.get(4));
            contact.setUsnFieldHomeIndex(contact.getUsnFieldHomeIndex() + 1);

            contact.setWorkCountry(mWorkAddress.get(0));
            contact.setUsnFieldWorkCountry(contact.getUsnFieldWorkCountry() + 1);
            contact.setWorkRegion(mWorkAddress.get(1));
            contact.setUsnFieldWorkRegion(contact.getUsnFieldWorkRegion() + 1);
            contact.setWorkCity(mWorkAddress.get(2));
            contact.setUsnFieldWorkCity(contact.getUsnFieldWorkCity() + 1);
            contact.setWorkStreet(mWorkAddress.get(3));
            contact.setUsnFieldWorkStreet(contact.getUsnFieldWorkStreet() + 1);
            contact.setWorkIndex(mWorkAddress.get(4));
            contact.setUsnFieldWorkIndex(contact.getUsnFieldWorkIndex() + 1);

            contact.setUsnPlusPlus();
        if(mNewContact) {
            //+другие параметры
            contact.setUid(UUID.randomUUID());
            contact.setOrder(getMaxPosition() + 1);
            contact.setUsnFieldFoto(0);
            contact.setUsnEntity(0);
            contact.setGender(0);
            contact.setNotifyBirthday(false);
            contact.setFavorite(false);
            contact.setShowNavigator(false);
            contact.setEmailCreator(LTSettings.getInstance().getUserName());
        }
        return contact;
    }

    private int getMaxPosition() {
        List<Contact> contacts = DbHelper.getInstance(getContext()).getAllContacts();
        int order = 0;
        if(contacts != null) {
            for (Contact contact: contacts) {
                if (contact.getOrder() > order) {
                    order = contact.getOrder();
                }
            }

        }
        return order;
    }

    private void setVisibleOfDelBirthdayIcon() {
        if (mDateBirthday != null) {
            mImageDelBirthday.setVisibility(VISIBLE);
        } else {
            mImageDelBirthday.setVisibility(GONE);
        }
    }

    public EditText getEditText() {
        return mDisplayAs;
    }

    private String getContactGroup(UUID uid) {
        List<ContactsGroup> groups = null;
        try {
            groups = DbHelper.getInstance(getContext()).getContactsGroupDao().queryBuilder().where().in(ContactsGroup.FIELD_UID, uid).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (groups == null || groups.size() == 0) {
            mGroupImage.setImageResource(R.drawable.cg_spr);
            return "";
        }
        else {
            if (groups.get(0).getCreator().equals(LTSettings.getInstance().getUserName())) {
                if (groups.get(0).getSharedUsers() != null) {
                    mGroupImage.setImageResource(R.drawable.cg_shared);
                } else {
                    mGroupImage.setImageResource(R.drawable.cg_my);
                }
            } else {
                mGroupImage.setImageResource(R.drawable.cg_avalibale);
            }
            return groups.get(0).getName();
        }
    }

    public void setContactAddressHome(ArrayList <String> arrayListHome) {
        mHomeAddress = arrayListHome;
        String strHomeAddress = getResources().getString(R.string.contact_home_adress);
        setAddress(tvHomeAddress, strHomeAddress, mHomeAddress);
    }

    public void setContactAddressWork(ArrayList <String> arrayListWork) {
        mWorkAddress = arrayListWork;
        String strWorkAddress = getResources().getString(R.string.contact_work_adress);
        setAddress(tvWorkAddress, strWorkAddress, mWorkAddress);
    }

    public void setAddress(TextView textView, String strAddress, ArrayList<String> arrayList) {
        textView.setText(strAddress + ": " + (arrayList.get(0) == null ? " " : arrayList.get(0)) + (arrayList.get(1) == null ? "" : " " + arrayList.get(1)) + (arrayList.get(2) == null ? "" : " " + arrayList.get(2)) + (arrayList.get(3) == null ? "" : " " + " " + arrayList.get(3)) + (arrayList.get(4) == null ? "" : " " + " " + arrayList.get(4)));
        }

    public void setContactGender(int i) {
        mGender = i;
        switch (i) {
            case 1:
                mImageSex.setImageResource(R.drawable.c_men);
                break;
            case 2:
                mImageSex.setImageResource(R.drawable.c_women);
                break;
            case 3:
                mImageSex.setImageResource(R.drawable.c_org);
                break;
            default:
                mImageSex.setImageResource(R.drawable.c_nobody);
                break;
        }
    }

    public void setContactGroup(ContactsGroup contactGroup) {
        if (contactGroup != null) {
            mUUID = contactGroup.getId();
            mGroup.setText(contactGroup.getName());
            if (contactGroup.getCreator() != null) {
                if (contactGroup.getCreator().equals(LTSettings.getInstance().getUserName())) {
                    if (contactGroup.getSharedUsers() != null) {
                        mGroupImage.setImageResource(R.drawable.cg_shared);
                    } else {
                        mGroupImage.setImageResource(R.drawable.cg_my);
                    }
                } else {
                    mGroupImage.setImageResource(R.drawable.cg_avalibale);
                }
            } else {
                mGroupImage.setImageResource(R.drawable.cg_avalibale);
            }
        }
        else {
            mUUID = null;
            mGroupImage.setImageResource(R.drawable.cg_spr);
            mGroup.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_container:
                ContactsGroup contactsGroup = null;
                try {
                    contactsGroup = DbHelper.getInstance(getContext()).getContactsGroupDao().queryBuilder().where().eq(ContactsGroup.FIELD_UID, mUUID).queryForFirst();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                SetContactsGroupDialog.newInstance(mFragment, contactsGroup).showDialog(mFragment.getFragmentManager());
                break;

            case R.id.tvBirthday:
                if (mDateBirthday != null) {
                    PickDateDialog.newInstance(mFragment, mDateBirthday).showDialog(mFragment.getFragmentManager());
                }else {
                    PickDateDialog.newInstance(mFragment, TimeHelper.currentTimeMillisWithoutTimeZone()).showDialog(mFragment.getFragmentManager());
                }
                break;

            case R.id.ivSex:
                GenderDialog.newInstance(mFragment, mGender).showDialog(mFragment.getFragmentManager());
                break;

            case R.id.tvHomeAdress:
                AddressDialog.newInstance(mFragment, mHomeAddress, true).showDialog(mFragment.getFragmentManager());
                break;
            case R.id.tvWorkAdress:
                AddressDialog.newInstance(mFragment, mWorkAddress, false).showDialog(mFragment.getFragmentManager());

            case R.id.remove_birthday:
                setBirthday(null);
                break;
            default:
                break;
        }
    }

    private String getBirthdayString(Date date) {
        return date == null ? "" : TimeHelper.getInstance().getSimpleDate(date);
    }

    public void setBirthday(Long date) {
        if (date == null) {
            mBirthday.setText(mStringBirthday+"");
            mDateBirthday = null;
        } else {
            mBirthday.setText(mStringBirthday+getBirthdayString(new Date(date)));
            mDateBirthday = date;
        }
        setVisibleOfDelBirthdayIcon();
    }
}