using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRouteHistory : Form
   {
      static FmRouteHistory instance = null;
      Route owner = null;
      Agent agent = null;

      SimpleDataSet<OrgRouteShedule> dsRoute = new SimpleDataSet<OrgRouteShedule>(OrgRouteShedule.ROUTE_INTERVAL_NAME, false);

      public FmRouteHistory()
      {
         InitializeComponent();
         dtpBegin.Value = DateTime.Now.Date;
         dtpEnd.Value = DateTime.Now.AddMonths(1).Date;

         tsbDay.Items.Add("<все>");
         for (int i = 1; i <= 7; i++)
            tsbDay.Items.Add(new WeekDay(i));

         tsbDay.SelectedIndexChanged -= tsbDay_SelectedIndexChanged;
         tsbDay.SelectedIndex = 0;
         tsbDay.SelectedIndexChanged += tsbDay_SelectedIndexChanged;

         dgvItems.AutoGenerateColumns = false;
      }

      void SetData(Route owner, Agent agent)
      {
         if(owner != null)
            owner.FormClosed -= owner_FormClosed;

         this.owner = owner;
         owner.FormClosed += owner_FormClosed;

         this.agent = agent;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         instance = null;
      }

      private void RefreshData()
      {
         if (agent == null)
            return;

         dsRoute.Filter = String.Format("'{0}';'{1:dd/MM/yyyy}';'{2:dd/MM/yyyy}'", agent.id, dtpBegin.Value.Date, dtpEnd.Value.Date);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsRoute);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         WeekDay wd = tsbDay.SelectedItem as WeekDay;

         Dictionary<RouteSheduleKey, Row> data = new Dictionary<RouteSheduleKey, Row>();
         foreach(OrgRouteShedule i in dsRoute.Data)
         {
            if((wd == null && WeekDay.CheckDay(i.name)) || wd.Equals(i.name))
            {
               WeekDay rd = new WeekDay(i.name);
               RouteSheduleKey ckey = new RouteSheduleKey(i.dateFrom, rd);
               if (data.ContainsKey(ckey))
                  data[ckey].Add(i);
               else
               {
                  Row r = new Row(ckey, i);
                  data[ckey] = r;
               }
            }
         }

         dgvItems.DataSource = new SortableBindingList<Row>(new List<Row>(data.Values));
      }

      void owner_FormClosed(object sender, FormClosedEventArgs e)
      {
         owner = null;
      }

      public static void Open(Agent agent, Route owner)
      {
         if (instance == null)
         {
            instance = new FmRouteHistory();
            instance.SetData(owner, agent);
            instance.Show();
         }
         else
         {
            instance.BringToFront();
            instance.SetData(owner, agent);
            instance.RefreshData();
         }
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      class Row
      {
         public List<OrgRouteShedule> data = new List<OrgRouteShedule>();

         RouteSheduleKey key;

         public Row(RouteSheduleKey key, OrgRouteShedule data)
         {
            this.data.Add(data);
            this.key = key;
         }

         public void Add(OrgRouteShedule data)
         {
            this.data.Add(data);
         }

         public string Day { get { return key.wd.FullName; } }
         public DateTime Date { get { return key.date; } }
         public WeekDay WeekDay { get { return key.wd; } }
      }

      void SendToOwner()
      {
         if( dgvItems.CurrentRow != null && owner != null)
         {
            Row r = dgvItems.CurrentRow.DataBoundItem as Row;
            owner.ShowHistoryDay(r.WeekDay, r.data);
            Close();
         }
      }

      private void dgvItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         SendToOwner();
      }

      private void dgvItems_KeyDown(object sender, KeyEventArgs e)
      {
         SendToOwner();
      }

      private void tsbDay_SelectedIndexChanged(object sender, EventArgs e)
      {
         DoLoadData();
      }
   }
}
