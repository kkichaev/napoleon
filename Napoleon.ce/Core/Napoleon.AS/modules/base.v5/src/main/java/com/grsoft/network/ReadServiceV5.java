package com.grsoft.network;

import android.content.Context;
import android.util.Log;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.napoleon.R;
import com.grsoft.network.exception.RuntimeException;

import java.util.List;

public class ReadServiceV5 extends ReadService{
    private static final String TAG = "ReadServiceV5";
    public ReadServiceV5(List<Hitching> hitchings) {
        super(hitchings);
    }

    @Override
    public boolean update(Context context, UserInfo userinfo, boolean clearTables) throws RuntimeException {
        Log.d(TAG,"updating.....");

        fireUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_UPDATE, 0);
        ConnectionHelper.Result cres = ConnectionHelper.getConnection(userinfo);
        if(cres.error != null) {
            message = cres.error;
            return false;
        }

        if(cres.connection == null) {
            message = context.getString(R.string.cant_connect_server);
            return false;
        }

        boolean res = false;
        SocketConnection connect = null;
        try {
            connect = cres.connection;
            DataObjectPool pool = makeCommandPool(userinfo);
            sendRequest(connect, pool, ByteStream.GZIP_TAG, context);
            ByteStream stream = ByteStream.receive(connect.getInputStream(), context);
            if(stream != null) {
                receivedBytes += stream.getReceived();
                fireUpdate(UpdateProcessInfo.UpdateStatus.ENDREQUEST_UPDATE, stream.getSize());

                if( clearTables )
                    clearBase();

                boolean readWhileNotContinue;
                for(Hitching h : recieveHitch)
                    h.prepareReading();

                do{
                    StreamReader reader = new StreamReader(recieveHitch);
                    reader.setUpdateProcessListener(updateProcessListener);

                    reader.read(stream);
                    readWhileNotContinue = reader.isContinue;

                    stream.close();
//                    if(reader.haveServerData()) {
//                        sendServerInfo(reader, connect, userinfo, context);
//                    }

                    if (readWhileNotContinue){
                        sendRequest(connect, DoneCommand.dbPool((LoginData) userinfo), "", context);
                        stream = ByteStream.receive(connect.getInputStream(), context);
                        if( stream == null )
                            break;
                    }
                    receivedBytes += stream.getReceived();
                }while(readWhileNotContinue);

                firePostUpdateWork();
                fireUpdate(UpdateProcessInfo.UpdateStatus.END, 0);

                sendRequest(connect, ByeCommand.dbPool((LoginData) userinfo), "", context);

                Log.d(TAG, "updated");
                res = login.isOK();
            } else {
                message = context.getString(R.string.cant_connect_server);
            }
        } catch (Exception e) {
            message = e.getLocalizedMessage();
        }

        if(connect != null)
            connect.close();
        return res;
    }
}
