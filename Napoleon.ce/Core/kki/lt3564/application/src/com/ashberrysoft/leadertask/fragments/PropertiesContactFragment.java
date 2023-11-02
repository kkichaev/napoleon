package com.ashberrysoft.leadertask.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CommunicationAdapter;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.DownloadFile;
import com.ashberrysoft.leadertask.dialogs.SetContactsGroupDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.interfaces.ObjectsReceiver;
import com.ashberrysoft.leadertask.modern.adapter.EditContactFilesAdapter;
import com.ashberrysoft.leadertask.modern.dialog.AddressDialog;
import com.ashberrysoft.leadertask.modern.dialog.GenderDialog;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.dialog.PickDateDialog;
import com.ashberrysoft.leadertask.modern.dialog.SetCommunicationDialog;
import com.ashberrysoft.leadertask.modern.view.list_item.ContactFileListItemView.OnContactFileListener;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.CommunicationListItemView;
import com.ashberrysoft.leadertask.views.PerformerListItemView.OnPerformerListItemListener;
import com.ashberrysoft.leadertask.views.PropertiesContactHeaderView;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesContactFragment extends BaseFeaturesFragment implements OnPerformerListItemListener, CommunicationListItemView.OnCommunicationListItemListener, OnContactFileListener, ObjectsReceiver {

    private static final String CLASS_PATH = PropertiesContactFragment.class.getSimpleName();
    private static final String EXTRA_CONTACT = CLASS_PATH + "EXTRA_CONTACT";
    private static final String EXTRA_CONTACT_NEW = CLASS_PATH + "EXTRA_CONTACT_NEW";

    private static final String ACTION_DOWNLOAD_RESULT_CONTACT = "ACTION_DOWNLOAD_RESULT_CONTACT";
    private static final String ACTION_MEDIA_RESULT_CONTACT = "ACTION_MEDIA_RESULT_CONTACT";
    private static final String EXTRA_CONTACT_FILE = "EXTRA_CONTACT_FILE";
    private static final String EXTRA_REQUEST_CODE = "EXTRA_REQUEST_CODE";
    private static final String EXTRA_TEMP_FILE = "EXTRA_TEMP_FILE";
    private static final String EXTRA_CONTACT_FILES_DELETED = "EXTRA_CONTACT_FILES_DELETED";
    private static final String EXTRA_ALL_CONTACTS = "EXTRA_ALL_CONTACTS";
    private static final String EXTRA_ALL_COMM = "EXTRA_ALL_COMM";
    private ProgressDialog mProgress;

    private LocalBroadcastManager mBroadcastManager;
    private BroadcastReceiver mReceiver;

    private enum RequestCode {
        NONE_CONTACT, CAMERA_CONTACT, GALLERY_CONTACT;
    }

    // VIEW's
    private PropertiesContactHeaderView mHeaderView;

    // VALUE's
    private Contact mContact;
    private boolean mContactNew;
    private boolean mShowKeyBoard;
    private int mPosition;
    private ListView mFooterFiles;
    private ContactFile mTempContactFile;
    private List<ContactFile> mContactFilesDeleted;
    private File mTempFile;
    private RequestCode mRequestCode;
    private List<ContactFile> mAllContactFiles;
    private List<String> mAllContactComm;
    private boolean isDeleteComm;

    // ADAPTER
    private CommunicationAdapter mAdapter;
    private EditContactFilesAdapter mFilesAdapter;


    public static PropertiesContactFragment newInstance(Contact contactGroup) {
        final PropertiesContactFragment f = new PropertiesContactFragment();

        if (contactGroup != null) {
            final Bundle b = new Bundle();
            b.putSerializable(EXTRA_CONTACT, contactGroup);
            f.setArguments(b);
        }

        return f;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        if (bundle != null) {
            mContactNew = bundle.getBoolean(EXTRA_CONTACT_NEW, false);
            mContact = (Contact) bundle.get(EXTRA_CONTACT);
            mRequestCode = RequestCode.values()[bundle.getInt(EXTRA_REQUEST_CODE, 0)];
            mContactFilesDeleted = (List<ContactFile>) bundle.getSerializable(EXTRA_CONTACT_FILES_DELETED);
            mAllContactFiles = (List<ContactFile>) bundle.getSerializable(EXTRA_ALL_CONTACTS);
            mAllContactComm = (List<String>) bundle.getSerializable(EXTRA_ALL_COMM);
            mTempFile = (File) bundle.getSerializable(EXTRA_TEMP_FILE);
            mShowKeyBoard = false;

        } else {
            mContactNew = true;
            mContact = new Contact();
            mRequestCode = RequestCode.NONE_CONTACT;
            mContact.setEmailCreator(LTSettings.getInstance().getUserName());
            mShowKeyBoard = true;
        }

        if (mContactFilesDeleted == null) {
            mContactFilesDeleted = new ArrayList<>();
        }

        mHeaderView = new PropertiesContactHeaderView(getActivity(), this);
        mHeaderView.setData(mContact, mContactNew);


        mAdapter = new CommunicationAdapter(getActivity(), this, LTSettings.getInstance().getUserName().equals(mContact.getEmailCreator()));

        if (mAllContactComm == null) {
            mAllContactComm = new ArrayList<>();
            if (mContact.getCommunications() != null) {
                mAllContactComm = getCommunications(mContact.getCommunications());
            }
        }

        mAdapter.setData(mAllContactComm);


        if (mAllContactFiles == null) {
            mAllContactFiles = new ArrayList<>();
            Cursor cursor = null;
            try {
                final ContentResolver cr = mApp.getContentResolver();
                cursor = cr.query(ContactsFileContract.CONTENT_URI, null, null, null, null);
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        ContactFile cF = new ContactFile(cursor);
                        if (cF.getContactId().equals(mContact.getUid()) && !cF.isDeleteObject()) {
                            mAllContactFiles.add(cF);
                        }
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }

        mFilesAdapter = new EditContactFilesAdapter(SortFilesList(mAllContactFiles), this);
    }

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter filter = getIntentFilter();
        if (filter != null) {
            mReceiver = getBroadcastReceiver();
            mBroadcastManager.registerReceiver(mReceiver, filter);
        }
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceive(context, intent);
            }
        };
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mBroadcastManager = LocalBroadcastManager.getInstance(mApp);
        mDbHelper = DbHelper.getInstance(mApp);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mShowKeyBoard) {
            showKeyboard(mHeaderView.getEditText());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        mListView = (ListView) inflater.inflate(R.layout.edit_features_fragment, container, false);
        if (getListViewHeader() != null) {
            mListView.addHeaderView(getListViewHeader());
        }

        final View footer = View.inflate(mApp, R.layout.custom_footer_to_project_user_adapter, null);
        TextView textFooter =(TextView) footer.findViewById(R.id.tv_add_user_to_project);
        textFooter.setText(getResources().getString(R.string.project_add_communication));
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnFooterClick();
            }
        });

        mFooterFiles =  (ListView) View.inflate(mApp, R.layout.edit_task_communication, null);
        mFooterFiles.setAdapter(mFilesAdapter);
        setFilesLayoutWidth();
        if (LTSettings.getInstance().getUserName().equals(mContact.getEmailCreator())) {
            mListView.addFooterView(footer, null, false);
        }

        //
        //FILES
        mListView.addFooterView(mFooterFiles, null, false);
        if (LTSettings.getInstance().getUserName().equals(mContact.getEmailCreator())) {
            final View footerAddFile = View.inflate(mApp, R.layout.custom_footer_to_project_user_adapter, null);
            TextView textFooterAddFile =(TextView) footerAddFile.findViewById(R.id.tv_add_user_to_project);
            textFooterAddFile.setText(getResources().getString(R.string.task_files));
            footerAddFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnFooterAddFileClick();
                }
            });
            mListView.addFooterView(footerAddFile, null, false);
        }
        //

        mListView.setAdapter(getAdapter());
        registerForContextMenu(mListView);

        return mListView;
    }

    private void OnFooterAddFileClick() {
        if (mApp.getSettings().getLicenseType() == mApp.getSettings().LICENSE_TYPE_FREE ||
                mApp.getSettings().getLicenseType() == mApp.getSettings().LICENSE_TYPE_NONE){
            LicenseDialog.newInstance().showDialog(getActivity().getFragmentManager());
        }
        else {
            AlertDialogAddFiles();
        }
    }

    private void AlertDialogAddFiles()
    {
        final String[] mCatsName ={getString(R.string.m_add_from_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setItems(mCatsName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                ChooseFileTipe(item);
            }
        });
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void ChooseFileTipe(int item)
    {
        switch (item) {
            case 0:
                galleryImage();
                break;

        }
    }

    private void OnFooterClick()
    {
        SetCommunicationDialog.newInstance(this).showDialog(getFragmentManager());
    }

    @Override
    public void onFragmentResult(Object object, int requestCode) {
        switch (requestCode) {
            case SetCommunicationDialog.CODE:
                final String communication = (String) object;
                    mAdapter.getData().add(communication);
                    mAdapter.notifyDataSetChanged();
                break;

            case SetContactsGroupDialog.REQUEST_CODE:
                mHeaderView.setContactGroup((ContactsGroup) object);
                break;

            case GenderDialog.CODE:
                mHeaderView.setContactGender((int) object);
                break;

            case AddressDialog.CODE:
                mHeaderView.setContactAddressHome((ArrayList) object);
                break;

            case AddressDialog.CODE_WORK:
                mHeaderView.setContactAddressWork((ArrayList) object);
                break;

            default:
                super.onFragmentResult(object, requestCode);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        mContact = mHeaderView.getData(mContact);
        b.putSerializable(EXTRA_CONTACT, mContact);
        b.putBoolean(EXTRA_CONTACT_NEW, mContactNew);
        b.putInt(EXTRA_REQUEST_CODE, mRequestCode.ordinal());
        b.putSerializable(EXTRA_TEMP_FILE, mTempFile);
        b.putSerializable(EXTRA_ALL_CONTACTS, (Serializable) mFilesAdapter.getData());
        b.putSerializable(EXTRA_ALL_COMM, (Serializable) mAdapter.getData());
        b.putSerializable(EXTRA_CONTACT_FILES_DELETED, (Serializable) mContactFilesDeleted);
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) { return false; }


    @Override
    public void onPerformerRemove(int position) {
        showSimpleDialog(R.string.d_performer_remove_title, R.string.d_performer_remove_message);
    }


    @Override
    protected View getListViewHeader() {
        return mHeaderView;
    }

    @Override
    protected int getActionBarTitle() {
        if (mContact == null || TextUtils.isEmpty(mContact.getTitle())) {
            return R.string.contact_new;
        } else {
            return R.string.contact_properties;
        }
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.groups;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return false;
    }

    private void galleryImage() {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(SharedStrings.MIME_TYPE_IMAGE);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_chooser_image)), RequestCode.GALLERY_CONTACT);
    }

    public void startActivityForResult(Intent intent, RequestCode code) {
        mRequestCode = code;
        super.startActivityForResult(intent, code.ordinal());
    }


    @Override
    protected boolean onSaveFeatureClick() {
        mContact = mHeaderView.getData(mContact);
        inputHide(mHeaderView);
        if (!TextUtils.isEmpty(mHeaderView.getEditText().getText().toString().trim())) {
            new SaveFilesThread().start();
            new Thread(mSaveContactRunnable).start();
        } else {
            Utils.showToast(getActivity(), R.string.t_error_feature_name);
        }

        return true;
    }

    private final Runnable mSaveContactRunnable = new Runnable() {
        @Override
        public void run() {
            List <Contact> contact = new ArrayList<>();
            setCommutication();
            contact.add(mContact);
            DbHelper.getInstance(mApp).updateContactsNew(contact);
            Utils.hideInput(mApp, mHeaderView);
        }
    };

    private void setCommutication() {
        mContact.setUsnFieldCommunications(mContact.getUsnFieldCommunications()+1);
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<mAdapter.getData().size(); i++) {
            sb.append(mAdapter.getItem(i));
            if (i+1 != mAdapter.getData().size()){
                sb.append("\n");
            }
        }
        byte[] data = new byte[0];
        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        mContact.setCommunications(base64);
    }

    @Override
    protected boolean onAddFeatureClick() {
        return false;
    }


    @Override
    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    @Override
    public void onDetach() { super.onDetach();}

    @Override
    public void onReceivingObjects(int code, Object... objects) {
        switch (code) {
            case PickDateDialog.CODE: {
                final long date = (long) objects[0];
                if (mContact.getBirthday() != null) {
                    if (date == mContact.getBirthday().getTime()) {
                        return;
                    } else {
                        mHeaderView.setBirthday(date);
                    }
                } else {
                    mHeaderView.setBirthday(date);
                }
            }
            break;

            default:
                return;
        }
    }

    @Override
    protected IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DOWNLOAD_RESULT_CONTACT);
        filter.addAction(ACTION_MEDIA_RESULT_CONTACT);

        return filter;
    }

    @Override
    public void onBroadcastReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_DOWNLOAD_RESULT_CONTACT:
                setBlocking(false);
                if (intent.hasExtra(EXTRA_CONTACT_FILE)) {
                    mFilesAdapter.notifyDataSetChanged();

                    final ContactFile file = (ContactFile) intent.getSerializableExtra(EXTRA_CONTACT_FILE);
                    onContactFileClick(file, false);

                } else {
                    Utils.showToast(getActivity(), R.string.error_file_not_downloaded);
                }
                break;

            case ACTION_MEDIA_RESULT_CONTACT:
                setBlocking(false);
                if (intent.hasExtra(EXTRA_CONTACT_FILE)) {
                    final File file = (File) intent.getSerializableExtra(EXTRA_CONTACT_FILE);
                    addContactFile(file);

                } else {
                    Utils.showToast(getActivity(), R.string.t_error_file_saving);
                }
                break;

            default:
                super.onBroadcastReceive(context, intent);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Utils.toLog("onActivityResult");
        if (data != null) {
            Utils.toLog(data);
        }

        if (resultCode != Activity.RESULT_OK) {
            if (mTempFile != null) {
                mTempFile.delete();
                mTempFile = null;
            }
            return;
        }

        final RequestCode code = mRequestCode;
        mRequestCode = RequestCode.NONE_CONTACT;

        switch (code) {
            case CAMERA_CONTACT:
                if (mTempFile != null && mTempFile.exists()) {
                    addContactFile(mTempFile);
                }
                mTempFile = null;
                break;

            case GALLERY_CONTACT:
                if (data != null) {
                    setBlocking(true);
                    new CopyFromGalleryThread(mApp, data.getData()).start();
                }
                break;

            default:
                break;
        }
    }

    public void addContactFile(File file) {
        final long order;
        if (mFilesAdapter.getCount() == 0) {
            order = 1;

        } else {
            order = mFilesAdapter.getData().get(mFilesAdapter.getCount() - 1).getOrder() + 1;
        }

        final ContactFile contactFile = new ContactFile(null, null, null,//
                file.getName(), file.length(), LTSettings.getInstance().getUserName(), order);

        mFilesAdapter.getData().add(contactFile);
        setFilesLayoutWidth();
    }

    @Override
    public void onCommunicationRemove(int position) {
        mPosition = position;
        isDeleteComm = true;
        showSimpleDialog(R.string.dell_communication, R.string.d_communication_remove_message);
    }

    @Override
    protected void onDialogPositiveButton() {
        if (isDeleteComm) {
            mAdapter.getData().remove(mPosition);
            mAdapter.notifyDataSetChanged();
        } else {
            if (mTempContactFile.isWeakLink()) {
                getFileFromContactFile(mTempContactFile).delete();

            } else {
                mTempContactFile.setDeleteObject(true);
                mContactFilesDeleted.add(mTempContactFile);
            }

            mTempContactFile = null;
            mFilesAdapter.getData().remove(mPosition);
            setFilesLayoutWidth();
        }
    }

    private void setFilesLayoutWidth() {
        mFilesAdapter.notifyDataSetChanged();
        final int h = mApp.getResources().getDimensionPixelSize(R.dimen.univ_ab_height);
        final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, h*mFilesAdapter.getCount()+mApp.getResources().getDimensionPixelSize(R.dimen.divider_small)*mFilesAdapter.getCount());
        mFooterFiles.setLayoutParams(lp);
    }

    private List<String> getCommunications(String base64String) {
        List <String> allComm = new ArrayList<String>();
        int lastIndexFounded = 0;
        byte[] data = Base64.decode(base64String, Base64.DEFAULT);
        String utf8Text ="";
        try {
            utf8Text = new String(data, "UTF-8");
            for(int i=0; i<utf8Text.length(); i++)
            {
                int indexMain = utf8Text.indexOf("\n", i);
                if (indexMain != -1) {
                    String subString = utf8Text.substring(lastIndexFounded == 0 ? lastIndexFounded : lastIndexFounded+1, indexMain);
                    allComm.add(subString);
                    lastIndexFounded = indexMain;
                    i = indexMain;
                }
                else {
                    String subString = utf8Text.substring(i, utf8Text.length());
                    allComm.add(subString);
                    break;
                }

            }
        } catch (UnsupportedEncodingException e) {
        }
        return allComm;
    }

    private List<ContactFile> SortFilesList(List<ContactFile> contactFiles)
    {
        // сортировка файлов
        for (int i=0;i<contactFiles.size(); i++)
        {
            ContactFile Item = contactFiles.get(i);
            int itemIndex = i;
            for (int j=i+1; j<contactFiles.size(); j++)
            {
                if(Item.getEmailCreator().equals(mApp.getSettings().getUserName())
                        && !Item.getEmailCreator().equals(contactFiles.get(j).getEmailCreator()))
                {
                    //меняем местами
                    ContactFile tempItem = contactFiles.get(itemIndex);
                    contactFiles.set(itemIndex, contactFiles.get(j));
                    contactFiles.set(j, tempItem);

                    Item = contactFiles.get(j);
                    itemIndex = j;
                }
            }
        }
        return contactFiles;
    }

    @Override
    public void onContactFileClick(ContactFile file, boolean remove) {
        if (remove) {
            mTempContactFile = file;
            isDeleteComm = false;
            Utils.getSimpleDialog(getActivity(), this, R.string.d_remove_file_title, R.string.d_remove_file_message_contact);
        } else {
            if (file.isFileExist()) {
                final File f = getFileFromContactFile(file);
                if (f.exists()) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f), Utils.FileWorker.getFileMimeType(f));
                    startActivity(Intent.createChooser(intent, getString(R.string.title_chooser_open)));

                } else {
                    file.setFileExist(false);
                    new ContactFileNotExistsThread(mApp, file.getFileId()).start();

                    Utils.showToast(getActivity(), file.isWeakLink() ? R.string.t_error_file_was_not_uploaded : R.string.t_error_file_not_exist);
                    mFilesAdapter.notifyDataSetChanged();
                }

            } else {
                if (file.isWeakLink()) {
                    Utils.showToast(getActivity(), R.string.t_error_file_was_not_uploaded);

                } else {
                    downloadFile(file);
                }
            }
        }
    }


    private File getFileFromContactFile(ContactFile file) {
        return new File(mApp.getAppFolder(), file.getFileName());
    }

    private void downloadFile(ContactFile file) {
        if (Utils.isNetworkAvailable(mApp)) {
            Utils.showToast(getActivity(), R.string.t_start_download_file);
            setBlocking(true);

            new DownloadFileThread(mApp, file).start();

        } else {
            Utils.showToast(getActivity(), R.string.error_internet_access);
        }
    }

    private static final class DownloadFileThread extends Thread {

        private final LTApplication mApp;
        private final ContactFile mFile;

        public DownloadFileThread(LTApplication app, ContactFile file) {
            super(DownloadFileThread.class.getSimpleName());

            mApp = app;
            mFile = file;
        }

        @Override
        public void run() {
            super.run();

            final String fileUid = String.valueOf(mFile.getFileId());
            final String fileName = mFile.getFileName();

            final Intent intent = new Intent(ACTION_DOWNLOAD_RESULT_CONTACT);
            try {
                new DownloadFile(mApp, fileUid, fileName, mApp.getSettings().getUserProfile(), mApp.getAppFolder(), 0).downloadFile();
                mFile.setFileExist(true);
                intent.putExtra(EXTRA_CONTACT_FILE, mFile);
                //
                final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                final ContentValues contentValues = new ContentValues(1);
                contentValues.put(ContactsFileContract.FILE_EXIST, true);
                StringBuilder sb = new StringBuilder();
                sb.delete(0, sb.length());
                operations.add(ContentProviderOperation.newUpdate(ContactsFileContract.CONTENT_URI).withValues(contentValues)
                    .withSelection(SelectionKeeper.equals(sb, ContactsFileContract.FIELD_FILEUID, String.valueOf(mFile.getFileId())), null).build());

                try {
                    mApp.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);
                } catch (Exception e) {
                    Utils.toLog(e);
                }
                //

            } catch (Exception e) {

            } finally {
                LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
            }
        }
    }

    private static final class ContactFileNotExistsThread extends Thread {

        private final ContentResolver mCr;
        private final String mContactFileUid;

        public ContactFileNotExistsThread(Context context, UUID contactFileUid) {
            mCr = context.getContentResolver();
            mContactFileUid = String.valueOf(contactFileUid);
        }

        @Override
        public void run() {
            super.run();

            final ContentValues cv = new ContentValues(1);
            cv.put(ContactsFileContract.FILE_EXIST, 0);

            mCr.update(ContactsFileContract.CONTENT_URI, cv, ContactsFileContract.selectionFieldFileUid(mContactFileUid), null);
        }
    }

    private void setBlocking(boolean value) {
        if (value) {
            if (mProgress == null) {
                mProgress = new ProgressDialog(getActivity());
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage(getString(R.string.blocking_process));
            }
            mProgress.show();

        } else {
            mHandler.post(mSetBlockFalse);
        }
    }

    private final Runnable mSetBlockFalse = new Runnable() {
        @Override
        public void run() {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    };

    private static final class CopyFromGalleryThread extends Thread {

        private final LTApplication mApp;
        private final Uri mUri;

        public CopyFromGalleryThread(LTApplication app, Uri uri) {
            super();

            mApp = app;
            mUri = uri;
        }

        @Override
        public void run() {
            super.run();

            final Intent intent = new Intent(ACTION_MEDIA_RESULT_CONTACT);
            final String path = Utils.getRealPathFromURI(mApp, mUri);

            try {
                if (path == null) {
                    return;
                }

                final File file = Utils.FileWorker.copyFile(Utils.FileWorker.FileType.PICTURE, path, mApp.getAppFolder());
                intent.putExtra(EXTRA_CONTACT_FILE, file);

            } catch (Exception e) {

            } finally {
                LocalBroadcastManager.getInstance(mApp).sendBroadcast(intent);
            }
        }
    }

    private final class SaveFilesThread extends Thread {
        @Override
        public void run() {
            super.run();

            final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

            StringBuilder mStringBuilder = new StringBuilder();
            ContentProviderOperation operation;
            ContentValues cv;
            String selection;
            int count = 1;

            for (ContactFile file : mFilesAdapter.getData()) {
                if (file.isWeakLink()) {
                    file.setOrder(count);
                    file.setWeakLink(false);
                    file.setContactId(mContact.getUid());

                    operation = ContentProviderOperation.newInsert(ContactsFileContract.CONTENT_URI).withValues(file.getContentValues(null)).build();

                } else {
                    if (file.getOrder() != count) {
                        Utils.clearStringBuilder(mStringBuilder);
                        selection = SelectionKeeper.equals(mStringBuilder, ContactsFileContract.FIELD_FILEUID, String.valueOf(file.getFileId()));

                        file.setOrder(count);
                        file.setUsnFieldOrder(file.getUsnFieldOrder() + 1);
                        file.setUsnEntity(0);

                        cv = new ContentValues(3);
                        cv.put(ContactsFileContract.ORDERS, file.getOrder());
                        cv.put(ContactsFileContract.FIELD_USN_ENTITY, file.getUsnEntity());
                        cv.put(ContactsFileContract.FIELD_USN_FIELD_ORDER, file.getUsnFieldOrder());

                        operation = ContentProviderOperation.newUpdate(ContactsFileContract.CONTENT_URI).//
                                withValues(cv).withSelection(selection, null).build();

                    } else {
                        operation = null;
                    }
                }

                if (operation != null) {
                    operations.add(operation);
                }
                count++;
            }                
            
            if (mContactFilesDeleted.size() > 0) {
                final ContentValues contentValues = new ContentValues(2);
                contentValues.put(ContactsFileContract.DELETE_OBJECT, true);
                contentValues.put(ContactsFileContract.WEAK_LINK, true);

                for (ContactFile file : mContactFilesDeleted) {
                    StringBuilder sb = new StringBuilder();
                    sb.delete(0, sb.length());
                    operations.add(ContentProviderOperation.newUpdate(ContactsFileContract.CONTENT_URI).withValues(contentValues)
                            .withSelection(SelectionKeeper.equals(sb, ContactsFileContract.FIELD_FILEUID, String.valueOf(file.getFileId())), null).build());
                }
            }

            if (operations.size() > 0) {
                try {
                    mApp.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);

                } catch (Exception e) {
                    Utils.toLog(e);
                }
            }
        }
    }
}