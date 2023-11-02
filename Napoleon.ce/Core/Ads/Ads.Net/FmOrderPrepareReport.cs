using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Ads.Utils;
using GRSoft.Ads.Report;
using System.Collections;

namespace GRSoft.Ads
{
   public partial class FmOrderPrepareReport : Form
   {
      private SettingFmOrderPrepareReport setting;
      private List<IOrderItems> orders = new List<IOrderItems>(); 

      public FmOrderPrepareReport(List<OrderRcv> orders)
      {
         InitializeComponent();
         setting = BaseFormSetting<SettingFmOrderPrepareReport>.Load();

         foreach (OrderRcv o in orders)
         {
            if (o is IOrderItems)
               this.orders.Add(o as IOrderItems);
         }
      }

      [Serializable]
      private class SettingFmOrderPrepareReport : BaseFormSetting<SettingFmOrderPrepareReport>
      {
         public bool number = true;
         public bool brigade = true;
         public bool date = true;
         public bool dateplan = true;
         public bool datefact = true;
         public bool text = true;
         public bool client = true;
         public bool address = true;
         public bool report = true;
         public bool name = true;
         public bool qty = true;
         public bool cost = true;
         public bool items = true;
      }

      private void FmOrderPrepareReport_Load(object sender, EventArgs e)
      {
         cbNumber.Checked = setting.number;
         cbBrigade.Checked = setting.brigade;
         cbDate.Checked = setting.date;
         cbDatePlan.Checked = setting.dateplan;
         cbDateFact.Checked = setting.datefact;
         cbText.Checked = setting.text;
         cbClient.Checked = setting.client;
         cbAddress.Checked = setting.address;
         cbReport.Checked = setting.report;
         cbName.Checked = setting.name;
         cbQty.Checked = setting.qty;
         cbCost.Checked = setting.cost;
         cbItems.Checked = setting.items;

         gbDetailData.Enabled = cbItems.Checked;
      }

      private void FmOrderPrepareReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.number = cbNumber.Checked;
         setting.brigade = cbBrigade.Checked;
         setting.date = cbDate.Checked;
         setting.dateplan = cbDatePlan.Checked;
         setting.datefact = cbDateFact.Checked;
         setting.text = cbText.Checked;
         setting.client = cbClient.Checked;
         setting.address = cbAddress.Checked;
         setting.report = cbReport.Checked;
         setting.name = cbName.Checked;
         setting.qty = cbQty.Checked;
         setting.cost = cbCost.Checked;
         setting.items = cbItems.Checked;
             
         setting.Save();
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         List<string> mainDataColumns = new List<string>();
         List<string> detailDataColumns = new List<string>();

         FillListFromGB(gbMainData, mainDataColumns);

         if (cbItems.Checked)
            FillListFromGB(gbDetailData, detailDataColumns);

         new OrderReport().build(mainDataColumns.ToArray(),
            detailDataColumns.ToArray(), orders);

      }

      private void FillListFromGB(GroupBox gb, List<string> columns)
      {
         List<Control> controls = new List<Control>();

         foreach (Control c in gb.Controls)
         {
            if (c is CheckBox)
            {
               if ((c as CheckBox).Checked)
                  controls.Add(c);
            }
         }

         controls.Sort(
            new Comparison<Control>(
               delegate(Control c1, Control c2) 
               { 
                  return c1.TabIndex.CompareTo(c2.TabIndex); 
               }));

         foreach (Control c in controls)
            columns.Add(c.Tag.ToString());
      }

      private void cbItems_CheckedChanged(object sender, EventArgs e)
      {
         gbDetailData.Enabled = ((CheckBox)sender).Checked;
      }

      private void btnClose_Click(object sender, EventArgs e)
      {
         Close();
      }
   }
}
