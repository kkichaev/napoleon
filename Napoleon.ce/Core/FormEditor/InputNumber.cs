using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace NFormEditor
{
   public partial class InputNumber : Form
   {
      public InputNumber(int startValue)
      {
         InitializeComponent();
         value.Text = startValue.ToString();
      }

      public int Value 
      { 
         get 
         { 
            int val;
            bool v = Int32.TryParse(value.Text, out val);
            return (v) ? val : 0;
         }
      }
   }
}
