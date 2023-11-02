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
using System.Runtime.InteropServices;

namespace GRSoft.NapoleonManager
{
   [PermissionSet(SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   [ClassInterface(ClassInterfaceType.AutoDual)]
   public partial class SelectOrgLocation : Form
   {
      static SelectOrgLocation instance = null;
      protected OrgLocations loc = OrgLocations.GetDataSet();
      DataSet<string, Org> orgs = null;
      protected List<Org> allOrgs = new List<Org>();
      DataSet<string, OrgLocation> chLoc = new DataSet<string, OrgLocation>(OrgLocation.OBJECT_NAME, false);

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

         //webBrowser1.AllowWebBrowserDrop = false;
         //webBrowser1.IsWebBrowserContextMenuEnabled = false;
         //webBrowser1.WebBrowserShortcutsEnabled = false;
         //webBrowser1.ObjectForScripting = this;

         string mapSource = Config.GetConfig().mapSource;
         if (mapSource.Length == 0)
         {
            MessageBox.Show("Задайте источник данных в настройках программы", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         }
         else
         {
            //string txt = MapEngine.OrgChangeLocation(mapSource);
            //webBrowser1.DocumentText = txt;
            webBrowser1.Init(true, WBInited);

         }
      }

      private void WBInited(WebView source)
      {
         string mapSource = Config.GetConfig().mapSource;
         string txt = MapEngine.OrgChangeLocation(mapSource);

         source.AddHostObjectToScript("host", this);
         source.NavigateToString(txt);

#if DEBUG || MAKE_HTML_FILE
         File.WriteAllText("orgloc.html", txt);
#endif
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
            Type t = FormEntries.GetFormType(typeof(SelectOrgLocation));
            ConstructorInfo ci = t.GetConstructor(Type.EmptyTypes);
            instance = (SelectOrgLocation)ci.Invoke(null);
            //instance = new SelectOrgLocation();
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

      protected virtual void BeforeRefresh(bool refreshLocations, List<IDataSet> upd) { }
      protected virtual void AfterRefresh() { }

      private void RefreshData(bool refreshLocations)
      {
         List<IDataSet> upd = new List<IDataSet>();
         if (loc.Count == 0 || refreshLocations)
            upd.Add(loc);

         Agent a = tsAgents.SelectedItem as Agent;
         if (a != null)
         {
            orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
           
            orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
            upd.Add(orgs);
         }

         BeforeRefresh(refreshLocations, upd);
         if( upd.Count > 0 )
            FmWait.StdDataRefresh(this, upd, DoLoadData);
         else
         {
            DoLoadData();
         }
      }

      private void DoLoadData()
      {
         AfterRefresh();

         //toolStripButton1_Click(this, EventArgs.Empty);

         dgvOrgs.SuspendLayout();

         allOrgs = new List<Org>();
         allOrgs.AddRange((ICollection<Org>)orgs.Data);
         allOrgs.Sort();
         dgvOrgs.DataSource = allOrgs;

         if(orgid != null && orgid.Trim().Length > 0)
         {
            for (int i = 0; i < allOrgs.Count; i++)
            {
               if (allOrgs[i].id.Equals(orgid))
               {
                  dgvOrgs.CurrentCell = dgvOrgs[0, i];
                  break;
               }
            }

            orgid = null;
         }
         dgvOrgs.ResumeLayout();
      }

      protected virtual void FormatCell(DataGridViewCellFormattingEventArgs e, Org o, OrgLocation ol)
      {
         if (ol != null)
            e.CellStyle.BackColor = Color.LightGray;
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
         OrgLocation ol = loc.GetLocation(o.id);
         FormatCell(e, o, ol);
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
            chLoc[o.id] = ol;

            tsbSave.Enabled = true;
            dgvOrgs.InvalidateRow(dgvOrgs.CurrentRow.Index);

            string txt = String.Format("ShowOrg({0},{1},'{2}',0);",
               ol.latitude.ToString().Replace(",", "."),
               ol.longitude.ToString().Replace(",", "."),
               o.Name);
            webBrowser1.ExecuteScript(txt);
            //webBrowser1.Document.InvokeScript("ShowOrg", new object[] { ol.latitude, ol.longitude, o.Name, 0 });
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
         {
            string txt = String.Format("ShowOrg({0},{1},'{2}',1);",
               l.Latitude.ToString().Replace(",", "."),
               l.Longitude.ToString().Replace(",", "."),
               o.Name);
            webBrowser1.ExecuteScriptAsync(txt);
            //webBrowser1.Document.InvokeScript("ShowOrg", new object[] { l.Latitude, l.Longitude, o.Name, 1 });
         }


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
         if (chLoc.Count > 0)
            wr.Add(chLoc);
         else
            wr.Add(loc);
         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (ret)
            chLoc.Clear();

         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      bool clearing = false;
      public string orgid;
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
