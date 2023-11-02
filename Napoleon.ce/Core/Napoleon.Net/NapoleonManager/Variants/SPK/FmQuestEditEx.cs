using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{ 
   class FmQuestEditEx : FmQuestEdit
   {
      CheckBox cbType2;

      public FmQuestEditEx(Question question) : base(question)
      {
         cbType2 = new CheckBox();
         cbType2.Text = "Тип 2";
         cbType2.Location = new System.Drawing.Point(330, 4);

         if (question != null)
            cbType2.Checked = question.type2 != 0;

         panel1.Controls.Add(cbType2);
      }

      public override void UpdateQuest(Question quest)
      {
         quest.type2 = cbType2.Checked ? 1 : 0;
      }
   }
}
