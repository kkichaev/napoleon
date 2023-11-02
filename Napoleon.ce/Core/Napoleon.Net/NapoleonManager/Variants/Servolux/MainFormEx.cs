using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.plan_editor;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "rttAgentPlans";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Планы";
         btn.Click += new System.EventHandler((o, e) => { DailyAgentPlans.Open(); });

         tsbConfig.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.remnants_doc;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "rttOrderChanges";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Подрезка";
         btn.Click += new System.EventHandler((o, e) =>
         {
            if (tgvAgentsSummary.CurrentRow != null)
            {
               Division d = tgvAgentsSummary.CurrentRow.Tag as Division;
               if (d != null)
                  FmChangeOrders.Open(d, GetBeginDateForSelection());
               else
               {
                  Agent a = tgvAgentsSummary.CurrentRow.Tag as Agent;
                  if (a != null)
                     FmChangeOrders.Open(a, GetBeginDateForSelection());
               }
            }
         });

         tsbConfig.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = Properties.Resources.return_doc;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "rttReqReturn";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Возвраты";
         btn.Click += new System.EventHandler((o, e) => { FmReturnRequestList.Open(); });

         tsbConfig.Items.Add(btn);


         //btn = new System.Windows.Forms.ToolStripButton();
         //btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //btn.Image = Properties.Resources.facing_doc;
         //btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         //btn.Name = "rttAgentReports";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Отчет по заявкам";
         //btn.Click += new System.EventHandler((o, e) => { new FmAgentOrderReport().Show(); });

         //tsbConfig.Items.Add(btn);


         smiRoute.Text = "Просмотр маршрута";
         DBConnection.TIMEOUT = 4 * 60 * 1000;
      }

      protected override void AddMainSets(List<IDataSet> upd)
      {
         DataSet<string, Factory> fc = (DataSet<string, Factory>)DataModule.Get(Factory.OBJECT_NAME) ?? new DataSet<string, Factory>(Factory.OBJECT_NAME);
         upd.Add(fc);
      }

      //private void InitializeComponent()
      //{
      //   ((System.ComponentModel.ISupportInitialize)(this.tgvAgentsSummary)).BeginInit();
      //   this.panel1.SuspendLayout();
      //   this.SuspendLayout();
      //   // 
      //   // dateWarningTooltip
      //   // 
      //   this.dateWarningTooltip.ClientSize = new System.Drawing.Size(250, 39);
      //   this.dateWarningTooltip.Location = new System.Drawing.Point(244, 12);
      //   // 
      //   // lbVersion
      //   // 
      //   this.lbVersion.Size = new System.Drawing.Size(245, 14);
      //   this.lbVersion.Text = "версия: 3.5.0.13 / 04.06.2019 проект: Наполеон";
      //   // 
      //   // cbConfig
      //   // 
      //   this.cbConfig.Size = new System.Drawing.Size(160, 22);
      //   // 
      //   // MainFormEx
      //   // 
      //   this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
      //   this.ClientSize = new System.Drawing.Size(998, 580);
      //   this.Name = "MainFormEx";
      //   this.Controls.SetChildIndex(this.dtpBeginDate, 0);
      //   this.Controls.SetChildIndex(this.dtpEndDate, 0);
      //   this.Controls.SetChildIndex(this.cbConfig, 0);
      //   this.Controls.SetChildIndex(this.panel1, 0);
      //   this.Controls.SetChildIndex(this.linkLabel1, 0);
      //   this.Controls.SetChildIndex(this.lbVersion, 0);
      //   ((System.ComponentModel.ISupportInitialize)(this.tgvAgentsSummary)).EndInit();
      //   this.panel1.ResumeLayout(false);
      //   this.ResumeLayout(false);
      //   this.PerformLayout();

      //}
   }

   class SummaryDataEx : SummaryData
   {
      bool inProgres = false;

      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> dsConfig)
         : base(agent, dsConfig)
      {
      }

      public override void CountProgress(DateTime start, DateTime end, DataSet<int, OrgFolder> dsOrgFolder)
      {
         inProgres = true;
         base.CountProgress(start, end, dsOrgFolder);
         inProgres = false;
      }

      public override List<OrgFolderItem> GetAgentRoute(DateTime date, System.Collections.ICollection dsOrgFolder)
      {
         List <OrgFolderItem>  ret = base.GetAgentRoute(date, dsOrgFolder);
         if( ret != null && inProgres)
         {
            List<OrgFolderItem> remove = new List<OrgFolderItem>();

            ret.ForEach(x =>
            {
               if (x.kind == "З")
                  remove.Add(x);
            });

            if (remove.Count > 0)
            {
               List<OrgFolderItem>  ret1 = new List<OrgFolderItem>(ret);
               remove.ForEach(x => ret1.Remove(x));
               return ret1;
            }
         }
         return ret;
      }
   }
}