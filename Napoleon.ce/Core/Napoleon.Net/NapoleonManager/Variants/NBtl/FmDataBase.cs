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
   public partial class FmDataBase : Form
   {
      public FmDataBase()
      {
         InitializeComponent();
      }

      private void btnOrgEd_Click(object sender, EventArgs e)
      {
         new FmOrg().Show(); 
      }

      private void btnCityEd_Click(object sender, EventArgs e)
      {
         new FmCity().Show();
      }

      private void btnSlsnetEd_Click(object sender, EventArgs e)
      {
          Type type = FormEntries.GetFormType(typeof(FmSlsnet));
          ConstructorInfo ci = type.GetConstructor(Type.EmptyTypes);
          Form fm = (Form)ci.Invoke(new object[] { });
          fm.Show();
      }

      private void btnOrgAssign_Click(object sender, EventArgs e)
      {
         new FmOrgAssign().Show();
      }

      private void edContractEd_Click(object sender, EventArgs e)
      {
         new FmContract().Show();
      }

      private void btnScriptAssign_Click(object sender, EventArgs e)
      {
         new FmScrAssign().Show();
      }

      private void btnShelfPart_Click(object sender, EventArgs e)
      {
         new FmPartShelf().Show();
      }

      private void btnReturnCause_Click(object sender, EventArgs e)
      {
         FmReturnCauseEdit.Open();
      }

      private void btnPlan_Click(object sender, EventArgs e)
      {
         new FmBtlPlan().Show();
      }

      private void btnPrice_Click(object sender, EventArgs e)
      {
         new FmGoods().Show();
      }

      private void btnAgentPlan_Click(object sender, EventArgs e)
      {
         new FmAgentPlan().Show();
      }

      private void btnVisitPlan_Click(object sender, EventArgs e)
      {
         new FmVisitPlanFact().Show();
      }

      private void btnEditPLU_Click(object sender, EventArgs e)
      {
         new FmEditPLU().Show();
      }
   }
}
