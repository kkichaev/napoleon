using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Properties;

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
         ToolStripMenuItem tsDistrib = new ToolStripMenuItem("Матрица фокусного ассортимента");
         tsDistrib.Click += new EventHandler((o, e) => { Form f = new FmFocusMtx(); f.Show(); });
         form.tb.Items.Add(tsDistrib);

         tsDistrib = new ToolStripMenuItem("");
         tsDistrib.Image = Resources.schedule;
         tsDistrib.Click += new EventHandler((o, e) => { Form f = new FmFirstDocTime(); f.Show(); });
         form.tb.Items.Add(tsDistrib);
      }
   }
}

