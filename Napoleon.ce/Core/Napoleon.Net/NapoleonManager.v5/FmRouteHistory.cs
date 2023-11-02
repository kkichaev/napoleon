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

      SimpleDataSet<Schedule> dsRoute = new SimpleDataSet<Schedule>(Schedule.OBJECT_NAME, false);

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

         Dictionary<DateTime, Row> data = new Dictionary<DateTime, Row>();
         foreach(Schedule i in dsRoute.Data)
         {
            Row r = new Row(i.date, i);
            data[i.date] = r;
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
         public List<Schedule> data = new List<Schedule>();

         DateTime key;

         public Row(DateTime key, Schedule data)
         {
            this.data.Add(data);
            this.key = key;
         }

         public void Add(Schedule data)
         {
            this.data.Add(data);
         }

         public string Day { get { return key.ToString(); } }
         public DateTime Date { get { return key; } }
      }

      void SendToOwner()
      {
         if( dgvItems.CurrentRow != null && owner != null)
         {
            Row r = dgvItems.CurrentRow.DataBoundItem as Row;
            //owner.ShowHistoryDay(r.WeekDay, r.data);
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
