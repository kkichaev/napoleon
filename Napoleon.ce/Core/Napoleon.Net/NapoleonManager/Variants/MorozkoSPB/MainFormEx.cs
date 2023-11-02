using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      protected SimpleDataSet<MonitoringW> dsMonitoring = null;
      protected SimpleDataSet<Merchendizing> dsMerch = null;

      public MainFormEx() 
      {
         dsMonitoring = new SimpleDataSet<MonitoringW>(MonitoringW.OBJECT_NAME);
         dsMerch = new SimpleDataSet<Merchendizing>(Merchendizing.OBJECT_NAME);

#if MorozkoSPBMonitor
         Config.FILE_NAME = "Monitor.cfg";
         Config.FOLDER = "\\GRSoft\\Monitor\\";
         Config.Reload();

         btnDivision.Visible = false;

         ServerCommand.Category = "monitor";
         tgvAgentsSummary.ContextMenu = null;
         while (tgvAgentsSummary.ContextMenuStrip.Items.Count > 1)
            tgvAgentsSummary.ContextMenuStrip.Items.RemoveAt(1);

         List<ToolStripItem> needRemove = new List<ToolStripItem>();
         foreach(ToolStripItem tsi in tsbConfig.Items)
         {
            if(tsi is ToolStripButton)
            {
//               if (tsi != tsbSelectRange && tsi != btnRefresh && tsi != tsbConfigBtn && tsi != btnSavePhoto && tsi != rttReport)
               if (tsi != btnRefresh && tsi != tsbConfigBtn && tsi != btnSavePhoto && tsi != rttReport)
                  needRemove.Add(tsi);
            }
         }

         needRemove.ForEach(x => tsbConfig.Items.Remove(x));

#else
         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.view_statistics;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Статистика визитов";
         rttReport.Click += new System.EventHandler((o, e) => { new FmVisitStatReport().Show();});

         tsbConfig.Items.Add(rttReport);
#endif
      }

#if MorozkoSPBMonitor
      public string ScriptFilter()
      {
         Manager mgr = CurrentUser.user as Manager;
         if (mgr != null && mgr.src != null)
            return "\"scriptId\" in (select \"id\" from \"ScriptDef\" where \"suppl\" = '" + mgr.src.suppl + "')";
         return "";
      }

      public string GetMonitorFilter(string userid)
      {
         Manager mgr = CurrentUser.user as Manager;
         if (mgr != null && mgr.src != null)
         {
            string uidFilter = " and \"userid\" = sd.\"userid\" ";
            if (userid != null)
               uidFilter = " and sd.\"userid\"='" + userid + "' ";

            string filter = "\"{0}\" in (select sdi.\"date\" from \"ScriptDoc$items\" sdi INNER JOIN \"ScriptDoc\" sd on " +
               "sdi.\"ScriptDoc$userid\" = sd.\"userid\" and sdi.\"ScriptDoc$created\" = sd.\"created\" where sd.\"created\" >= ToDate('{1:dd/MM/yyyy}') and " +
               "sd.\"created\" < ToDate('{2:dd/MM/yyyy} 23:59:59') and sd.\"scriptId\" in (select \"id\" from \"ScriptDef\" where \"suppl\" = '" + mgr.src.suppl + "')" + uidFilter + ")";
            return filter;
         }

         return "";
      }
#endif


      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);

         updSets.Add(dsMonitoring);
         updSets.Add(dsMerch);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
#if MorozkoSPBMonitor
         COMMON_FILTER_STR = GetMonitorFilter(null);
#endif
         base.AdjustFilterForDS(dateBegin, dateEnd);

         String crdFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd); ;
         dsMonitoring.Filter = crdFilter;
         dsMerch.Filter = crdFilter;         
      }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig)
         : base(dsConfig)
      { }

      protected override void PostAddData()
      {
         IDataSet cdata = DataModule.Get(Merchendizing.OBJECT_NAME);
         if (cdata != null)
            foreach (Merchendizing i in cdata.Data)
               this.Add(i);

         cdata = DataModule.Get(Monitoring.OBJECT_NAME);
         if (cdata != null)
            foreach (MonitoringW i in cdata.Data)
               this.Add(i);
      }

      internal virtual void Add(Merchendizing i)
      {
         if (i.agent != null && ContainsKey(i.AgentID))
         {
            SummaryData sd = (SummaryData)this[i.AgentID];
            sd.AddOrg(i);
         }
      }

      internal virtual void Add(MonitoringW i)
      {
         if (i.agent != null && ContainsKey(i.AgentID))
         {
            SummaryData sd = (SummaryData)this[i.AgentID];
            sd.AddOrg(i);
         }
      }
   }
}
