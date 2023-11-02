using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmContractReport : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;
      private DataSet<string, ContractDef> dsContract;


      public FmContractReport()
      {
         InitializeComponent();
         dpv.Start = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         dpv.Finish = dpv.Start.Date.AddMonths(1).AddDays(-1);
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshSync();
      }

      private void RefreshSync()
      {
         const string CONTRACT_FILTER = "\"start\" <= ToDate('{0:dd/MM/yyyy}') and finish >= ToDate('{1:dd/MM/yyyy}')";
         dsContract.Filter = string.Format(CONTRACT_FILTER, dpv.Finish.AddDays(1), dpv.Start);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsContract);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         List<ContractDef> list = new List<ContractDef>();
         list.AddRange(dsContract.Values);
         list.Sort((lhs, rhs) => {return lhs.start.CompareTo(rhs.start);});
         lbContract.Items.Clear();
         lbContract.Items.AddRange(list.ToArray());
      }

      private void FmContractReport_Load(object sender, EventArgs e)
      {
         RefreshSync();
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string cid = string.Empty;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         ContractDef cd = lbContract.SelectedItem as ContractDef;

         if(cd == null)
            return;

         const string MODULE_NAME = "contract";
         Param param = new Param();
         param.start = dpv.Start.Date;
         param.finish = dpv.Finish.Date;
         param.cid = cd.id;

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(MODULE_NAME, param, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = ReportFileName(MODULE_NAME);
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");

      }

      private static string ReportFileName(string MODULE_NAME)
      {
         string fileName = GenFileName(MODULE_NAME);

         while (File.Exists(fileName))
         {
            count++;
            fileName = GenFileName(MODULE_NAME);
         }

         return fileName;
      }

      private static string GenFileName(string MODULE_NAME)
      {
         return Path.GetTempPath() + "\\" + MODULE_NAME + count.ToString() + ".xlsx";
      }


   }
}
