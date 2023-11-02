package com.ashberrysoft.leadertask.adapters;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuHeader;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.views.SlidingMenuListItem;
import com.ashberrysoft.leadertask.views.SlidingMenuListItemHeader;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.adapters.TreeAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Адаптер для слайдинг меню
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 */

@SuppressWarnings("deprecation")
public class SlidingMenuAdapter extends TreeAdapter {

    static CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>> sFactory = new CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>>() {
        /**
         * режим отображения задач: 0 - сегодня; 1 - входящие; 2 - я поручил; 3 - проекты и доступные мне; 4 -
         * категории; 5 - мне поручено
         * 
         */
        @Override
        public IDataView<ITreePureNode> createView(Context context, int type) {
            switch (ETreeDataNodeLevel.values()[type]) {
            case TODAY:
                return new SlidingMenuListItem(context, R.drawable.calendar_today, TaskMode.TODAY);

            case INBOX:
                return new SlidingMenuListItem(context, R.drawable.inbox, TaskMode.INBOX);

            case HEADER:
                return new SlidingMenuListItemHeader(context);

            case CATEGORY:
                return new SlidingMenuListItem(context, R.drawable.category_white_big, TaskMode.CATEGORIES);

            case PROJECT:
                return new SlidingMenuListItem(context, R.drawable.project, TaskMode.PROJECTS);

            case INSTRUCTI:
                return new SlidingMenuListItem(context, R.drawable.up_white_big, TaskMode.ASSIGNED_BY_ME);

            case INSTRUCTME:
                return new SlidingMenuListItem(context, R.drawable.down_white_big, TaskMode.ASSIGNED_TO_ME);

            default:
                return null;
            }
        }
    };

    public SlidingMenuAdapter(Context context, ITreeData<?> root) {
        super(context, root, sFactory);
    }

    @Override
    public int getViewTypeCount() {
        return ETreeDataNodeLevel.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return ((ITreeData<?>) getItem(position)).getNodeLevel();
    }

    private void toggle() {
        if (!((HomeActivity) mContext).isLandOrientation()) {
            //((SlidingFragmentActivity) mContext).getSlidingMenu().toggle();
        }
    }

    @Override
    public void onItemClicked(int position, ITreePureNode node) {
        final Class<?> inputClass = node.getClass();

        if (inputClass.equals(SlidingMenuHeader.class)) {
            // super.onItemClicked(position, node);
            // ((SlidingMenuHeader) mItems.get(position)).saveExpanded(mContext);
            return;
        }

        toggle();
        if (position == 0) {
            // save filter selected date
            final TimeZone timeZone = Calendar.getInstance().getTimeZone();
            LTSettings.getInstance(mContext).setFilterSelectedDate(System.currentTimeMillis() + timeZone.getRawOffset() + timeZone.getDSTSavings());

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ServiceConstants.ACTION_TASKS_TODAY));
        }

        else if (position == 1) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ServiceConstants.ACTION_TASKS_INPUT));
        }

        else if (inputClass.equals(Email.class)) {
            final Email email = (Email) node;
            final Intent intent = new Intent(ServiceConstants.ACTION_TASK_INSTRUCT);
            intent.putExtra(ServiceConstants.VALUE_EMAIL, email);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        else if (inputClass.equals(Project.class)) {
            final Project project = (Project) node;
            final Intent intent = new Intent(ServiceConstants.ACTION_TASK_PROJECT);
            intent.putExtra(ServiceConstants.VALUE_PROJECT, project);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        else if (inputClass.equals(Category.class)) {
            final Category category = (Category) node;
            final Intent intent = new Intent(ServiceConstants.ACTION_TASK_CATEGORY);
            intent.putExtra(ServiceConstants.VALUE_CATEGORY, category);

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }
}
