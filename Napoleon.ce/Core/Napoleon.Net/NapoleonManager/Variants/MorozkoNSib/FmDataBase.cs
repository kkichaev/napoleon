using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
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
         new FmSlsnet().Show();
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
   }
}
