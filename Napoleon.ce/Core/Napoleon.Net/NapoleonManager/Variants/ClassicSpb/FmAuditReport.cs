using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAuditReport : Form
   {
      private SettingFmAuditReport setting = null;

      public class Data : Network.DataObject
      {
         public DateTime date = DateTime.Now;
         public string division = string.Empty;
         public String time = string.Empty;
      }

      public FmAuditReport()
      {
         InitializeComponent();
      }

      private void FmWorkReport_Load(object sender, EventArgs e)
      {
         setting = BaseFormSetting<SettingFmAuditReport>.Load();

         dtpTimeStart.Value = setting.start;
         dtpTimeEnd.Value = setting.finish;
       
         dtpDate.Value = DateTime.Now.Date;

         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            cbDivision.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivision.Items.Add(d);
         }

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.date = dtpDate.Value.Date;
         data.division = GetDivision();
         data.time = GetTime();
         ReportResult.DoReport("auditreport", data, this);
      }

      private string GetDivision()
      {
         string res = string.Empty;

         Division d = cbDivision.SelectedItem as Division;

         if (d != null)
         {
            res = d.id.ToString();
         }

         return res;
      }

      private string GetTime()
      {
         const string TIME_MASK = "HH:mm";
         return dtpTimeStart.Value.ToString(TIME_MASK) + "|" + dtpTimeEnd.Value.ToString(TIME_MASK);
      }

      private void FmAuditReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.start = dtpTimeStart.Value;
         setting.finish = dtpTimeEnd.Value;
         setting.Save();
      }
   }

   [Serializable]
   public class SettingFmAuditReport : BaseFormSetting<SettingFmAuditReport>
   {
      public DateTime start = new DateTime(DateTimePicker.MinimumDateTime.Year, DateTimePicker.MinimumDateTime.Month, DateTimePicker.MinimumDateTime.Day, 9, 00, 00, 00);
      public DateTime finish = new DateTime(DateTimePicker.MinimumDateTime.Year, DateTimePicker.MinimumDateTime.Month, DateTimePicker.MinimumDateTime.Day, 18, 00, 00, 00);
   } 

}
