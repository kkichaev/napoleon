using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;
using System.Reflection.Emit;
using System.Reflection;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public class FmMatrixDesignerEx : FmMatrixDesigner
   {
      ContextMenuStrip menu;
      ToolStripMenuItem mi = new ToolStripMenuItem();
      Font boldFont;

      public FmMatrixDesignerEx()
         : base()
      {
         menu = new ContextMenuStrip();
         mi.Name = "miSetCommon";
         mi.Size = new System.Drawing.Size(179, 22);
         mi.Text = "Сделать универсальной";
         mi.Click += new EventHandler(mi_Click);
         menu.Opening += new CancelEventHandler(menu_Opening);
         menu.Items.Add(mi);

         tvMatrix.ContextMenuStrip = menu;
      }

      void menu_Opening(object sender, CancelEventArgs e)
      {
         TreeNode selected = tvMatrix.SelectedNode;
         bool enabled = false;
         String text = "Сделать универсальной";
         if (selected != null)
         {
            Matrix m = selected.Tag as Matrix;
            if (m != null)
            {
               enabled = true;
               if( m.common != 0 )
                  text = "Сделать обычной";
            }
         }
         mi.Enabled = enabled;
         mi.Text = text;
      }

      void mi_Click(object sender, EventArgs e)
      {
         TreeNode selected = tvMatrix.SelectedNode;
         if (selected != null)
         {
            Matrix m = selected.Tag as Matrix;
            if (m.common != 0)
            {
               m.common = 0;
               selected = null;
            }
         }
         RefreshMatrix(selected);
         controller.SetNoSaveStatus();
      }

      protected override void BeforeMatrixAdded(Matrix matrix, TreeNode matrixNode)
      {
         Matrix src = matrixNode.Tag as Matrix;
         matrix.common = (src != null && src.common != 0) ? 1 : 0;
      }

      void RefreshMatrix(TreeNode selected)
      {
         foreach (TreeNode tn in tvMatrix.Nodes)
         {
            Matrix m = tn.Tag as Matrix;
            if (m != null)
            {
               if( selected != null )
                  m.common = (tn == selected) ? 1 : 0;

               Font f = tvMatrix.Font;
               if (m.common != 0)
               {
                  if (boldFont == null)
                     boldFont = new Font(tvMatrix.Font, FontStyle.Bold);
                  f = boldFont;
               }
               tn.NodeFont = f;
               tn.Text += string.Empty;
            }
         }
      }

      protected override void FillMatrixEnded()
      {
         RefreshMatrix(null);
      }
   }
}