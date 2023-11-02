using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class DailyReportParams : Form
   {
      SalesHistory.Data data;

      internal DailyReportParams(SalesHistory.Data data)
      {
         this.data = data;
         InitializeComponent();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         data.from = new DateTime(dateFrom.Value.Year, dateFrom.Value.Month, dateFrom.Value.Day);
         base.OnClosing(e);
      }
   }
}
