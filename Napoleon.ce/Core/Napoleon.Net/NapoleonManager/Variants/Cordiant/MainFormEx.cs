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
            tgvAgentsSummaryCount.Visible = false;
            tgvAgentsSummarySum.Visible = false;
        }

        public SimpleDataSet<CMonitoring> dsMonitoring = new SimpleDataSet<CMonitoring>(CMonitoring.OBJECT_NAME);

        protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
        {
           base.AdjustFilterForDS(dateBegin, dateEnd);

           dsMonitoring.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);

        }

        protected override void AddUpdateDataSet(List<IDataSet> updSets)
        {
           base.AddUpdateDataSet(updSets);
           updSets.Add(dsMonitoring);
        }

    }
}
