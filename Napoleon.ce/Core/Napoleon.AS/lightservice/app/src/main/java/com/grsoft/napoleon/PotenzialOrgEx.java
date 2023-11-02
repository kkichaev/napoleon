package com.grsoft.napoleon;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.QuestionImpl;
import com.grsoft.util.ExtrasConst;

public class PotenzialOrgEx extends PotenzialOrg {
    public final String PTNC_QUEST_KEY = "ptnc_quest";
    public String idquest = "";
    public String orgID = "";
    private long answerRowid;
    private TextView btnQuest;
    private QuestionImpl quest;

    @Override
    protected int getContentViewId() {
        return R.layout.potenzial_orgex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnQuest = findViewById(R.id.btnQuest);

        orgID = genOrgId();

        if (editMode)
            orgID = orgImpl.getData().id;

        ConfigImpl config = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        config.getValue(sb, PTNC_QUEST_KEY);

        idquest = sb.toString().trim();
        quest = new QuestionImpl();

        if (!quest.read("idquest", idquest))
            findViewById(R.id.questview).setVisibility(View.GONE);

        btnQuest.setOnClickListener((v) -> {
            if (answerRowid == ExtrasConst.INVALID_ROWID)
                QuestionWebView.open(PotenzialOrgEx.this, quest.getRowid(), orgID);
            else
                QuestionWebView.open(PotenzialOrgEx.this, quest.getRowid(), orgID, answerRowid);
        });
    }

    private long getAnswerRowid() {
        long res = ExtrasConst.INVALID_ROWID;
        SQLiteDatabase db = DataBaseManager.getDataBase();
        Cursor c = null;

        DbWriter.checkDBTable(Answer.class);

        try {
            c = db.query(DataObjectInfo.getInstance().getTableName(Answer.class),
                    new String[]{"rowid"}, "question=? and id=?",
                    new String[]{idquest, orgID}, null, null, "created DESC");

            if (c.moveToFirst())
                res = c.getLong(0);
        }finally {
            if (c != null)
                c.close();
        }

        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();
        answerRowid = getAnswerRowid();

        if (answerRowid != ExtrasConst.INVALID_ROWID)
            btnQuest.setText(R.string.fill_question_done);
    }

    @Override
    public boolean checkExitCondition() {
        boolean ret = super.checkExitCondition();

        if (ret && quest.getRowid() != ExtrasConst.INVALID_ROWID && answerRowid == ExtrasConst.INVALID_ROWID){
            ret = false;
            Toast.makeText(this, "Анкета обязательна к заполнению", Toast.LENGTH_LONG).show();
            btnQuest.performClick();
        }
        return ret;
    }

    @Override
    public String genOrgId() {
        return orgID.length() == 0 ?  super.genOrgId() : orgID;
    }
}
