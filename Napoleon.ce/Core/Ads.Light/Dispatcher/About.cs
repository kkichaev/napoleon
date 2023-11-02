using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Ads.Dispatcher.Properties;

namespace GRSoft.Ads.Dispatcher
{
   public partial class About : Form
   {
      public About()
      {
         InitializeComponent();
      }

      private void About_Load(object sender, EventArgs e)
      {
         lblVersion.Text = Resources.version;
      }

      private void btnClose_Click(object sender, EventArgs e)
      {
         Close();
      }

      private void lblSite_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         System.Diagnostics.Process.Start(Resources.homepage);
      }

      private void lblMail_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         System.Diagnostics.Process.Start(Resources.mail);
      }
   }
}
