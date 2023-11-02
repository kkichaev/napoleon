using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class MovementDetail : UserControl
   {
      public MovementDetail()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      internal void SetMovement(Movement doc, String whStr)
      {
         dgvItems.DataSource = doc.items;
         labelSrcWh.Text = "Склад отправитель " + ParseWh(doc.whSrc, whStr);
         labelDestWh.Text = "Склад получатель " + ParseWh(doc.whDest, whStr);
      }

      private string ParseWh(string key, string whStr)
      {
         foreach (string el in whStr.Split(new char[] { ';' }))
         {
            if (el.Contains(key))
               return el.Split(new char[] { '\t' })[0];
         }

         return "<" + key + ">";
      }
   }
}
