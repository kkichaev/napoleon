/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Отчет по заявкам
 * 
 * kki   21/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Html;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrdersReportEx : FmOrdersReport
   {
      //SimpleDataSet<OrderAddConfig> firms = new SimpleDataSet<OrderAddConfig>("Firms", false);
      ComboBox cbFirms = new ComboBox();
      CheckBox cbTotal = new CheckBox();
      ComboBox cbThermal = new ComboBox();

      protected FmOrdersReportEx()
      {
         this.ClientSize = new System.Drawing.Size(321, 281 + 25);

         this.label2.Location = new System.Drawing.Point(27, 97 + 25);
         this.dtpBegin.Location = new System.Drawing.Point(123, 93 + 25);
         this.dtpEnd.Location = new System.Drawing.Point(123, 119 + 25);
         this.cbPeriod.Location = new System.Drawing.Point(12, 122 + 25);
         this.btnHtml.Location = new System.Drawing.Point(57, 254 + 25);
         this.btnExcelReport.Location = new System.Drawing.Point(172, 254 + 25);
         this.groupBox1.Location = new System.Drawing.Point(12, 197 + 25);
         this.groupBox2.Location = new System.Drawing.Point(12, 146 + 25);
         
         this.cbPackets.Location = new Point(cbPiece.Right + 7, cbPackets.Top);

         cbTotal.Location = new Point(cbPackets.Right + 7, cbPackets.Top);
         cbTotal.Name = "cbTotal";
         cbTotal.Text = "Только итоги";
         cbTotal.Size = new Size(94, 17);
         groupBox1.Controls.Add(cbTotal);

         System.Windows.Forms.Label lFirm = new System.Windows.Forms.Label();

         lFirm.AutoSize = true;
         lFirm.Location = new System.Drawing.Point(27, 69);
         lFirm.Name = "lFirm";
         lFirm.Size = new System.Drawing.Size(74, 13);
         lFirm.TabIndex = 27;
         lFirm.Text = "Организация";

         this.cbFirms.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFirms.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbFirms.FormattingEnabled = true;
         this.cbFirms.Location = new System.Drawing.Point(123, 66);
         this.cbFirms.Name = "cbFirms";
         this.cbFirms.Size = new System.Drawing.Size(166, 21);
         this.cbFirms.TabIndex = 28;

         this.Controls.Add(this.cbFirms);
         this.Controls.Add(lFirm);

         System.Windows.Forms.Label lTh = new System.Windows.Forms.Label();
         lTh.AutoSize = true;
         lTh.Location = new System.Drawing.Point(27, 95);
         lTh.Name = "lTh";
         lTh.Size = new System.Drawing.Size(74, 13);
         lTh.TabIndex = 27;
         lTh.Text = "Состояние";

         this.cbThermal.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbThermal.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbThermal.FormattingEnabled = true;
         this.cbThermal.Location = new System.Drawing.Point(123, 92);
         this.cbThermal.Name = "cbThermal";
         this.cbThermal.Size = new System.Drawing.Size(166, 21);
         this.cbThermal.TabIndex = 29;
         cbThermal.Items.AddRange(new object[] { "<Все>", "Охл", "Зам" });
         cbThermal.SelectedIndex = 0;

         this.Controls.Add(this.cbThermal);
         this.Controls.Add(lTh);
      }

      override protected void AddUpdatableSets(List<IDataSet> updSets)
      {
         base.AddUpdatableSets(updSets);
         IDataSet firms = DataModule.Get(Factory.OBJECT_NAME);
         if (firms != null)
            updSets.Add(firms);
      }

      protected override void FillControls()
      {
         cbFirms.Items.Clear();
         cbFirms.SelectedIndex = cbFirms.Items.Add("Все");

         Factory.GetFactories().ForEach((x) =>
         {
            cbFirms.Items.Add(x);
         });

         base.FillControls();
      }

      override protected string GetOrderFilter()
      {
         string initFilter = base.GetOrderFilter();
         if (0 != cbFirms.SelectedIndex)
         {
            Factory selectedFactory = cbFirms.SelectedItem as Factory;
            if (null != selectedFactory)
               initFilter += string.Format(" and \"firmCode\" = '{0}'", selectedFactory.id);
         }

         return initFilter;
      }

      protected override bool InSet(Price p)
      {
         int selIndex = cbThermal.SelectedIndex;
         if( selIndex == 0 )
            return base.InSet(p);

         return p.thermalState == cbThermal.SelectedItem as string;
      }

      protected override string MakeFilterStr()
      {
         String filter = base.MakeFilterStr();
         Factory selectedFactory = cbFirms.SelectedItem as Factory;
         if (selectedFactory != null)
            filter += ", фабрика: " + selectedFactory.name;
         if (cbThermal.SelectedIndex != 0)
            filter += ", терм.состояние: " + cbThermal.SelectedItem as string;
         return filter;
      }

      protected override bool OnlyTotalData()
      {
         return cbTotal.Checked;
      }
   }
}