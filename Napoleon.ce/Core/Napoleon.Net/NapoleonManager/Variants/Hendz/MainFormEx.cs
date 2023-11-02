using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      protected DataSet<int, VandAudit> dsVandAudit = new DataSet<int, VandAudit>(VandAudit.OBJECT_NAME);
      protected DataSet<int, VandReload> dsVandReload = new DataSet<int, VandReload>(VandReload.OBJECT_NAME);
      protected DataSet<int, VandSales> dsVandSales = new DataSet<int, VandSales>(VandSales.OBJECT_NAME);

      public MainFormEx()
      {
         btnOrderReport.Visible = false;
         btnCensus.Visible = false;
         btnPriceRemnants.Visible = false;
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         updSets.Add(dsVandAudit);
         updSets.Add(dsVandReload);
         updSets.Add(dsVandSales);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsVandAudit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsVandReload.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsVandSales.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig) { }

      protected override void PostAddData()
      {
         IDataSet cdata;

         cdata = DataModule.Get(VandAudit.OBJECT_NAME);
         if (cdata != null)
            foreach (VandAudit va in cdata.Data)
               this.Add(va);

         cdata = DataModule.Get(VandSales.OBJECT_NAME);
         if (cdata != null)
            foreach (VandSales vs in cdata.Data)
               this.Add(vs);

         cdata = DataModule.Get(VandReload.OBJECT_NAME);
         if (cdata != null)
            foreach (VandReload vr in cdata.Data)
               this.Add(vr);
      }

      private void Add(VandReload vr)
      {
         if (vr.agent != null && ContainsKey(vr.agent.id))
         {
            SummaryData sd = this[vr.agent.id];
            sd.AddOrg(vr);
         }
      }

      private void Add(VandSales vs)
      {
         if (vs.agent != null && ContainsKey(vs.agent.id))
         {
            SummaryData sd = this[vs.agent.id];
            sd.orders++;
            sd.sum += vs.Sum();
            sd.AddOrg(vs);
         }
      }

      private void Add(VandAudit va)
      {
         if (va.agent != null && ContainsKey(va.userid))
         {
            SummaryData sd = this[va.userid];
            sd.AddOrg(va);
         }
      }
   }
}