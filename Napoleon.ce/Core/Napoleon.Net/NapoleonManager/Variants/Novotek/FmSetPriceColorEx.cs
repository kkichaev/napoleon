using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class FmSetPriceColorEx : FmSetPriceColor
   {
      DataSet<int, SysColor> dsSysColor = new DataSet<int, SysColor>(SysColor.OBJECT_NAME, false);
      Dictionary<string, SysColor> priceColors = new Dictionary<string, SysColor>();
      TSColorFilter bkgFilter;

      internal FmSetPriceColorEx(SysColors colors) :
         base(colors)
      {
         dsSysColor.Filter = "\"type\"=0";

         tvArticles.DrawMode = TreeViewDrawMode.OwnerDrawAll;
         tvArticles.DrawNode += tvArticles_DrawNode;

         colorFilter.Text = "Фильтр по цвету шрифта";

         this.Size = new Size(600, 500);

         bkgFilter = new TSColorFilter();
         bkgFilter.Text = "Фильтр по цвету фона";
         bkgFilter.SelectColor += colorFilter_SelectColorEx;
         bkgFilter.Colors = colors;
         bkgFilter.ClearChecked();

         Size sz = new Size(235, 22);
         ToolStripMenuItem m1 = new ToolStripMenuItem(ColorMenu.CreateImage(sz, Color.White));
         m1.Checked = true;
         m1.Tag = Color.White;
         bkgFilter.DropDownItems.Insert(0,m1);
         toolStrip1.Items.Add(bkgFilter);
      }

      private void colorFilter_SelectColorEx(Color clr, int index)
      {
         bkgFilter.ForeColor = Color.Black;
         bkgFilter.BackColor = clr;
         colorFilter_SelectColor(clr, index);
      }

      void tvArticles_DrawNode(object sender, DrawTreeNodeEventArgs e)
      {
         Price p = e.Node.Tag as Price;

         if (p != null)
         {
            if (priceColors.ContainsKey(p.id))
            {
               SysColor c = priceColors[p.id];
               e.Node.ForeColor = ToWinColor(c.face);
               e.Node.BackColor = ToWinColor(c.back);
            }
         }
        
         e.DrawDefault = true;
      }

      Color ToWinColor(int color)
      {
         int r = color & 0xFF;
         int g = (color & 0xFF00) >> 8;
         int b = (color & 0xFF0000) >> 16;
         return Color.FromArgb(r, g, b);
      }

      int ToSrvColor(Color c)
      {
         int result = RemoveAlpha(c);
         return (((result & 0xFF0000) >> 16) | (result & 0xFF00) | ((result & 0xFF) << 16)); ;
      }

      protected override ColorMenu CreateColorMenu(SysColors colors)
      {
         return new ColorMenuEx(colors);
      }

      protected override SelectColorHandler CreateMenuSelector()
      {
         return new SelectColorHandler(menu_SelectColor);
      }

      protected override void BeforeRefreshDataSet(List<IDataSet> list)
      {
         list.Add(dsSysColor);
      }

      protected override void AfterFrefreshDataSet()
      {
         priceColors.Clear();

         foreach (SysColor c in dsSysColor.Values)
            priceColors[c.id] = c;

         base.AfterFrefreshDataSet();
      }

      private int RemoveAlpha(Color c)
      {
         return c.ToArgb() & 0xFFFFFF;
      }

      private void menu_SelectColor(object[] args)
      {
         if (args.Length > 0)
         {
            SelColor sc = (SelColor)args[0];
            TreeNode selected = tvArticles.SelectedNode;
            if (selected != null && sc != null)
            {
               DataSet<int, SysColor> uc = new DataSet<int, SysColor>(SysColor.OBJECT_NAME, false);
               int rgbColor = ToSrvColor(sc.color); 
               Price p = selected.Tag as Price;
               if (p != null)
               {

                  if (!priceColors.ContainsKey(p.id))
                  {
                     priceColors[p.id] = new SysColor();
                  }

                  SysColor sysClr = priceColors[p.id];

                  if (sc.type == SelColor.SelectColorType.ForeColor)
                  {
                     selected.ForeColor = sc.color;
                     sysClr.face = rgbColor;
                  }
                  else
                  {
                     selected.BackColor = sc.color;
                     sysClr.back = rgbColor;
                  }

                  sysClr.id = p.id;
                  sysClr.type = (int)SysColor.Type.Price;

                  uc[uc.Count] = sysClr;
               }

               if (uc.Count > 0)
               {
                  List<IDataSet> update = new List<IDataSet>();

                  update.Add(uc);
                  Config cfg = Config.GetConfig();
                  DataModule.UpdateDataSet(update, null, null, cfg.GetConnection());
               }
            }
         }
      }

      protected override void colorFilter_SelectColor(Color clr, int index)
      {
         FillTreeView(tvArticles, dsManagerFolder, dsPrice);

         if (!IsFilterToReset())
         {
            Color selFore = colorFilter.GetSelected();
            Color selBkg = bkgFilter.GetSelected();

            TreeNode[] nodes = new TreeNode[tvArticles.Nodes.Count];
            tvArticles.Nodes.CopyTo(nodes, 0);

            tvArticles.BeginUpdate();
            tvArticles.Nodes.Clear();

            foreach (TreeNode fn in nodes)
               AddColorItemW(tvArticles.Nodes, fn, selFore, selBkg);

            tvArticles.EndUpdate();
         }
      }

      protected void AddColorItemW(TreeNodeCollection dest, TreeNode source, Color fore, Color bkg)
      {
         Price p = source.Tag as Price;

         if (p != null)
         {
            if (priceColors.ContainsKey(p.id))
            {
               SysColor sc = priceColors[p.id];

               int rgbFore = ToSrvColor(fore);
               int rgbBkg = ToSrvColor(bkg);

               if (sc.face == rgbFore && sc.back == rgbBkg)
               {
                  TreeNode dn = new TreeNode(source.Text);
                  dn.Tag = source.Tag;
                  dn.ForeColor = source.ForeColor;

                  if(source.BackColor.ToArgb() != 0)
                     dn.BackColor = source.BackColor;
                  dest.Add(dn);
               }
            }
         }

         foreach (TreeNode tn in source.Nodes)
            AddColorItemW(dest, tn, fore, bkg);
      }

      protected override bool IsFilterToReset()
      {
         bool result =  base.IsFilterToReset();

         if (result)
         {
            if (bkgFilter.DropDownItems.Count > 0)
            {
               ToolStripMenuItem i = bkgFilter.DropDownItems[0] as ToolStripMenuItem;

               if (i != null)
                  result = i.Checked;
            }
         }

         return result;
      }
   }

   public partial class SysColor
   {
      public SysColor()
      {
         this.back = Color.White.ToArgb() & 0xFFFFFF;
         this.face = Color.Black.ToArgb() & 0xFFFFFF;
      }
   }

   class ColorMenuEx : ColorMenu
   { 
      public ColorMenuEx(SysColors colors)
         : base(colors)
      {
      }

      internal override void RefreshItems()
      {
         Items.Clear();

         ToolStripMenuItem m = new ToolStripMenuItem("Цвет шрифта");
         m.Enabled = false;
         Items.Add(m);

         Size sz = new Size(235, 22);

         AddColorsItems(sz, GRSoft.NapoleonManager.SelColor.SelectColorType.ForeColor);
         m = new ToolStripMenuItem("По умолчанию");
         m.Tag = new SelColor(Color.Black, GRSoft.NapoleonManager.SelColor.SelectColorType.ForeColor);
         Items.Add(m);
         Items.Add("-");
         

         m = new ToolStripMenuItem("Цвет фона");
         m.Enabled = false;
         Items.Add(m);

         AddColorsItems(sz, GRSoft.NapoleonManager.SelColor.SelectColorType.BkgColor);
         m = new ToolStripMenuItem("По умолчанию");
         m.Tag = new SelColor(Color.White, GRSoft.NapoleonManager.SelColor.SelectColorType.BkgColor);
         Items.Add(m);
      }

      private void AddColorsItems(Size sz, SelColor.SelectColorType type)
      {
         ToolStripMenuItem m1 = new ToolStripMenuItem(CreateImage(sz, Color.Black));
         m1.Tag = new SelColor(Color.Black, type);
         Items.Add(m1);

         foreach (Color clr in colors)
         {
            ToolStripMenuItem mi = new ToolStripMenuItem(CreateImage(sz, clr));
            mi.Tag = new SelColor(clr, type);
            Items.Add(mi);
         }
      }

      protected override void OnItemClicked(ToolStripItemClickedEventArgs e)
      {
         Hide();
         SelColor sc = e.ClickedItem.Tag as SelColor;
         if (sc != null)
         {
            SelectColor(new object[] { sc });
         }
      }
   }

   class SelColor
   {
      public enum SelectColorType { ForeColor, BkgColor}
      public Color color;
      public SelectColorType type;

      public SelColor(Color color, SelectColorType type)
      {
         this.color = color;
         this.type = type;
      }
   }
}
