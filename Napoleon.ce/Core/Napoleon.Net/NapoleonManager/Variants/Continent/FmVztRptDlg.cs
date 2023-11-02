using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVztRptDlg : Form
   {
      delegate void DAIterProcess(Agent a);
      private SettingFmVztPrtDlg setting = null;

      public FmVztRptDlg()
      {
         InitializeComponent();
      }

      private void FmVztPrtDlg_Load(object sender, EventArgs e)
      {
         FillAgentView(CreateAgentList());

         setting = BaseFormSetting<SettingFmVztPrtDlg>.Load();
         dateTimePicker1.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day , setting.hour, setting.min, 0);
      }

      private void FillAgentView(List<Agent> lst)
      {
         foreach (Agent a in lst)
            cbAgent.Items.Add(a);
      }

      private static List<Agent> CreateAgentList()
      {
         List<Agent> lst = new List<Agent>();
         DAIter((a) => { lst.Add(a); });
         lst.Sort();
         return lst;
      }

      private static void DAIter(DAIterProcess run)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

              run(da.agent);
            }
         }
      }

      public string AgentIds { 
         get
         {
            StringBuilder sb = new StringBuilder();
            foreach (Object o in cbAgent.CheckedItems)
            {
               Agent a = o as Agent;

               if (sb.Length > 0)
                  sb.Append(",");

               if (a != null)
                  sb.Append(a.id);
            }

            return sb.ToString();
         } 
      }

      private void FmVztPrtDlg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == System.Windows.Forms.DialogResult.OK && AgentIds.Length == 0)
         {
            MessageBox.Show("Выберите агента!");
            e.Cancel = true;
         }
      }

      private void FmVztPrtDlg_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.hour = dateTimePicker1.Value.Hour;
         setting.min = dateTimePicker1.Value.Minute;
         setting.Save();
      }
   }

   [Serializable]
   public class SettingFmVztPrtDlg : BaseFormSetting<SettingFmVztPrtDlg>
   {
      public int hour = 0;
      public int min = 0;
   } 
}
