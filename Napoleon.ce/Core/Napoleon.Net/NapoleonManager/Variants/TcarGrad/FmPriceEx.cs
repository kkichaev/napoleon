using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.UILib;
using GRSoft.NapoleonManager.Utils;
using System.IO;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceEx : FmPrice
   {
      DataSet<string, PriceActions> dsActions = new DataSet<string, PriceActions>(PriceActions.OBJECT_NAME, false);
      DataSet<string, PriceActions> dsDelActions = new DataSet<string, PriceActions>(PriceActions.OBJECT_NAME, false);

      Font boldFont, normalFont;
      ContextMenuStrip priceMenu = new ContextMenuStrip();
      ToolStripMenuItem addItem, chgItem, delItem;
      Price curItem;
      int curRow;

      DataGridViewTextBoxColumn packClmn;

      public FmPriceEx()
      {
         tgvPrice.CellFormatting += new DataGridViewCellFormattingEventHandler(CellFormatting);
         
         addItem = new ToolStripMenuItem("Добавить акцию");
         addItem.Click += new EventHandler(chgItem_Click);
         
         chgItem = new ToolStripMenuItem("Изменить акцию");
         chgItem.Click += new EventHandler(chgItem_Click);
         
         delItem = new ToolStripMenuItem("Удалить акцию");
         delItem.Click += new EventHandler(delItem_Click);

         tgvPrice.CellMouseDown += new DataGridViewCellMouseEventHandler(tgvPrice_CellMouseDown);
         tgvPrice.MultiSelect = true;
         tgvPrice.RowHeadersVisible = true;

         packClmn = new DataGridViewTextBoxColumn();

         packClmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         packClmn.FillWeight = 80F;
         packClmn.HeaderText = "В упаковке";
         packClmn.Name = "packClmn";
         packClmn.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         tgvPrice.Columns.Insert(1, packClmn);
      }

      protected override TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         TreeGridNode result = parent.Add(dsFolder[f.id].name, null, null, null);
         result.Tag = f;
         return result;
      }

      protected override TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         double cost = p.cost != null && p.cost.Length > 0 ? p.cost[0] : 0.0;
         TreeGridNode result = parent.Add(p.name, p.inPack, cost, p.qty);
         result.Tag = p;
         return result;
      }

      void tgvPrice_CellMouseDown(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            if (tgvPrice.SelectedRows.Count > 1 )
            {
               priceMenu.Items.Clear();
               priceMenu.Items.AddRange(new ToolStripItem[] { addItem, delItem });
               priceMenu.Show(Cursor.Position);
            } else if(e.RowIndex != -1 && e.ColumnIndex != -1)
            {
               TreeGridNode node = (TreeGridNode)tgvPrice.Rows[e.RowIndex];
               curItem = node.Tag as Price;
               if (curItem != null && e.ColumnIndex == dgvItemsName.DisplayIndex)
               {
                  curRow = e.RowIndex;
                  priceMenu.Items.Clear();
                  priceMenu.Items.AddRange((dsActions.ContainsKey(curItem.id))
                     ? new ToolStripItem[] { chgItem, delItem }
                     : new ToolStripItem[] { addItem });

                  priceMenu.Show(Cursor.Position);
               }
            }
         }
      }

      protected override void BeforeWrite(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      {
         if (dsActions.Count > 0)
            wrSet.Add(dsActions);
         if (dsDelActions.Count > 0)
            rmvSet.Add(dsDelActions);
      }

      protected override void BeforeProceeded()
      {
         dsDelActions.Clear();
      }

      void AddAction(String id, FmPriceActionEdit edit)
      {
         PriceActions pa = new PriceActions();
         pa.id = id;
         pa.action = edit.Action;
         pa.start = edit.Start;
         pa.end = edit.End;

         dsActions[id] = pa;
         if (dsDelActions.ContainsKey(id))
            dsDelActions.Remove(id);
      }

      void SetAction(string id)
      {
         string action = null;
         if (id != null && dsActions.ContainsKey(id))
            action = dsActions[id].action;

         FmPriceActionEdit edit = new FmPriceActionEdit(action);
         if (edit.ShowDialog() == DialogResult.OK)
         {
            AddAction(id, edit);

            btnSave.Enabled = true;

            tgvPrice.InvalidateCell(dgvItemsName.DisplayIndex, curRow);
            tgvPrice.Update();
         }
      }

      void chgItem_Click(object sender, EventArgs e)
      {
         if (tgvPrice.SelectedRows.Count > 1)
         {
            FmPriceActionEdit edit = new FmPriceActionEdit(null);
            if (edit.ShowDialog() == DialogResult.OK)
            {
               foreach (DataGridViewRow row in tgvPrice.SelectedRows)
               {
                  Price p = row.Tag as Price;
                  if (p != null)
                  {
                     AddAction(p.id, edit);
                     tgvPrice.InvalidateCell(dgvItemsName.DisplayIndex, row.Index);
                  }
               }

               btnSave.Enabled = true;
               tgvPrice.Update();
            }

         }
         else
            SetAction(curItem.id);
      }

      void delItem_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Удалить акцию?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            if (tgvPrice.SelectedRows.Count > 1)
            {
               foreach (DataGridViewRow row in tgvPrice.SelectedRows)
               {
                  Price p = row.Tag as Price;
                  if (p != null && dsActions.ContainsKey(p.id))
                  {
                     dsDelActions[p.id] = dsActions[p.id];
                     dsActions.Remove(p.id);
                     tgvPrice.InvalidateCell(dgvItemsName.DisplayIndex, row.Index);
                  }
               }
               tgvPrice.Update();
            }
            else
            {
               dsDelActions[curItem.id] = dsActions[curItem.id];
               dsActions.Remove(curItem.id);
            }
            btnSave.Enabled = true;
         }
      }

      void CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode node = (TreeGridNode)tgvPrice.Rows[e.RowIndex];
         Price p = node.Tag as Price;
         if (p != null && e.ColumnIndex == dgvItemsName.DisplayIndex)
         {
            Font cellFont = e.CellStyle.Font;
            bool containsAction = dsActions.ContainsKey(p.id);
            if (cellFont.Bold != containsAction)
            {
               CheckFonts(cellFont);
               e.CellStyle.Font = (containsAction) ? boldFont : normalFont;
            }
         }
      }

      private void CheckFonts(Font cellFont)
      {
         if (cellFont.Bold == false)
         {
            normalFont = cellFont;
            boldFont = new Font(normalFont, FontStyle.Bold);
         }
         else
         {
            boldFont = cellFont;
            normalFont = new Font(boldFont, FontStyle.Regular);
         }
      }

      protected override void BeforeRefresh(List<IDataSet> updSet)
      {
         updSet.Add(dsActions);
      }
   }

   class PriceActions : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "PriceActions";

      [KeyField]
      public string id = "";
      public string action = "";
      public DateTime start = DateTime.MaxValue;
      public DateTime end = DateTime.MaxValue;
   }
}