using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx() : base()
      {
#if MerchUnionMonitor
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
               if (tsi != btnRefresh && tsi != tsbConfigBtn && tsi != btnSavePhoto && tsi != rttReport)
                  needRemove.Add(tsi);
            }
         }

         needRemove.ForEach(x => tsbConfig.Items.Remove(x));

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.excel;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mecrh";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Oтчёт по мерчендайзингу";
         button.Click += new System.EventHandler((s, e) => { FmMerchRep.Do(dtpBeginDate.Value.Date, GetFinishDate(), this); });

         tsbConfig.Items.Add(button);

#else

#endif
      }


#if MerchUnionMonitor
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

      private void LoadScripts(object sender, EventArgs e)
      {
         SimpleDataSet<LSResult> res = new SimpleDataSet<LSResult>(LSResult.OBJECT_NAME, false);
         LSParam data  = new LSParam();
         data.start = GetBeginDateForSelection();
         data.finish = GetRangeEndDate();
         Report r = new Report("load_scripts", data, res);
         
         DataModule.ClearEvents();
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, null).Join();
         int count = 0;
         if (res.Count > 0)
         {
            foreach (LSResult lsr in res.Data)
            {
               count = lsr.count;
               break;
            }
         }

         MessageBox.Show("Выгружено " + count.ToString() + " документов");
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
#if MerchUnionMonitor
         COMMON_FILTER_STR = GetMonitorFilter(null);

#endif
         base.AdjustFilterForDS(dateBegin, dateEnd);
      }

      class LSParam : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
      }

      class LSResult : GRSoft.Network.DataObject
      {
         public static readonly string OBJECT_NAME = "ScriptUnloadResult";
         public int count = 0;
      }
   }
}
