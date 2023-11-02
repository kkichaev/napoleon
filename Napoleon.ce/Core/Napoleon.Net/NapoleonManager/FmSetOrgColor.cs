using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmSetOrgColor : Form
   {
      private ColorMenu menu;
      List<Org> orgList = new List<Org>();
      private SearchEngine searchEngine;
      private DataSet<string, Org> corgs;
      DataSet<string, PotenzialOrg> dsPotenzailOrg;

      private int CompareOrg(Org l, Org r)
      {
         return l.Name.CompareTo(r.Name);
      }

      internal FmSetOrgColor(SysColors colors)
      {
         InitializeComponent();

         orgs.AutoGenerateColumns = false;

         menu = new ColorMenu(colors);
         menu.RefreshItems();
         menu.SelectColor += new SelectColorHandler(menu_SelectColor);

         colorFilter.Colors = colors;
         colorFilter.SelectColor += new TSSelectColorHandler(colorFilter_SelectColor);

         corgs = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsPotenzailOrg = (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME) ?? new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);

         searchEngine = new SearchEngine(new FindDataGridObject(orgs, 0)); 
      }

      public override void Refresh()
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(corgs);
         upd.Add(dsPotenzailOrg);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         orgList.Clear();

         foreach (Org org in corgs.Data)
            orgList.Add(org);

         foreach (Org po in dsPotenzailOrg.Data)
            orgList.Add(po);

         orgList.Sort(CompareOrg);
         orgs.DataSource = orgList;
      }

      void colorFilter_SelectColor(Color clr, int index)
      {
         if (index <= 0)
            orgs.DataSource = orgList;
         else
         {
            int c = clr.ToArgb();
            List<Org> ol = new List<Org>();
            foreach (Org o in orgList)
               if (o.Color.ToArgb() == c)
                  ol.Add(o);

            orgs.DataSource = ol;
         }
      }

      void menu_SelectColor(object [] args)
      {
         if (args.Length > 0)
         {
            Color clr = (Color)args[0];
            DataSet<int, SysColor> uc = new DataSet<int, SysColor>(SysColor.OBJECT_NAME, false);
            int rgbColor = clr.ToArgb() & 0xFFFFFF; // remove alpha chanel

            orgs.SuspendLayout();
            DataGridViewSelectedRowCollection sr = orgs.SelectedRows;
            foreach (DataGridViewRow row in sr)
            {
               Org o = row.DataBoundItem as Org;

               if ((o.Color.ToArgb() & 0xFFFFFF) != rgbColor)
               {
                  // идет преобразование цвета
                  o.Color = clr;

                  SysColor sysClr = new SysColor();
                  sysClr.id = o.id;
                  sysClr.type = (int)SysColor.Type.Org;
                  sysClr.face = o.color; // идет преобразование цвета

                  uc[uc.Count] = sysClr;

                  orgs.UpdateCellValue(0, row.Index);
               }
            }
            orgs.ResumeLayout();

            if (uc.Count > 0)
            {
               List<IDataSet> update = new List<IDataSet>();

               update.Add(uc);
               Config cfg = Config.GetConfig();
               DataModule.UpdateDataSet(update, null, null, cfg.GetConnection());
            }
         }
      }

      private void orgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = orgs.Rows[e.RowIndex].DataBoundItem as Org;

         //Org o = orgList[e.RowIndex];
         e.CellStyle.ForeColor = o.Color;
         e.CellStyle.SelectionForeColor = o.Color;
      }

      private void orgs_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button != MouseButtons.Right)
            return;

         DataGridView.HitTestInfo hi = orgs.HitTest(e.X, e.Y);
         if (hi.Type == DataGridViewHitTestType.Cell)
         {
            DataGridViewRow r = orgs.Rows[hi.RowIndex];
            if( !r.Selected )
               r.Selected = true;

            menu.Show(orgs, e.Location, ToolStripDropDownDirection.BelowRight);
         }
      }

      private void btnFindUp_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnFindDown_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void FmSetOrgColor_Load(object sender, EventArgs e)
      {
         Refresh();
      }
   }

   class ColorCB : ComboBox
   {
      private SysColors colors;

      //protected override void OnPaint(PaintEventArgs e)
      //{
      //   if( SelectedIndex < 0 )
      //      base.OnPaint(e);
      //   using (Brush b = new SolidBrush(colors[SelectedIndex]))
      //   {
      //      Rectangle r = Bounds;
      //      r.Inflate(-2, -2);
      //      e.Graphics.FillRectangle(b, r);
      //   }
      //}

      protected override void OnDrawItem(DrawItemEventArgs e)
      {
         if ((e.State & DrawItemState.Focus) != 0)
            e.DrawFocusRectangle();

         Color item = (Color) Items[e.Index];
         using (Brush b = new SolidBrush(((e.State & DrawItemState.Selected) != 0) ? Color.Blue : e.BackColor))
            e.Graphics.FillRectangle(b, e.Bounds);

         Rectangle bnds = e.Bounds;
         bnds.Inflate(new Size(-2, -2));
         using (Brush fb = new SolidBrush((Color)item))
            e.Graphics.FillRectangle(fb, bnds);

         //base.OnDrawItem(e);
      }

      public SysColors Colors
      {
         get { return colors; }
         set
         {
            if (value != null)
               colors = value;
            else
            {
               colors = new SysColors();
               colors.Clear();
            }

            colors.Insert(0, Color.Black);

            BeginUpdate();
            Items.Clear();
            foreach (Color c in colors)
               Items.Add(c);
            EndUpdate();
         }
      }
   }
}
