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
      public DataSet<int, Facing> dsFacing = new DataSet<int,Facing>(Facing.OBJECT_NAME);
      public DataSet<int, InvFrg> dsInvFrg = new DataSet<int, InvFrg>(InvFrg.OBJECT_NAME);
      public DataSet<int, InvEqu> dsInvEqu = new DataSet<int, InvEqu>(InvEqu.OBJECT_NAME);

      public MainFormEx() : base()
      {
#if ClassicMonitor
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
               if (tsi != tsbSelectRange && tsi != btnRefresh && tsi != tsbConfigBtn)
                  needRemove.Add(tsi);
            }
         }

         needRemove.ForEach(x => tsbConfig.Items.Remove(x));

#else
         ToolStripButton btnDubDocs = new ToolStripButton();
         btnDubDocs.Click += LoadScripts;
         btnDubDocs.Image = Resources.abiword_3;
         btnDubDocs.Name = "btnDubDocs";
         btnDubDocs.Text = "Выгрузка сценариев";
         btnDubDocs.DisplayStyle = ToolStripItemDisplayStyle.Image;
         tsbConfig.Items.Add(btnDubDocs);

         btnDubDocs = new ToolStripButton();
         btnDubDocs.Click += ((s, e) => (new FmAuditReport()).Show());
         btnDubDocs.Image = Resources.view_statistics;
         btnDubDocs.Name = "btnAuditRpt";
         btnDubDocs.Text = "Аудит работы";
         btnDubDocs.DisplayStyle = ToolStripItemDisplayStyle.Image;
         tsbConfig.Items.Add(btnDubDocs);

         btnDivision.Visible = false;
#endif
      }

      protected override void AfterRefreshData()
      {
         Manager m = (Manager)CurrentUser.user;
         if( m != null )
            btnDivision.Visible = m.HaveRight(RightTokens.Get("EnterToDivision"), RightActions.Write);
      }

#if ClassicMonitor
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
               "sd.\"created\" < ToDate('{2:dd/MM/yyyy}') and sd.\"scriptId\" in (select \"id\" from \"ScriptDef\" where \"suppl\" = '" + mgr.src.suppl + "')"+ uidFilter + ")";
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
#if ClassicMonitor
         COMMON_FILTER_STR = GetMonitorFilter(null);

#endif
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsFacing.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsInvFrg.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsInvEqu.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsFacing);
         updSets.Add(dsInvFrg);
         updSets.Add(dsInvEqu);
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
