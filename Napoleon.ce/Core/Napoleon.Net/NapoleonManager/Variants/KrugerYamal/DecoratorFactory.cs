using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.Network;
using System.Threading;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      { 
         Type formType = form.GetType();

         if (formType == typeof(Divisions))
            return new DivisionsFormDecorator((Divisions)form);

         return new EmptyDecorator();
      }
   }

   class DivisionsFormDecorator : IDecorator
   {
      Divisions form;

      public DivisionsFormDecorator(Divisions form)
      {
         this.form = form;
      }

      public void AdjustForm()
      {
         ToolStripButton tsBonus = new ToolStripButton();
         tsBonus.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tsBonus.Name = "tsBonus";
         tsBonus.Size = new System.Drawing.Size(101, 22);
         tsBonus.Text = "Акции";
         tsBonus.Click += new System.EventHandler(OpenBonuses);

         this.form.tb.Items.Add(tsBonus);
      }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      void OpenBonuses(object sender, EventArgs e)
      {
         FmBonuses.Open();
      }

   }
}