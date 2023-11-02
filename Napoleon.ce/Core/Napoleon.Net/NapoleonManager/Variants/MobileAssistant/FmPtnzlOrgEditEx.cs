using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmPtnzlOrgEditEx : FmPtnzlOrgEdit
   {
      public FmPtnzlOrgEditEx()
      {
         label2.Visible = false;
         tbAddress.Visible = false;
         Text = "Редактировать задачу";
         Height = 130;
      }

      protected override void OnActivated(EventArgs e)
      {
         base.OnActivated(e);

         tbName.Focus();
      }
   }
}
