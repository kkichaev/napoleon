using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainEx : MainForm
   {
      public MainEx()
      {
         ToolStripButton btn;

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.office_calendar;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnRtpAction";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Отчет по эффективности в разрезе по ТТ";
         btn.Click += new System.EventHandler(delegate(object sender, EventArgs e) 
         {
            new FmRptAction().Show();
         });

         tsbConfig.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.incass_doc;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnIncass";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Отчет по инкассации";
         btn.Click += new System.EventHandler(delegate(object sender, EventArgs e)
         {
            FmIncassRptParam.Do(dtpBeginDate.Value.Date, dtpEndDate.Value.Date); ;
         });

         tsbConfig.Items.Add(btn);
      }
/*
      private void InitializeComponent()
      {
         ((System.ComponentModel.ISupportInitialize)(this.tgvAgentsSummary)).BeginInit();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dateWarningTooltip
         // 
         this.dateWarningTooltip.ClientSize = new System.Drawing.Size(250, 39);
         this.dateWarningTooltip.Location = new System.Drawing.Point(244, 12);
         // 
         // lbVersion
         // 
         this.lbVersion.Size = new System.Drawing.Size(245, 14);
         this.lbVersion.Text = "версия: 3.5.0.13 / 12.02.2018 проект: Наполеон";
         // 
         // cbConfig
         // 
         this.cbConfig.Size = new System.Drawing.Size(160, 22);
         // 
         // MainEx
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.ClientSize = new System.Drawing.Size(998, 580);
         this.Name = "MainEx";
         this.Controls.SetChildIndex(this.dtpBeginDate, 0);
         this.Controls.SetChildIndex(this.dtpEndDate, 0);
         this.Controls.SetChildIndex(this.cbConfig, 0);
         this.Controls.SetChildIndex(this.panel1, 0);
         this.Controls.SetChildIndex(this.linkLabel1, 0);
         this.Controls.SetChildIndex(this.lbVersion, 0);
         ((System.ComponentModel.ISupportInitialize)(this.tgvAgentsSummary)).EndInit();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }
*/
   }
}
