using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgMatrixEdit : Form
   {
      OrgMatrix matrix;
      List<ContractDef> contracts;
      List<Matrix> matrixes;
      
      public FmOrgMatrixEdit()
      {
         InitializeComponent();
      }

      public static bool Edit(OrgMatrix matrix, List<ContractDef> contracts, List<Matrix> matrixes)
      {
         FmOrgMatrixEdit f = new FmOrgMatrixEdit();
         
         f.matrix = matrix;
         f.contracts = contracts;
         f.matrixes = matrixes;
         
         ContractDef sel = null;
         foreach (ContractDef cd in contracts)
         {
            f.cbContract.Items.Add(cd);
            if (matrix.cdef == cd.id)
               sel = cd;
         }
         f.cbContract.SelectedItem = sel;

         return f.ShowDialog() == DialogResult.OK;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if(DialogResult == System.Windows.Forms.DialogResult.OK)
         {
            if(cbMatrix.SelectedItem == null)
            {
               cbMatrix.Focus();
               e.Cancel = true;
            } else if( cbContract.SelectedItem == null)
            {
               cbContract.Focus();
               e.Cancel = true;
            }

            if( !e.Cancel)
            {
               matrix.name = (cbMatrix.SelectedItem as Matrix).name;
               matrix.contract = cbContract.SelectedItem as ContractDef;
               matrix.cdef = matrix.contract.id;
            }
         }
      }

      private void cbContract_SelectedIndexChanged(object sender, EventArgs e)
      {
         Matrix sel = null;
         foreach(Matrix m in matrixes)
         {
            if (matrix.name == m.name)
               sel = m;
            cbMatrix.Items.Add(m);
         }
         cbMatrix.SelectedItem = sel;
      }
   }
}
