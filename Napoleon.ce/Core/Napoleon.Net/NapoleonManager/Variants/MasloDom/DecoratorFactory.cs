using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Properties;
using System.Runtime.InteropServices;
using GRSoft.Network;
using System.Threading;
using System.IO;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      {
         Type formType = form.GetType();

         if (formType == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);
         else if (formType == typeof(Divisions))
            return new DivisionDecorator(form);

         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.qty2report;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "spancop";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "SPANCOP";
         rttReport.Click += new System.EventHandler(spancop_Click);

         form.tsbConfig.Items.Add(rttReport);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      public class Data : GRSoft.Network.DataObject
      {
      }

      private void spancop_Click(object sender, EventArgs e)
      {
         const string REPORT_NAME = "spancop";

         Data data = new Data();
         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(REPORT_NAME, data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(form, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");
      }
   }

   class DivisionDecorator : IDecorator
   {
      public DivisionDecorator(Form form)
      {
         Divisions divisions = (Divisions)form;
         
         ToolStripButton btnTypeEdit = new ToolStripButton();
         btnTypeEdit.Text = "SPANCOP справочники";
         btnTypeEdit.Click += new EventHandler(btnTypeEdit_Click);
         divisions.tb.Items.Add(btnTypeEdit);

         ToolStripButton orgDsc = new ToolStripButton("Лимиты скидок");
         orgDsc.Click += new EventHandler((o, e) => { OrgDiscountEditor.Open(); });
         divisions.tb.Items.Add(orgDsc);
      }

      void btnActionEdit_Click(object sender, EventArgs e)
      {
         new FmSpancopTable().Show();
      }

      void btnTypeEdit_Click(object sender, EventArgs e)
      {
         new FmSpancopTable().Show();
      }

      #region IDecorator Members

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) {return false; }

      #endregion
   }
}
