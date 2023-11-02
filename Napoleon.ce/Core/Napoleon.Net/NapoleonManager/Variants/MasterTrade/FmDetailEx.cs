using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         DataGridViewTextBoxColumn disc = new DataGridViewTextBoxColumn();
         disc.DataPropertyName = "Discount";
         disc.HeaderText = "Скидка %";
         disc.Name = "disc";

         dgvOrderItems.Columns.Add(disc);
      }
   }
}
