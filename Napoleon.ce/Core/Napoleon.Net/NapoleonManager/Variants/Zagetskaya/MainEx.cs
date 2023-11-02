using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainEx : MainForm
   {
      protected DataSet<int, Layout> dsLayout;

      public MainEx()
      {
         dsLayout = (DataSet<int, Layout>)DataModule.Get(GRSoft.NapoleonManager.Layout.OBJECT_NAME) ?? new DataSet<int, Layout>(GRSoft.NapoleonManager.Layout.OBJECT_NAME);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsLayout.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsLayout);
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
