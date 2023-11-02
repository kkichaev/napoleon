using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
    class FmScriptEditEx : FmScriptEdit
    {
       protected FmScriptEditEx(PostProcess postProcess) : base(postProcess)
       {
       }

        public override void __Initing(PostProcess postProcess)
        {
            base.__Initing(postProcess);

           Manager m = CurrentUser.user as Manager;

           if (m != null)
           {
              if (!m.HaveRight(RightTokens.Get(FormEntries.DISABLE_SAVE), RightActions.Write))
              {
                 btnSave.Visible = false;
              }
           }
        }
    }
}
