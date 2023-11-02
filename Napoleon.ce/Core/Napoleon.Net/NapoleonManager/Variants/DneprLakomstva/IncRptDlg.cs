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
   public partial class IncRptDlg : Form
   {
      public SimpleDataSet<Incass> dsIncass = new SimpleDataSet<Incass>(Incass.OBJECT_NAME);
      public List<IDataSet> orgs = new List<IDataSet>();
      public string userids = string.Empty;
      public Dictionary<string, Agent> ag = new Dictionary<string, Agent>();

      public const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59') and \"userid\" in ({3})";

      public IncRptDlg()
      {
         InitializeComponent();
         userids = CreateAgentsData();
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         if (userids.Length > 0)
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.AddRange(orgs);
            dsIncass.Filter = string.Format(COMMON_FILTER_STR, "created", datePeriodView1.Start.Date, datePeriodView1.Finish.Date, userids);
            upd.Add(dsIncass);
            FmWait.StdDataRefresh(this, upd, DoLoadData);
            
         }
         else
            MessageBox.Show("Нет данных о агентах, нажмите кнопку обновить в окне дела");
      }

      private void DoLoadData()
      {
         IncassReport rpt = new IncassReport();
         rpt.org = orgs;
         rpt.doc = dsIncass.Data;
         rpt.agent = ag;
         rpt.start = datePeriodView1.Start;
         rpt.finish = datePeriodView1.Finish;
         rpt.Build();
      }

      private string CreateAgentsData()
      {
         StringBuilder result = new StringBuilder();

         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               if (result.Length > 0)
                  result.Append(", ");

               result.Append("'").Append(da.id).Append("'");

               orgs.Add(DataModule.GetUserDataSet(da.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true) as DataSet<string, Org>);
               ag[da.id] = da.agent;
            }
         }

         return result.ToString();
      }
   }
}
