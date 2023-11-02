using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.Ads
{
   public partial class FmDatePeriod : Form
   {
      public FmDatePeriod()
      {
         InitializeComponent();
      }

      public DateTime Begin { get { return dtpBegin.Value.Date; } }
      public DateTime End { get { return dtpEnd.Value.Date; } }
   }
}
