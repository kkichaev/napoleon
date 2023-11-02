using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using GRSoft.UILib;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   class FmPriceEx : FmPrice
   {
      private DataSet<string, Price> dsDelPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

      public FmPriceEx()
      {
         ToolStripButton btnAddFolder = new ToolStripButton();
         ToolStripButton btnAddSKU = new ToolStripButton();
         ToolStripButton btnEdit = new ToolStripButton();
         ToolStripButton btnDel = new ToolStripButton(); ;

         // 
         // btnAddFolder
         // 
         btnAddFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnAddFolder.Image = global::GRSoft.NapoleonManager.Properties.Resources.journal_new;
         btnAddFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnAddFolder.Name = "btnAddFolder";
         btnAddFolder.Size = new System.Drawing.Size(23, 22);
         btnAddFolder.Text = "Добавить папку";
         btnAddFolder.Click += new System.EventHandler(this.btnCreateFolder_Click);
         // 
         // btnAddSKU
         // 
         btnAddSKU.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnAddSKU.Image = global::GRSoft.NapoleonManager.Properties.Resources.quest_doc1;
         btnAddSKU.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnAddSKU.Name = "btnAddSKU";
         btnAddSKU.Size = new System.Drawing.Size(23, 22);
         btnAddSKU.Text = "Добавить SKU";
         btnAddSKU.Click += new System.EventHandler(this.btnAddSKU_Click);
         // 
         // btnEdit
         // 
         btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnEdit.Name = "btnEdit";
         btnEdit.Size = new System.Drawing.Size(23, 22);
         btnEdit.Text = "Изменить";
         btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnDel.Name = "btnDel";
         btnDel.Size = new System.Drawing.Size(23, 22);
         btnDel.Text = "Удалить";
         btnDel.Click += new EventHandler(btnDel_Click);

         // 
         // tsPriceControl
         // 
         ToolStrip tsPriceControl = new System.Windows.Forms.ToolStrip();
         tsPriceControl.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            btnAddFolder,
            btnAddSKU,
            btnEdit,
            btnDel});
         tsPriceControl.Location = new System.Drawing.Point(0, 0);
         tsPriceControl.Name = "tsPriceControl";
         tsPriceControl.Size = new System.Drawing.Size(360, 25);
         tsPriceControl.TabIndex = 4;
         tsPriceControl.Text = "toolStrip3";

         splitContainer1.Panel1.Controls.Add(tsPriceControl);
         tgvPrice.ColumnHeaderMouseClick += new DataGridViewCellMouseEventHandler(tgvPrice_ColumnHeaderMouseClick);
         tgvPrice.CellFormatting += new DataGridViewCellFormattingEventHandler(tgvPrice_CellFormattingEx);
         cbAgents.Visible = false;
         FPCost.Visible = false;
         FPQty.Visible = false;

         btnRefresh.Margin = new Padding();
      }

      void btnDel_Click(object sender, EventArgs e)
      {
         TreeGridNode row = tgvPrice.CurrentRow;

         if (row.Tag is ArchiveNode)
            return;

         if (row.Parent != null && row.Parent.Tag is ArchiveNode)
            return;

         if (row != null && MessageBox.Show("Запись будет удалена", "Ошибка", 
               MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DeleteRecursive(row);
            row.Parent.Nodes.Remove(row);
            btnSave.Enabled = true;
         }
      }

      private void DeleteRecursive(TreeGridNode row)
      {
         if (row.Tag is ManagerFolder)
         {
            foreach (TreeGridNode node in row.Nodes)
               DeleteRecursive(node);

            ManagerFolder f = (ManagerFolder)row.Tag;
         }
         else if (row.Tag is Price)
         {
            Price p = (Price)row.Tag;
            p.fid = string.Empty;
            p.folderID = -1;

            dsDelPrice.Add(p.id, p);
         }
      }

      private void btnCreateFolder_Click(object sender, EventArgs e)
      {
         CreateFolder(false);
      }

      private void tgvPrice_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         CreateFolder(true);
      }

      private void CreateFolder(bool root)
      {
         ManagerFolder f = FmFolderEdit.EditFolder(null);

         if (f != null)
         {
            dsFolder.Add(f.id, f);
            TreeGridNode curRow = tgvPrice.CurrentRow;

            if (curRow != null && curRow.Tag is Price)
               curRow = curRow.Parent;

            if (curRow != null && curRow.Tag is ArchiveNode)
               return;

            TreeGridNodeCollection nodes = curRow == null || root
               ? tgvPrice.Nodes : curRow.Nodes;

            int level = curRow == null || root ? 0 : curRow.Level;

            AddFolderNode(nodes, f);
            btnSave.Enabled = true;

            if (curRow != null && !root)
            {
               f.level = level;
               curRow.Expand();
            }
         }
      }

      protected void tgvPrice_CellFormattingEx(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode node = (TreeGridNode)tgvPrice.Rows[e.RowIndex];

         if (node != null)
         {
            if (node.Tag is ManagerFolder)
               e.CellStyle.Font = new System.Drawing.Font(tgvPrice.Font, FontStyle.Bold);
            else if (node.Tag is ArchiveNode)
            {
               e.CellStyle.Font = new System.Drawing.Font(tgvPrice.Font, FontStyle.Bold);
               e.CellStyle.ForeColor = Color.Blue;
            }
            else
               e.CellStyle.Font = new System.Drawing.Font(tgvPrice.Font, FontStyle.Regular);
         }
      }

      protected override void BeforeWrite(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      {
         DataSet<string, Folder> editFolders = new DataSet<string, Folder>(Folder.OBJECT_NAME, false);

         Ids ids = new Ids();
         foreach (TreeGridNode node in tgvPrice.Nodes)
            CollectFolders(node, editFolders, ids);

         if (editFolders.Count > 0)
         {
            rpcSet.Add(new ReplacedSet(editFolders));
         }

         if (dsPrice.Count > 0)
            wrSet.Add(dsPrice);

         if (dsDelPrice.Count > 0)
            rmvSet.Add(dsDelPrice);
      }

      protected override void AfterWrite(bool result, List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      {
         if (result)
         {
            dsDelPrice.Clear();
         }
      }

      class Ids
      {
         public int id = 0;
         public void Inc() { id++; }
      }

      private void CollectFolders(TreeGridNode parent, DataSet<string, Folder> editFolders, Ids ids)
      {
         if (parent.Tag is ManagerFolder)
         {
            ManagerFolder f = parent.Tag as ManagerFolder;
            Folder fld = new Folder();
            fld.name = f.name;
            fld.id = ids.id;
            fld.level = f.level;
            fld.fid = fld.id.ToString();
            editFolders.Add(f.id, fld);
            ids.Inc();

            foreach (TreeGridNode node in parent.Nodes)
            {
               if (node.Tag is Price)
               {
                  Price p = (Price)node.Tag;
                  if (p.folderID != -1)
                  {
                     p.folderID = fld.id;
                     p.fid = p.folderID.ToString();
                  }
               }
               else
                  CollectFolders(node, editFolders, ids);
            }
         }
      }

      private void btnAddSKU_Click(object sender, EventArgs e)
      {
         TreeGridNode row = tgvPrice.CurrentRow;

         if (row != null)
         {
            if (row.Tag is ArchiveNode)
               return;

            ManagerFolder folder = row.Tag as ManagerFolder;

            if (folder == null)
               folder = row.Parent.Tag as ManagerFolder;

            if (row.Parent.Tag is ArchiveNode)
               return;

            if (folder != null)
            {
               Price price = FmSKUEdit.EditSKU(null);

               if (price != null)
               {
                  AddPriceNode(row.Nodes, price);
                  dsPrice.Add(price.id, price);
                  tgvPrice.CurrentRow.Expand();
                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         TreeGridNode row = tgvPrice.CurrentRow;

         if (row != null)
         {
            if (row.Tag is ManagerFolder)
            {
               ManagerFolder f = (ManagerFolder)row.Tag;
               FmFolderEdit.EditFolder(f);
               row.Cells[0].Value = f.name;
            }
            else if (row.Tag is Price)
            {
               Price p = (Price)row.Tag;
               FmSKUEdit.EditSKU(p);
               row.Cells[0].Value = p.name;
               btnSave.Enabled = true;
            }
         }
      }

      protected override void AfterCreatePriceTree()
      {
         SortTreeRecursive(tgvPrice.Nodes);
      }

      Comparison<TreeGridNode> priceComparator = new Comparison<TreeGridNode>(delegate(TreeGridNode n1, TreeGridNode n2)
            {
               int result = -1;

               if (n1.Tag is ManagerFolder && n2.Tag is ManagerFolder)
               {
                  result = ((ManagerFolder)n1.Tag).id.CompareTo(((ManagerFolder)n2.Tag).id);
               }
               else if (n1.Tag is ManagerFolder && n2.Tag is Price)
               {
                  result = -1;
               }
               else if (n1.Tag is Price && n2.Tag is ManagerFolder)
               {
                  result = 1;
               }
               else if (n1.Tag is Price && n2.Tag is Price)
               {
                  result = ((Price)n1.Tag).name.CompareTo(((Price)n2.Tag).name);
               }

               return result;

            });

      private void SortTreeRecursive(TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode node in nodes)
            if (node.Nodes.Count > 0)
               SortTreeRecursive(node.Nodes);

         List<TreeGridNode> list = new List<TreeGridNode>();

         foreach (TreeGridNode unSortNode in nodes)
            list.Add(unSortNode);

         list.Sort(priceComparator);

         nodes.Clear();

         foreach (TreeGridNode sortNode in list)
         {
            sortNode.Index = -1;
            nodes.Add(sortNode);
         }
      }

      protected override void BeforeRefresh(List<IDataSet> updSet)
      {
         if (!updSet.Contains(dsCommonFolder))
         {
            dsCommonFolder.Filter = "userid is null or userid=''";
            updSet.Add(dsCommonFolder);
         }

         if(!updSet.Contains(dsCommonPrice))
         {
            dsCommonPrice.Filter = "userid is null or userid=''";
            updSet.Add(dsCommonPrice);
         }

         //if (!updSet.Contains(dsFolder))
         //{
         //   updSet.Add(dsFolder);
         //}
      }
   }

   class ArchiveNode
   {
      public readonly static String TITLE = "Архив";
   }
}
