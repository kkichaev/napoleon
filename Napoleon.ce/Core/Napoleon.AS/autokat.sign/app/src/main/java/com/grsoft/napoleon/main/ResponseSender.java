package com.grsoft.napoleon.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DocsToSign;
import com.grsoft.dataobjects.SignDocResponse;
import com.grsoft.napoleon.BaseFragment;
import com.grsoft.napoleon.MainActivity;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateDBW;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.List;

public class ResponseSender extends BaseFragment {

    Object sync = new Object();
    boolean elapsed = false;
    boolean sended = false;

    @Override protected int getLayoutID() {return R.layout.sign_response;}
    @Override public String TAG() {return "ResponseSender";}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        SignDocResponse rsp = model.signDocResponse;
        if(rsp.status == SignDocResponse.SIGNED) {
            show(v, R.id.signed, 2, rsp);
        } else {
            show(v, R.id.rejected, 3, rsp);
        }
        return v;
    }

    void requestFinish() {
        if(elapsed && sended) {
            try {
                ((MainActivity) getActivity()).openStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void show(View v, int id, int wait, SignDocResponse rsp) {
        v.findViewById(id).setVisibility(View.VISIBLE);
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(wait * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (sync) {
                elapsed = true;
            }
            requestFinish();
        });
        t.start();

        Thread t1 = new Thread(() -> {
            List<ObjectListener> snd = new ArrayList<>();
            snd.add(new SignSender(rsp));
            WriteServiceBase wsb = new WriteServiceBase(snd, false);

            Config config = ConfigManager.getConfig();
            LoginData ld = new LoginData(config.login, config.passw, config.impersonate, getContext()
                    ,config.uuid, config.serverCode);

            wsb.write(getContext(), ld);
            DbWriter.eraseTable(DocsToSign.class);
            synchronized (sync) {
                sended = true;
            }
            requestFinish();
        });
        t1.start();
    }

    public static class SignSender implements ObjectExportListener {
        SignDocResponse doc;

        public SignSender(SignDocResponse doc) {
            this.doc = doc;
        }

        @Override public int size() {return 1;}
        @Override public DataObject get(int i) {return i == 0 ? doc : null;}
        @Override public void onStart() {}
        @Override public void onRead(RawObject rawObject) throws RuntimeException {}
        @Override public void onSave() {}
        @Override public void onEnd() {}

        @Override public String getObjectName() {return "SignDocResponse";}
    }
}
