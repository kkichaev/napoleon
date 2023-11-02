package com.ashberrysoft.leadertask.fragments;

import java.util.Random;
import java.util.UUID;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.dialogs.SetColorDialogBuilder;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView.OnFeaturePropertiesHeaderListener;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * 
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesCategoryFragment extends BaseFeaturesFragment implements OnFeaturePropertiesHeaderListener, AmbilWarnaDialog.OnAmbilWarnaListener {

    private static final String CLASS_PATH = PropertiesCategoryFragment.class.getSimpleName();
    private static final String EXTRA_CATEGORY = CLASS_PATH + "EXTRA_CATEGORY";
    private static final String EXTRA_CATEGORY_NEW = CLASS_PATH + "EXTRA_CATEGORY_NEW";

    // VIEW's
    private PropertiesFeatureHeaderView mHeaderView;

    // VALUE's
    private Category mCategory;
    private boolean mCategoryNew;
    private boolean mShowKeyBoard;

    // ADAPTER
    private ArrayAdapter<Integer> mAdapter;

    public static PropertiesCategoryFragment newInstance(Category category) {
        final PropertiesCategoryFragment f = new PropertiesCategoryFragment();

        if (category != null) {
            final Bundle b = new Bundle();
            b.putSerializable(EXTRA_CATEGORY, category);
            f.setArguments(b);
        }

        return f;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        final Bundle bundle = b != null ? b : getArguments();
        if (bundle != null) {
            mCategoryNew = bundle.getBoolean(EXTRA_CATEGORY_NEW, false);
            mCategory = (Category) bundle.get(EXTRA_CATEGORY);
            mShowKeyBoard = false;

        } else {
            mCategory = new Category();
            mCategoryNew = true;
            mShowKeyBoard = true;
            String defaultColor = getDefaultColor();
            mCategory.setColor(defaultColor);

        }

        mHeaderView = new PropertiesFeatureHeaderView(getActivity(), this);
        mHeaderView.setCategoryData(mCategory);

        mAdapter = new ArrayAdapter<Integer>(getActivity(), R.layout.simple_text_view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mShowKeyBoard) {
            showKeyboard(mHeaderView.getEditText());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (!TextUtils.isEmpty(mHeaderView.getName().trim())) {
            mCategory.setName(mHeaderView.getName());
        }
        if (!TextUtils.isEmpty(mHeaderView.getComment().trim())) {
            mCategory.setComment(mHeaderView.getComment());
        }
        b.putSerializable(EXTRA_CATEGORY, mCategory);
        b.putBoolean(EXTRA_CATEGORY_NEW, mCategoryNew);
    }

    @Override
    public void onFeaturePropertiesChecked(int id, boolean isChecked) {
        switch (id) {
            case R.id.llColorBack:
                selectColor();
                break;
        default:
            break;
        }
    }

    private void selectColor() {
        final int color;
        if (mCategory.getColor() == null || Marker.NO_COLOR.equals(mCategory.getColor())) {
            color = mSettings.isThemeDark() ? Color.WHITE : Color.BLACK;

        } else {
            color = Color.parseColor(mCategory.getColor());
        }

        new SetColorDialogBuilder(getActivity(), color, this).show();
    }

    @Override
    protected View getListViewHeader() {
        return mHeaderView;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        if (mCategory == null || TextUtils.isEmpty(mCategory.getName())) {
            return R.string.category_new;
        } else {
            return R.string.category_properties;
        }
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.category_white_big;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return false;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        inputHide(mHeaderView);
        if (!TextUtils.isEmpty(mHeaderView.getName().trim())) {
            new Thread(mSaveProjectRunnable).start();
        } else {
            Utils.showToast(getActivity(), R.string.t_error_feature_name);
        }

        return true;
    }

    private Runnable mSaveProjectRunnable = new Runnable() {
        @Override
        public void run() {
            mCategory.setName(mHeaderView.getName());
            mCategory.setComment(mHeaderView.getComment());

            mCategory.setUsn(0);
            mCategory.setUsnName(mCategory.getUsnName() + 1);
            mCategory.setUsnComment(mCategory.getUsnComment() + 1);
            mCategory.setUsnColor(mCategory.getUsnColor() + 1);


            if (mCategoryNew) {
                mCategory.setId(UUID.randomUUID());
                mCategory.setCreator(LTSettings.getInstance().getUserName());
                mCategory.setOrder(mSettings.getLastFeatureOrder() + 1);
                mCategory.setUsnOrder(mCategory.getUsnOrder() + 1);

                mCategory.setParent(null);
                mCategory.setParentId(null);
                mCategory.setUsnParent(mCategory.getUsnParent() + 1);
            }

            try {
                if (mCategoryNew) {
                    mDbHelper.getCategoryDao().create(mCategory);
                } else {
                    mDbHelper.getCategoryDao().update(mCategory);
                }

            } catch (Exception e) {
                Utils.toLog(e);
            }
            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(mApp);
            if (mCategoryNew) {
                linkHelper.createTotalLink(mCategory);

            } else {
                linkHelper.updateTotalLink(mCategory);
            }

            mApp.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);
        }
    };

    private String getDefaultColor() {
        String defaultColors[]={"#DF0C0C","#FF8C68","#CD5F00","#965500","#FFEB00","#878700","#73D246","#008C8C","#5ACDFF","#0F5FFF","#5F32BE","#A05AB9","#FF4BC8","#5A0046","#BEBEBE","#465069"};
        return defaultColors[new Random().nextInt(defaultColors.length)];
    }

    @Override
    protected void onDialogPositiveButton() {}

    @Override
    protected boolean onAddFeatureClick() {
        return false;
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCancel(AmbilWarnaDialog dialog) {}

    @Override
    public void onOk(AmbilWarnaDialog dialog, int color) {
        String hexColor = SetColorDialogBuilder.NO_COLOR == color ? Marker.NO_COLOR : Utils.getColor(color);
        if (hexColor.equals("-1")) {
            hexColor = null;
        }
        mCategory.setColor(hexColor);
        mHeaderView.setColorBack(hexColor);
    }
}