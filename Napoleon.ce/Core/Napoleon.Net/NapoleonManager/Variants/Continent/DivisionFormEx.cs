using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      private DataSet<int, AgentMatrix> dsAgentMatrix;
      private DataSet<string, AgentQuest> dsAgentQuest;
      protected DataSet<int, Matrix> dsCommonMatrix;
      private DataSet<string, Question> dsCommonQuest;

      public DivisionFormEx()
      {
         dsAgentMatrix = DataModule.Get(AgentMatrix.OBJECT_NAME) == null ? new DataSet<int, AgentMatrix>(AgentMatrix.OBJECT_NAME) :
            (DataSet<int, AgentMatrix>)DataModule.Get(AgentMatrix.OBJECT_NAME);
         dsAgentQuest = (DataSet<string, AgentQuest>)DataModule.Get(AgentQuest.OBJECT_NAME) ??
            new DataSet<string, AgentQuest>(AgentQuest.OBJECT_NAME);

         dsCommonMatrix = DataModule.Get(Matrix.OBJECT_NAME) == null ? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true) :
            (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME);
         dsCommonMatrix.Filter = DataUtils.USERID_IS_NULL_STR;
         dsCommonQuest = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
         dsCommonQuest.Filter = DataUtils.USERID_IS_NULL_STR;
      }

      public override void AddAgents(GRSoft.NapoleonManager.Agent[] agents, bool exclusive)
      {
         if (MessageBox.Show(this,  "Сделать доступными для агента все Матрицы и Анкеты?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
         {
            foreach (Agent a in agents)
            {
               foreach (Question q in dsCommonQuest.Values)
               {
                  AgentQuest quest = new AgentQuest();
                  quest.idquest = q.idquest;
                  quest.userid = a.id;
                  dsAgentQuest.Add(quest.idquest+a.id, quest);
               }

               foreach (Matrix m in dsCommonMatrix.Values)
               {
                  AgentMatrix am = new AgentMatrix();
                  am.name = m.name;
                  am.userid = a.id;
                  dsAgentMatrix.Add(dsAgentMatrix.Count, am);
               }

            }
         }

         base.AddAgents(agents, exclusive);

         parent.saveButton.PerformClick();
      }

      internal override bool BeforeWriteChanges(List<IDataSet> wrObj, List<IDataSet> rmvObj, List<ReplacedSet> replaced, DBConnection conn)
      {
         if (dsAgentQuest.Count > 0)
            wrObj.Add(dsAgentQuest);

         if(dsAgentMatrix.Count > 0)
            wrObj.Add(dsAgentMatrix);

         return base.BeforeWriteChanges(wrObj, rmvObj, replaced, conn);
      }

      internal override void BeforeUpdate(List<IDataSet> updSet)
      {
         updSet.Add(dsCommonMatrix);
         updSet.Add(dsCommonQuest);

         base.BeforeUpdate(updSet);
      }

      internal override void AfterWrited()
      {
         base.AfterWrited();

         dsAgentQuest.Clear();
         dsAgentMatrix.Clear();
      }
   }
}
