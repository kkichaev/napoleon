using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;
using System.Security;
using System.Security.Permissions;
using GRSoft.NapoleonManager.Maps;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class SelectOrgLocationEx : SelectOrgLocation
   {
      SimpleDataSet<GPSGather> dsGather = new SimpleDataSet<GPSGather>(GPSGather.OBJECT_NAME, false);
      Dictionary<string, DateTime> lastCoords = new Dictionary<string, DateTime>();
      ToolStripComboBox filter;

      public SelectOrgLocationEx() :
         base()
      {
         ToolStripButton tsb = new ToolStripButton();
         tsb.Text = "Экспорт в 1с";
         tsb.Image = GRSoft.NapoleonManager.Properties.Resources.document_export;
         tsb.DisplayStyle = ToolStripItemDisplayStyle.Image;
         tsb.ImageTransparentColor = System.Drawing.Color.Magenta;
         tsb.Name = "tbExport";
         tsb.Size = new System.Drawing.Size(23, 22);
         tsb.Click += ExportData;

         filter = new ToolStripComboBox();
         filter.Name = "tsAgents";
         filter.Size = new System.Drawing.Size(150, 25);
         filter.Items.AddRange(new string[] { "Все", "Назначенные", "Не назначенные", "Новые" });
         filter.SelectedIndex = 0;
         filter.SelectedIndexChanged += filter_SelectedIndexChanged;


         toolStrip1.Items.Add(tsb);
         toolStrip1.Items.Add(filter);

         splitContainer1.SplitterDistance += 50;
      }

      void filter_SelectedIndexChanged(object sender, EventArgs e)
      {
         ToolStripComboBox tsb = (ToolStripComboBox)sender;
         if (tsb == null || dgvOrgs == null)
            return;

         int index = tsb.SelectedIndex;
         if (index == 0)
            dgvOrgs.DataSource = allOrgs;
         else
         {
            List<Org> src = new List<Org>();
            foreach(Org o in allOrgs)
            {
               bool haveLoc = loc.ContainsKey(o.id);
               bool isNew = lastCoords.ContainsKey(o.id);

               if ((index == 1 && haveLoc && !isNew) || (index == 2 && !haveLoc) || (index == 3 && isNew) )
                  src.Add(o);
            }

            dgvOrgs.DataSource = src;
         }
      }

      void ExportData(object sender, EventArgs e)
      {

         ReportParam rp = new ReportParam();
         foreach(DataGridViewRow r in dgvOrgs.SelectedRows)
         {
            Org o = r.DataBoundItem as Org;
            if (loc.ContainsKey(o.id))
               rp.orgs.Add(loc[o.id]);
         }
         if (rp.orgs.Count > 0)
         {
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), new Report("put_location", rp, null), null).Join();
         }

         List<IDataSet> wr = new List<IDataSet>();
         SimpleDataSet<OrgLocation> wrset = new SimpleDataSet<OrgLocation>(OrgLocation.OBJECT_NAME, false);
         foreach (OrgLocation ol in loc.Data)
         {
            if (lastCoords.ContainsKey(ol.id))
               wrset.Add(ol);
         }
         if (wrset.Count > 0)
            wr.Add(wrset);

         if (wr.Count > 0 && DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection()))
            lastCoords.Clear();
      }

      class ReportParam : GRSoft.Network.DataObject
      {
         public List<OrgLocation> orgs = new List<OrgLocation>();
      }

      protected override void FormatCell(DataGridViewCellFormattingEventArgs e, Org o, OrgLocation ol)
      {
         base.FormatCell(e, o, ol);

         if (lastCoords.ContainsKey(o.id))
            e.CellStyle.BackColor = Color.LightSkyBlue;
      }

      protected override void BeforeRefresh(bool refreshLocations, List<IDataSet> upd)
      {
         if (refreshLocations || dsGather.Count == 0)
         {
            dsGather.Filter = Utils.DataUtils.MakeFilterFromAgents(null, ((Manager)CurrentUser.user).GetAgents()) + 
               String.Format(" and \"created\" > ToDate('{0:dd/MM/yyyy}')", DateTime.Now.AddMonths(-1));
            upd.Add(dsGather);
         }
      }

      protected override void AfterRefresh()
      {
         filter.SelectedIndex = 0;
         lastCoords.Clear();

         foreach(GPSGather doc in dsGather.Data)
         {
            if (loc.ContainsKey(doc.id) && loc[doc.id].date.CompareTo(doc.created) >= 0)
               continue;
            if(lastCoords.ContainsKey(doc.id) == false || lastCoords[doc.id].CompareTo(doc.created) < 0)
            {
               OrgLocation ol = new OrgLocation();
               ol.id = doc.id;
               ol.latitude = doc.latitude;
               ol.longitude = doc.longitude;
               ol.date = doc.created;
               loc[doc.id] = ol;
               lastCoords[doc.id] = doc.created;
            }
         }
      }
   }
}