using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripComboBox tsbDocuments;

      public MainFormEx()
      {
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.tsbDocuments = new System.Windows.Forms.ToolStripComboBox();

         this.tsbConfig.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel3,
            this.tsbDocuments});

         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(70, 22);
         this.toolStripLabel3.Text = "Фильтр документов";
         // 
         // tsbDocuments
         // 
         this.tsbDocuments.Name = "tsbDocuments";
         this.tsbDocuments.Size = new System.Drawing.Size(121, 25);

         tsbDocuments.Items.AddRange(new string[] { 
            "<Все>",
            "Заказы",
            "Продажи",
            "ПКО",
         });
         tsbDocuments.SelectedIndex = 0;
         tsbDocuments.SelectedIndexChanged += tsbDocuments_SelectedIndexChanged;

         tgvAgentsSummaryCount.HeaderText = "Документы";
      }

      protected override void AfterRefreshData()
      {
         Invoke((EmptyParamHandler)SetDocumentsIndex);
      }

      void SetDocumentsIndex() { tsbDocuments.SelectedIndex = 0; }

      void tsbDocuments_SelectedIndexChanged(object sender, EventArgs e)
      {
         IDataSet[] sets = new IDataSet[] { dsOrder, dsSales, dsPKO, dsIncass };
         foreach (IDataSet ds in sets)
            DataModule.Remove(ds.Name);

         int selIndex = tsbDocuments.SelectedIndex;
         if( selIndex == 0 )
         {
            foreach(IDataSet ds in sets)
               DataModule.AddDataSet(ds);
         } else
         {
            switch(tsbDocuments.SelectedItem as string)
            {
               case "Заказы":
                  DataModule.AddDataSet(dsOrder);
                  break;
               case "Продажи":
                  DataModule.AddDataSet(dsSales);
                  break;
               case "ПКО":
                  DataModule.AddDataSet(dsIncass);
                  DataModule.AddDataSet(dsPKO);
                  break;
            }
         }
         LoadTgvAgentSummary();
      }
   }
}