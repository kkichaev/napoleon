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
   public partial class FmAutoCoef : Form
   {
      public FmAutoCoef()
      {
         InitializeComponent();
      }

      public double Coef
      {
         get
         {
            double cvalue;
            try
            {
               string v = this.value.Text;
               v = v.Replace('.', ',');
               cvalue = Double.Parse(v, Config.GetCultureInfo());
            }
            catch
            {
               cvalue = 1.5;
            }
            return cvalue; 
         }
         set 
         {
            this.value.Text = value.ToString(Config.GetCultureInfo());
         }
      }
   }
}
