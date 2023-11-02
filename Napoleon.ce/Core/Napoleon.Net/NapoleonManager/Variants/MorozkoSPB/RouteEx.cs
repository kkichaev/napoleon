using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;
using System.Drawing;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class RouteEx : Route
   {
      ToolStripButton tsbPaste = new ToolStripButton();
      ToolStripButton tsbCopy = new ToolStripButton();

      static RouteClipboard clp = new RouteClipboard();

      public RouteEx()
      {
         ToolStripSeparator ts = new ToolStripSeparator();
         ts.Name="Sep";
         ts.Size = new Size(6, 25);
         int insPos = toolStrip1.Items.IndexOf(tsbDelete)+1;

         tsbCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         tsbCopy.Image = Resources.copy;
         tsbCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         tsbCopy.Name = "Copy";
         tsbCopy.Size = new System.Drawing.Size(23, 22);
         tsbCopy.Text = "Копировать";
         tsbCopy.Click += new System.EventHandler(CopyOrgs);
         tsbCopy.Enabled = false;

         tsbPaste.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         tsbPaste.Image = Resources.paste;
         tsbPaste.ImageTransparentColor = System.Drawing.Color.Magenta;
         tsbPaste.Name = "tsbPaste";
         tsbPaste.Size = new System.Drawing.Size(23, 22);
         tsbPaste.Text = "Вставить";
         tsbPaste.Click += new System.EventHandler(PasteOrgs);
         tsbPaste.Enabled = false;

         toolStrip1.Items.Insert(insPos++, ts);
         toolStrip1.Items.Insert(insPos++, tsbCopy);
         toolStrip1.Items.Insert(insPos++, tsbPaste);
      }

      void CopyOrgs(object sender, EventArgs arg)
      {
         WeekDay wd = new WeekDay(GetSelectedDay());
         
         clp.Clear();
         foreach(DataGridViewRow dr in dgvOrgs.SelectedRows)
         {
            OrgRouteQueueItem i = dr.DataBoundItem as OrgRouteQueueItem;
            if (i != null)
               clp.Add(i, wd);
         }
         RefreshPaste(IsSelectedAllDays());
      }

      void PasteOrgs(object sender, EventArgs arg)
      {
         List<OrgRouteQueueItem> sel = clp.GetData();
         if( sel.Count > 0 )
         {
            OrgRouteQueue src = (OrgRouteQueue)dgvOrgs.DataSource;
            foreach(OrgRouteQueueItem oi in sel)
            {
               src.AddItem(oi, true);
            }
            dgvOrgs.DataSource = null;
            dgvOrgs.DataSource = src;
            routeWasChanged.SetChanges();
         }
      }

      protected override void AdjustControls(bool selectAllDays)
      {
         base.AdjustControls(selectAllDays);
         
         tsbCopy.Enabled = !selectAllDays;
         RefreshPaste(selectAllDays);
      }

      private void RefreshPaste(bool selectAllDays)
      {
         tsbPaste.Enabled = !selectAllDays && clp.HaveData;
      }

      class RouteClipboard
      {
         List<OrgRouteQueueItem> sel = new List<OrgRouteQueueItem>();

         public void Add(OrgRouteQueueItem i, WeekDay wd)
         {
            OrgRouteQueueItem dst = new OrgRouteQueueItem(sel, sel.Count, i.Item, wd);
            dst.W1 = i.W1;
            dst.W2 = i.W2;
            dst.W3 = i.W3;
            dst.W4 = i.W4;
            sel.Add(dst);
         }

         public bool HaveData { get { return sel.Count > 0; } }

         public List<OrgRouteQueueItem> GetData()
         {
            List<OrgRouteQueueItem> ret = sel;
            sel = new List<OrgRouteQueueItem>();
            return ret;
         }

         public void Clear() { sel.Clear(); }
      }
   }

}