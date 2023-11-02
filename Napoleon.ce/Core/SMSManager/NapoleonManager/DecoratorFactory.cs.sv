using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetMainFormDecorator(MainForm form)
      {
         return new EmptyDecorator();
      }

      public static IDecorator GetFmDetailDecorator(FmDetail form)
      {
         return new EmptyDecorator();
      }
   }

   class EmptyDecorator : IDecorator
   {

      #region IDecorator Members

      public void AdjustForm()
      {
         //ƒл€ общего декоратора этот метод ничего не делает...
      }

      #endregion

      #region IDecorator Members


      public bool ExecFunction(FunctionArgsType args)
      {
         return false;
      }

      #endregion
   }
}
