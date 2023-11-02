using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace GRSoft.Ads.Dispatcher
{
   class TransparentControl : Control
   {

      public TransparentControl()
      {
         //SetStyle(ControlStyles.SupportsTransparentBackColor, true);
         //this.BackColor = Color.Transparent;
      }

      protected override CreateParams CreateParams
      {
         get
         {
            CreateParams cp = base.CreateParams;
            cp.ExStyle |= 0x00000020; //WS_EX_TRANSPARENT
            return cp;
         }
      }
      protected override void OnPaintBackground(PaintEventArgs pevent)
      {
        
      } 

   }
}
