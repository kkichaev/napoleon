using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEditAgentLimit : Form
   {
      ReturnLimit limit;
      SimpleDataSet<ReturnLimit> dsLimits;

      public FmEditAgentLimit()
      {
         InitializeComponent();
      }

      void LoadPriceTypes(DataSet<string, PriceType> types)
      {
         cbPriceType.Items.Clear();
         foreach (PriceType pt in types.Data)
            cbPriceType.Items.Add(pt);
      }

      ReturnLimit Limit
      {
         set
         {
            limit = value;
            dpvDates.Start = limit.start;
            dpvDates.Finish = limit.end;

            cbPriceType.SelectedItem = limit.type;
            cbCanOverLimit.Checked = limit.CanOverLimit;

            if (limit.limitType == ReturnLimit.LIMIT_SUM)
            {
               rbLimitSum.Checked = true;
               tbLimitSum.Text = limit.limit.ToString();
            } else
            {
               rbLimitWeight.Checked = true;
               tbLimitWeight.Text = limit.limit.ToString();
            }
         }
      }

      public bool IsLimitCorrected(ReturnLimit ri, out string message)
      {
         if (ri.end < ri.start)
         {
            message = "конечная дата меньше начальной";
            return false;
         }

         if (ri.end < DateTime.Now.Date)
         {
            message = "дата окончания меньше текущей";
            return false;
         }

         bool isCurrentLimit = ri.start <= DateTime.Now && ri.end >= DateTime.Now;

         foreach (ReturnLimit src in dsLimits.Data)
         {
            if (src.userid != ri.userid || src.priceType != ri.priceType)
               continue;

            if (src.start == ri.start)
            {
               if (isCurrentLimit && (src.limitType != ri.limitType || src.limit > ri.limit))
               {
                  message = "в текущем лимите можно только изменить значение в большую сторону";
                  return false;
               }
            } else if (src.start <= ri.end && src.end >= ri.start)
            {
               message = "введенные данные пересекаются с другим лимитом от " + src.start.ToShortDateString();
               return false;
            }
         }

         message = "";
         return true;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (DialogResult == System.Windows.Forms.DialogResult.OK)
         {
            if(cbPriceType.SelectedItem == null)
            {
               MessageBox.Show("Выберите тип продукции");
               e.Cancel = true;
               return;
            }

            ReturnLimit ri = new ReturnLimit();
            ri.userid = limit.userid;

            ri.start = dpvDates.Start;
            ri.end = dpvDates.Finish;
            int value = 0;
            if (rbLimitSum.Checked)
            {
               Int32.TryParse(tbLimitSum.Text, out value);
               ri.limitType = ReturnLimit.LIMIT_SUM;
            }
            else
            {
               Int32.TryParse(tbLimitWeight.Text, out value);
               ri.limitType = ReturnLimit.LIMIT_WEIGHT;
            }
            ri.limit = value;
            ri.type = cbPriceType.SelectedItem as PriceType;
            ri.priceType = ri.type.id;
            ri.CanOverLimit = cbCanOverLimit.Checked;

            string message;
            if( !IsLimitCorrected(ri, out message) )
            {
               MessageBox.Show("Обнаружена ошибка:\n" + message, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               e.Cancel = true;
            }

            limit.SetFrom(ri);
         }
         base.OnClosing(e);
      }

      private void rbLimitWeight_CheckedChanged(object sender, EventArgs e)
      {
         UpdateControls(rbLimitWeight.Checked);
      }

      public static bool EditLimit(ReturnLimit limit, DataSet<string, PriceType> types, SimpleDataSet<ReturnLimit> dsLimits)
      {
         FmEditAgentLimit form = new FmEditAgentLimit();

         form.dsLimits = dsLimits;
         form.LoadPriceTypes(types);
         form.Limit = limit;

         return form.ShowDialog() == DialogResult.OK;
      }

      private void rbLimitSum_CheckedChanged(object sender, EventArgs e)
      {
         UpdateControls(!rbLimitSum.Checked);
      }

      void UpdateControls(bool inWeight)
      {
         tbLimitSum.Enabled = !inWeight;
         tbLimitWeight.Enabled = inWeight;
         if (inWeight)
            tbLimitWeight.Focus();
         else
            tbLimitSum.Focus();
      }
   }
}
