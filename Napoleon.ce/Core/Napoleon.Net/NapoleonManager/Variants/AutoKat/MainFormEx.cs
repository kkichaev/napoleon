using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public DataSet<int, Purchase> dsPurchase = new DataSet<int, Purchase>(Purchase.OBJECT_NAME);
      public DataSet<int, Selling> dsSelling = new DataSet<int, Selling>(Selling.OBJECT_NAME);

      public MainFormEx()
      {
         btnSavePhoto.Visible = false;
         btnTask.Visible = false;
         tgvAgentsSummaryProgres.Visible = false;
         tgvAgentsSummaryCount.HeaderText = "Закупки";
         tsbCoverArea.Visible = false;

         menuAgentsSummary.Items.Remove(smiRoute);
         btnDivision.Enabled = false;
      }

      protected override bool IsPotenzialOrgOutOfPlan(Agent a)
      {
         return false;
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsPurchase.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsSelling.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsOrderCommitted.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsPurchase);
         updSets.Add(dsSelling);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
         if (CurrentUser.user != null)
         {
            BeginInvoke(new InvokeDelegate(delegate
            {
               btnDivision.Enabled = CurrentUser.user.HaveRight(RightTokens.Get("DisableEditDivision"), RightActions.Write);
            }));

         }
      }

   }
}
