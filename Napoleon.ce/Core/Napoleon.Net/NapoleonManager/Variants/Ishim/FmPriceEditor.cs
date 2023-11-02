using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Xml.Serialization;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceEditor : Form
   {
      static FmPriceEditor instance = null;

      DataSet<string, Price> dsPrice;
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, PriceFolderOrder> dsPriceOrder;
      string prevSelectedFolder = "";

      static Color HIDDEN_COLOR = Color.LightGray;

      TreeNode rootNode;

      public FmPriceEditor()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public static void Open()
      {
         if (instance != null)
            instance.BringToFront();
         else
         {
            instance = new FmPriceEditor();
            instance.Show();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData(false);
      }

      private void RefreshData(bool forceReload)
      {
         if(dsPrice == null)
            dsPrice = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         if (dsFolders == null)
            dsFolders = DataModule.Get(ManagerFolder.OBJECT_NAME) as DataSet<string, ManagerFolder> ?? new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
         if (dsPriceOrder == null)
            dsPriceOrder = new DataSet<string, PriceFolderOrder>(PriceFolderOrder.OBJECT_NAME, false);

         List<IDataSet> upd = new List<IDataSet>();
         if (forceReload || dsPrice.Count == 0)
            upd.Add(dsPrice);
         if (forceReload || dsFolders.Count == 0)
            upd.Add(dsFolders);
         if (forceReload || dsPriceOrder.Count == 0)
            upd.Add(dsPriceOrder);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      void SetNodesBackColor(TreeNodeCollection nodes, Color hiddenColor, Color normalColor)
      {
         foreach(TreeNode tn in nodes)
         {
            ManagerFolder mf = tn.Tag as ManagerFolder;
            tn.BackColor = mf.hidden > 0 ? hiddenColor : normalColor;

            if (tn.Nodes.Count > 0)
               SetNodesBackColor(tn.Nodes, hiddenColor, normalColor);
         }
      }

      private void DoLoadData()
      {
         List<string> rmv = new List<string>();
         foreach (KeyValuePair<string, PriceFolderOrder> kv in dsPriceOrder)
         {
            if (!dsFolders.ContainsKey(kv.Value.fid))
               rmv.Add(kv.Key);
         }
         rmv.ForEach(x => dsPriceOrder.Remove(x));

         TreeView tv = new TreeView();
         ArticlesTreeConstructor atc = new ArticlesTreeConstructor(tv, dsFolders);
         atc.MakeArticlesTree(0, 2, null, 1);

         SetNodesBackColor(tv.Nodes, HIDDEN_COLOR, tv.BackColor);

         rootNode = new TreeNode("Прайс-лист", 0, 1);
         while(tv.Nodes.Count > 0)
         {
            TreeNode tn = tv.Nodes[0];
            tv.Nodes.RemoveAt(0);
            rootNode.Nodes.Add(tn);
         }

         tvFolders.SuspendLayout();
         tvFolders.Nodes.Clear();
         tvFolders.Nodes.Add(rootNode);
         //tvFolders.ExpandAll();
         tvFolders.ResumeLayout();
      }

      void LoadFoldersData(TreeNodeCollection nodes, int level, String folderID, SimpleDataSet<ManagerFolder> folders)
      {
         foreach(TreeNode tn in nodes)
         {
            ManagerFolder mf = tn.Tag as ManagerFolder;
            if(mf != null)
            {
               mf.level = level;
               mf.name = tn.Text;
               folders.Add(mf);
               LoadFoldersData(tn.Nodes, level + 1, mf.id, folders);
            }
         }
      }

      bool SaveChanges(bool showDialog)
      {
         List<Price> curPrc = (List<Price>)dgvItems.DataSource;
         if (curPrc != null) 
         {
            TreeNode sel = GetSelectedFolder(tvFolders.SelectedNode);
            if(sel != null )
            {
               ManagerFolder mf = sel.Tag as ManagerFolder;
               if (mf != null)
                  ReorderPriceList(curPrc, mf.id);
            }
         }

         SimpleDataSet<ManagerFolder> wrFolders = new SimpleDataSet<ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         LoadFoldersData(rootNode.Nodes, 0, "", wrFolders);


         List<ReplacedSet> rpl = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(wrFolders);
         rs.RemoveCommand = new ServerCommand(Commands.REMOVE, ManagerFolder.OBJECT_NAME + ":");
         rpl.Add(rs);

         if (dsPriceOrder.Count > 0)
         {
            ReplacedSet rpr = new ReplacedSet(dsPriceOrder);
            rpr.RemoveCommand = new ServerCommand(Commands.REMOVE, PriceFolderOrder.OBJECT_NAME + ":");
            rpl.Add(rpr);
         }

         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());


         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         return ret;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         if (!CheckChanges())
            return;
         RefreshData(true);
      }

      TreeNode GetSelectedFolder(TreeNode tn)
      {
         while (tn != null)
         {
            if (tn.Tag is ManagerFolder)
               break;
            tn = tn.Parent;
         }
         return tn;
      }

      private void tsbAddFolder_Click(object sender, EventArgs e)
      {
         TreeNode tn = (tvFolders.SelectedNode == rootNode) ? rootNode : GetSelectedFolder(tvFolders.SelectedNode);
         if(tn == null)
         {
            MessageBox.Show("Выделите, пожалуйста, папку для вставки");
            return;
         }

         ManagerFolder mf = new ManagerFolder();
         mf.id = Guid.NewGuid().ToString().Replace("-", "");
         mf.name = "Новая папка";
         TreeNode fn = new TreeNode(mf.name);
         fn.Tag = mf;
         tn.Nodes.Add(fn);
         tvFolders.SelectedNode = fn;
         fn.BeginEdit();
      }

      List<Price> GetFolderItems(ManagerFolder mf)
      {
         List<Price> ret = new List<Price>();

         foreach(PriceFolderOrder p in dsPriceOrder.Data)
         {
            if (p.fid == mf.id)
            {
               if(dsPrice.ContainsKey(p.id))
                  ret.Add(dsPrice[p.id]);
            }
         }

         ret.Sort(CmpPrice);

         return ret;
      }

      private int CmpPrice(Price x, Price y)
      {
         int xo = -1, yo = -1;
         if (dsPriceOrder.ContainsKey(x.id))
            xo = dsPriceOrder[x.id].ord;
         if (dsPriceOrder.ContainsKey(y.id))
            yo = dsPriceOrder[y.id].ord;

         return xo < 0 && yo < 0 ? x.name.CompareTo(y.name) :
            xo < 0 ? 1 : 
            yo < 0 ? -1 :
            xo - yo;
      }

      void ReorderPriceList(List<Price> price, string folderID)
      {
         int order = 0;
         foreach (Price p in price)
         {
            PriceFolderOrder pfo = null;
            if (dsPriceOrder.ContainsKey(p.id))
            {
               pfo = dsPriceOrder[p.id];
            }
            else
            {
               pfo = new PriceFolderOrder();
               pfo.id = p.id;
               dsPriceOrder[p.id] = pfo;
            }
            pfo.fid = folderID;
            pfo.ord = order++;
         }
      }

      private void tvFolders_AfterSelect(object sender, TreeViewEventArgs e)
      {
         List<Price> oldPrc = (List<Price>)dgvItems.DataSource;
         if (oldPrc != null)
            ReorderPriceList(oldPrc, prevSelectedFolder);

         prevSelectedFolder = "";
         List<Price> prc = new List<Price>();
         TreeNode sel = GetSelectedFolder(e.Node);
         if (sel != null)
         {
            ManagerFolder mf = sel.Tag as ManagerFolder;
            if (mf != null)
            {
               prevSelectedFolder = mf.id;
               prc = GetFolderItems(mf);
               prc.Sort(CmpPrice);
            }
         }
         dgvItems.DataSource = prc;
      }

      private void tvFolders_AfterLabelEdit(object sender, NodeLabelEditEventArgs e)
      {
         ManagerFolder mf = e.Node.Tag as ManagerFolder;
         if(mf != null)
         {
            tsbSave.Enabled = true;
            mf.name = e.Label;
         }
      }

      void RemovePriceObjects(TreeNodeCollection nodes)
      {
         foreach(TreeNode tn in nodes)
         {
            ManagerFolder mf = tn.Tag as ManagerFolder;
            if(mf != null)
            {
               List<PriceFolderOrder> rmv = new List<PriceFolderOrder>();
               foreach (PriceFolderOrder pfo in dsPriceOrder.Data)
               {
                  if (pfo.fid == mf.id)
                     rmv.Add(pfo);
               }
               rmv.ForEach(x => dsPriceOrder.Remove(x.id));
            }
            if (tn.Nodes.Count > 0)
               RemovePriceObjects(tn.Nodes);
         }
      }

      private void tsbDelFolder_Click(object sender, EventArgs e)
      {
         TreeNode sel = GetSelectedFolder(tvFolders.SelectedNode);
         if(sel == null)
         {
            MessageBox.Show("Не выбрана папка для удаления");
            return;
         }
         ManagerFolder mf = sel.Tag as ManagerFolder;
         if( mf == null)
            return;

         if (MessageBox.Show("Удалить папку и все подпапки?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != System.Windows.Forms.DialogResult.Yes)
            return;

         List<Price> lst = (List<Price>)dgvItems.DataSource;
         foreach (Price p in lst)
            dsPriceOrder.Remove(p.id);

         if (sel.Nodes.Count > 0)
            RemovePriceObjects(sel.Nodes);

         tsbSave.Enabled = true;
         sel.Remove();
      }

      bool IsPriceAssigned(Price p) { return dsPriceOrder.ContainsKey(p.id); }

      private void tsbAddItem_Click(object sender, EventArgs e)
      {
         TreeNode sel = GetSelectedFolder(tvFolders.SelectedNode);
         ManagerFolder mf = sel == null ? null : sel.Tag as ManagerFolder;
         if(sel == null || mf == null)
         {
            MessageBox.Show("Не выбрана папка для добавления товаров");
            return;
         }

         List<Price> lst = (List<Price>)dgvItems.DataSource;

         List<Price> newSel = FmSelectPrice.SelectPrice(dsPrice, lst, IsPriceAssigned);
         if(newSel != null)
         {
            newSel.Sort();
            dgvItems.DataSource = newSel;

            foreach(Price p in lst)
            {
               if(!newSel.Contains(p))
                  dsPriceOrder.Remove(p.id);
            }

            newSel.Sort(CmpPrice);

            tsbSave.Enabled = true;
         }
      }

      private void tsbDelItem_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Удалить выделенные товары?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != System.Windows.Forms.DialogResult.Yes)
            return;

         List<Price> rmv = new List<Price>();
         foreach(DataGridViewRow r in dgvItems.SelectedRows)
         {
            Price p = r.DataBoundItem as Price;
            if (p != null)
            {
               dsPriceOrder.Remove(p.id);
               rmv.Add(p);
            }
         }
         List<Price> src = (List<Price>)dgvItems.DataSource;
         rmv.ForEach(x => src.Remove(x));
         dgvItems.DataSource = null;
         dgvItems.DataSource = src;

         tsbSave.Enabled = true;
      }

      private void tsbUpItem_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = dgvItems.CurrentRow;
         if (r == null)
            return;

         int i = dgvItems.Rows.IndexOf(r);
         if (i > 0)
         {
            Price p = r.DataBoundItem as Price;
            List<Price> src = (List<Price>)dgvItems.DataSource;
            src.Remove(p);
            src.Insert(i - 1, p);
            dgvItems.DataSource = null;
            dgvItems.DataSource = src;
            dgvItems.CurrentCell = dgvItems.Rows[i - 1].Cells[0];

            tsbSave.Enabled = true;
         }
      }

      private void tsbDnItem_Click(object sender, EventArgs e)
      {
         DataGridViewRow r = dgvItems.CurrentRow;
         if (r == null)
            return;
         int i = dgvItems.Rows.IndexOf(r);
         if (i < dgvItems.Rows.Count - 1)
         {
            Price p = r.DataBoundItem as Price;
            List<Price> src = (List<Price>)dgvItems.DataSource;
            src.Remove(p);
            src.Insert(i+1, p);
            dgvItems.CurrentCell = dgvItems.Rows[i+1].Cells[0];

            dgvItems.DataSource = null;
            dgvItems.DataSource = src;
            tsbSave.Enabled = true;
         }
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         List<Price> src = (List<Price>)dgvItems.DataSource;
         if (src != null && src.Count > 0)
         {
            src.Sort();
            dgvItems.DataSource = null;
            dgvItems.DataSource = src;
            tsbSave.Enabled = true;
         }
      }

      void PutNodesForExport(TreeNodeCollection nodes, string parent, List<ExportFolder> folders, int level)
      {
         foreach(TreeNode tn in nodes)
         {
            ManagerFolder mf = tn.Tag as ManagerFolder;
            mf.parent = parent;
            mf.level = level;
            ExportFolder ef = new ExportFolder();
            ef.Set(mf);
            foreach (PriceFolderOrder p in dsPriceOrder.Data)
               if (p.fid == mf.id)
               {
                  ExportPrice ep = new ExportPrice();
                  ep.id = p.id;
                  ep.order = p.ord;
                  ef.price.Add(ep);
               }

            folders.Add(ef);

            PutNodesForExport(tn.Nodes, mf.id, folders, level+1);
         }
      }

      private void tsbExport_Click(object sender, EventArgs e)
      {
         SaveFileDialog sfd = new SaveFileDialog();
         sfd.Title = "Выберите файл для записи";
         if(sfd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            ExportFolderObject efo = new ExportFolderObject();

            PutNodesForExport(rootNode.Nodes, "", efo.folders, 0);
            XmlSerializer s = new XmlSerializer(typeof(ExportFolderObject));
            try
            {
               using (TextWriter w = new StreamWriter(sfd.FileName))
               {
                  s.Serialize(w, efo);
                  w.Close();
               }
            }
            catch (Exception)
            {
            }
         }
      }

      private void tsbImport_Click(object sender, EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Title = "Выберите файл для загрузки";
         if(ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            XmlSerializer s = new XmlSerializer(typeof(ExportFolderObject));
            using (FileStream fs = new FileStream(ofd.FileName, FileMode.Open, FileAccess.Read))
            {
               try
               {
                  ExportFolderObject efo = (ExportFolderObject)s.Deserialize(fs);
                  fs.Close();

                  dsPriceOrder.Clear();
                  dsFolders.Clear();

                  foreach(ExportFolder ef in efo.folders)
                  {
                     ManagerFolder mf = ef.CreateFolder();
                     dsFolders[mf.id] = mf;
                     
                     foreach (ExportPrice ep in ef.price)
                     {
                        PriceFolderOrder pfo = new PriceFolderOrder();
                        pfo.id = ep.id;
                        pfo.fid = mf.id;
                        pfo.ord = ep.order;
                        dsPriceOrder[ep.id] = pfo;
                     }
                  }

                  DoLoadData();
                  tsbSave.Enabled = true;
               }
               catch
               {
               }
            }
         }
      }

      void SetChildNodes(TreeNodeCollection nodes, int hidden, Color backColor)
      {
         foreach(TreeNode tn in nodes)
         {
            ManagerFolder mf = tn.Tag as ManagerFolder;
            mf.hidden = hidden;
            tn.BackColor = backColor;

            if (tn.Nodes.Count > 0)
               SetChildNodes(tn.Nodes, hidden, backColor);
         }
      }

      private void tsbHidden_Click(object sender, EventArgs e)
      {
         if( tvFolders.SelectedNode != null)
         {
            ManagerFolder mf = tvFolders.SelectedNode.Tag as ManagerFolder;
            if(mf != null)
            {
               int newVal = (mf.hidden > 0) ? 0 : 1;
               Color bkColor = (newVal > 0) ? HIDDEN_COLOR : tvFolders.BackColor;
               mf.hidden = newVal;
               tvFolders.SelectedNode.BackColor = bkColor;
               SetChildNodes(tvFolders.SelectedNode.Nodes, newVal, bkColor);
               tsbSave.Enabled = true;
            }
         }
      }

      private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
      {
         bool check = false;
         if( tvFolders.SelectedNode != null)
         {
            ManagerFolder mf = tvFolders.SelectedNode.Tag as ManagerFolder;
            check = (mf != null && mf.hidden > 0);
         }
         tsbHidden.Checked = check;
      }

      private void tvFolders_MouseClick(object sender, MouseEventArgs e)
      {
         if(e.Button == System.Windows.Forms.MouseButtons.Right)
         {
            TreeNode tn = tvFolders.GetNodeAt(e.X, e.Y);
            if (tn != null)
               tvFolders.SelectedNode = tn;
         }
      }

      private void tvFolders_ItemDrag(object sender, ItemDragEventArgs e)
      {
         if( e.Button == System.Windows.Forms.MouseButtons.Left)
         {
            DoDragDrop(e.Item, DragDropEffects.Move);
         }
      }

      private void tvFolders_DragDrop(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvFolders, new Point(e.X, e.Y));
         if (targetNode == null)
            return;

         TreeNode src = e.Data.GetData(typeof(TreeNode)) as TreeNode;
         if (src == null)
            return;

         src.Remove();
         targetNode.Nodes.Add(src);
         tsbSave.Enabled = true;
      }

      private void tvFolders_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(TreeNode)))
            e.Effect = DragDropEffects.Move;
      }

      private void tvFolders_DragOver(object sender, DragEventArgs e)
      {
         TreeNode targetNode = DataUtils.GetNodeFromPoint(tvFolders, new Point(e.X, e.Y));
         if (targetNode == null)
            return;

         tvFolders.SelectedNode = targetNode;
      }
   }


   public class ExportPrice
   {
      public string id = "";
      public int order;
   }

   public class ExportFolder
   {
      public string name;
      public string id;
      public string parent;
      public string fid;
      public int hidden;
      public int level;

      public List<ExportPrice> price = new List<ExportPrice>();

      public void Set(ManagerFolder src)
      {
         name = src.name;
         id = src.id;
         fid = src.fid;
         parent = src.parent;
         hidden = src.hidden;
         level = src.level;
      }

      public ManagerFolder CreateFolder()
      {
         ManagerFolder mf = new ManagerFolder();
         mf.id = id;
         mf.name = name;
         mf.hidden = hidden;
         mf.fid = fid;
         mf.parent = parent;
         mf.level = level;

         return mf;
      }
   }
   public class ExportFolderObject
   {
      public ExportFolderObject() { }

      public List<ExportFolder> folders = new List<ExportFolder>();
   }
}
