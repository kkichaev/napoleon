using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads.Dispatcher
{
   public partial class TaskViewProp : Form
   {
      public TaskViewProp()
      {
         InitializeComponent();
      }

      private void TaskViewProp_Load(object sender, EventArgs e)
      {
         Config cfg = Config.GetConfig();
         cbAddress.Checked = cfg.addrView;
         cbName.Checked = cfg.clitnView;
         cbText.Checked = cfg.textView;
      }

      private void TaskViewProp_FormClosed(object sender, FormClosedEventArgs e)
      {
         Config cfg = Config.GetConfig();
         cfg.addrView = cbAddress.Checked;
         cfg.clitnView = cbName.Checked;
         cfg.textView = cbText.Checked;
         cfg.Save();
      }
   }
}
