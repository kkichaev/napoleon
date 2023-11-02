using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FacingControl : UserControl, DataObjectViewer
   {
      public FacingControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         Facing f = dataObject as Facing;

         if (f != null)
         {
            textBox1.Text = FmDetailEx.FacingToString(f);
         }
      }
   }
}
