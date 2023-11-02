package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Message;
import com.grsoft.network.IRecievedMessageDlg;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.util.MessageStock;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class VyatichRecievedMessageDlg implements IRecievedMessageDlg {
    @Override
    public boolean showDialogue(Context context, final Runnable action) {
        MessageStock.getNewMessage();

        final  List<Message> list = new ArrayList<>();

        String where = "readed is null or readed = 0";

        DataTraveler.travel(Message.class, new DataTraveler.Travel<Message>() {
            @Override
            public boolean travel(DataTraveler<Message> item) {
                list.add(item.data);
                return true;
            }
        }, where);

        if (list.size() == 0)
            return false;

        Message[] arr = list.toArray(new Message[list.size()]);

        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.message_input);
            final View dialogView = View.inflate(context, R.layout.messagesex, null);
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

            lvMessages.setAdapter(new NewMessageAdapter(context, arr));
            builder.setView(dialogView);


            builder.setCancelable(false);

            final AlertDialog newMessagesDlg = builder.create();

            dialogView.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = dialogView.findViewById(R.id.cbReaded);

                    if (cb.isChecked()){
                        String sql = "update message set readed = 1";
                        SQLiteDatabase db =  DataBaseManager.getDataBase();
                        db.execSQL(sql);
                    }

                    if (action != null)
                        action.run();
                    else
                        newMessagesDlg.dismiss();
                }
            });

            newMessagesDlg.show();

            return true;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
