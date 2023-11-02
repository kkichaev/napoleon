using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   class Decorator
   {
      public static void Adjust(Form form)
      {
         if (form is FmMain)
         {
            FmMain mainForm = (FmMain)form;
            ToolStripMenuItem miCounter = new System.Windows.Forms.ToolStripMenuItem();
            miCounter.Name = "miCounter";
            miCounter.Size = new System.Drawing.Size(196, 22);
            miCounter.Text = "Счетчики";
            miCounter.Click += new System.EventHandler(new EventHandler(delegate(object o, EventArgs e)
               { FmCounter.ShowInstance(); }));

            ToolStripMenuItem miCertificates = new System.Windows.Forms.ToolStripMenuItem();
            miCertificates.Name = "miSertificates";
            miCertificates.Size = new System.Drawing.Size(196, 22);
            miCertificates.Text = "Свидетельства";
            miCertificates.Click += new System.EventHandler(new EventHandler(delegate(object o, EventArgs e)
            { FmCertificate.ShowInstance(); }));

            ToolStripMenuItem miProtocol = new System.Windows.Forms.ToolStripMenuItem();
            miProtocol.Name = "miProtocol";
            miProtocol.Size = new System.Drawing.Size(196, 22);
            miProtocol.Text = "Протоколы";
            miProtocol.Click += new System.EventHandler(new EventHandler(delegate(object o, EventArgs e)
            { FmProtocol.ShowInstance(); }));

            ToolStripMenuItem miSMSTemplate = new System.Windows.Forms.ToolStripMenuItem();
            miSMSTemplate.Name = "miSMSTemplate";
            miSMSTemplate.Size = new System.Drawing.Size(196, 22);
            miSMSTemplate.Text = "Текст СМС сообщения";
            miSMSTemplate.Click += new System.EventHandler(new EventHandler(delegate(object o, EventArgs e)
            { new FmSMSTemplate().ShowDialog(); }));

            //ToolStripMenuItem miOrderDBF = new System.Windows.Forms.ToolStripMenuItem();
            //miOrderDBF.Name = "miOrderDBF";
            //miOrderDBF.Size = new System.Drawing.Size(196, 22);
            //miOrderDBF.Text = "Выгрузка заявок в DBF";
            //miOrderDBF.Click += new System.EventHandler(new EventHandler(delegate(object o, EventArgs e)
            //{ FmOrderDBF.ShowInstance(); }));

            ToolStripButton btnPlanFm = new System.Windows.Forms.ToolStripButton();
            btnPlanFm.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            btnPlanFm.Image = global::GRSoft.Ads.Properties.Resources.appointment_new;
            btnPlanFm.ImageTransparentColor = System.Drawing.Color.Magenta;
            btnPlanFm.Name = "btnPlanFm";
            btnPlanFm.Size = new System.Drawing.Size(23, 22);
            btnPlanFm.Text = "Форма планирования";
            btnPlanFm.Click += new System.EventHandler(new EventHandler(delegate(object o, EventArgs e)
            { new FmPlanning(mainForm.datePickerCtrl1.Date).Show(); }));
            btnPlanFm.Enabled = false;

            mainForm.toolStrip1.Items.Add(btnPlanFm);
      
            mainForm.miReference.DropDownItems.Add(miCounter);
            mainForm.miReference.DropDownItems.Add(miCertificates);
            mainForm.miReference.DropDownItems.Add(miProtocol);
            mainForm.miReference.DropDownItems.Add(miSMSTemplate);
            //mainForm.miReport.DropDownItems.Add(miOrderDBF);

            mainForm.OnRefreshData += new EmptyInvoker(delegate()
            {
               DsDistrict dsDistrict = (DsDistrict)DataModule.Get(District.OBJECT_NAME);
               btnPlanFm.Enabled = dsDistrict != null && dsDistrict.Count > 0;
            });

            mainForm.contextMenuStrip1.Items.Add("Маршрут", null, new EventHandler(delegate(object sender, EventArgs args)
               {
                  FmRouteEx fmRoute = (FmRouteEx)FormEnties.CreateRouteForm(mainForm.selectedBrigade.id, DateTime.Now);
                  if(mainForm.selectedOrder != null && mainForm.selectedOrder.client != null)
                     fmRoute.address = mainForm.selectedOrder.client.address;
                  fmRoute.Show();
               }));
         }
      }
   }
}
