using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Windows.Forms.VisualStyles;

namespace GRSoft.NapoleonManager
{
   public partial class CopyMatrixTo : Form
   {
      List<Org> selected = new List<Org>();
      DataSet<string, DistributionMatrix> dsDistrib = null;
      MemoryBox mbr = new MemoryBox();

      public CopyMatrixTo()
      {
         InitializeComponent();

         dgvOrgs.AutoGenerateColumns = false;
      }

      void Init(DataSet<string, Org> dsOrgs, Org copyFrom, DataSet<string, DistributionMatrix> dsDistrib)
      {
         List<OrgSelectItem> items = new List<OrgSelectItem>();
         foreach (Org o in dsOrgs.Data)
         {
            if (o != copyFrom && o != null)
               items.Add(new OrgSelectItem(o));
         }
         items.Sort();
         dgvOrgs.DataSource = items;


         this.dsDistrib = dsDistrib;
      }

      internal static List<Org> SelectedOrgsToCopy(DataSet<string, Org> dsOrgs, Org copyFrom, DataSet<string, DistributionMatrix> dsDistrib)
      {
         CopyMatrixTo frm = new CopyMatrixTo();
         frm.Init(dsOrgs, copyFrom, dsDistrib);
         frm.ShowDialog();

         return frm.selected;
      }

      private void tsbCancel_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.Cancel;
      }

      private void tsbOK_Click(object sender, EventArgs e)
      {
         foreach (DataGridViewRow row in dgvOrgs.Rows)
         {
            OrgSelectItem osi = row.DataBoundItem as OrgSelectItem;
            if( osi.Checked )
               selected.Add(osi.o);
         }

         DialogResult = DialogResult.OK;
      }

      private void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvOrgs.CurrentCell.ColumnIndex == clmnChecked.DisplayIndex)
         {
            if (dgvOrgs.IsCurrentCellDirty)
            {
               OrgSelectItem osi = dgvOrgs.CurrentRow.DataBoundItem as OrgSelectItem;
               if (dsDistrib.ContainsKey(osi.o.id) && osi.Checked == false)
               {
                  MemoryBoxResult mr = mbr.ShowMemoryDialog("Заменить матрицу дистрибуции?", "Вопрос");
                  if (mr == MemoryBoxResult.No || mr == MemoryBoxResult.NoToAll)
                  {
                     dgvOrgs.CancelEdit();
                     return;
                  }
               }
               dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
            }
         }
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         OrgSelectItem o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as OrgSelectItem;
         if (dsDistrib.ContainsKey(o.o .id))
            e.CellStyle.BackColor = Color.LightGray;
      }

      private void CopyMatrixTo_KeyPress(object sender, KeyPressEventArgs e)
      {
         if (e.KeyChar == (char)Keys.Escape)
            DialogResult = DialogResult.Cancel;
      }

      private void tsbSelectAllToolStripMenuItem_Click(object sender, EventArgs e)
      {
         foreach (OrgSelectItem osi in dgvOrgs.DataSource as List<OrgSelectItem>)
            osi.Checked = true;
         dgvOrgs.CurrentCell = null;
         dgvOrgs.Invalidate();
      }

      private void tsbUnselectAllToolStripMenuItem_Click(object sender, EventArgs e)
      {
         foreach (OrgSelectItem osi in dgvOrgs.DataSource as List<OrgSelectItem>)
            osi.Checked = false;
         dgvOrgs.CurrentCell = null;
         dgvOrgs.Invalidate();
      }

      private void dgvOrgs_CellValidating(object sender, DataGridViewCellValidatingEventArgs e)
      {
         //if (e.ColumnIndex == clmnChecked.DisplayIndex && Visible)
         //{
         //   OrgSelectItem osi = dgvOrgs.Rows[e.RowIndex].DataBoundItem as OrgSelectItem;
         //   if (dsDistrib.ContainsKey(osi.o.id) && ((bool)e.FormattedValue) == true)
         //   {
         //      MemoryBoxResult mr = mbr.ShowMemoryDialog("Заменить матрицу дистрибуции?", "Вопрос");
         //      if (mr == MemoryBoxResult.No || mr == MemoryBoxResult.NoToAll)
         //         e.Cancel = true;
         //   }
         //}
      }
   }

   class OrgSelectItem : IComparable<OrgSelectItem>
   {
      public bool check;
      public Org o;

      public OrgSelectItem(Org o) { this.o = o; }
      public bool Checked
      {
         get { return check; } 
         set { check = value; }
      }

      public string Name { get { return o.name; } }

      #region IComparable<OrgSelectItem> Members

      public int CompareTo(OrgSelectItem other)
      {
         return Name.CompareTo(other.Name);
      }

      #endregion
   }
}
