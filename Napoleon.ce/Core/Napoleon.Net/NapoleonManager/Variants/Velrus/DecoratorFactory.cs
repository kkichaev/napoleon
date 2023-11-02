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
         if (form is Divisions)
            return new DivisionDecorator(form as Divisions);

         return new EmptyDecorator();
      }
   }

   class DivisionDecorator : EmptyDecorator
   {
      public DivisionDecorator(Divisions form)
      {
         ToolStripMenuItem tsDistrib = new ToolStripMenuItem("Матрица дистриб.");
         tsDistrib.Click += new EventHandler((o, e) => { Form f = new DistribMatrix(); f.Show(); });
         form.tb.Items.Add(tsDistrib);
      }
   }
}

