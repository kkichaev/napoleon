using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Globalization;

namespace GRSoft.NapoleonManager
{
   public partial class InputCoef : Form
   {
      double coef = 1.5;

      public InputCoef()
      {
         InitializeComponent();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);

         double newCoef = coef;

         if (Double.TryParse(tbCoef.Text.Replace(',', '.'), NumberStyles.Number, System.Globalization.CultureInfo.InvariantCulture, out newCoef))
            coef = newCoef;
      }

      public double Coef
      {
         set
         {
            coef = value;
            tbCoef.Text = value.ToString("N2");
            tbCoef.SelectAll();
         }

         get
         {
            return coef;
         }
      }
   }
}
