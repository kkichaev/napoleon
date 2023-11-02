using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmMatrixColor : Form
   {
      private DataSet<int, Matrix> dsMatrix;
      private ColorMenu menu;

      public FmMatrixColor(SysColors colors)
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME) ?? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true);
         menu = new ColorMenu(colors);
         menu.SelectColor += new SelectColorHandler(menu_SelectColor);
      }

      private void FmMatrixColor_Load(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void RefreshData()
      {
         dsMatrix.Filter = DataUtils.USERID_IS_NULL_STR;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsMatrix);

         FmWait.StdDataRefresh(this, upd, DocLoadData);
      }

      private void DocLoadData()
      {
         List<Matrix> data = new List<Matrix>();
         data.AddRange(dsMatrix.Values);
         data.Sort((x, y) => { return x.name.CompareTo(y.name); });

         grid.DataSource = data;
      }

      private void grid_MouseDown(object sender, MouseEventArgs e)
      {
         if(e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo info = ((DataGridView)sender).HitTest(e.X, e.Y);

            if (info.RowIndex != -1) 
            {
               ((DataGridView)sender).CurrentCell = grid[0, info.RowIndex];
               menu.RefreshItems();
               menu.Show((Control)sender, e.Location, ToolStripDropDownDirection.BelowRight);
            }
         }
      }

      void menu_SelectColor(object[] args)
      {
         if (args.Length == 0)
            return;

         Color clr = (Color)args[0];
         Matrix m = grid.CurrentRow.DataBoundItem as Matrix;

         if (m != null)
         {
            int rgbColor = clr.ToArgb() & 0xFFFFFF; // remove alpha chanel
            
            if ((m.Color.ToArgb() & 0xFFFFFF) != rgbColor)
            {
               m.Color = clr;
               List<ReplacedSet> list = new List<ReplacedSet>();
               ReplacedSet rs = new ReplacedSet(null, dsMatrix);
               list.Add(rs);
               DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection());
            }
         }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Matrix m = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Matrix;

         if (m != null)
            e.CellStyle.ForeColor = m.Color;
         else
            e.CellStyle.ForeColor = Color.Black;
      }
   }
}
