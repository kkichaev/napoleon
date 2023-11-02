using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmMatrixDesignerEx : FmMatrixDesigner
   {
      public override void __Initing()
      {
         base.__Initing();

         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            if (!m.HaveRight(RightTokens.Get(FormEntries.DISABLE_SAVE), RightActions.Write))
            {
               tsbSave.Visible = false;
            }
         }
      }
   }
}
