using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class InputQty : Form
   {
      double qtyVal;

      public InputQty()
      {
         InitializeComponent();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         qtyVal = (double)nmQty.Value;
         base.OnClosing(e);
      }

      public int MaxValue
      {
         get { return (int)nmQty.Maximum; }
         set { nmQty.Maximum = (decimal)value; }
      }

      public double Qty
      {
         get
         {
            return qtyVal;
         }

         set
         {
            qtyVal = value;
            nmQty.Value = (decimal)((qtyVal < 0) ? qtyVal - 0.0005 : qtyVal + 0.0005);
            nmQty.Select(0, ((UpDownBase)nmQty).Text.Length);
         }
      }

      private void InputQty_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Escape)
            DialogResult = DialogResult.Cancel;
         else if (e.KeyCode == Keys.Return)
            DialogResult = DialogResult.OK;
      }
   }
}
