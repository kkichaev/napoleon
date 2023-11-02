using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
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
         if (form.GetType() == typeof(MainForm))
            return new MainFormDecorator((MainForm)form);

         return new EmptyDecorator();
      }
   }

   class DivisionDecorator : IDecorator
   {
      public DivisionDecorator(Form f)
      {
         Divisions df = (Divisions)f;
         ToolStripButton tb = new ToolStripButton();
         tb.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         tb.Name = "tb";
         tb.Size = new System.Drawing.Size(101, 22);
         tb.Text = "Обновить список контрагентов";
         tb.Click += new System.EventHandler(RefreshClients);

         df.GetToolStrip().Items.Add(tb);
      }

      void RefreshClients(object sender, EventArgs e)
      {
         SimpleDataSet<ReqClients> rc = new SimpleDataSet<ReqClients>(ReqClients.OBJECT_NAME);
         ReqClients data = new ReqClients();
         data.id = "tests";

         rc.Add(data);
         List<IDataSet> wr = new List<IDataSet>(new IDataSet[] { rc });
         DBConnection c = Config.GetConfig().GetConnection();
         if (DataModule.UpdateDataSet(wr, null, null, c))
            MessageBox.Show("Запрос отправлен, список клиентов на сервере обновляется.");
      }

      public void AdjustForm()
      {

      }

      public bool ExecFunction(FunctionArgsType args)
      {
         return false;
      }
   }

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton btnVisit = new System.Windows.Forms.ToolStripButton();
         btnVisit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnVisit.Image = Properties.Resources.ic_image_search;
         btnVisit.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnVisit.Name = "btnVisit";
         btnVisit.Size = new System.Drawing.Size(23, 22);
         btnVisit.Text = "Посещения";
         btnVisit.Click += new System.EventHandler((s, e) => { new FmVisitInfo().Show(); });

         form.tsbConfig.Items.Add(btnVisit);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return true; }

      
   }

   class ReqClients : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ReqClients";
      public string id;
   }

}
