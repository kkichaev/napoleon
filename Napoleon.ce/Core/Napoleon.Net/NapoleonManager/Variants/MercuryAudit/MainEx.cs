using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainEx : MainForm
   {
      private DataSet<int, OrgDistrib> dsOrgDistrinb = new DataSet<int,OrgDistrib>(OrgDistrib.OBJECT_NAME);

      public MainEx()
      {
         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;
         tgvAgentsSummaryProgres.Visible = false;

         btnTask.Visible = false;
         tsbMakeHtml.Visible = false;
         btnOrderReport.Visible = false;
         btnCensus.Visible = false;
         btnPriceRemnants.Visible = false;

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.view_calendar_timeline;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnReport";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчет по дистрибьюции";
         button.Click += new System.EventHandler((s, e) => { FmDistrRptParam.Do(dtpBeginDate.Value.Date, dtpBeginDate.Value.Date); });

         tsbConfig.Items.Add(button);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         updSets.Add(dsOrgDistrinb);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsOrgDistrinb.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

      class DivisionSummaryEx : DivisionSummary
      {
         public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig) { }

         protected override void PostAddData()
         {
            IDataSet cdata = DataModule.Get(OrgDistrib.OBJECT_NAME);
            if (cdata != null)
               foreach (OrgDistrib o in cdata.Data)
                  this.Add(o);
         }

         private void Add(OrgDistrib o)
         {
            if (o.agent != null && ContainsKey(o.userid))
            {
               SummaryData sd = this[o.userid];
               sd.AddOrg(o);
            }
         }
      }

      protected override bool IsMenuItemVisible(System.Windows.Forms.ToolStripItem menu)
      {
         if (menu == smiRoute)
            return false;
         else
            return base.IsMenuItemVisible(menu);
      }
   }
}
