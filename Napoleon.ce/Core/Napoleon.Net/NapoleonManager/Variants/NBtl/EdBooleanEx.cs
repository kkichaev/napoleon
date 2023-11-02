using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class EdBooleanEx : EdBoolean
   {
      TextBox tbPercent;

      public EdBooleanEx() 
      {
         Label lbl = new Label();
         tbPercent = new TextBox();

         lbl.Text = "%";
         lbl.Location = new System.Drawing.Point(4,92);
         lbl.Size = new System.Drawing.Size(35, 13);

         tbPercent.Location = new System.Drawing.Point(54,92);
         tbPercent.Size = new System.Drawing.Size(241,20);
         tbPercent.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;

         Controls.Add(lbl);
         Controls.Add(tbPercent);
      }

      public override List<QuestionItemValue> GetValues()
      {
         List<QuestionItemValue> result =  base.GetValues();

         int val = 0;
         if (Int32.TryParse(tbPercent.Text, out val))
            result[0].value2 = val.ToString();

         return result;
      }

      public override void SetValues(List<QuestionItemValue> list)
      {
         base.SetValues(list);

         tbPercent.Text = list[0].value2;
      }
   }
}
