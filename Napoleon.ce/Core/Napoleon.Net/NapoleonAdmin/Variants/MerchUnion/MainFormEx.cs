using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataSet<string, Supplier> dsSuppliers = new DataSet<string,Supplier>(Supplier.OBJECT_NAME);
      DataGridViewComboBoxColumn dgvSuppl = new DataGridViewComboBoxColumn();
      List<Supplier> supplItems;

      public MainFormEx()
      {
         dgvSuppl.DataPropertyName = "Supplier";
         dgvSuppl.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         dgvSuppl.FillWeight = 594.0206F;
         dgvSuppl.HeaderText = "Заказчик";
         dgvSuppl.Name = "suppl";
         dgvSuppl.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         dgvSuppl.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         dgvSuppl.Width = 200;

         int idx = usersView.Columns.IndexOf(registred);
         usersView.Columns.Insert(idx, dgvSuppl);
/*
         string[] right = {"EditRouteRight", "EnterToDivision"};
         string[] text = {"Сохранять маршрут", "Просмотр подразделения"};
         InitRightColumns(right, text);

         Width += 40;
*/
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);
         dgvSuppl.Visible = !agentView;
      }

      protected override void AddUpdDataSet(System.Collections.Generic.List<IDataSet> upd)
      {
         upd.Add(dsSuppliers);
         dgvSuppl.Items.Clear();
      }

      protected override void usersView_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if(e.ColumnIndex == dgvSuppl.Index)
         {
            UserDataItem di = usersView.Rows[e.RowIndex].DataBoundItem as UserDataItem;
            DataGridViewComboBoxCell cell = usersView.Rows[e.RowIndex].Cells[e.ColumnIndex] as DataGridViewComboBoxCell;
            cell.Items.Clear();
            supplItems.ForEach(x => cell.Items.Add(x));
         } else
            base.usersView_CellEnter(sender, e);
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (usersView.CurrentCell.ColumnIndex == dgvSuppl.Index)
         {
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
         } else
            base.usersView_CurrentCellDirtyStateChanged(sender, e);
      }

      protected override void RefreshUserData()
      {
         if (dgvSuppl.Items.Count == 0)
         {
            supplItems = new List<Supplier>();
            foreach (Supplier i in dsSuppliers.Data)
               supplItems.Add(i);
            supplItems.Sort();
            Supplier s = new Supplier();
            supplItems.Insert(0, s);
            supplItems.ForEach(x => dgvSuppl.Items.Add(x));
            dgvSuppl.DisplayMember = "Name";
            dgvSuppl.ValueMember = "Id";
         }
         base.RefreshUserData();
      }
   }
}