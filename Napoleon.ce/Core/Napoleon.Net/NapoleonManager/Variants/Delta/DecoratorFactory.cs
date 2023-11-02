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

   class MainFormDecorator : IDecorator
   {
      MainForm form;

      public MainFormDecorator(MainForm form)
      {
         this.form = form;

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.copy;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "CopyOrders";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Дублировать заказы";
         rttReport.Click += new System.EventHandler(rttReport_Click);

         form.tsbConfig.Items.Add(rttReport);
      }

      public void AdjustForm() { }

      public bool ExecFunction(FunctionArgsType args) { return false; }

      private void rttReport_Click(object sender, EventArgs e)
      {
         DataSet<int, Order> ords = form.GetOrders();
         if (ords.Count == 0)
         {
            MessageBox.Show("Нет заявок. Нажмите, пожалуйста, на кнопку обновить");
            return;
         }
         DateTime date = form.GetBeginDateForSelection();
         string msg = String.Format("Дублировать заказы за {0:dd/MM/yyyy}?", date);
         if (MessageBox.Show(msg, "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            Dictionary<string, ReplacedSet> wr = new Dictionary<string, ReplacedSet>();
            foreach (Order o in ords.Data)
            {
               if (o.created.Date != date.Date)
                  continue;

               ReplacedSet rs = null;
               if (wr.ContainsKey(o.userid))
                  rs = wr[o.userid];
               else
               {
                  rs = new ReplacedSet(o.userid, new SimpleDataSet<Order>("Order", false, true));
                  rs.dontRemove = true;
                  wr[o.userid] = rs;
               }

               ((SimpleDataSet<Order>)rs.data).Add(o);
            }

            List<ReplacedSet> rpl = new List<ReplacedSet>();
            foreach (ReplacedSet rs in wr.Values)
               rpl.Add(rs);
            bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
            MessageBox.Show(ret ? "Заявки продублированы" : "Ошибка при записи");
         }
      }
   }
}
