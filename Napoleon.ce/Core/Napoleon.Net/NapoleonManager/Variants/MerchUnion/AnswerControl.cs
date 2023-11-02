using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class AnswerControl : UserControl, DocControl
   {
      Answer answer;

      public AnswerControl(Answer answer)
      {
         InitializeComponent();
         this.answer = answer;

         Question quest = answer.quest;
         quest.items.Sort((x, y) => y.number.CompareTo(x.number));

         Dictionary<string, List<AnswerItem>> hash = new Dictionary<string, List<AnswerItem>>();

         foreach(AnswerItem item in answer.items)
         {
            if (!hash.ContainsKey(item.iditem))
               hash[item.iditem] = new List<AnswerItem>();

            hash[item.iditem].Add(item);
         }

         foreach (QuestionItem i in quest.items)
         {
            if (!hash.ContainsKey(i.iditem))
               continue;

            IAnswerControl c = null;
            if (i.type.Equals(QuestionItem.TEXT) || i.type.Equals(QuestionItem.NUMBER))
            {
               c = new AnswerText(i);
            }
            else if (i.type.Equals(QuestionItem.LIST) || i.type.Equals(QuestionItem.NUMBER_LIST))
            {
               c = new AnswerList(i);
            }
            else if (i.type.Equals(QuestionItem.SET))
            {
               c = new AnswerSet(i);
            }
            else if (i.type.Equals(QuestionItem.BOOLEAN))
            {
               c = new AnswerBoolean(i);
            }
            else if (i.type.Equals(QuestionItem.SPINNER))
            {
               c = new AnswerSpinner(i);
            }

            if (c != null && hash.ContainsKey(i.iditem))
            {
               c.SetValue(hash[i.iditem]);
               ((Control)c).Dock = DockStyle.Top;
               Controls.Add((Control)c);
            }
         }
      }

      public GRSoft.Network.DataObject UpdateDoc()
      {
         List<AnswerItem> items = new List<AnswerItem>();

         foreach (Control c in Controls)
         {
            IAnswerControl a = c as IAnswerControl;

            if (a != null)
               items.AddRange(a.GetValue());
         }

         answer.items = items;
         answer.created = DateTime.Now;

         return answer;
      }
   }
}
