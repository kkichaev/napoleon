using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class RouteEx : Route
   {
      public RouteEx()
      {
         tsbShowMap.Visible = false;
         wb.Visible = false;
         splitContainer1.Panel2Collapsed = true;
         tsbAddOrg.Text = "Добавить задачу";
         btnReport.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         Text = "Распределение задач";
      }
   }
}
