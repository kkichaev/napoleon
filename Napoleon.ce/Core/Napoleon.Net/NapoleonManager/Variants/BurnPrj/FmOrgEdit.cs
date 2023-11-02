using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgEdit : Form
   {
      private Org org = null;

      public FmOrgEdit(Org org)
      {
         InitializeComponent();
         this.org = org;

         tbAddress.Text = org.address;
         tbName.Text = org.name;
         tbFormat.Text = org.formatTT;
      }

      private void FmOrgEdit_FormClosed(object sender, FormClosedEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            org.name = tbName.Text.Trim();
            org.address = tbAddress.Text.Trim();
            org.formatTT = tbFormat.Text.Trim();
         }
      }
   }
}
