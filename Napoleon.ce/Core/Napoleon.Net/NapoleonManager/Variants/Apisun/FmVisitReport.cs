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
   public partial class FmVisitReport : Form
   {
      private DataSet<string, Org> dsOrg;
      private List<Org> allOrgs = new List<Org>();
      private bool clearing = false;

      public FmVisitReport()
      {
         InitializeComponent();

         dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
         dsOrg.Filter = "id not null";

         dgvOrg.AutoGenerateColumns = false;
        
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         allOrgs.Clear();
         allOrgs.AddRange(dsOrg.Values);

         dgvOrg.DataSource = new SortableBindingList<Org>(allOrgs);

         dgvOrg.Sort(dgvOrg.Columns[0], ListSortDirection.Ascending);
      }

      private void FmVisitReport_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void DoSearch(string p)
      {
         p = p.ToUpper();

         List<Org> src = new List<Org>();
         foreach (Org mrd in allOrgs)
         {
            if (mrd.Name.ToUpper().Contains(p) || mrd.Address.ToUpper().Contains(p))
               src.Add(mrd);
         }

         dgvOrg.DataSource = new SortableBindingList<Org>(src);
         dgvOrg.Sort(dgvOrg.Columns[0], ListSortDirection.Ascending);
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         timer1.Stop();
         clearing = true;
         tbFind.Clear();

         dgvOrg.DataSource = new SortableBindingList<Org>(allOrgs);

         clearing = false;
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnClearFind_Click(sender, e);
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      class Data : GRSoft.Network.DataObject
      {
         public string id = string.Empty;
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvOrg.CurrentRow;

         if (row != null)
         {
            Org org = row.DataBoundItem as Org;

            if (org != null)
            {
               Data data = new Data();
               data.id = org.id;
               data.start = dtpStart.Value.Date;
               data.finish = dtpFinish.Value.Date;

               ReportResult.DoReport("visit_report", data, this);
            }
         }

      }
   }
}
