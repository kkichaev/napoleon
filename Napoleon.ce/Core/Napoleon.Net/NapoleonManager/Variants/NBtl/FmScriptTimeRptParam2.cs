using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmScriptTimeRptParam2 : Form
   {
      public FmScriptTimeRptParam2()
      {
         InitializeComponent();
      }

      private void FmScriptTimeRptParam2_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Division> list = new List<Division>();
            list.AddRange(m.AllDivisions);
            
            foreach (Division a in list)
               cbDivision.Items.Add(a);
         }

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;

         cbDivision.Enabled = true;
      }

      public DateTime Start { get { return dtpStart.Value.Date; } set { dtpStart.Value = value; } }
      public DateTime Finish { get { return dtpFinish.Value.Date.AddDays(1); } set { dtpFinish.Value = value; } }
      public string DivID { get { return cbDivision.SelectedItem as Division != null ? "'" + ((Division)cbDivision.SelectedItem).id + "'" : string.Empty; } }
   }
}
