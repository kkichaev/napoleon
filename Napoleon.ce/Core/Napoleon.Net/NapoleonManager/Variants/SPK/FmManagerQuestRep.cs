using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmManagerQuestRep : FmQuestionReport
   {
      private DataSet<string, DivisionManager> dsManagers;

      public FmManagerQuestRep()
      {
         dsManagers = (DataSet<string, DivisionManager>)DataModule.Get(DivisionManager.OBJECT_NAME) ??
            new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);

         lblAgent.Text = "Руководители";
      }

      public override void OnLoad()
      {
         List<IDataSet> ret = new List<IDataSet>();
         ret.Add(dsManagers);

         FmWait.StdDataRefresh(this, ret, DoLoadData);
      }

      private void DoLoadData()
      {
         List<DivisionManager> managers = new List<DivisionManager>();
         managers.AddRange(dsManagers.Values);

         if (managers.Count > 0)
         {
            managers.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
            for (int i = 0; i < managers.Count; i++)
            {
               clbAgent.Items.Add(managers[i]);
               clbAgent.SetItemChecked(i, true);
            }
         }
      }

      protected override string GetReportName(bool horizontal)
      {
         return horizontal ? "quest_rep" : "quest_pivot_mgr_rep";
      }

      protected override FmQuestionReport.Param CreateParam()
      {
         FmQuestionReport.Param ret = base.CreateParam();;
         ret.param = 1;

         return ret;
      }

      protected override List<string> CollectUserids()
      {
         List<string> result = new List<string>();
         CheckedListBox.CheckedIndexCollection list = clbAgent.CheckedIndices;

         for (int i = 0; i < list.Count; i++)
            result.Add(String.Format("'{0}'", ((DivisionManager)clbAgent.Items[list[i]]).login));

         return result;
      }
   }
}
