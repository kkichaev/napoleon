using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ReportResult : GRSoft.Network.DataObject
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);


      static int count = 1;

      public string name = "";
      public byte[] file = null;

      public static void DoReport(string repName, string fileName, GRSoft.Network.DataObject param, Form owner)
      {
         ReportResult result = new ReportResult();
         SimpleDataSet<ReportResult> resultSet = new SimpleDataSet<ReportResult>("Result", false);
         SimpleDataSet<PythonExecError> errors = new SimpleDataSet<PythonExecError>("PythonExecError", false);

         List<IDataSet> results = new List<IDataSet>();
         results.Add(resultSet);
         results.Add(errors);
         Report r = new Report(repName, Report.CreateSimplDataSet(param), results);

         DBConnection conn = Config.GetConfig().GetConnection();
         int svTm = conn.ReceiveTimeout;
         conn.ReceiveTimeout = 20 * 60 * 1000;
         Thread th = DataModule.RefreshGiveSets(conn, r, FmWait.ProgressIndicator);
         FmWait.ShowForm(owner, th);
         th.Join();
         FmWait.CloseForm();
         conn.ReceiveTimeout = svTm;

         if (resultSet.Count > 0)
         {
            ReportResult res = resultSet[0];
            if (res.file.Length > 0)
            {
               string f = Path.GetTempPath() + "\\" + fileName + count.ToString() + ".xlsx";
               while (File.Exists(f))
               {
                  count++;
                  f = Path.GetTempPath() + "\\" + fileName + count.ToString() + ".xlsx";
               }

               File.WriteAllBytes(f, res.file);
               ShellExecute(IntPtr.Zero, "open", f, "", "", 1);
            }
         }
         else
         {
            if (errors.Count > 0)
            {
               ReportErrorMessage em = new ReportErrorMessage();
               foreach (KeyValuePair<int, PythonExecError> kv in errors)
               {
                  em.errMessage.Text = kv.Value.stackTrace.Replace("\n", Environment.NewLine);
                  em.errMessage.Select(0, 0);
                  break;
               }
               em.ShowDialog();
            }
            else
            {
               MessageBox.Show("Ошибка построения отчета");
            }
         }
      }

      public static void DoReport(string repName, GRSoft.Network.DataObject param, Form owner)
      {
         DoReport(repName, repName, param, owner);
      }

      public static string GetReport(string repName, GRSoft.Network.DataObject param)
      {
         string res = null;

         ReportResult result = new ReportResult();
         SimpleDataSet<ReportResult> resultSet = new SimpleDataSet<ReportResult>("Result", false);
         Report r = new Report(repName, param, resultSet);

         DBConnection conn = Config.GetConfig().GetConnection();
         int svTm = conn.ReceiveTimeout;
         conn.ReceiveTimeout = 10 * 60 * 1000;
         Thread th = DataModule.RefreshGiveSets(conn, r, FmWait.ProgressIndicator);
         th.Join();
         conn.ReceiveTimeout = svTm;

         if (resultSet.Count > 0)
         {
            ReportResult rr = resultSet[0];
            if (rr.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + repName + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + repName + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, rr.file);
               res = fileName;
            }
         }

         return res;
      }
   }

   class PythonExecError : GRSoft.Network.DataObject
   {
      public String type = "";
      public String text = "";
      public String stackTrace = "";
   }


   class ReportErrorMessage : Form 
   {
      public ReportErrorMessage()
      {
         InitializeComponent();
      }

      private void InitializeComponent()
      {
         this.errMessage = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // errMessage
         // 
         this.errMessage.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
            | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
         this.errMessage.Location = new System.Drawing.Point(12, 58);
         this.errMessage.Multiline = true;
         this.errMessage.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.errMessage.Name = "errMessage";
         this.errMessage.Size = new System.Drawing.Size(597, 173);
         this.errMessage.ReadOnly = true;
         this.errMessage.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 42);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(98, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Описание ошибки";
         // 
         // label2
         // 
         this.label2.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
         this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label2.Location = new System.Drawing.Point(14, 13);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(595, 23);
         this.label2.TabIndex = 3;
         this.label2.Text = "Произошла ошибка";
         this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button1.Location = new System.Drawing.Point(534, 241);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 2;
         this.button1.Text = "Закрыть";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += button1_Click;
         this.button1.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         // 
         // Form1
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(621, 276);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.errMessage);
         this.CancelButton = button1;
         this.Name = "Form1";
         this.Text = "Ошибка";
         this.ResumeLayout(false);
         this.PerformLayout();
      }

      void button1_Click(object sender, EventArgs e)
      {
         this.Close();
      }

      public System.Windows.Forms.TextBox errMessage;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button button1;
   }
}
