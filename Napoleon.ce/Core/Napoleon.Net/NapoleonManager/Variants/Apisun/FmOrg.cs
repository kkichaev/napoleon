using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.UILib;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrg : Form
   {
      private DataSet<string, OrgRegion> dsOrgRegion;
      private DataSet<string, OrgRegion> dsDelOrgRegion;
      private DataSet<string, OrgEx> dsOrg;
      private DataSet<string, OrgEx> dsDelOrg;
      private DataSet<string, OrgType> dsOrgType;
      private DataSet<string, Dealer> dsDealer;
      private bool expanded = false;

      public FmOrg()
      {
         InitializeComponent();
         dsOrgRegion = (DataSet<string, OrgRegion>)DataModule.Get(OrgRegion.OBJECT_NAME) ?? 
            new DataSet<string, OrgRegion>(OrgRegion.OBJECT_NAME);
         dsOrgRegion.Filter = "id not null";
         dsDelOrgRegion = new DataSet<string, OrgRegion>(OrgRegion.OBJECT_NAME, false);
         dsOrg = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME, false);
         dsOrg.Filter = "id not null";
         dsDelOrg = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME);
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsDealer = (DataSet<string, Dealer>)DataModule.Get(Dealer.OBJECT_NAME) ??
            new DataSet<string, Dealer>(Dealer.OBJECT_NAME);
         btnConvert.Visible = false;
         btnSave.Enabled = false;
      }

      private void btnAddRegion_Click(object sender, EventArgs e)
      {
         CreateRegion(false);
      }

      private void CreateRegion(bool root)
      {
         TreeGridNode row = tgvOrg.CurrentRow;

         OrgRegion region = FmRegionEdit.EditRegion(null);

         if (region != null)
         {
            dsOrgRegion.Add(region.id, region);
            TreeGridNodeCollection nodes = row == null || root ? tgvOrg.Nodes : row.Nodes;

            AddNode(nodes, region);

            if (row != null && !root)
            {
               region.parent = ((OrgRegion)row.Tag).id;
               row.Expand();
            }

            btnSave.Enabled = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         if (dsOrgRegion.Count > 0)
            wrSet.Add(dsOrgRegion);

         if (dsOrg.Count > 0)
            wrSet.Add(dsOrg);

         if (dsDelOrgRegion.Count > 0)
            rmvSet.Add(dsDelOrgRegion);

         if (dsDelOrg.Count > 0)
            rmvSet.Add(dsDelOrg);

         if (!DataModule.UpdateDataSet
            (wrSet, rmvSet, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
            btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnRefresh.Enabled = false;
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(dsDealer);
         updSet.Add(dsOrgRegion);
         updSet.Add(dsOrg);
         updSet.Add(dsOrgType);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            tgvOrg.SuspendLayout();
            tgvOrg.Nodes.Clear();

            List<OrgRegion> list = new List<OrgRegion>();
            list.AddRange(dsOrgRegion.Values);
            list.Sort(new Comparison<OrgRegion>(delegate(OrgRegion o1, OrgRegion o2) { return o1.name.CompareTo(o2.name); }));

            Tree data = Tree.Create(dsOrgRegion, dsOrg);

            foreach (Tree.Node n in data.nodes)
               InsertTreeNode(tgvOrg.Nodes, n);

            SortTreeRecursive(tgvOrg.Nodes);

            tgvOrg.ResumeLayout();
            btnRefresh.Enabled = true;
         }));
      }

      Comparison<TreeGridNode> priceComparator = new Comparison<TreeGridNode>(delegate(TreeGridNode n1, TreeGridNode n2)
         {
            
            return ((TreeData)n1.Tag).Data[0].CompareTo(((TreeData)n2.Tag).Data[0]);
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

      private void InsertTreeNode(TreeGridNodeCollection nodes, Tree.Node n)
      {
         TreeGridNode nn = nodes.Add(((TreeData)n.value).Data);
         nn.Tag = n.value;

         foreach (Tree.Node child in n.nodes)
            InsertTreeNode(nn.Nodes, child);
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            btnRefresh.Enabled = true;
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void tgvOrg_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode node = (TreeGridNode)((TreeGridView)sender).Rows[e.RowIndex];

         if (node != null)
         {
            if (node.Tag is OrgRegion)
            {
               e.CellStyle.Font = new System.Drawing.Font(((TreeGridView)sender).Font, FontStyle.Bold);
            }
            else
               e.CellStyle.Font = new System.Drawing.Font(((TreeGridView)sender).Font, FontStyle.Regular);
         }
      }

      private void btnAddOrg_Click(object sender, EventArgs e)
      {
         CreateOrg(false);
      }

      private void CreateOrg(bool root)
      {
         TreeGridNode row = tgvOrg.CurrentRow;

         OrgEx org = FmOrgEdit.EditOrg(null);

         if (org != null)
         {
            dsOrg.Add(org.id, org);
            TreeGridNodeCollection nodes = row == null || root ? tgvOrg.Nodes : row.Nodes;

            AddNode(nodes, org);

            if (row != null && !root)
            {
               org.parent = ((TreeData)row.Tag).Id;
               row.Expand();
            }

            btnSave.Enabled = true;
         }
      }

      private TreeGridNode AddNode(TreeGridNodeCollection nodes, TreeData data)
      {
         TreeGridNode result = nodes.Add(data.Data);
         result.Tag = data;
         return result;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         TreeGridNode row = tgvOrg.CurrentRow;
         TreeData data = null;

         if (row.Tag is OrgEx)
         {
            data = FmOrgEdit.EditOrg((OrgEx)row.Tag);
         }
         else if(row.Tag is OrgRegion)
         {
            data = FmRegionEdit.EditRegion((OrgRegion)row.Tag);
         }

         if (data != null)
         {
            for (int i = 0; i < data.Data.Length; i++)
            {
               row.Cells[i].Value = data.Data[i];
            }

            btnSave.Enabled = true;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         TreeGridNode row = tgvOrg.CurrentRow;

         if (row != null && MessageBox.Show("Запись будет удалена, удалить?", 
            "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            if (row.Tag is OrgEx)
            {
               OrgEx org = (OrgEx)row.Tag;
               dsDelOrg.Add(org.id, org);
               dsOrg.Remove(org.id);
            }
            else if (row.Tag is OrgRegion)
            {
               OrgRegion reg = (OrgRegion)row.Tag;
               dsDelOrgRegion.Add(reg.id, reg);
               dsOrgRegion.Remove(reg.id);
            }

            row.Parent.Nodes.Remove(row);
            //tgvOrg.Nodes.Remove(row);
            btnSave.Enabled = true;
         }
      }

      private void DeleteRecursive(TreeGridNode row)
      {
         foreach (TreeGridNode node in row.Nodes)
            DeleteRecursive(node);

         if (row.Tag is OrgRegion)
         {
            OrgRegion o = (OrgRegion)row.Tag;
            dsDelOrgRegion.Add(o.id, o);
         }
         else if (row.Tag is OrgEx)
         {
            OrgEx o = (OrgEx)row.Tag;
            dsDelOrg.Add(o.id, o);
         }

         tgvOrg.Nodes.Remove(row);
      }

      private void btnOrgType_Click(object sender, EventArgs e)
      {
         new FmOrgType().Show();
      }

      private void btnDealer_Click(object sender, EventArgs e)
      {
         new FmDealer().Show();
      }

      private void btnAgentOrg_Click(object sender, EventArgs e)
      {
         new FmAgentOrg().Show();
      }

      private void btnConvert_Click(object sender, EventArgs e)
      {
         DataSet<string, OrgType> dsOrgType = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
         DataSet<string, Dealer> dsDealer = new DataSet<string, Dealer>(Dealer.OBJECT_NAME, false);
         DataSet<int, Folder> dsFolder = new DataSet<int, Folder>(Folder.OBJECT_NAME, false);
         DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

         ConvertOrg(dsOrgType, dsDealer);
         ConvertPrice(dsFolder, dsPrice);

         List<ReplacedSet> list = new List<ReplacedSet>();
         list.Add(new ReplacedSet(dsOrgRegion));
         list.Add(new ReplacedSet(dsOrg));
         list.Add(new ReplacedSet(dsOrgType));
         list.Add(new ReplacedSet(dsDealer));
         list.Add(new ReplacedSet(dsPrice));
         list.Add(new ReplacedSet(dsFolder));

         if (!DataModule.UpdateDataSet
            (null, null, list, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
      }

      private void ConvertPrice(DataSet<int, Folder> dsFolder, DataSet<string, Price> dsPrice)
      {
         var fileName = string.Format("{0}\\Прайс.xls", System.IO.Directory.GetCurrentDirectory());
         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", fileName);

         var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [Лист1$]", connectionString);
         var ds = new DataSet();

         adapter.Fill(ds, "name1");

         DataTable data = ds.Tables["name1"];
         string fid = string.Empty;
         foreach (DataRow row in data.Rows)
         {
            object[] r = row.ItemArray;

            if (r[0].ToString().Trim().Length == 0 && r[1].ToString().Trim().Length > 0
               && r[2].ToString().Trim().Length == 0)
            {
               Folder f = new Folder();
               f.id = dsFolder.Count + 1;
               f.name = r[1].ToString();
               fid = f.id.ToString();

               if(dsFolder.Name.Trim().Length > 0)
                  dsFolder.Add(f.id, f);
            }
            else
            {
               Price p = new Price();
               p.id = GRSoft.Network.DataObject.GenId();
               p.name = r[0].ToString();
               p.fid = fid;
               p.folderID = Int32.Parse(fid);

               if(p.name.Trim().Length > 0)
                  dsPrice.Add(p.id, p);
            }
         }
      }

      private void ConvertOrg(DataSet<string, OrgType> dsOrgType, DataSet<string, Dealer> dsDealer)
      {
         string[] files = new string[] { "Питер", "ННовгород", "Москва", "Казань", "Воронеж" };

         foreach (string f in files)
         {
            var fileName = string.Format("{0}\\" + f + ".xls", System.IO.Directory.GetCurrentDirectory());
            var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", fileName);

            var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [Лист1$]", connectionString);
            var ds = new DataSet();

            adapter.Fill(ds, "name");

            DataTable data = ds.Tables["name"];
            OrgRegion papa = new OrgRegion();
            papa.id = GRSoft.Network.DataObject.GenId();
            papa.name = f;

            dsOrgRegion.Add(papa.id, papa);

            OrgRegion reg = null;

            foreach (DataRow row in data.Rows)
            {
               object[] r = row.ItemArray;

               if (r[0].ToString().Trim().Length == 0 && r[1].ToString().Trim().Length > 0
                  && r[2].ToString().Trim().Length == 0)
               {
                  reg = new OrgRegion();
                  reg.id = GRSoft.Network.DataObject.GenId();
                  reg.name = r[1].ToString().Trim();
                  reg.parent = papa.id;

                  if (reg.name.Trim().Length > 0)
                     dsOrgRegion.Add(reg.id, reg);
               }
               else
               {
                  OrgEx org = new OrgEx();
                  org.id = GRSoft.Network.DataObject.GenId();
                  org.name = r[0].ToString().Trim();
                  org.address = r[1].ToString().Trim();

                  if (r[2].ToString().Trim().Length > 0)
                     org.orgType = GetOrgType(dsOrgType, r[2].ToString());

                  //if (r[3].ToString().Trim().Length > 0)
                  //   org.dealer = GetDealer(dsDealer, r[3].ToString());

                  org.license = GetLicense(r[4].ToString());
                  org.cheif = r[5].ToString().Trim();
                  org.cheifPhone = r[6].ToString().Trim();
                  org.contact = r[7].ToString().Trim();
                  org.contactPhone = r[8].ToString().Trim();

                  if (reg != null)
                     org.parent = reg.id;
                  else if (papa != null)
                     org.parent = papa.id;

                  if (org.name.Trim().Length > 0)
                     dsOrg.Add(org.id, org);
               }
            }
         }
      }

      private void AddFolder(DataSet<int, Folder> dsFolder, DataSet<string, Price> dsPrice, DataRow dataRow, DataRow dataRow_4)
      {
         string fid = string.Empty;

         for (int i = 44; i < dataRow.ItemArray.Length; i++)
         {
            if (dataRow.ItemArray[i].ToString().Trim().Length > 0)
            {
               Folder f = new Folder();
               f.id = dsFolder.Count + 1;
               f.name = dataRow.ItemArray[i].ToString().Trim();
               fid = f.id.ToString();
               dsFolder.Add(f.id, f);
            }

            Price p = new Price();
            p.id = GRSoft.Network.DataObject.GenId();
            p.name = dataRow_4.ItemArray[i].ToString().Trim();
            p.fid = fid;
            p.folderID = Int32.Parse(fid);

            dsPrice.Add(p.id, p);
         }
      }

      private int GetLicense(string p)
      {
         int result = 0;

         if (p.ToUpper().Equals("есть".ToUpper()))
            result = 1;

         return result;
      }

      private string GetDealer(DataSet<string, Dealer> dsDealer, string p)
      {
         foreach(Dealer d in dsDealer.Data)
            if(d.name.ToUpper().Equals(p.ToUpper()))
               return d.id;

         Dealer dd = new Dealer();
         dd.id = (dsDealer.Count +1).ToString();
         dd.name = p.Trim();

         dsDealer.Add(dd.id, dd);

         return dd.id;
      }

      private string GetOrgType(DataSet<string, OrgType> dsOrgType, string p)
      {
         foreach (OrgType o in dsOrgType.Data)
            if (o.name.ToUpper().Equals(p.ToUpper()))
               return o.id;

         OrgType oo = new OrgType();
         oo.id = (dsOrgType.Count + 1).ToString();
         oo.name = p.Trim();

         dsOrgType.Add(oo.id, oo);

         return oo.id;
      }

      //Искать в направлениее Direction
      private void Find(Direction dir)
      {
         expanded = false;
         ExpandNodes();
         TreeGridNode node = tgvOrg.CurrentRow;

         if (node != null)
         {
            int index = node.RowIndex;

            while (!IsFindOver(dir, ref index))
            {
               for (int c = 0; c < tgvOrg.Rows[index].Cells.Count; c++ )
                  if (tgvOrg.Rows[index].Cells[c].Value != null && 
                     tgvOrg.Rows[index].Cells[c].Value.ToString().ToUpper().Contains(tbFind.Text.ToUpper()))
                  {
                     tgvOrg.CurrentCell = tgvOrg.Rows[index].Cells[c];
                     return;
                  }
            }
         }
      }

      private bool IsFindOver(Direction dir, ref int index)
      {
         index = Next(dir, index);

         if (dir == Direction.UP)
            return index < 0;
         else
            return index >= tgvOrg.Rows.Count;
      }

      //Вычислить следующий индекс в соответсвии с направление поиска
      private int Next(Direction dir, int value)
      {
         if (dir == Direction.UP)
            return --value;
         else
            return ++value;
      }

      //Раскрыть дерево прайса
      private void ExpandNodes()
      {
         if (tgvOrg.Nodes.Count > 0)
         {
            tgvOrg.SuspendLayout();
            ExpandNodesRecursive(tgvOrg.Nodes);
            tgvOrg.ResumeLayout();
            expanded = !expanded;
         }
      }

      private void ExpandNodesRecursive(TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode node in nodes)
         {
            if (expanded)
               node.Collapse();
            else
               node.Expand();

            if (node.Nodes.Count > 0)
               ExpandNodesRecursive(node.Nodes);
         }
      }

      private void btnFindDown_Click(object sender, EventArgs e)
      {
         Find(Direction.DOWN);
      }

      private void btnFindUp_Click(object sender, EventArgs e)
      {
         Find(Direction.UP);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            Find(Direction.DOWN);
      }

      private void FmOrg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true && MessageBox.Show("Сохранить изменения?", "Вопрос",
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(btnSave, EventArgs.Empty);
         }
      }

      private void tgvOrg_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (e.Button == MouseButtons.Left && e.ColumnIndex == 0)
         {
            MenuItem[] items = new MenuItem[] 
            { 
               new MenuItem("Район", new EventHandler(delegate(object o, EventArgs a){CreateRegion(true);})),
               new MenuItem("Организация", new EventHandler(delegate(object o, EventArgs a){CreateOrg(true);}))
            };

            ContextMenu contextMenu = new ContextMenu(items);
            contextMenu.Show((Control)sender, new Point(e.X, e.Y));
         }
      }
   }
}
