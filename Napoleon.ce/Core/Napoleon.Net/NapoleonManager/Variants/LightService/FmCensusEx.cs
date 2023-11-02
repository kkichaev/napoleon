using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmCensusEx : FmCensus
   {
      public static string PTNC_QUEST_KEY = "ptnc_quest";

      ToolStripComboBox cbQuest;
      IDataSet dsQuestion;
      DataSet<int, CommonConfig> dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);

      public FmCensusEx()
      {
         Size = new System.Drawing.Size(Size.Width + 200, Size.Height);

         cbQuest = new ToolStripComboBox();
         cbQuest.Size = new System.Drawing.Size(250, 26);
         cbQuest.SelectedIndexChanged += QuestSelectionChanged;
         
         ToolStripLabel lbl = new ToolStripLabel();
         lbl.Text = "Анкета";

         tsbConfig.Items.Add(lbl);
         tsbConfig.Items.Add(cbQuest);

         dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsQuestion.Filter = "\"idquest\" is null or \"idquest\" is not null";
      }

      private void QuestSelectionChanged(object sender, EventArgs e)
      {
         CommonConfig cfg = null;

         foreach (CommonConfig cc in dsConfig.Values)
         {
            if (cc.key == PTNC_QUEST_KEY)
            {
               cfg = cc;
               break;
            }
         }

         if (cfg == null)
         {
            cfg = new CommonConfig();
            cfg.key = PTNC_QUEST_KEY;

            dsConfig.Add(dsConfig.Count, cfg);
         }

         Question q = ((ToolStripComboBox)sender).SelectedItem as Question;

         if (q != null)
            cfg.value = q.idquest;

         else
            cfg.value = "";

         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(dsConfig);

         if (!DataModule.WriteDataSet(wr, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
      }

      protected override List<IDataSet> CreateUpdateList()
      {
         List <IDataSet> ret =  base.CreateUpdateList();
         ret.Add(dsQuestion);
         ret.Add(dsConfig);

         return ret;
      }

      public override void RefreshData()
      {
         base.RefreshData();

         cbQuest.SelectedIndexChanged -= QuestSelectionChanged;
         cbQuest.Items.Clear();
         cbQuest.Items.Add("");

         foreach (Question q in dsQuestion.Data)
         {
            cbQuest.Items.Add(q);
         }

         string id = string.Empty;

         foreach (CommonConfig cc in dsConfig.Values)
         {
            if (cc.key == PTNC_QUEST_KEY)
            {
               id = cc.value;
               break;
            }
         }

         if (id.Trim().Length > 0)
         {
            for (int i = 0; i < cbQuest.Items.Count; i++)
            {
               Question q = cbQuest.Items[i] as Question;

               if (q != null && q.idquest == id) 
               {
                  cbQuest.SelectedIndex = i;
                  break;
               }
            }
         }

         cbQuest.SelectedIndexChanged += QuestSelectionChanged;
      }
   }
}
