using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class DatePeriodView : UserControl
   {
      public DatePeriodView()
      {
         InitializeComponent();
      }

      public DateTime Start
      {
         get { return dtpStart.Value.Date; }
         set { dtpStart.Value = value; }
      }

      public DateTime Finish
      {
         get { return dtpFinish.Value.Date; }
         set { dtpFinish.Value = value; }
      }
   }
}
