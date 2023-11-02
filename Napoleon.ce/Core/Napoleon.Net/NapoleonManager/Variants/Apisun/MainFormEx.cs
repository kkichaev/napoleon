using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public SimpleDataSet<Planogram> dsPlanogramm = new SimpleDataSet<Planogram>(Planogram.OBJECT_NAME);

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsPlanogramm.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsPlanogramm);
      }

   }
}
