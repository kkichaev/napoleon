using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSummayRptParam : Form
   {
      public FmSummayRptParam()
      {
         InitializeComponent();
      }

      public DateTime Start { get { return dtpStart.Value.Date; } set { dtpStart.Value = value; } }
      public DateTime Finish { get { return dtpFinish.Value.Date; } set { dtpFinish.Value = value; } }
      public string DivIds { get { return CollectDivIds(); } }

      void AddDivision(Division d, StringBuilder sb)
      {
         const string DELIMITER = ",";
         if (d != null)
         {
            if (sb.Length > 0)
               sb.Append(DELIMITER);

            sb.Append(d.id);

            foreach (Division ch in d.Childs)
               AddDivision(ch, sb);
         }
      }

      private string CollectDivIds()
      {
         StringBuilder sb = new StringBuilder();

         foreach (object o in listBox.CheckedItems)
         {
            Division d = o as Division;
            AddDivision(d, sb);
         }

         return sb.ToString();
      }

      private void FmSummayRptParam_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            listBox.Items.Add(m.Division);

            foreach (Division d in m.Childs)
               listBox.Items.Add(d);
         }
      }
   }
}
