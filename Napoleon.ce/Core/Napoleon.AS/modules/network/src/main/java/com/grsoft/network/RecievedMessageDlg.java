package com.grsoft.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Message;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateMessageBox;
import com.grsoft.util.MessageStock;
import com.grsoft.util.Util;

public class RecievedMessageDlg implements IRecievedMessageDlg {


    @Override
    public boolean showDialogue(Context context, final Runnable action) {
        Message[] receivedMessages = MessageStock.getNewMessage();

        if(receivedMessages.length == 0)
            return false;
        for(Message m : receivedMessages) {
            if(m.kind.equals(UpdateMessageBox.UPDATE_KIND) && context instanceof Activity) {
                UpdateMessageBox.show(m.message, context.getString(R.string.alert), (Activity) context);
                return true;
            }
        }
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.message_input);
            View dialogView = View.inflate(context, NetworkAsyncTask.MESSAGE_VIEW_LAYOUT, null);
            ListView lvMessages = (ListView) dialogView.findViewById(R.id.lvMessages);

            class NewMessageAdapter extends BaseAdapter
            {
                private Message[] message;
                private Context context;

                public NewMessageAdapter(Context context, Message[] message) {
                    this.message = message;
                    this.context = context;
                }

                @Override
                public int getCount() { return message.length; }

                @Override
                public Object getItem(int arg0) { return message[arg0]; }

                @Override
                public long getItemId(int arg0) { return 0; }

                @Override
                public View getView(int arg0, View arg1, ViewGroup arg2) {
                    Message message = (Message) getItem(arg0);

                    if (arg1 == null)
                        arg1 = View.inflate(context, NetworkAsyncTask.MESSAGE_ROW_LAYOUT, null);

                    TextView tvDate = (TextView) arg1.findViewById(R.id.tvDate);
                    tvDate.setText(Util.simpleDateFormat.format(message.date));

                    TextView tvMessage = (TextView) arg1.findViewById(R.id.tvMessage);
                    tvMessage.setText(message.message);

                    return arg1;
                }
            }

            lvMessages.setAdapter(new NewMessageAdapter(context, receivedMessages));
            builder.setView(dialogView);


            builder.setCancelable(true);

            if (action != null)
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        action.run();
                    }
                });

            AlertDialog newMessagesDlg = builder.create();
            newMessagesDlg.show();
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
