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

         if (formType == typeof(FmDetail) ||
            formType == typeof(FmDetailBase))
            return new FmDetailDecorator((FmDetailBase) form);

         if (formType == typeof(Divisions))
            return new DivisionsDecorator((Divisions)form);
         return new EmptyDecorator();
      }
   }

   class DivisionsDecorator : EmptyDecorator
   {
      public DivisionsDecorator(Divisions form)
      {
         ToolStripButton tb = new ToolStripButton();
         tb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tb.Name = "tb";
         tb.Size = new System.Drawing.Size(101, 22);
         tb.Text = "Товарн.группы";
         tb.Click += new System.EventHandler((o,e)=>ItemGroupEditor.Open());

         form.GetToolStrip().Items.Add(tb);
      }
   }


   class FmDetailDecorator : EmptyDecorator
   {
      public FmDetailDecorator(FmDetailBase form)
      {
         ToolStripMenuItem itOneDayReport = new ToolStripMenuItem();
         itOneDayReport.Name = "itOneDayReport";
         itOneDayReport.Size = new System.Drawing.Size(161, 22);
         itOneDayReport.Text = "Дневной отчет";
         itOneDayReport.Click += new System.EventHandler((o, e) => { new FmOneDayReport().Show(); });

         form.tsReportMenu.DropDownItems.Add(itOneDayReport);
      }
   }
}
