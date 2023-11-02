/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitchingNew;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Purchase;
import com.grsoft.dataobjects.PurchaseItem;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.AnswerImplEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.BlackSellingDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.ScriptPropDoc;
import com.grsoft.napoleon.documents.SellingDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.quest_control.QuestControlsFactoryEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.WriteService;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.script.dataobjects.Script;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

import java.util.List;

public class NapoleonApp extends NapoleonAppBase {
    public boolean need_sync = true;

    @Override
    public void onCreate() {
        ConfigManager.initConfig(new CfgNplEx());

        QuestControlsFactory.instance = new QuestControlsFactoryEx();

        RWServiceFactory.instance = new RWServiceFactory() {
            @Override
            public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend) {
                return new WriteService(objectsToSend, false) {
                    @Override
                    protected Hitching createMessageHitching() {
                        return new MessageHitchingNew();
                    }
                };
            }
        };

        super.onCreate();

        setProgrammVersion();

        //NapoleonChat.init(this);
    }

    @Override
    protected void defineNewType() {
        super.defineNewType();

        ServerCommand.Category = "vanpda";

        NPrinter.forms.put("pca", "pca");
        NPrinter.forms.put("pca2", "pca2");
        NPrinter.forms.put("quest", "quest");

        DbObject.regNewDataType(AgentPrefix.class, AgentPrefixEx.class);
        DbObject.regNewDataType(Org.class, OrgEx.class);
        DbObject.regNewDataType(Script.class, ScriptEx.class);
        DbObject.regNewDataType(ScriptDef.class, ScriptDefEx.class);
        DbObject.regNewDataType(Price.class, PriceEx.class);

        DataObjectInfo.getInstance().replaceListType(Purchase.class, "items", PurchaseItem.class);
        DataObjectInfo.getInstance().replaceListType(Visit.class, "items", VisitItemEx.class);

        CostStrategy.defaultInstance = new CostStrategyEx();
    }

    @Override
    protected void initDocTypes() {
        initFeatures();

        DocType.addType(PurchaseDoc.instance());
        DocType.addType(SellingDoc.instance());
        DocType.addType(BlackSellingDoc.instance());
        DocType.addType(ScriptDoc.instance(scriptImplType()));
        DocType.addType(ScriptPropDoc.instance());
        DocType.addType(QuestionDoc.instance(AnswerImplEx.class));
    }

    @Override
    protected Class<? extends ScriptImpl> scriptImplType() {
        return ScriptImplEx.class;
    }

    private void setProgrammVersion() {
        try {
            ServerCommand.ProgramVersion = getResources().getString(R.string.version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
