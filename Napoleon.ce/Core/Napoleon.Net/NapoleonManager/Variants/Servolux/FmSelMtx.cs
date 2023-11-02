using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSelMtx : Form
   {
      public FmSelMtx()
      {
         InitializeComponent();
      }

      public static Matrix SelMtx(List<Matrix> list)
      {
         Matrix result = null;
         FmSelMtx dlg = new FmSelMtx();

         list.Sort((x, y) => { return x.name.CompareTo(y.name); });

         foreach (Matrix m in list)
            dlg.listBox.Items.Add(m);

         if (dlg.ShowDialog() == DialogResult.OK)
            result = dlg.listBox.SelectedItem as Matrix;

         return result;
      }
   }
}
