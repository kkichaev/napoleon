using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgGoodsMatrixEdit : Form
   {
      public FmOrgGoodsMatrixEdit()
      {
         InitializeComponent();
      }

      public List<GoodsMatrix> Matrixs 
      { 
         set 
         {
            BindingList<GoodsMatrix> data = new BindingList<GoodsMatrix>();
            foreach (GoodsMatrix gm in value)
               data.Add(gm);

            grid.DataSource = data;
         }
      }

      public GoodsMatrix Selected { get { return grid.CurrentRow.DataBoundItem as GoodsMatrix; } }
   }
}
