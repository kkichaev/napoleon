package com.grsoft.network;

import android.content.Context;

import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.ForcePutCommandArgs;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Debug;

import java.util.List;

public class WriteServiceV5 extends WriteService{
    public WriteServiceV5(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
        super(objectsToSend, rcvRemnants);
    }

    public boolean sendPhotos(Context context, UserInfo userInfo, List<ObjectExportListener> objects, SocketConnection conn, boolean needPackObjects) {
        if(conn == null) {
            return false;
        }

        boolean result = false;
        try{
            DataObjectPool dataObjectPool = new DataObjectPool();
            ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
            serverCommand.setCommandParams(new ForcePutCommandArgs(userInfo.impersonate));
            dataObjectPool.add(serverCommand);
            for(ObjectExportListener ol : objects) {
                for( int i=0; i < ol.size(); i++)
                    dataObjectPool.add(ol.get(i), ol.getObjectName());
            }

            byte[] streamData = dataObjectPool.toStreamData();
            ByteStream byteStream = new ByteStream(streamData, context);
            String tag = needPackObjects ? ByteStream.GZIP_TAG : ByteStream.CRC_TAG;
            sendedBytes += byteStream.send(conn.getOutputStream(), tag);
            ByteStream outStream = ByteStream.receive(conn.getInputStream(), context);
            if (outStream == null) {
                conn.close();
                message = context.getString(R.string.server_not_approved);
            } else {
                serverAnswerHitching.setObjects(objectsToSend);
                reader.read(outStream);
                outStream.close();

                if (!serverAnswerHitching.IsOK()) {
                    message = context.getString(R.string.cant_write_object) + " " + serverAnswerHitching.getMessage();
                } else {
                    result = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean write(Context context, UserInfo userInfo) {
        fireUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_SEND, 0);
        ConnectionHelper.Result cres = ConnectionHelper.getConnection(userInfo);
        if(cres.error != null) {
            message = cres.error;
            return false;
        }

        boolean res = false;
        SocketConnection conn = null;
        try {
            conn = cres.connection;
            conn.setWin();

            DataObjectPool dataObjectPool = new DataObjectPool();
            ServerCommand serverCommand = new ServerCommand((LoginData) userInfo);
            serverCommand.setCommandParams(new ForcePutCommandArgs(userInfo.impersonate));
            dataObjectPool.add(serverCommand);
            addObjectsToSend(dataObjectPool, userInfo);
            reader.addHitching(requestHitchs);

            byte[] streamData = dataObjectPool.toStreamData();
            ByteStream byteStream = new ByteStream(streamData, context);
            sendedBytes += byteStream.send(conn.getOutputStream(), ByteStream.GZIP_TAG);

            fireUpdate(UpdateProcessInfo.UpdateStatus.ENDREQUEST_SEND, 1);

            ByteStream bs = ByteStream.receive(conn.getInputStream(), context);
            if(bs == null) {
                message = context.getString(R.string.server_not_approved);
            } else {
                serverAnswerHitching.setObjects(objectsToSend);
                reader.read(bs);
                bs.close();
                sendByeCommanToCloseSession(userInfo, winConnect, context);

                if (!serverAnswerHitching.IsOK()){
                    message = context.getString(R.string.cant_write_object) + " " + serverAnswerHitching.getMessage();
                } else {
                    res = true;

                    for(ObjectListener ol : objectsToSend) {
                        if (ol instanceof DocExportListener){
                            DocList list = ((DocExportListener)ol).getDocuments();
                            list.close();
                        }
                    }
                }
                fireUpdate(UpdateProcessInfo.UpdateStatus.END, 3);
            }
        } catch (RuntimeException e) {
            Debug.dbgPrint(e.getMessage());
            e.printStackTrace();
            message = e.getInnerException().getMessage();
        }finally{
            if(conn != null)
                conn.close();
        }

        return res;
    }
}
