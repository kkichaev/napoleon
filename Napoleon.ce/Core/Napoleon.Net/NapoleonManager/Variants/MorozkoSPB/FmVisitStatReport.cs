using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmVisitStatReport : Form
   {
      DataSet<string, Org> dsOrgs = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      DataSet<string, Company> dsCompanies = new DataSet<string, Company>(Company.OBJECT_NAME, false);

      public FmVisitStatReport()
      {
         InitializeComponent();
         
         cbInterval.SelectedIndex = 0;
         
         dpvDate.Start = DateTime.Now.AddMonths(-1);
         dpvDate.Finish = DateTime.Now;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         upd.Add(dsOrgs);
         upd.Add(dsCompanies);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void DoLoadData()
      {
         cbCompanies.Items.Clear();
         foreach (Company c in dsCompanies.Data)
            cbCompanies.Items.Add(c);

         Org o = new Org();
         o.name = "<все>";
         o.id = "";
         cbOrg.Items.Add(o);
         cbOrg.SelectedIndex = 0;
         
         Manager m = CurrentUser.user as Manager;
         if(m != null)
         {
            cbDivision.Items.Clear();
            foreach (Division d in m.AllDivisions)
               cbDivision.Items.Add(d);

            if (cbDivision.Items.Count > 0)
               cbDivision.SelectedIndex = 0;
         }
      }

      private void cbCompanies_SelectedIndexChanged(object sender, EventArgs e)
      {
         cbOrg.Items.Clear();
         Org o = new Org();
         o.name = "<все>";
         o.id = "";
         cbOrg.Items.Add(o);
         cbOrg.SelectedIndex = 0;

         Company c = cbCompanies.SelectedItem as Company;
         if(c != null)
         {
            foreach (Org oo in dsOrgs.Data)
               if (oo.idCompany.Equals(c.id))
                  cbOrg.Items.Add(oo);
         }
      }

      private void button1_Click(object sender, EventArgs e)
      {
         ReportData rd = new ReportData();
         if(cbDivision.Items.Count > 0)
         {
            rd.division = (cbDivision.SelectedItem as Division).id;
            rd.end = dpvDate.Finish;
            rd.start = dpvDate.Start;
            rd.intervalType = cbInterval.SelectedIndex;
            Company c = cbCompanies.SelectedItem as Company;
            rd.idCompany = c == null ? "" : c.id;
            rd.id = (cbOrg.SelectedItem as Org).id;

            ReportResult.DoReport("visit_stat_report", rd, this);
         }
      }

      class ReportData : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime end = DateTime.Now;

         public int intervalType = 0;

         public string idCompany = "";
         public string id = "";
         public int division = 0;
      }
   }

   public partial class Org : GRSoft.Network.DataObject
   {
      public string idCompany = "";
   }

   public class Company : GRSoft.Network.DataObject, IComparable<Company>
   {
      public static readonly string OBJECT_NAME = "Companies";

      [KeyField]
      public string id = "";

      public string name = "";

      public override string ToString() { return name; }
      public int CompareTo(Company other) { return name.CompareTo(other.name); }
   }
}
