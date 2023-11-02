using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public partial class SetContracts : Form
   {
      public SetContracts()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public List<ContractEx> Contracts { set { dgvItems.DataSource = value; } get { return dgvItems.DataSource as List<ContractEx>; } }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvItems.CurrentCell.ColumnIndex == clmnUsed.Index)
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }
   }

   public class ContractEx
   {
      Contracts contract;
      bool selected;

      public ContractEx(Contracts contract, bool selected)
      {
         this.contract = contract;
         this.selected = selected;
      }

      public bool Used { get { return selected; } set { selected = value; } }
      public string Contract { get { return String.Format("{0} c {1:dd.MM.yy} по {2:dd.MM.yy}", contract.name, contract.start, contract.finish); } }
      public string ID { get { return contract.id; } }
   }
}
