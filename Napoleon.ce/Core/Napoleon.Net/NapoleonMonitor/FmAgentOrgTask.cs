using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentOrgTask : Form
   {
      OrgData data;
      String taskValue;

      public FmAgentOrgTask()
      {
         InitializeComponent();
      }

      internal OrgData Data
      { 
         get { return data; } 
         set 
         { 
            data = value;
            orgName.Text = data.OrgName;
         }
      }

      public string Task { get { return taskValue; } set { taskValue = value; task.Text = value; } }

      private void ok_Click(object sender, EventArgs e)
      {
         taskValue = task.Text;
      }
   }
}
