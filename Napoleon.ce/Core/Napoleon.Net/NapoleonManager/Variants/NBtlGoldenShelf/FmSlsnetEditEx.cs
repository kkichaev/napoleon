using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{ 
   class FmSlsnetEditEx : FmSlsnetEdit
   {
      TextBox tbKoef;

      public FmSlsnetEditEx()
      {
         Height = 180;

         Label label = new Label();
         label.Text = "Коэффициент";
         label.Location = new Point(7, 37);
         label.Size = new Size(83,14);

         panel1.Controls.Add(label);

         tbKoef = new TextBox();
         tbKoef.Location = new Point(95, 35);
         tbKoef.Size = new Size(100, 20);

         panel1.Controls.Add(tbKoef);
      }

      public int Koef { get { return GetKoef(); } set { tbKoef.Text = value.ToString(); } }

      private int GetKoef()
      {
         int result = 0;
         int.TryParse(tbKoef.Text.Trim(), out result);
         return result;
      }
   }
}
