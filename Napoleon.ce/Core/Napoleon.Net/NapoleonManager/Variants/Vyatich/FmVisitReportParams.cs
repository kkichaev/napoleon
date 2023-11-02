using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVisitReportParams : Form
   {
      public enum SortMode { Created, Org}

      public FmVisitReportParams()
      {
         InitializeComponent();
      }
         
      public SortMode SortType { get { return rbCreated.Checked ? SortMode.Created : SortMode.Org; } 
         set 
         {
            if (value == SortMode.Created)
               rbCreated.Checked = true;
            else
               rbOrg.Checked = true;
         } 
      }

      public int Prec { get { return (int)precision.Value; } set { precision.Value = value; } }
   }
}
