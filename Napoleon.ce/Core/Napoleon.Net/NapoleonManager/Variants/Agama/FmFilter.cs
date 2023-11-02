using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmFilter : Form
   {
      public FmFilter()
      {
         InitializeComponent();
      }

      public List<string> Values
      {
         set 
         {
            list.Items.Clear();

            foreach (string v in value)
               list.Items.Add(v);
         }
      }

      public void SetSelected(bool value)
      {
         for (int i = 0; i < list.Items.Count; i++)
            list.SetItemChecked(i, value);
      }

      private void btnSelect_Click(object sender, EventArgs e)
      {
         SetSelected(true);
      }

      private void btnReset_Click(object sender, EventArgs e)
      {
         SetSelected(false);
      }

      public bool CheckItem(string val)
      {
         bool result = false;

         if (list.CheckedItems.Count == 0)
            result = true;
         else
            for (int i = 0; i < list.CheckedItems.Count; i++)
               if(val.Equals(list.CheckedItems[i]))
               {
                  result = true;
                  break;
               }

         return result;
      }

      public string ItemsText
      {
         get
         {
            const string DELIMETER = ";";

            StringBuilder result = new StringBuilder();

            if (list.CheckedItems.Count == 0)
               result.Append("Все");
            else
               for (int i = 0; i < list.CheckedItems.Count; i++)
               {
                  if (result.Length > 0)
                     result.Append(DELIMETER);
                  result.Append(list.CheckedItems[i]);
               }

            return result.ToString();
         }
      }
   }
}
