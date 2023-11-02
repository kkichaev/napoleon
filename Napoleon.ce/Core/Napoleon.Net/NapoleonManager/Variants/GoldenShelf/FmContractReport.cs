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
      private DataSet<string, ContractDef> dsContract;

      public FmContractReport()
      {
         InitializeComponent();
         dpv.Start = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         dpv.Finish = dpv.Start.Date.AddMonths(1).AddDays(-1);
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
      }

      class ResultItem : GRSoft.Network.DataObject
      {
         public string name = string.Empty;
         public byte[] photo = null;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
         public List<ResultItem> items = null;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshSync();
      }

      private void RefreshSync()
      {
         const string CONTRACT_FILTER = "\"start\" <= ToDate('{0:dd/MM/yyyy}') and \"finish\" >= ToDate('{1:dd/MM/yyyy}')";
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
         public int photo = 0;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         ContractDef cd = lbContract.SelectedItem as ContractDef;

         if (cd == null)
         {
            SayNeedSelectContract();
            return;
         }

         if (tbPath.Text.Trim().Length == 0)
         {
            SayNeedSelectPath();
            return;
         }

         const string MODULE_NAME = "contract";
         Param param = new Param();
         param.start = dpv.Start.Date;
         param.finish = dpv.Finish.Date;
         param.cid = cd.id;
         param.photo = cbPhoto.Checked ? 1 : 0;

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(MODULE_NAME, param, resultSet);

         DBConnection conn = Config.GetConfig().GetConnection();
         conn.ReceiveTimeout = 60 * 1000 * 10;

         Thread th = DataModule.RefreshGiveSets(conn, r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            try
            {
               Result res = resultSet[0];
               if (res.file.Length > 0)
               {
                  string fileName = ReportFileName(MODULE_NAME);
                  File.WriteAllBytes(fileName, res.file);

                  foreach (ResultItem i in res.items)
                     if (i.name.Length > 0 && i.photo.Length > 0)
                        File.WriteAllBytes(String.Format("{0}\\{1}", tbPath.Text, i.name), i.photo);

                  ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
               }
            }
            catch (Exception excetion)
            {
               MainForm.Instance.Invoke(new EmptyParamHandler(delegate()
               {
                  ViewException ve = new ViewException();
                  ve.Exception = excetion;
                  ve.Show(MainForm.Instance);
               }));
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");

      }

      private void SayNeedSelectPath()
      {
         MessageBox.Show("Выберите папку для сохранения отчета");
      }

      private void SayNeedSelectContract()
      {
         MessageBox.Show("Выбирете контракт");
      }

      private string ReportFileName(string MODULE_NAME)
      {
         string fileName = GenFileName(MODULE_NAME);
         return fileName;
      }

      private string GenFileName(string MODULE_NAME)
      {
         return tbPath.Text + "\\" + lbContract.SelectedItem.ToString() + ".xlsx";
      }

      private void btnFolder_Click(object sender, EventArgs e)
      {
         FolderBrowserDialog dlg = new FolderBrowserDialog();

         if (dlg.ShowDialog() == DialogResult.OK)
            tbPath.Text = dlg.SelectedPath;
      }
   }
}
