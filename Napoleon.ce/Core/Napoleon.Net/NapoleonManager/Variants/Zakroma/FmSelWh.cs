using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSelWh : Form
   {
      public FmSelWh()
      {
         InitializeComponent();
      }

      public List<WHouses> List {  
         set
         {
            listBox.Items.Clear();

            foreach(WHouses w in value)
               listBox.Items.Add(w);
         } 
      }

      public string Selected {
         get
         {
            StringBuilder sb = new StringBuilder();

            foreach (object o in listBox.CheckedItems)
            {
               WHouses w = o as WHouses;

               if (w != null)
               {
                  if (sb.Length > 0)
                     sb.Append(';');

                  sb.Append(w.name);
               }
            }

            return sb.ToString();
         }

         set
         {
            string[] v = value.Split(';');
            List<string> vv = new List<string>(v);

            for (int i = 0; i < listBox.Items.Count; i++)
            {
               WHouses w = listBox.Items[i] as WHouses;

               if (w != null)
               {
                  if (vv.Contains(w.name))
                     listBox.SetItemChecked(i, true);
               }
            }
         }
      }

   }
}
