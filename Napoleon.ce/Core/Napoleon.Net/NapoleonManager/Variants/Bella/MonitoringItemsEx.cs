using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MonitoringItemsEx : MonitoringItems
   {
      public MonitoringItemsEx()
      {
         ToolStripButton btnUp = new ToolStripButton();
         btnUp.Text = "Вверх";
         btnUp.Image = Resources.go_up_4;
         btnUp.DisplayStyle = ToolStripItemDisplayStyle.Image;
         btnUp.Click += btnUp_Click;

         ToolStripButton btnDown = new ToolStripButton();
         btnDown.Text = "Вниз";
         btnDown.Image = Resources.go_down_4;
         btnDown.DisplayStyle = ToolStripItemDisplayStyle.Image;
         btnDown.Click += btnDown_Click;
         toolStrip.Items.Add(btnUp);
         toolStrip.Items.Add(btnDown);
      }

      void btnDown_Click(object sender, EventArgs e)
      {
         MoveToDirection(Direction.DOWN);
      }

      void MoveToDirection(Direction dir)
      {
         if (dgvItems.CurrentRow != null)
         {
            List<MonitoringItem> list = ((BindingSource)dgvItems.DataSource).DataSource as List<MonitoringItem>;

            if (list != null)
            {
               int idx = dgvItems.CurrentRow.Index;

               
               if (dir == Direction.DOWN && idx + 1 < list.Count || dir == Direction.UP && idx > 0)
               {
                  MonitoringItem i = list[idx];
                  list.RemoveAt(idx);
                  idx = dir == Direction.DOWN ? idx+1 : idx-1;
                  list.Insert(idx, i);

                  dgvItems.Refresh();
                  dgvItems.CurrentCell = dgvItems.Rows[idx].Cells[0];
                  tbSave.Enabled = true;
               }
            }
         }
      }

      void btnUp_Click(object sender, EventArgs e)
      {
         MoveToDirection(Direction.UP);
      }

      protected override void SortItems()
      {
         items.Sort((lhs, rhs) => { return lhs.pos - rhs.pos; });
      }

      protected override void ProcessItem(MonitoringItem item)
      {
         List<MonitoringItem> list = ((BindingSource)dgvItems.DataSource).DataSource as List<MonitoringItem>;

         if (list != null)
            item.pos = list.IndexOf(item);
      }
   }
}
