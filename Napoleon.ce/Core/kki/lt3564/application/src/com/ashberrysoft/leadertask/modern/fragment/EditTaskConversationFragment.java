package com.ashberrysoft.leadertask.modern.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.CommentsAdapter;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.activity.EditTaskActivity;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.Utils;

public class EditTaskConversationFragment extends BaseFragment {

    public static final String CLASS_PATH = EditTaskConversationFragment.class.getSimpleName();
    // VIEW
    private EditText mEditTextComment;
    private Button mAddComment;

    // VALUE's
    private static LTask mTask;
    private static List<TaskMessage> mMessages;
    //private static List<TaskMessage> mMessagesOld = new ArrayList<>();
    private ListView mListView;

    // ADAPTER
    private CommentsAdapter mAdapter;

    public static EditTaskConversationFragment newInstance(LTask task, List<TaskMessage> messages) {
        mTask = task;
        mMessages = messages;
        return new EditTaskConversationFragment();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onStart();
        //mMessagesOld.addAll(mMessages);

        mAdapter = new CommentsAdapter(getActivity(), mMessages);
    }

    public ListView getListView() {
        return mListView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return inflater.inflate(R.layout.fragment_comments_task, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle b) {
        super.onViewCreated(v, b);


        mEditTextComment = (EditText) v.findViewById(R.id.comments_new);
        mAddComment = (Button) v.findViewById(R.id.add_comment);
        {
            //mListView = (ListView) v.findViewById(R.id.listComments);
            mListView.setAdapter(mAdapter);

            /*float dps = 40;
            float pxs = dps * getResources().getDisplayMetrics().density;
            int Height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxs, getResources().getDisplayMetrics());
            //mListView.setMinimumHeight();
            //
            mListView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Height*mMessages.size()));*/
            //
            //scrollCommentsListToBottom();
        }

        mAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage();
                //scrollCommentsListToBottom();
            }
        });
    }



    public List<TaskMessage> getMessages() {
        return mAdapter.getData();
    }

    @Override
    protected Boolean showSlidingMenu() {
        return false;
    }

    public void addMessage() {
        final String message = mEditTextComment.getText().toString().trim();
        mEditTextComment.setText(null);

        if (message.length() > 0) {
            final Date date = new Date();
            final TaskMessage taskMessage = new TaskMessage(UUID.randomUUID(), getSettings().getUserName(), message,
                    UUID.fromString(mTask.getUid()), date, date, false, 0, 0, 0);

            mMessages.add(taskMessage);

            //mAdapter.setData(mMessages);
            mAdapter.notifyDataSetChanged();
        }
        //scrollCommentsListToBottom();
    }

    /*public boolean needToAddCommentOrAddedComment() {
        //что-то ввели в строку воода коммента
        final String message = mEditTextComment.getText().toString().trim();
        // или добавили коммент
        boolean isAddedComment = mMessagesOld.size() < mMessages.size();
        return message.length() > 0 ? true : false || isAddedComment;
    }*/

    public LTask getTask() {
        return mTask;
    }

    private void scrollCommentsListToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
            }
        });
    }
}