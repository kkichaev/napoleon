using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx() : base()
      {
         //btnDivision.Visible = (Config.GetConfig().login == "admin");
      }

      protected override void AfterRefreshData()
      {
         //Manager m = (Manager)CurrentUser.user;
         //if( m != null )
            //btnDivision.Visible = m.HaveRight(RightTokens.Get("EnterToDivision"), RightActions.Write);
      }
   }
}
