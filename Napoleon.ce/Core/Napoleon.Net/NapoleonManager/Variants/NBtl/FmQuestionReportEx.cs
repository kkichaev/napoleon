using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmQuestionReportEx : FmQuestionReport
   {
      RadioButton rbStats;

      public FmQuestionReportEx()
      {
         gbQuestType.Size = new System.Drawing.Size(400, 35);

         rbStats = new RadioButton();
         rbStats.Text = "Показатели";
         rbStats.Location = new System.Drawing.Point(230, 10);

         gbQuestType.Controls.Add(rbStats);
      }

      protected override string GetReportName(bool horizontal)
      {
         if (rbStats.Checked)
            return "quest_stats_rep";
         else
            return base.GetReportName(horizontal);
      }
   }
}
