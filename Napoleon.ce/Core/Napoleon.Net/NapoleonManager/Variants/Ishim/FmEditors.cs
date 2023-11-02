using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEditors : Form
   {
      public FmEditors()
      {
         InitializeComponent();
      }

      private void btnSklad_Click(object sender, EventArgs e)
      {
         FmSkladBind.Open();
      }

      private void btnPrice_Click(object sender, EventArgs e)
      {
         FmPriceEditor.Open(); 
      }

      private void btnMatrix_Click(object sender, EventArgs e)
      {
         new FmOrgProp().Show();
      }

      private void btnCause_Click(object sender, EventArgs e)
      {
         new FmStringCauseEdit().Show();
      }

      private void btnQuest_Click(object sender, EventArgs e)
      {
         new FmQuestionary().Show();
      }

      private void btnMon_Click(object sender, EventArgs e)
      {
         Type agentTask = FormEntries.GetFormType(typeof(MonitoringItems));
         ConstructorInfo ci = agentTask.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      private void bntStopList_Click(object sender, EventArgs e)
      {
         Type stopType = FormEntries.GetFormType(typeof(FmStopOrgList));
         ConstructorInfo ci = stopType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }
   }
}
