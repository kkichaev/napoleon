using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace Ads2017
{
   class ReportHelper : Update.IDataLoadProcess
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);

      public void DoLoadData(Update.UpdateResult data)
      {
         ReportResult rr = data.GetReportResult();

         if (rr != null)
            ShowReport(rr);
         else
            ShowError();
      }

      private void ShowError()
      {
         MessageBox.Show("Ошибка построения отчета", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
      }

      private void ShowReport(ReportResult rr)
      {
         int count = 0;

         string fileName = Path.GetTempPath() + "\\" + rr.name + count.ToString() + ".xlsx";
         while (File.Exists(fileName))
         {
            count++;
            fileName = Path.GetTempPath() + "\\" + rr.name + count.ToString() + ".xlsx";
         }

         File.WriteAllBytes(fileName, rr.file);
         ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
      }

      public void DoReport(string name, object arg)
      {
         Update.QueryList query = new Update.QueryList();
         query.SetReport(name, arg);
         Update.StdDataRefresh(query, this);
      }

      public UIElement[] GetRefreshControls() { return null; }
   }
}
