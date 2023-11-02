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

         if (formType == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);
         
         return new EmptyDecorator();
      }
   }

   class MainFormDecorator : EmptyDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.notvisit_report;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnNotVisit";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "ќтчет по непосещенным точкам ";
         btn.Click += new System.EventHandler((s, e) => { new FmNotVisitReport().Show(); });

         form.tsbConfig.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.everyday_report;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnReport";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "≈жедневный отчет";
         btn.Click += new System.EventHandler((s, e) => { new FmEverydayRpt().Show(); });

         form.tsbConfig.Items.Add(btn);
      }
   }
}
