using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmSelectContrAgentEx : FmSelectContrAgent
   {
      public FmSelectContrAgentEx()
      {
         initView();
      }

      private void initView()
      {
         Text = "Выберите задачу";
         dgvOrgsName.HeaderText = "Задача";
         dgvOrgsAddress.Visible = false;
      }
   }
}
