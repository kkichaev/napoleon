using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager.Utils
{
   interface IDecorator
   {
      void AdjustForm();
      bool ExecFunction(FunctionArgsType args);
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

   class FunctionArgsType
   {
      string funcName;
      object[] args;
      object retVal;

      public FunctionArgsType(string funcName, params object[] args)
      {
         this.args = args;
         this.retVal = null;
         this.funcName = funcName;
      }

      public string FuncName { get { return funcName; } }
      public object RetVal { get { return retVal; } set { retVal = value; } }
      public object[] Args { get { return args; } }
   }
}
