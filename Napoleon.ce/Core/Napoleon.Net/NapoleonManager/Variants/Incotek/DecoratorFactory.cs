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
            return new DivisionDecorator((Divisions)form);

         if (formType == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);

         return new EmptyDecorator();
      }
   }

   class DivisionDecorator : EmptyDecorator
   {
      Divisions form;

      public DivisionDecorator(Divisions form)
      {
         this.form = form;
         ToolStripButton btnOTE = new System.Windows.Forms.ToolStripButton();
         btnOTE.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnOTE.Name = "btnOTE";
         btnOTE.Text = "Акции \"Сторчек\"";
         btnOTE.Click += new System.EventHandler((o, e) => { FmSCActionList.Open(); });

         form.tb.Items.Add(btnOTE);

         btnOTE = new System.Windows.Forms.ToolStripButton();
         btnOTE.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btnOTE.Name = "btnOTE1";
         btnOTE.Text = "Товар \"Сторчек\"";
         btnOTE.Click += new System.EventHandler((o, e) => { FmSCGoodsList.Open(); });

         form.tb.Items.Add(btnOTE);
      }
   }

   class MainFormDecorator : EmptyDecorator
   {
      public MainFormDecorator(MainForm form)
      {
         ToolStripButton btnRep = new System.Windows.Forms.ToolStripButton();
         btnRep.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnRep.Image = Properties.Resources.dialog_apply;
         btnRep.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnRep.Name = "btnMonitoringDBF";
         btnRep.Size = new System.Drawing.Size(23, 22);
         btnRep.Text = "Сторчек";
         btnRep.Click += new System.EventHandler((o, e) => { (new FmStorcheckReport()).Show(); });

         form.tsbConfig.Items.Add(btnRep);
      }
   }
}
