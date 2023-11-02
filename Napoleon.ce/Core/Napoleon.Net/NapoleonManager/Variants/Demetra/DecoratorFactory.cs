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
         if (form.GetType() == typeof(Divisions))
            return new DivisionDecorator(form);

         return new EmptyDecorator();
      }
   }

   class DivisionDecorator : EmptyDecorator
   {
      public DivisionDecorator(Form f)
      {
         Divisions df = (Divisions)f;
         ToolStripButton tb = new ToolStripButton();
         tb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tb.Name = "tb";
         tb.Size = new System.Drawing.Size(101, 22);
         tb.Text = "Группы дистрибуции";
         tb.Click += new System.EventHandler((o, e) => { FmDistribGroupEditor.Open(); });

         df.GetToolStrip().Items.Add(tb);
      }
   }
}
