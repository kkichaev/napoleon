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
      public static IDecorator GetDecorator(Form form)
      {
         if (form.GetType() == typeof(FmCoverArea))
            return new FmCoverAreaDecorator((FmCoverArea)form);

         return new EmptyDecorator();
      }
   }

   class FmCoverAreaDecorator : IDecorator
   {
      public FmCoverAreaDecorator(FmCoverArea form)
      {
         form.btnOrder.Visible = false;
         form.btnMove.Visible = false;
         form.btnSales.Visible = false;
         //form.btnQuestion.Visible = false;
         form.btnMove.Visible = false;
         form.btnIncass.Visible = false;
         form.btnOnlyFromRoute.Visible = false;
         form.toolStripSeparator3.Visible = false;
      }

      #region IDecorator Members

      public void AdjustForm()
      {
         throw new NotImplementedException();
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         throw new NotImplementedException();
      }

      #endregion
   }

}
