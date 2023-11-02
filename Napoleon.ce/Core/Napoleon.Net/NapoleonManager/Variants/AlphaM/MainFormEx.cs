using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    class MainFormEx : MainForm
    {
        public MainFormEx()
        {
        }

        public SimpleDataSet<WhRequest> whReq = new SimpleDataSet<WhRequest>(WhRequest.OBJ_NAME);

        protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
        {
           base.AdjustFilterForDS(dateBegin, dateEnd);

         whReq.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);

        }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
        {
           base.AddUpdateDataSet(updSets);
           updSets.Add(whReq);
        }

    }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig)
      {
      }

      protected override void PostAddData()
      {
         IDataSet cdata = DataModule.Get(WhRequest.OBJ_NAME);
         if (cdata != null)
            foreach (Order doc in cdata.Data)
               this.Add(doc);
      }
   }
}
