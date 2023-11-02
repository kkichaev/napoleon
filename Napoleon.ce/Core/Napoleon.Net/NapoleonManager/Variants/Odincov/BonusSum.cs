using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class BonusSum : UserControl, IBonus
   {
      public static readonly int CODE = 1;
      public event EventHandler ValueChanged;

      public BonusSum()
      {
         InitializeComponent();
      }

      public bool save(BonusDef bonus)
      {
         double sum = 0.0;
         bool result = Double.TryParse(tbSum.Text.Trim(), out sum) && sum > 0;

         if (result)
         {
            bonus.sum = sum;
            bonus.type = CODE;
         }

         return result;
      }

      public void load(BonusDef curBonus)
      {
         tbSum.Text = curBonus.sum.ToString();   
      }

      private void tbSum_TextChanged(object sender, EventArgs e)
      {
         fireValueChanged();
      }

      private void fireValueChanged()
      {
         if (ValueChanged != null)
            ValueChanged(this, EventArgs.Empty);
      }
   }
}
