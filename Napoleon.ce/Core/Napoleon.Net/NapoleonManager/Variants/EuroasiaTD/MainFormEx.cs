using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public DataSet<string, ManagerFolder> dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      public DataSet<string, Monitor> dsMonitor = new DataSet<string, Monitor>(Monitor.OBJECT_NAME);

      public MainFormEx()
      {
#if EUROASIA_MONTOR
         btnDivision.Visible = false;

         ServerCommand.Category = "monitor";
         tgvAgentsSummary.ContextMenu = null;
         while (tgvAgentsSummary.ContextMenuStrip.Items.Count > 1)
            tgvAgentsSummary.ContextMenuStrip.Items.RemoveAt(1);

         List<ToolStripItem> needRemove = new List<ToolStripItem>();
         foreach (ToolStripItem tsi in tsbConfig.Items)
         {
            if (tsi is ToolStripButton)
            {
               if (tsi != btnRefresh && tsi != tsbConfigBtn)
                  needRemove.Add(tsi);
            }
         }

         btnRefresh.Margin = new Padding(0,1,0,2);
         dtpBeginDate.Visible = false;
         needRemove.ForEach(x => tsbConfig.Items.Remove(x));
#endif

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = global::GRSoft.NapoleonManager.Properties.Resources.distrib_doc;
         button.Name = "distrrep";
         button.Size = new System.Drawing.Size(123, 22);
         button.Text = "Наличие товара";
         button.Click += new System.EventHandler((s, e) =>
         {
            FmDistrRepParam param = new FmDistrRepParam();

            if (param.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
               new DistrReport().Do(this, param);
            }
         });


         tsbConfig.Items.Add(button);
      }

#if EUROASIA_MONTOR
      protected override void AddMainSets(List<IDataSet> upd)
      {
         upd.Add(dsMonitor);
      }

      public string ScriptFilter()
      {
         Manager mgr = CurrentUser.user as Manager;
         if (mgr != null && mgr.src != null)
            return "\"scriptId\" in (select \"id\" from \"Monitor$scripts\" where \"Monitor$userid\" = '" + mgr.src.guid + "')";
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
               "sd.\"created\" < ToDate('{2:dd/MM/yyyy}') and sd.\"scriptId\" in (select \"id\" from \"Monitor$scripts\" where \"Monitor$userid\" = '" + mgr.src.guid + "')" + uidFilter + ")";
            return filter;
         }

         return "";
      }
#endif
      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
#if EUROASIA_MONTOR
         COMMON_FILTER_STR = GetMonitorFilter(null);
#endif
         updSets.Add(dsFolder);
      }

#if EUROASIA_MONTOR
      protected override void AfterRefreshData()
      {
         PrepareDocs();
      }

      public void PrepareDocs()
      {
         Manager mgr = CurrentUser.user as Manager;
         if (mgr == null || mgr.src == null)
            return;

         Monitor m;
         if (!dsMonitor.TryGetValue(mgr.src.guid, out m))
            return;

         string[] sets = { Order.OBJECT_NAME, Distrib.OBJECT_NAME, OrgRemnants.OBJECT_NAME };

         foreach (string setName in sets)
         {
            IDataSet ds = DataModule.Get(setName);
            if (ds == null)
               continue;

            foreach (object cd in ds.Data)
            {
               Order doc = cd as Order;
               if (doc != null)
               {
                  ReduceItems(doc, m);
                  continue;
               }

               Distrib ddoc = cd as Distrib;
               if (ddoc != null)
               {
                  ReduceItems(ddoc, m);
                  continue;
               }

               OrgRemnants rdoc = cd as OrgRemnants;
               if (rdoc != null)
               {
                  ReduceItems(rdoc, m);
                  continue;
               }
            }
         }
      }

      private void ReduceItems(OrgRemnants doc, Monitor m)
      {
         List<OrgRemnantsItem> rmv = new List<OrgRemnantsItem>();
         foreach (OrgRemnantsItem oi in doc.items)
            if (oi.item == null || oi.item.fid.Length == 0 || m.HaveFolder(oi.item.fid))
               rmv.Add(oi);

         rmv.ForEach(x => doc.items.Remove(x));
      }

      private void ReduceItems(Distrib doc, Monitor m)
      {
         List<Distrib.Item> rmv = new List<Distrib.Item>();
         foreach (Distrib.Item oi in doc.items)
            if (oi.item == null || oi.item.fid.Length == 0 || m.HaveFolder(oi.item.fid))
               rmv.Add(oi);

         rmv.ForEach(x => doc.items.Remove(x));
      }

      private void ReduceItems(Order doc, Monitor m)
      {
         List<OrderItem> rmv = new List<OrderItem>();
         foreach (OrderItem oi in doc.items)
            if (oi.item == null || oi.item.fid.Length == 0 || m.HaveFolder(oi.item.fid))
               rmv.Add(oi);

         rmv.ForEach(x => doc.items.Remove(x));
      }
#endif
   }
}
