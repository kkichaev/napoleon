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
         if (form.GetType() == typeof(MainForm))
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

         ToolStripButton btnPlans = new System.Windows.Forms.ToolStripButton();
         btnPlans.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnPlans.Image = Properties.Resources.accessorieseditor;
         btnPlans.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnPlans.Name = "Plans";
         btnPlans.Size = new System.Drawing.Size(23, 22);
         btnPlans.Text = "Планы";
         btnPlans.Click += new System.EventHandler((s,e)=>{
            if (form.CheckIsMainDataPresents(false) == true)
            {
               Form f = new AgentSalesPlan();
               f.Show();
            }
         });

         form.tsbConfig.Items.Add(btnPlans);

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.emblem_documents;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Презентация";
         rttReport.Click += new System.EventHandler((s,e) => new FmPrezentList().Show());

         ToolStripButton rttMsg = new System.Windows.Forms.ToolStripButton();
         rttMsg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttMsg.Image = Properties.Resources.mail_message_new_2;
         rttMsg.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttMsg.Name = "rttMsg";
         rttMsg.Size = new System.Drawing.Size(23, 22);
         rttMsg.Text = "Отправить сообщение";
         rttMsg.Click += new System.EventHandler((s, e) => sendMsg());

         form.tsbConfig.Items.Add(rttReport);
         form.tsbConfig.Items.Add(rttMsg);
      }

      private void sendMsg()
      {
         if ((CurrentUser.user as Manager) != null)
         {
            List<Agent> list = new List<Agent>();
            AgentChoose form = new AgentChoose(list);
            
            if (form.ShowDialog() == DialogResult.OK)
            {
               list = form.SelectedAgents;

               List<Division.DivisionAgent> trans = new List<Division.DivisionAgent>();

               StringBuilder sb = new StringBuilder();

               foreach (Agent a in list)
               {
                  if(sb.Length > 0)
                     sb.Append(", ");

                  sb.Append(a.Name);

                  Division.DivisionAgent da = new Division.DivisionAgent();
                  da.agent = a;

                  trans.Add(da);
               }

               FmMessage msg = new FmMessage(trans);
               msg.lbAgent.Text = sb.ToString();
               msg.Show();
            }
         }
      }
   }
}
