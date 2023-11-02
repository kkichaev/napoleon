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

namespace GRSoft.NapoleonManager
{
   [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class SelectOrgLocation : Form
   {
      static SelectOrgLocation instance = null;
      OrgLocations loc = OrgLocations.GetDataSet();
      DataSet<string, Org> orgs = null;
      List<Org> allOrgs;

      public SelectOrgLocation()
      {
         InitializeComponent();
         dgvOrgs.AutoGenerateColumns = false;

         Manager m = CurrentUser.user as Manager;
         if (m == null)
            return;

         foreach (Agent a in m.GetAgents().Data)
            tsAgents.Items.Add(a);
         if (tsAgents.Items.Count > 0)
            tsAgents.SelectedIndex = 0;

         tsAgents.SelectedIndexChanged += new EventHandler((o,e) => RefreshData(false));

         webBrowser1.AllowWebBrowserDrop = false;
         webBrowser1.IsWebBrowserContextMenuEnabled = false;
         webBrowser1.WebBrowserShortcutsEnabled = false;
         webBrowser1.ObjectForScripting = this;

         string mapSource = Config.GetConfig().mapSource;
         if (mapSource.Length == 0)
         {
            MessageBox.Show("Задайте источник данных в настройках программы", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         }
         else
            webBrowser1.DocumentText = MapEngine.OrgChangeLocation(mapSource);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         RefreshData(true);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      public static void Open(Agent selected)
      {
         if (instance == null)
         {
            instance = new SelectOrgLocation();
            if (selected != null)
               instance.Agent = selected;
            instance.Show();
         }
         else
         {
            if (selected != null)
               instance.Agent = selected;
            instance.BringToFront();
         }
      }

      public Agent Agent
      {
         get { return tsAgents.SelectedItem as Agent; }
         set 
         {
            tsAgents.SelectedItem = value;
            if( Visible )
               RefreshData(false);
         }
      }

      private void RefreshData(bool refreshLocations)
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (loc.Count == 0 || refreshLocations)
            upd.Add(loc);

         Agent a = tsAgents.SelectedItem as Agent;
         if (a != null)
         {
            orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
         }
         if( upd.Count > 0 )
            FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         //toolStripButton1_Click(this, EventArgs.Empty);

         dgvOrgs.SuspendLayout();

         allOrgs = new List<Org>();
         allOrgs.AddRange((ICollection<Org>)orgs.Data);
         allOrgs.Sort();
         dgvOrgs.DataSource = allOrgs;

         dgvOrgs.ResumeLayout();
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         OrgLocation ol = loc.GetLocation(o.id);
         if (ol != null)
            e.CellStyle.BackColor = Color.LightGray;
      }

      public void SetNewLocation(double lat, double lng)
      {
         if (dgvOrgs.CurrentRow == null)
            return;

         Org o = dgvOrgs.CurrentRow.DataBoundItem as Org;
         if (o != null)
         {
            OrgLocation ol = new OrgLocation();
            ol.id = o.id;
            ol.latitude = lat;
            ol.longitude = lng;

            loc[o.id] = ol;
            tsbSave.Enabled = true;
            dgvOrgs.InvalidateRow(dgvOrgs.CurrentRow.Index);
            webBrowser1.Document.InvokeScript("ShowOrg", new object[] { ol.latitude, ol.longitude, o.Name, 0 });
         }
      }

      public object GetCurrentOrg(string what)
      {
         if (dgvOrgs.CurrentRow == null)
            return 0;

         Location l = null;
         Org o = dgvOrgs.CurrentRow.DataBoundItem as Org;
         if (o != null)
         {
            OrgLocation ol = loc.GetLocation(o.id);
            l = (ol == null) ? Route.GetLocation(o) : new Location(ol.latitude, ol.longitude);
         }
         if (l == null)
            return 0;
         return what == "lat" ? l.Latitude : what == "lng" ? (object)l.Longitude : o.Name;
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         OrgLocation ol = loc.GetLocation(o.id);
         Location l = (ol==null) ? Route.GetLocation(o) : new Location(ol.latitude, ol.longitude);
         if (l != null)
            webBrowser1.Document.InvokeScript("ShowOrg", new object[] { l.Latitude, l.Longitude, o.Name, 1 });
         /*
          * 
        webBrowser1.DocumentText =
            "<html><head><script>" +
            "function test(message) { alert(message); }" +
            "</script></head><body><button " +
            "onclick=\"window.external.Test('called from script code')\">" +
            "call client code from script code</button>" +
            "</body></html>";
          *
          * 
        webBrowser1.Document.InvokeScript("test",
            new String[] { "called from client code" });
          */
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(loc);
         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      bool clearing = false;
      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind();
      }

      private void tbClearFind_Click(object sender, EventArgs e)
      {
         ClearFind();
      }

      private void ClearFind()
      {
         clearing = true;
         tbFind.Text = "";

         dgvOrgs.DataSource = allOrgs;

         clearing = false;
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string str)
      {
         str = str.ToUpper();

         List<Org> src = new List<Org>();
         foreach (Org o in allOrgs)
            if (o.Name.ToUpper().Contains(str))
               src.Add(o);

         dgvOrgs.DataSource = src;
      }

      //private void toolStripButton1_Click(object sender, EventArgs e)
      //{
      //   using (StreamReader r = new StreamReader(@"D:\Works\Napoleon.ce\Projects\AutoHit\test.html"))
      //      webBrowser1.DocumentText = r.ReadToEnd();
      //}
   }
}
