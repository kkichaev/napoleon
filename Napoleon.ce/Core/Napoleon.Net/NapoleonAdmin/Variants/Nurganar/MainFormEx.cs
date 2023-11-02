using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         string[] right = {"EditRouteRight", "EnterToDivision"};
         string[] text = {"Сохранять маршрут", "Просмотр подразделения"};

         InitRightColumns(right, text);

         Width += 40;
      }
   }
}