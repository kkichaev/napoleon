using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmCalendar : Form
   {
      public FmCalendar()
      {
         InitializeComponent();
      }

      public DateTime Date 
      {
         get { return monthCalendar1.SelectionStart.Date; } 
         set 
         { 
            DateTime min = new DateTime(value.Year, value.Month, 1);
            DateTime max = new DateTime(value.Year, value.Month, DateTime.DaysInMonth(value.Year, value.Month));
            monthCalendar1.MinDate = min;
            monthCalendar1.MaxDate = max;
            monthCalendar1.SetDate(value); 
         } 
      }
   }
}
