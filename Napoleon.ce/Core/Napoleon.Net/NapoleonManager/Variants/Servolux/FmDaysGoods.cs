using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDaysGoods : Form
   {
      SimpleDataSet<DaysGoods> goods = new SimpleDataSet<DaysGoods>(DaysGoods.OBJECT_NAME, false);
      private SimpleDataSet<OrgDogovor> dogovors = new SimpleDataSet<OrgDogovor>(OrgDogovor.OBJECT_NAME, false);
      private DataSet<string, Factory> dsFactory;
      List<NodeData> allNodes;
      private System.Threading.Timer textWait = null;

      public FmDaysGoods()
      {
         InitializeComponent();

         dsFactory = (DataSet<string, Factory>)DataModule.Get(Factory.OBJECT_NAME) ?? new DataSet<string, Factory>(Factory.OBJECT_NAME);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         Manager dm = CurrentUser.user as Manager;

         if (dm != null)
         {
            foreach (Agent a in dm.GetAgents().Data)
            {
               DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

               if (orgs.Count == 0)
               {
                  orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
                  upd.Add(orgs);
               }
            }
         }
         if(dsFactory.Count == 0)
            upd.Add(dsFactory);
         if(dogovors.Count == 0)
            upd.Add(dogovors);

         DataSet<string, Price> price = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price>;
         if(price == null)
            price = new DataSet<string, Price>(Price.OBJECT_NAME);
         if(price.Count == 0)
         {
            price.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(price);
         }

         DataSet<string, ManagerFolder> folders = DataModule.Get(ManagerFolder.OBJECT_NAME) as DataSet<string, ManagerFolder>;
         if (folders == null)
            folders = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
         if(folders.Count == 0)
         {
            folders.Filter = DataUtils.USERID_IS_NULL_STR;
            upd.Add(folders);
         }

         upd.Add(goods);

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         Dictionary<string, DaysGoods> orgGoods = new Dictionary<string, DaysGoods>();
         Dictionary<string, DaysGoods> adrGoods = new Dictionary<string, DaysGoods>();
         Dictionary<string, List<Factory>> availFirms = new Dictionary<string, List<Factory>>();

         LoadAvaiFirms(availFirms);
         LoadGoods(orgGoods, adrGoods);

         Dictionary<string, bool> loadedOrgs = new Dictionary<string, bool>();
         Dictionary<string, NodeData> nodes = new Dictionary<string, NodeData>();
         foreach (Agent a in (CurrentUser.user as Manager).GetAgents().Data)
         {
            DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            foreach(Org o in orgs.Data)
            {
               if (loadedOrgs.ContainsKey(o.id))
                  continue;
               loadedOrgs[o.id] = true;

               if (availFirms.ContainsKey(o.ido) == false)
                  continue;

               List<Factory> firms = availFirms[o.ido];
               NodeData orgNode = null;
               if (nodes.ContainsKey(o.ido))
                  orgNode = nodes[o.ido];
               else
               {
                  orgNode = new NodeData();
                  orgNode.isOrg = 1;
                  orgNode.id = o.ido;
                  orgNode.name = o.name;
                  orgNode.LoadItems(firms, orgGoods);
                  nodes[o.ido] = orgNode;
               }

               NodeData chNode = new NodeData();
               chNode.isOrg = 0;
               chNode.id = o.id;
               chNode.name = o.address;
               chNode.LoadItems(firms, adrGoods);
               orgNode.childs.Add(chNode);
            }
         }

         NodeData commonNode = new NodeData();
         commonNode.name = "<Все>";
         commonNode.isOrg = 1;
         List<Factory> allf = new List<Factory>();
         foreach(Factory fi in dsFactory.Data)
            allf.Add(fi);
         commonNode.LoadItems(allf, orgGoods);

         allNodes = new List<NodeData>(nodes.Values);
         allNodes.Sort();
         allNodes.Insert(0, commonNode);

         AddNodes(allNodes, null);
      }

      private void LoadGoods(Dictionary<string, DaysGoods> orgGoods, Dictionary<string, DaysGoods> adrGoods)
      {
         foreach(DaysGoods dg in goods.Data)
         {
            string key = dg.Key;
            if (dg.isOrg == 0)
               adrGoods[key] = dg;
            else
               orgGoods[key] = dg;
         }
      }

      private void LoadAvaiFirms(Dictionary<string, List<Factory>> availFirms)
      {
         availFirms.Clear();
         foreach(OrgDogovor dog in dogovors.Data)
         {
            if (dsFactory.ContainsKey(dog.firm) == false)
               continue;
            Factory fct = dsFactory[dog.firm];

            List<Factory> fl = null;
            if (availFirms.ContainsKey(dog.ido))
               fl = availFirms[dog.ido];
            else
            {
               fl = new List<Factory>();
               availFirms.Add(dog.ido, fl);
            }

            if (fl.Contains(fct) == false)
               fl.Add(fct);
         }
      }

      void AddNodes(List<NodeData> nodes, string filter)
      {
         treeView.BeginUpdate();
         treeView.Nodes.Clear();

         foreach (NodeData nd in allNodes)
         {
            bool removed = true;

            bool matchParent = filter == null || nd.name.ToUpper().Contains(filter);
            if (matchParent)
               removed = false;

            TreeNode node = new TreeNode(nd.name);
            node.Tag = nd;

            foreach (NodeData chNode in nd.childs)
            {
               if (!matchParent && filter != null && !chNode.name.ToUpper().Contains(filter))
                  continue;

               removed = false;

               TreeNode child = new TreeNode(chNode.name);
               child.Tag = chNode;
               node.Nodes.Add(child);
            }

            if (!removed)
               treeView.Nodes.Add(node);
         }

         treeView.EndUpdate();
      }

      private void tbSearch_TextChanged(object sender, EventArgs e)
      {
         if (textWait != null)
            textWait.Dispose();
         textWait = new System.Threading.Timer(new TimerCallback(TimePassed), ((ToolStripTextBox)sender).Text, 500, 0);
      }

      private void DoSearch(string filter)
      {
         AddNodes(allNodes, filter.ToUpper());
      }

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMDaysGoods");
            if (m.WaitOne(0))
               treeView.Invoke(new InvokeParamHandler(delegate(object param) { DoSearch((string)param); }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception) { }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }


      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      class NodeData : IComparable<NodeData>
      {
         public string id = "";
         public string name = "";
         public int isOrg = 0;

         public class Item
         {
            public Factory firm = null;
            public DaysGoods data = null;

            public String Name { get { return firm == null ? "" : firm.name; } }
            public String Mtx
            {
               get
               {
                  if (data == null)
                     return "";
                  String ret = "";
                  foreach(MatrixItem mi in data.items)
                     if( mi.price != null )
                     {
                        Price p = mi.price;
                        ret += p.Name + " " + p.thermalState + "/" + p.packName + ",";
                        if(ret.Length > 50)
                        {
                           ret = ret.Substring(0, ret.Length - 1);
                           ret += "...,";
                           break;
                        }
                     }
                  return ret.Substring(0, ret.Length - 1);
               }
            }
         }

         public List<Item> items = new List<Item>();

         public List<NodeData> childs = new List<NodeData>();

         public int CompareTo(NodeData other)
         {
            return name.CompareTo(other.name);
         }

         public void LoadItems(List<Factory> firms, Dictionary<string, DaysGoods> goods)
         {
            foreach(Factory f in firms)
            {
               string key = f.id + id;
               Item item = new Item();
               item.firm = f;
               if (goods.ContainsKey(key))
                  item.data = goods[key];

               items.Add(item);
            }
         }
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbSearch.Text = string.Empty;
      }

      private void treeView_AfterSelect(object sender, TreeViewEventArgs e)
      {
         TreeNode node = ((TreeView)sender).SelectedNode;
         if (node == null)
            return;

         NodeData nd = node.Tag as NodeData;
         grid.DataSource = nd.items;
      }

      void PutDaysGoods(SimpleDataSet<DaysGoods> set, NodeData nd)
      {
         foreach(NodeData.Item i in nd.items)
            if (i.data != null)
               set.Add(i.data);
      }

      private bool SaveChanges(bool showDialog)
      {
         List<ReplacedSet> rpl = new List<ReplacedSet>();

         goods.Clear();
         foreach(NodeData nd in allNodes)
         {
            PutDaysGoods(goods, nd);
            foreach(NodeData ch in nd.childs)
               PutDaysGoods(goods, ch);
         }

         rpl.Add(new ReplacedSet(goods));
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());

         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow == null || treeView.SelectedNode == null)
            return;

         NodeData selNode = treeView.SelectedNode.Tag as NodeData;
         NodeData.Item item = grid.CurrentRow.DataBoundItem as NodeData.Item;
         
         List<Price> selected = new List<Price>();
         if (item.data != null)
         {
            foreach (MatrixItem mi in item.data.items)
            {
               if (mi.price != null)
                  selected.Add(mi.price);
            }
         }
         selected = FmSelectSKUEx.SelectItemsEx(this, selected, null, true);

         if (selected == null)
            return;

         if (selected.Count == 0)
            item.data = null;
         else
         {
            item.data = new DaysGoods();
            item.data.firm = item.firm.id;
            item.data.id = selNode.id;
            item.data.isOrg = selNode.isOrg;

            foreach(Price p in selected)
            {
               MatrixItem mi = new MatrixItem();
               mi.id = p.id;
               mi.price = p;
               item.data.items.Add(mi);
            }
         }

         grid.InvalidateCell(clmnDaysGoods.DisplayIndex, grid.CurrentRow.Index);
         btnSave.Enabled = true;
      }

      private void btnRem_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow == null)
            return;

         NodeData.Item item = grid.CurrentRow.DataBoundItem as NodeData.Item;
         item.data = null;
         grid.InvalidateCell(clmnDaysGoods.DisplayIndex, grid.CurrentRow.Index);
         grid.Refresh();
         btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }
   }
}
