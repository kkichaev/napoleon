using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx(){
         ToolStripMenuItem it = new ToolStripMenuItem();
         it.Text = "Матрицы";
         it.Click += it_Click;

         setColor.DropDownItems.Insert(2, it);
      }

      void it_Click(object sender, EventArgs e)
      {
         colors.Load(dsCommonConfig);
         new FmMatrixColor(colors).Show();
      }
   }
}
