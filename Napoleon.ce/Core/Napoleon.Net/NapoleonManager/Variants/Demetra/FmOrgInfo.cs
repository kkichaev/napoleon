using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgInfo : Form
   {
      public FmOrgInfo()
      {
         InitializeComponent();
      }

      public string Address { get; set; }

      public string OrgName { get; set; }

      private void lblName_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         System.Windows.Forms.Clipboard.SetText(((LinkLabel)sender).Text);
      }

      private void FmOrgInfo_Load(object sender, EventArgs e)
      {
         lblName.Text = OrgName ?? string.Empty;
         lblAddress.Text = Address ?? string.Empty;
      }
   }
}
