using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceActionEdit : Form
   {
      private string action;
      private DateTime start, end;

      public FmPriceActionEdit(string action)
      {
         InitializeComponent();
         text.Text = action;
         this.action = null;

         startDate.Value = DateTime.Now;
         endDate.Value = DateTime.Now.AddMonths(1);
      }

      private void ok_Click(object sender, EventArgs e)
      {
         action = text.Text;
         start = startDate.Value;
         end = endDate.Value;
      }

      public string Action { get { return action; } }
      public DateTime Start { get { return start; } }
      public DateTime End { get { return end; } }
   }
}
