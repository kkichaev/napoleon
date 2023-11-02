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
   public partial class FmReturnReport : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;

      public FmReturnReport()
      {
         InitializeComponent();
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         const string MODULE_NAME = "return";
         Param param = new Param();
         param.start = dpv.Start.Date;
         param.finish = dpv.Finish.Date;

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
