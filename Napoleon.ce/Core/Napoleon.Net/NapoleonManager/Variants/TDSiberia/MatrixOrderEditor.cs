using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class MatrixOrderEditor : UserControl
   {
      public MatrixOrderEditor()
      {
         InitializeComponent();
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         TreeNode sel = tvAgentMatrix.SelectedNode;
         if( sel == null )
            return;
         Matrix m = sel.Tag as Matrix;
         if (m == null)
         {
            sel = sel.Parent;
            if (sel != null)
               m = sel.Tag as Matrix;
         }

         if (m == null)
            return;

         MatrixOrder.Item mi = new MatrixOrder.Item();
         mi.name = m.name;
         mi.order = lvOrderedMatrix.Items.Count;

         ListViewItem lvi = new ListViewItem(mi.name);
         lvi.Tag = mi;
         lvOrderedMatrix.Items.Add(lvi);
         if (DataChanged != null)
            DataChanged(this, EventArgs.Empty);

         UpdateButtonsState();
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         if (lvOrderedMatrix.SelectedItems.Count == 0)
            return;

         lvOrderedMatrix.Items.Remove(lvOrderedMatrix.SelectedItems[0]);
         if (DataChanged != null)
            DataChanged(this, EventArgs.Empty);
         UpdateButtonsState();
      }

      private void tsbMoveUp_Click(object sender, EventArgs e)
      {
         if (lvOrderedMatrix.SelectedItems.Count == 0)
            return;

         ListViewItem lvi = lvOrderedMatrix.SelectedItems[0];
         int index = lvi.Index;
         if (index == 0)
            return;

         lvOrderedMatrix.Items.Remove(lvi);
         lvOrderedMatrix.Items.Insert(index - 1, lvi);
         if (DataChanged != null)
            DataChanged(this, EventArgs.Empty);
         UpdateButtonsState();
      }

      private void tsbMoveDn_Click(object sender, EventArgs e)
      {
         if (lvOrderedMatrix.SelectedItems.Count == 0)
            return;

         ListViewItem lvi = lvOrderedMatrix.SelectedItems[0];
         int index = lvi.Index;
         if (index == lvOrderedMatrix.Items.Count - 1)
            return;

         lvOrderedMatrix.Items.Remove(lvi);
         lvOrderedMatrix.Items.Insert(index+1, lvi);
         if (DataChanged != null)
            DataChanged(this, EventArgs.Empty);
         UpdateButtonsState();
      }

      public event EventHandler DataChanged;


      public void UpdateButtonsState()
      {
         Invoke(new EmptyParamHandler(() =>
            {
               int selected = (lvOrderedMatrix.SelectedItems.Count > 0) ? lvOrderedMatrix.SelectedItems[0].Index : -1;

               tsbAdd.Enabled = tvAgentMatrix.SelectedNode != null;
               tsbRemove.Enabled = lvOrderedMatrix.SelectedItems.Count > 0 && lvOrderedMatrix.Items.Count > 0;
               tsbMoveDn.Enabled = selected >= 0 && selected < lvOrderedMatrix.Items.Count - 1;
               tsbMoveUp.Enabled = selected > 0;
            }));
      }

      private void tvAgentMatrix_AfterSelect(object sender, TreeViewEventArgs e)
      {
         UpdateButtonsState();
      }

      private void lvOrderedMatrix_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateButtonsState();
      }

      private void tvAgentMatrix_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
      {
         tsbAdd_Click(sender, e);
      }

      private void lvOrderedMatrix_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         Point pt = new Point(e.X, e.Y);
         //pt = PointToClient(pt);
         ListViewItem lv = lvOrderedMatrix.GetItemAt(pt.X, pt.Y);
         if (lv != null)
            tsbRemove_Click(sender, e);
      }
   }
}
