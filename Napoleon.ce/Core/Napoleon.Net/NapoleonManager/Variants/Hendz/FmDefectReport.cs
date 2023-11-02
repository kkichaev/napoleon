using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.IO;
using System.Runtime.InteropServices;

namespace GRSoft.NapoleonManager
{
   public partial class FmDefectReport : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;

      private DateTime begin = DateTime.Now;
      private DateTime end = DateTime.Now;
      private Agent agent = null;

      public FmDefectReport()
      {
         InitializeComponent();
      }

      public DateTime Begin { get { return begin; } set { begin = value; } }
      public DateTime End { get { return end; } set { end = value; } }
      public Agent Agent { get { return agent; } set { agent = value; } } 

      private void FmDefectReport_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;
               list.Add(da.agent);
            }

            List<Division> div = new List<Division>();
            div.AddRange(mc.AllDivisions);
            div.Sort(new Comparison<Division>(delegate(Division lhs, Division rhs) { return lhs.name.CompareTo(rhs.name); }));

            cbDivisions.Items.AddRange(div.ToArray());

            if (cbDivisions.Items.Count > 0)
               cbDivisions.SelectedIndex = 0;
         }

         list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         cbAgents.Items.AddRange(list.ToArray());

         if (cbAgents.Items.Count > 0)
         {
            if (Agent != null)
            {
               for (int i = 0; i < cbAgents.Items.Count; i++)
               {
                  Agent a = cbAgents.Items[i] as Agent;

                  if(agent.id.Equals(a.id))
                  {
                     cbAgents.SelectedIndex = i;
                     break;
                  }
               }
            }else
               cbAgents.SelectedIndex = 0;
         }

         dtpBegin.Value = Begin;
         dtpEnd.Value = End;
      }

      private void rb_CheckedChanged(object sender, EventArgs e)
      {
         RadioButton rb = sender as RadioButton;

         if (rb != null)
         {
            string name = rb.Tag as string;

            if (name != null)
            {
               try
               {
                  Control c = Controls[name];
                  c.Enabled = rb.Checked;
               }
               catch (Exception) { }
            }
         }
      }

      public class Data : GRSoft.Network.DataObject
      {
         public string userid = "";
         public DateTime begin = DateTime.Now;
         public DateTime end = DateTime.Now;
         public bool pics = false;
      }

      class ResultItem : GRSoft.Network.DataObject
      {
         public string name = string.Empty;
         public byte[] pic = null;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;

         [ItemType(typeof(ResultItem))]
         public List<ResultItem> items = null;
      }

      private void ok_Click(object sender, EventArgs e)
      {
         const string REPORT_NAME = "defect_report";

         string userid = GetSelectedAgent();

         if (userid != null)
         {
            Data data = new Data();
            data.userid = userid;
            data.begin = dtpBegin.Value.Date;
            data.end = dtpEnd.Value.Date;
            data.pics = cbPics.Checked;

            Result result = new Result();
            SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
            Report r = new Report(REPORT_NAME, data, resultSet);

            Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
            FmWait.ShowForm(this, th);
            th.Join();
            FmWait.CloseForm();

            if (resultSet.Count > 0)
            {
               Result res = resultSet[0];
               if (res.file.Length > 0)
               {
                  foreach (ResultItem i in res.items)
                  {
                     MemoryStream stream = null;
                     FileStream outstr = null;
                     try
                     {
                        stream = new MemoryStream(i.pic);
                        outstr = new FileStream(Path.GetTempPath() + "\\" + i.name + ".jpg", FileMode.Create, FileAccess.Write);
                        stream.WriteTo(outstr);
                     }
                     catch (Exception) { }
                     finally
                     {
                        if (stream != null) stream.Close();
                        if (outstr != null) outstr.Close();
                     }
                  }

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

      private void AppendVal(StringBuilder sb, string val)
      {
         const string SCOPE = "'";
         sb.Append(SCOPE).Append(val).Append(SCOPE);
      }

      private string GetSelectedAgent()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgents.Checked && cbAgents.SelectedIndex != -1)
            AppendVal(result, (cbAgents.SelectedItem as Agent).id);
         else if (rbDivision.Checked && cbDivisions.SelectedIndex != -1)
         {
            Division d = cbDivisions.SelectedItem as Division;

            if (d != null)
            {
               foreach (Division.DivisionAgent a in d.GetAllAgents())
               {
                  if (result.Length > 0)
                     result.Append(",");

                  AppendVal(result, a.id);
               }
            }
         }

         return result.ToString();
      }
   }
}
