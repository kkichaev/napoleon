using System.Collections.Generic;
using System.Windows.Forms;
using GRSoft.Network;
using System.Drawing;
using System.Runtime.InteropServices;
using System;
using System.Drawing.Drawing2D;

namespace GRSoft.NapoleonManager
{
   public partial class FmMatrixDesignerEx : FmMatrixDesigner
   {
      ListItemSource formats = new ListItemSource(ListItemSource.FORMAT_OBJECT);
      ComboBox cbName;

      

      public FmMatrixDesignerEx() :
         base()
      {
         int offset = tbMatrixName.Margin.Left + tbMatrixName.Margin.Right + tbMatrixName.Width;
         cbName = new ComboBox();
         cbName.Size = new System.Drawing.Size(tbMatrixName.Size.Width + 2, tbMatrixName.Size.Height);
         Controls.Add(cbName);
         cbName.Location = tbMatrixName.TextBox.Location;
         cbName.Location.Offset(-2, 4);

         cbName.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
         cbName.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.ListItems;
         cbName.FormattingEnabled = true;
         cbName.BringToFront();

         cbName.SelectedIndexChanged += CbName_SelectedIndexChanged;

         Padding p = tsbAdd.Margin;
         p.Left += offset;
         tsbAdd.Margin = p;

         tbMatrixName.Visible = false;
         tsbClearName.Visible = false;

         tvMatrix.CheckBoxes = true;

         tvMatrix.DrawMode = TreeViewDrawMode.OwnerDrawText;
         tvMatrix.DrawNode += tvMatrix_DrawNode;
         tvMatrix.AfterCheck += tvMatrix_AfterCheck;
      }

      void tvMatrix_AfterCheck(object sender, TreeViewEventArgs e)
      {
         controller.SetNoSaveStatus();         
      }

      void tvMatrix_DrawNode(object sender, DrawTreeNodeEventArgs e)
      {
         if (e.Node.Level == 1)
         {
            HideCheckBox(e.Node);
            e.DrawDefault = true;
         }
         else
         {
            e.Graphics.DrawString(e.Node.Text, e.Node.TreeView.Font,
               Brushes.Black, e.Node.Bounds.X, e.Node.Bounds.Y);
         }
      }

      // constants used to hide a checkbox
      public const int TVIF_STATE = 0x8;
      public const int TVIS_STATEIMAGEMASK = 0xF000;
      public const int TV_FIRST = 0x1100;
      public const int TVM_SETITEM = TV_FIRST + 63;

      [DllImport("user32.dll")]
      static extern IntPtr SendMessage(IntPtr hWnd, uint Msg, IntPtr wParam,
      IntPtr lParam);

      // struct used to set node properties
      public struct TVITEM
      {
         public int mask;
         public IntPtr hItem;
         public int state;
         public int stateMask;
         [MarshalAs(UnmanagedType.LPTStr)]
         public String lpszText;
         public int cchTextMax;
         public int iImage;
         public int iSelectedImage;
         public int cChildren;
         public IntPtr lParam;

      } 

      private void HideCheckBox(TreeNode node)
      {
         TVITEM tvi = new TVITEM();
         tvi.hItem = node.Handle;
         tvi.mask = TVIF_STATE;
         tvi.stateMask = TVIS_STATEIMAGEMASK;
         tvi.state = 0;
         IntPtr lparam = Marshal.AllocHGlobal(Marshal.SizeOf(tvi));
         Marshal.StructureToPtr(tvi, lparam, false);
         SendMessage(node.TreeView.Handle, TVM_SETITEM, IntPtr.Zero, lparam);
      }

      //protected override void BeforeMatrixAdded(Matrix matrix, TreeNode matrixNode)
      //{
      //   int idx = 1;
      //   foreach(MatrixItem mi in matrix.items)
      //   {
      //      mi.order = idx++;
      //   }
      //}

      private void CbName_SelectedIndexChanged(object sender, System.EventArgs e)
      {
         controller.SetNoSaveStatus();
      }

      protected override void SetMatrixName(string text)
      {
         base.SetMatrixName(text);
         cbName.Text = text;
      }

      protected override string EditMatrixName(string val)
      {
         return cbName.Text.Trim();
      }

      protected override void PullRefreshList(List<IDataSet> list, bool reload)
      {
         base.PullRefreshList(list, reload);
         list.Add(formats);
         dsPrice.Filter = "";
         dsMatrix.Filter = "";
      }

      protected override bool CheckMatrix(Matrix mtx)
      {
         mtx.items.Sort((x, y) =>
         {
            return x.order - y.order;
         });
         return base.CheckMatrix(mtx);
      }

      protected override void ControlsFillAfterLoaded()
      {
         base.ControlsFillAfterLoaded();

         cbName.Items.AddRange(formats.Items);
      }

      public override void InitMatrix(Matrix m, TreeNode node)
      {
         base.InitMatrix(m, node);

         m.priority = node.Checked ? 1 : 0;
      }

      public override void InitNode(TreeNode n, Matrix m)
      {
         base.InitNode(n, m);

         n.Checked = m.priority == 1;
      }

      protected override bool SaveData()
      {
         foreach(Matrix m in dsMatrix.Values)
         {
            int idx = 1;
            foreach (MatrixItem mi in m.items)
            {
               mi.order = idx++;
            }
         }

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsMatrix);


         return DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection());
      }

      protected override void RemoveMatrix(Matrix mtx)
      {
         foreach (int k in dsMatrix.Keys)
         {
            Matrix m = dsMatrix[k];
            if (m.name.Equals(mtx.name))
            {
               m.rem = 1;
               break;
            }
         }
      }

      protected override void RenameMatrix(Matrix m, string name)
      {
         Matrix copy = (Matrix)m.Clone();
         m.rem = 1;
         copy.name = name;
         dsMatrix.Add(dsMatrix.Count, copy);

      }

      protected override void RemoveMatrixItem(MatrixItem mi)
      {
         TreeNode node = tvMatrix.SelectedNode;

         Matrix m = node.Parent.Tag as Matrix;

         if(m != null)
            m.items.Remove(mi);
      }

   }
}