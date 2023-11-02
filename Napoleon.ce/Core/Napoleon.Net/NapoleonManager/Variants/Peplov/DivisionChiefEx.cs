using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionChiefEx : DivisionChief
   {
      public DivisionChiefEx(Division d) :
         base(d)
      {
         DataGridViewTextBoxColumn clmn = new DataGridViewTextBoxColumn();
         clmn.DataPropertyName = "Email";
         clmn.HeaderText = "Email";
         clmn.Name = "clmn";
         
         clmn.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;

         dgvManagers.Columns.Add(clmn);
      }
   }
}
