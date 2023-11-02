package com.grsoft.napoleon.script_wizard;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.grsoft.camera.CameraActivity;
import com.grsoft.camera.TakePhotoHandler;
import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.dataobjects.impl.QuestionImpl;
import com.grsoft.napoleon.QuestControl;
import com.grsoft.napoleon.QuestControlsFactory;
import com.grsoft.napoleon.QuestImage;
import com.grsoft.napoleon.QuestPhoto;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.main.ImageActionDlg;
import com.grsoft.napoleon.main.Schedule;
import com.grsoft.napoleon.main.ScriptWizard;
import com.grsoft.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuestEdit extends BaseFragment implements QuestPhoto {
    static final String TAG = Schedule.class.toString();

    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String ANSWER_ROW_ID = "answerrowid";
    private View btnSave;
    private List<Pair<QuestControl, View>> controls = new ArrayList<>();

    private AnswerImpl doc;
    private QuestionImpl quest = new QuestionImpl();
    private String orgid = "";
    private static final String COUNTER = "counter";
    private String storePath = "";
    private static final int CAMERA_ACTIVITY = 0x181212; //1;
    private QuestImage image;
    private TextView tvName;

    @Override
    protected int getLayoutID() {
        return R.layout.quest_view;
    }

    @Override
    public String TAG() {
        return TAG;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        doc = (AnswerImpl) getCurDoc(getContext());
        quest.read("idquest", doc.getData().question);

        ((TextView)v.findViewById(R.id.name)).setText(quest.getData().name);

        createQuestControls(v);

        Fragment f = getParentFragment();

        if (f instanceof ScriptWizard) {
            f.getView().findViewById(R.id.btnBack).setEnabled(true);
            f.getView().findViewById(R.id.btnOK).setEnabled(true);
        }

        return v;
    }

    private void createQuestControls(View view) {

        Map<String, List<AnswerItem>> map = new HashMap<>();

        for (AnswerItem i : doc.getData().items) {
            if (!map.containsKey(i.iditem))
                map.put(i.iditem, new ArrayList<>());

            map.get(i.iditem).add(i);
        }

        LinearLayout holder = view.findViewById(R.id.holder);
        QuestControlsFactory factory = QuestControlsFactory.getInstance();

        Collections.sort(quest.getData().items, new Comparator<QuestionItem>() {

            @Override
            public int compare(QuestionItem lhs, QuestionItem rhs) {
                return lhs.number - rhs.number;
            }
        });

        for (int pos = 0; pos < quest.getData().items.size(); pos++) {
            QuestionItem i = quest.getData().items.get(pos);
            QuestControl iv = factory.createItem(i);

            if (iv != null) {
                View v = iv.createView(getContext());
                iv.setOwner(this);

                v.findViewById(R.id.status).setVisibility(i.optional == 0 ? View.VISIBLE : View.INVISIBLE);

                holder.addView(v);

                if (map.containsKey(i.iditem))
                    iv.setValue(map.get(i.iditem));

                controls.add(new Pair<>(iv, v));
            }
        }
    }

    @Override
    public boolean validate(boolean moveBack) {
        boolean isOk = true;
        List<AnswerItem> items = new ArrayList<AnswerItem>();
        int priClr = getContext().getColor(R.color.primary);
        int errClr = getContext().getColor(R.color.quest_error);

        for (Pair<QuestControl, View> kv : controls) {
            QuestControl c = kv.first;
            List<AnswerItem> val = c.getValue();

            if(c.item.optional == 0) {
                Drawable right = null;
                int color = priClr;
                if(val.size() == 0) {
                    isOk = false;
                    color = errClr;
                    right = getResources().getDrawable(R.drawable.ic_error, null);
                }
                kv.second.findViewById(R.id.status).setBackgroundColor(color);
                ((TextView)kv.second.findViewById(R.id.tvText)).setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
            }

            items.addAll(val);
        }

        if (!isOk && !moveBack)
            Toast.makeText(getContext(), R.string.all_question_should_been_processed,
                    Toast.LENGTH_LONG).show();
        else {
            doc.getData().qname = quest.getData().name;
            doc.getData().items = items;
        }

        return isOk || moveBack;
    }

    @Override
    public void doPhoto(QuestImage questImage) {
        image = questImage;

        CameraActivity.openCamera(getContext(), new TakePhotoHandler() {
            @Override
            public File getPhotoFile() {
                SimpleDateFormat sdf = new SimpleDateFormat(FILENAME_FORMAT);
                String name = sdf.format(System.currentTimeMillis()) + ".jpeg";

                File[] externalStorageVolumes = ContextCompat.getExternalFilesDirs(getContext().getApplicationContext(), null);
                File primaryExternalStorage = externalStorageVolumes[0];
                File dataDir = new File(primaryExternalStorage, "datadir");
                dataDir.mkdirs();

                return new File(dataDir, name);
            }

            @Override
            public boolean photoSaved(File file, Uri savedUri) {
                PicStoreImpl picStore = new PicStoreImpl();
                picStore.getData().id = Util.genUUID();
                picStore.getData().picture = file.getAbsolutePath().getBytes();
                picStore.getData().date = doc.getData().created;
                picStore.getData().created = Util.getDateTime();
                picStore.write();
                picStore.close();

                image.addImage(getContext(), picStore.getData().id);

                storePath = "";

                return true;
            }
        });
    }

    @Override
    public void longClick(QuestImage questImage, String id) {
        this.image = questImage;
        ImageActionDlg dlg = new ImageActionDlg();
        Bundle args = new Bundle();
        args.putString(ImageActionDlg.PIC_ID, id);
        dlg.setArguments(args);
        dlg.setImageActionListener(i->image.delImage(getContext(), i));
        dlg.show(getActivity().getSupportFragmentManager(), "");
    }
}
