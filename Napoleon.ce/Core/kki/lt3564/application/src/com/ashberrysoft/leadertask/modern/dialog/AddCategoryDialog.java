package com.ashberrysoft.leadertask.modern.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.modern.fragment.BaseSyncStatusFragment;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class AddCategoryDialog extends BaseDialog {

    public static final int CODE = R.id.dialog_add_category;
    private static final String CLASS_PATH = AddCategoryDialog.class.getSimpleName();

    // ADAPTER
    private EditText editText1;
    private View mBg;
    private String mDefaultColor;
    private static Context mContext;


    public static AddCategoryDialog newInstance(Fragment target) {
        final AddCategoryDialog d = new AddCategoryDialog();
        mContext = target.getActivity().getApplicationContext();
        d.setTargetFragment(target, CODE);
        final Bundle b = new Bundle();
        d.setArguments(b);
        return d;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final Bundle bundle = getArguments();
        if ( bundle != null) {

        }
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_marker_category, null);
        editText1 = (EditText) v.findViewById((R.id.editText1));
        mDefaultColor = getDefaultColor();
        mBg = (View) v.findViewById(R.id.backColor);
        mBg.setBackgroundColor(Color.parseColor(mDefaultColor));
        mBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultColor = getDefaultColor();
                mBg.setBackgroundColor(Color.parseColor(mDefaultColor));
            }
        });

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(v);
        ad.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = editText1.getText().toString().trim();

                if (s.length() > 0) {
                    Category category = new Category();
                    setCategoryParams(category);
                    category.setName(s);
                    saveCategory(category);
                    if (getTargetFragment() instanceof BaseSyncStatusFragment) {
                        ((BaseSyncStatusFragment) getTargetFragment()).onFragmentResult(category, CODE);
                    }
                }
                getDialog().dismiss();
            }
        });
        ad.setNegativeButton(R.string.btn_cancel, null);

        Dialog d = ad.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
        return d;
    }
    
    
    private void setCategoryParams(Category category) {
        category.setUsn(0);
        category.setUsnName(category.getUsnName() + 1);
        category.setColor(mDefaultColor);

        category.setId(UUID.randomUUID());
        category.setCreator(LTSettings.getInstance().getUserName());

        category.setOrder(getOrder(mContext) + 1);
        category.setUsnOrder(category.getUsnOrder() + 1);
        //
        getApp().getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
    }

    private int getOrder(Context context) {
        int order = 0;

        final List<Category> categories;
        try {
            categories = DbHelper.getInstance(context).getCategoryDao().queryForAll();
        } catch (SQLException e) {
            return order;
        }

        order = categories.size();
        return order;
    }

    private void saveCategory(Category category) {
        try {
            DbHelper.getInstance(getApp()).getCategoryDao().create(category);
            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(mContext);
                linkHelper.createTotalLink(category);

            mContext.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
        } catch (SQLException e) {
            Utils.toLog(e);
        }

        Utils.startSync(getApp());
    }

    private String getDefaultColor() {
        String defaultColors[]={"#DF0C0C","#FF8C68","#CD5F00","#965500","#FFEB00","#878700","#73D246","#008C8C","#5ACDFF","#0F5FFF","#5F32BE","#A05AB9","#FF4BC8","#5A0046","#BEBEBE","#465069"};
        return defaultColors[new Random().nextInt(defaultColors.length)];
    }

}