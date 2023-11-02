using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSkladBind : Form
   {
      static FmSkladBind instance = null;

      DataSet<string, Sklad> sklads = new DataSet<string, Sklad>(Sklad.OBJECT_NAME, false);
      DataSet<string, PriceSklads> prcSklads = new DataSet<string, PriceSklads>(PriceSklads.OBJECT_NAME, false);
      DataSet<string, Price> dsPrice;
      DataSet<string, ManagerFolder> dsFolder;

      bool clearing = false;
      TreeGridNode[] priceNodes;

      //List<DataRow> allData;

      Color folderBackColor = Color.LightGray;

      public FmSkladBind()
      {
         InitializeComponent();
         tgvItems.AutoGenerateColumns = false;

         dsPrice = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ??
            new DataSet<string, Price>(Price.OBJECT_NAME);

         dsFolder = DataModule.Get(ManagerFolder.OBJECT_NAME) as DataSet<string, ManagerFolder> ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
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

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(sklads);
         upd.Add(prcSklads);

         if(dsPrice.Count == 0 || dsPrice.Filter != DataUtils.COMMON_PRICE_FILTER_STR)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void fillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         if (node.Tag is ManagerFolder)
         {
            TreeGridNode child = parent.AddDataItem(new DataRow(node.Tag as ManagerFolder, this));
            child.DefaultCellStyle.BackColor = folderBackColor;

            foreach (TreeNode n in node.Nodes)
               fillGridRecursive(n, child.Nodes);
         }
         else if (node.Tag is Price)
         {
            Price p = (Price)node.Tag;
            parent.AddDataItem(new DataRow(p, prcSklads, this));
         }
      }

      void DoLoadData()
      {
         List<Sklad> s_src = new List<Sklad>();
         Sklad empty = new Sklad();
         s_src.Add(empty);

         foreach (Sklad s in sklads.Data)
            s_src.Add(s);

         clmnSklad.DataSource = s_src;
         clmnSklad.DisplayMember = "Name";
         clmnSklad.ValueMember = "ID";

         TreeView tmpTree = new TreeView();
         ArticlesTreeConstructor treeCnt = new ArticlesTreeConstructor(tmpTree, dsFolder, dsPrice);
         treeCnt.MakeArticlesTree();

         tgvItems.CellValueChanged -= tgvItems_CellValueChanged;

         tgvItems.SuspendLayout();
         tgvItems.Nodes.Clear();
         tgvItems.Rows.Clear();

         foreach (TreeNode n in tmpTree.Nodes)
            fillGridRecursive(n, tgvItems.Nodes);

         //priceNodes = new TreeGridNode[tgvItems.Nodes.Count];
         //tgvItems.Nodes.CopyTo(priceNodes, 0);

         priceNodes = new TreeGridNode[tgvItems.Nodes.Count];
         tgvItems.Nodes.CopyTo(priceNodes, 0);

         tgvItems.ResumeLayout();

         tgvItems.CellValueChanged += tgvItems_CellValueChanged;

         //List<string> ids = new List<string>();
         //allData = new List<DataRow>();
         //foreach(Price p in dsPrice.Data)
         //{
         //   string id = p.id;
         //   int pos = id.IndexOf('\t');
         //   if( pos >= 0 )
         //   {
         //      id = id.Substring(pos + 1);
         //   }
         //   if (ids.Contains(id))
         //      continue;

         //   ids.Add(id);
         //   allData.Add(new DataRow(p, id, prcSklads, this));
         //}

         //tgvItems.DataSource = new SortableBindingList<DataRow>(allData);
      }

      TreeGridNode FindNode(TreeGridNodeCollection nodes, DataRow row)
      {
         foreach(TreeGridNode tn in nodes)
         {
            if (tn.DataItem == row)
               return tn;

            if(tn.Nodes.Count > 0)
            {
               TreeGridNode r = FindNode(tn.Nodes, row);
               if (r != null)
                  return r;
            }
         }

         return null;
      }

      void MarkPrice(TreeGridNodeCollection nodes, String idwh)
      {
         foreach (TreeGridNode tn in nodes)
         {
            DataRow dr = tn.DataItem as DataRow;
            if (dr != null)
            {
               dr.Sklad = idwh;
               tn.SetValues(tgvItems, dr);
            }

            if (tn.Nodes.Count > 0)
               MarkPrice(tn.Nodes, idwh);
         }
      }

      void MarkChanged(TreeGridNode node)
      {
         DataRow dr = node.DataItem as DataRow;
         String wh = (String)node.Cells[clmnSklad.DisplayIndex].Value;
         dr.Sklad = wh;
         MarkPrice(node.Nodes, wh);

         tsbSave.Enabled = true;
      }

      internal class DataRow
      {
         FmSkladBind owner;
         Price item;
         ManagerFolder folder;
         string idwh = "";

         public DataRow(Price item, DataSet<string, PriceSklads> prcSklads, FmSkladBind owner)
         {
            this.item = item;
            this.owner = owner;

            if (prcSklads.ContainsKey(item.id))
               idwh = prcSklads[item.id].idwh;
         }

         public DataRow(ManagerFolder folder, FmSkladBind owner)
         {
            this.folder = folder;
            this.owner = owner;
         }

         public bool IsFolder { get { return folder != null; } }

         public string Name { get { return item != null ? item.Name : folder.name; } }
         public string Sklad 
         { 
            get { return idwh; } 
            set 
            {
               if (idwh != value)
                  idwh = value;
            } 
         }

         public string ID { get { return item == null ? "" : item.id; } }
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmSkladBind();
            instance.Show();
         }
         else
            instance.BringToFront();
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tsbFind.Text.ToUpper());
      }

      private void DoSearch(string text)
      {
         //List<DataRow> src = new List<DataRow>();
         //foreach(DataRow dr in allData)
         //{
         //   if (dr.Name.ToUpper().Contains(text))
         //      src.Add(dr);
         //}

         //tgvItems.DataSource = new SortableBindingList<DataRow>(src);
      }

      private void tsbClearFind_Click(object sender, EventArgs e)
      {
         ClearFind();
      }

      private void ClearFind()
      {
         //clearing = true;
         //tsbFind.Text = "";

         //tgvItems.DataSource = allData;

         //clearing = false;
      }

      private void tsbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tsbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind();
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         DoLoadData();
      }

      private bool SaveChanges(bool showDialog)
      {
         tgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<ReplacedSet> rpl = new List<ReplacedSet>();

         prcSklads.Clear();
         PutNodes(prcSklads, tgvItems.Nodes);

         //foreach(DataRow dr in allData)
         //{
         //   if (dr.Sklad.Length > 0)
         //   {
         //      PriceSklads pc = new PriceSklads();
         //      pc.id = "1\t" + dr.ID;
         //      pc.idwh = dr.Sklad;

         //      prcSklads.Add(pc.id, pc);

         //      pc = new PriceSklads();
         //      pc.id = "2\t" + dr.ID;
         //      pc.idwh = dr.Sklad;
         //      prcSklads.Add(pc.id, pc);
         //   }
         //}

         rpl.Add(new ReplacedSet(prcSklads));
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void PutNodes(DataSet<string, PriceSklads> psklads, TreeGridNodeCollection nodes)
      {
         foreach(TreeGridNode tn in nodes)
         {
            DataRow dr = tn.DataItem as DataRow;
            if(dr != null && dr.ID.Length > 0 && dr.Sklad.Length > 0)
            {
               PriceSklads pc = new PriceSklads();
               pc.id = dr.ID;
               pc.idwh = dr.Sklad;
               psklads.Add(pc.id, pc);
            }
            if (tn.Nodes.Count > 0)
               PutNodes(psklads, tn.Nodes);
         }
      }

      private void tgvItems_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if(e.ColumnIndex == clmnSklad.DisplayIndex && e.RowIndex >= 0)
         {
            TreeGridNode tn = (TreeGridNode)tgvItems.Rows[e.RowIndex];
            MarkChanged(tn);
         }
      }

      private void tgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (tgvItems.CurrentCell.ColumnIndex == clmnSklad.DisplayIndex)
            tgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

   }
}
