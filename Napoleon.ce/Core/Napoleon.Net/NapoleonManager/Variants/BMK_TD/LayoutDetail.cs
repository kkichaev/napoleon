using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class LayoutDetail : UserControl, DataObjectViewer
   {
      FmDetailEx owner;
      Font itemsBoldFont = null;
      int rowIndex = -1, gridRowIndex = -1;

      public LayoutDetail()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      internal void SetOwner(FmDetailEx owner) { this.owner = owner; }

      public int RowIndex { get { return rowIndex; } set { rowIndex = value; } }
      public int GridRowIndex { set { gridRowIndex = value; } }

      public void SetData(GRSoft.Network.DataObject dataObject )
      {
         GRSoft.NapoleonManager.Layout doc = dataObject as Layout;
         if (doc != null)
         {
            List<Item> data = new List<Item>();
            doc.items.Sort((x, y) => { return cmpLayoutItem(x, y); });

            GroupItem group = null;

            foreach (GRSoft.NapoleonManager.Layout.LayotItem i in doc.items)
            {
               if (group == null || !group.id.Equals(i.grid))
               {
                  group = new GroupItem(this);
                  group.name = i.grname;
                  group.id = i.grid;
                  data.Add(group);
               }

               Item item = new Item(i, this);
               data.Add(item);
               group.qty += item.Qty;
               group.items.Add(item);
            }

            grid.DataSource = data;
            rowIndex = gridRowIndex;
         }
         else
         {
            grid.DataSource = new List<Item>();
         }
      }

      int cmpLayoutItem(GRSoft.NapoleonManager.Layout.LayotItem x, GRSoft.NapoleonManager.Layout.LayotItem y)
      {
         int result = x.grpos - y.grpos;

         if (result == 0)
            result = x.itname.CompareTo(y.itname);

         return result;
      }

      class Item
      {
         public string name = string.Empty;
         public double qty = 0.0;
         public double chQty = 0.0;
         LayoutDetail owner;
         public string cause;

         public Item(Layout.LayotItem src, LayoutDetail owner)
         {
            this.owner = owner;
            if (src == null)
               return;

            name = src.itname;
            qty = src.qty;
            cause = string.Format("{0} {1}", src.cause, src.remark);
         }


         public string Name { get { return name; } }
         public double Qty { get { return qty; } }
         public string Cause { get { return cause;  } }
      }

      class GroupItem : Item
      {
         public string id = string.Empty;
         public List<Item> items = new List<Item>();

         public GroupItem(LayoutDetail owner)
            : base(null, owner)
         {

         }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (((DataGridView)sender).Rows[e.RowIndex].DataBoundItem is GroupItem)
         {
            if (itemsBoldFont == null)
               itemsBoldFont = new System.Drawing.Font(e.CellStyle.Font, FontStyle.Bold);
            e.CellStyle.Font = itemsBoldFont;
            e.CellStyle.BackColor = Color.LightGray;
         }
      }
   }
}
