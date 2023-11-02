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
         Type formType = form.GetType();

         if (typeof(FmDetailBase).IsAssignableFrom(formType))
            return new DetailFormDecorator((FmDetailBase)form);

         return new EmptyDecorator();
      }

      class DetailFormDecorator : IDecorator
      {
         public DetailFormDecorator(FmDetailBase form)
         {
            DataGridViewTextBoxColumn clmn1 = new DataGridViewTextBoxColumn();
            clmn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            clmn1.DataPropertyName = "Notes";
            clmn1.FillWeight = 100F;
            clmn1.HeaderText = "Примечание";
            clmn1.Name = "dgvNotes";

            form.dgvDetail.Columns.Add(clmn1);
         }

         public void AdjustForm() { }
         public bool ExecFunction(FunctionArgsType args) { return false; }
      }
   }
}
