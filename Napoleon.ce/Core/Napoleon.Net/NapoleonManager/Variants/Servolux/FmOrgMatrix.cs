using GRSoft.NapoleonManager.Properties;
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
   public partial class FmOrgMatrix : Form
   {
      private DataSet<string, Factory> dsFactory;
      private DataSet<int, Matrix> dsMatrix;
      private SimpleDataSet<OrgDogovor> dogovors = new SimpleDataSet<OrgDogovor>(OrgDogovor.OBJECT_NAME, false);
      private Dictionary<string, List<Matrix>> firmMtx = new Dictionary<string, List<Matrix>>();
      private List<string> firmFilter = new List<string>();
      private Dictionary<string, Matrix> mtxcache = new Dictionary<string, Matrix>();

      DataSet<string, OrgType> orgTypes = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
      DataSet<string, SalesChannel> salesChannels;
      DataSet<string, Retailers> retailers = new DataSet<string, Retailers>(Retailers.OBJECT_NAME, false);
      DataSet<string, PriceType> priceTypes = new DataSet<string, PriceType>(PriceType.OBJECT_NAME, false);

      private SimpleDataSet<IDMTX> dsAddrMatrix = new SimpleDataSet<IDMTX>(IDMTX.IDMTX_OBJ_NAME, false);
      private SimpleDataSet<ObjectMatrix> dsOrgMatrix = new SimpleDataSet<ObjectMatrix>(ObjectMatrix.IDOMTX_OBJ_NAME, false);

      DataSet<string, Org> orgs = new DataSet<string, Org>(Org.OBJECT_NAME, false);

      /// <summary>
      /// Ключ id + firm
      /// </summary>
      private DataSet<string, IDMTX> chAddrMatrix = new DataSet<string, IDMTX>(IDMTX.IDMTX_OBJ_NAME, false);
      private DataSet<string, IDMTX> remAddrMatrix = new DataSet<string, IDMTX>(IDMTX.IDMTX_OBJ_NAME, false);

      private DataSet<string, ObjectMatrix> chOrgMatrix = new DataSet<string, ObjectMatrix>(ObjectMatrix.IDOMTX_OBJ_NAME, false);
      private DataSet<string, ObjectMatrix> remOrgMatrix = new DataSet<string, ObjectMatrix>(ObjectMatrix.IDOMTX_OBJ_NAME, false);
      
      private System.Threading.Timer textWait = null;
      private OrgTree orgTree = new OrgTree();

      public FmOrgMatrix()
      {
         InitializeComponent();
         dsFactory = (DataSet<string, Factory>)DataModule.Get(Factory.OBJECT_NAME) ?? new DataSet<string, Factory>(Factory.OBJECT_NAME);
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.ORG_MATRIX) ?? new DataSet<int, Matrix>(Matrix.ORG_MATRIX);

         salesChannels = (DataSet<string, SalesChannel>)DataModule.Get(SalesChannel.OBJECT_NAME) ?? new DataSet<string, SalesChannel>(SalesChannel.OBJECT_NAME);

         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();

         Manager dm = CurrentUser.user as Manager;

         if (dm != null) 
         {
            if (orgs.Count == 0)
            {
               orgs.Filter = "\"formatTT\" <> ''";
               upd.Add(orgs);
            }
            //foreach (Agent a in dm.GetAgents().Data)
            //{
            //   DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            //   if (orgs.Count == 0)
            //   {
            //      orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
            //      upd.Add(orgs);
            //   }
            //}

            upd.Add(dsFactory);
            upd.Add(dsMatrix);
            if(dogovors.Count == 0)
               upd.Add(dogovors);
            upd.Add(dsAddrMatrix);
            upd.Add(dsOrgMatrix);

            if (orgTypes.Count == 0)
               upd.Add(orgTypes);
            if (salesChannels.Count == 0)
               upd.Add(salesChannels);
            if (retailers.Count == 0)
               upd.Add(retailers);
            if (priceTypes.Count == 0)
               upd.Add(priceTypes);

            FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
         }
      }

      private void DoLoadData()
      {
         //Dictionary<string, Org> allorgs = new Dictionary<string, Org>();
         //DataSet<string, Org> orgs = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org>;

         Manager dm = CurrentUser.user as Manager;

         if (dm != null)
         {
            //foreach (Agent a in dm.GetAgents().Data)
            //{
            //   DataSet<string, Org> orgs = DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            //   foreach (Org o in orgs.Data)
            //      allorgs[o.id] = o;
            //}
         }

         firmMtx.Clear();
         mtxcache.Clear();
         foreach (Matrix m in dsMatrix.Data)
         {
            if (!firmMtx.ContainsKey(m.firm))
               firmMtx[m.firm] = new List<Matrix>();

            firmMtx[m.firm].Add(m);
            mtxcache.Add(m.name, m);
         }

         Dictionary<string, Matrix> omc = new Dictionary<string, Matrix>();
         foreach (ObjectMatrix am in dsOrgMatrix.Data)
         {
            Matrix m;
            if(mtxcache.TryGetValue(am.mtx, out m))
               omc[am.id + am.objectType + am.firm] = m;
         }

         Dictionary<string, Matrix> amc = new Dictionary<string, Matrix>();
         foreach (IDMTX am in dsAddrMatrix.Data)
         {
            Matrix m;
            if (mtxcache.TryGetValue(am.mtx, out m))
               amc[am.id + am.firm] = m;
         }

         List<Factory> fl = new List<Factory>();
         foreach (Factory f in dsFactory.Data)
            if (firmFilter.Contains(f.id))
               fl.Add(f);

         orgTree.Clear();

         foreach (Org o in orgs.Data)
            orgTree.Add(o, orgTypes, salesChannels, retailers, fl, dogovors, omc, amc);

         treeView.BeginUpdate();
         treeView.Nodes.Clear();

         orgTree.BuildNodes(treeView.Nodes, "");

         treeView.EndUpdate();

         remAddrMatrix.Clear();
         remOrgMatrix.Clear();
         chAddrMatrix.Clear();
         chOrgMatrix.Clear();

         btnSave.Enabled = false;
      }

      private void AssignMatrix(Dictionary<string, string> c, ChildNode n)
      {
         foreach (FactoryData fd in n.factory)
         {
            string key = n.ID + fd.fact.id;
            if (c.ContainsKey(key) && mtxcache.ContainsKey(c[key]))
               fd.mtx = mtxcache[c[key]];
         }
      }

      class OrgTree
      {
         private Dictionary<string, ParentNode> topNodes = new Dictionary<string, ParentNode>();

         public void Add(Org o, DataSet<string, OrgType> orgTypes, DataSet<string, SalesChannel> salesChannels, DataSet<string, Retailers> retailers,
            List<Factory> fl, IDataSet dogovors, Dictionary<string, Matrix> omc, Dictionary<string, Matrix> amc)
         {
            ParentNode chnl;
            if(!topNodes.TryGetValue(o.idChannel, out chnl))
            {
               SalesChannel slsc;
               if (!salesChannels.TryGetValue(o.idChannel, out slsc))
               {
                  slsc = new SalesChannel();
                  slsc.id = o.idChannel;
                  slsc.name = "Канал с кодом <" + slsc.id + ">";
                  salesChannels[slsc.id] = slsc;
               }

               chnl = new ParentNode(slsc.id, slsc.name, ObjectMatrix.CHANNEL_OBJ, fl, null, omc);
               topNodes[slsc.id] = chnl;
            }

            if( chnl == null )
            {
               return;
            }

            ParentNode rtlr = (ParentNode)chnl.FindChild(o.idRetailer);
            if(rtlr == null)
            {
               Retailers retailer;
               if(!retailers.TryGetValue(o.idRetailer, out retailer))
               {
                  retailer = new Retailers();
                  retailer.id = o.idRetailer;
                  retailer.name = "Сеть с кодом <" + retailer.id + ">";
                  retailers[retailer.id] = retailer;
               }
               rtlr = new ParentNode(o.idRetailer, retailer.name, ObjectMatrix.RETAIL_OBJ, fl, null, omc);
               chnl.Add(rtlr);
            }
            if(rtlr == null)
            {
               return;
            }

            ParentNode otp = (ParentNode)rtlr.FindChild(o.formatTT);
            if(otp == null)
            {
               OrgType orgT;
               if(!orgTypes.TryGetValue(o.formatTT, out orgT))
               {
                  orgT = new OrgType();
                  orgT.id = o.formatTT;
                  orgT.name = "Формат ТТ с кодом <" + o.formatTT + ">";
                  orgTypes[o.formatTT] = orgT;
               }
               otp = new ParentNode(o.formatTT, orgT.name, ObjectMatrix.ORG_TYPE_OBJ, fl, null, omc);
               rtlr.Add(otp);
            }
            if(otp == null)
            {
               return;
            }

            OrgNode ond = (OrgNode)otp.FindChild(o.ido);
            if(ond == null)
            {
               ond = new OrgNode(o.ido, o.name, ObjectMatrix.ORG_OBJ, fl, dogovors, omc);
               otp.Add(ond);
            }

            ond.Add(o, amc);
         }

         int CmpNodes(ParentNode l, ParentNode r) { return l.Name.CompareTo(r.Name); }

         public void BuildNodes(TreeNodeCollection nodes, string filter)
         {
            List<ParentNode> lpn = new List<ParentNode>(topNodes.Values);
            lpn.Sort(CmpNodes);
            foreach(ParentNode pn in lpn)
            {
               pn.Sort();
               TreeNode tn = pn.CreateNode(filter);
               if (tn != null)
                  nodes.Add(tn);
            }
         }

         internal void Clear()
         {
            topNodes.Clear();
         }
      }

      interface Ido
      {
         string GetIdo();
      }

      class ParentNode : OrgNode
      {
         public ParentNode(string id, string name, string objType, List<Factory> fl, IDataSet dogovors, Dictionary<string, Matrix> omc) :
            base(id, name, objType, fl, dogovors, omc)
         {
         }

         public override TreeNode CreateNode(string filter)
         {
            TreeNode ret = new TreeNode();
            ret.Text = Name;
            ret.Tag = this;

            if (ret != null)
            {
               foreach (ChildNode ch in childs)
               {
                  TreeNode tnc = ch.CreateNode(filter);
                  if (tnc != null)
                     ret.Nodes.Add(tnc);
               }
               if (ret.Nodes.Count == 0)
                  ret = null;
            }

            return ret;
         }
      }

      class OrgNode : ChildNode
      {
         protected List<ChildNode> childs = new List<ChildNode>();
         protected string objType;

         public OrgNode(string id, string name, string objType, List<Factory> fl, IDataSet dogovors, Dictionary<string, Matrix> omc)
            : base(id, name)
         {
            this.objType = objType;

            foreach (Factory f in fl)
            {
               if (dogovors == null || Factory.HaveFirm(id, f.id, dogovors))
               {
                  FactoryData fd = new FactoryData();
                  fd.fact = f;
                  fd.parent = this;
                  factory.Add(fd);

                  string key = ID + objType + fd.fact.id;
                  Matrix mtx;
                  if (omc.TryGetValue(key, out mtx))
                     fd.mtx = mtx;
               }
            }
         }

         public override void Sort()
         {
            childs.Sort();
            foreach (ChildNode ch in childs)
               ch.Sort();
         }

         public void Add(Org o, Dictionary<string, Matrix> amc)
         {
            foreach (ChildNode ch in childs)
               if (ch.ID == o.id)
                  return;
            ChildNode chn = new ChildNode(o.id, o.address);
            chn.CopyFact(factory, amc);
            childs.Add(chn);
         }

         public void Add(OrgNode ch)
         {
            childs.Add(ch);
         }

         public ChildNode FindChild(string id)
         {
            foreach (ChildNode ch in childs)
               if (ch.ID == id)
                  return ch;
            return null;
         }

         public string ObjectType { get { return objType; } }

         public List<ChildNode> SortList()
         {
            List<ChildNode> result = new List<ChildNode>();
            result.AddRange(childs);
            result.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

            return result;
         }

         public override TreeNode CreateNode(string filter)
         {
            TreeNode ret = base.CreateNode(filter);
            if(ret != null)
            {
               foreach(ChildNode ch in childs)
               {
                  TreeNode tnc = ch.CreateNode("");
                  ret.Nodes.Add(tnc);
               }
            }
            else
            {
               foreach (ChildNode ch in childs)
               {
                  TreeNode tnc = ch.CreateNode(filter);
                  if (tnc != null)
                  {
                     if (ret == null)
                        ret = base.CreateNode("");
                     ret.Nodes.Add(tnc);
                  }
               }
            }

            if (ret != null && ret.Nodes.Count == 0)
               ret = null;
            return ret;
         }

         public override bool Contains(string filter)
         {
            return objType != ObjectMatrix.ORG_TYPE_OBJ || base.Contains(filter);
         }
      }

      class ChildNode : IComparable<ChildNode>
      {
         string name;
         protected string nodeId;
         public List<FactoryData> factory = new List<FactoryData>();

         public ChildNode(string id, string name)
         {
            this.nodeId = id;
            this.name = name;
         }

         public string Name { get { return name; } }
         public virtual void Sort() { }

         internal void CopyFact(List<FactoryData> list, Dictionary<string, Matrix> amc)
         {
            foreach (FactoryData fd in list)
            {
               FactoryData f = new FactoryData();
               f.fact = fd.fact;
               f.parent = this;
               factory.Add(f);
               string key = ID + f.fact.id;
               Matrix mtx;
               if(amc.TryGetValue(key, out mtx))
                  f.mtx = mtx;
            }
         }

         public string ID { get { return nodeId; } }

         public virtual bool Contains(string filter) { return name.ToUpper().Contains(filter); }

         public virtual TreeNode CreateNode(string filter)
         {
            TreeNode ret = null;
            if(filter.Length == 0 || name.ToUpper().Contains(filter))
            {
               ret = new TreeNode();
               ret.Text = Name;
               ret.Tag = this;
            }

            return ret;
         }

         public int CompareTo(ChildNode other)
         {
            return name.CompareTo(other.name);
         }
      }

      private void treeView_AfterSelect(object sender, TreeViewEventArgs e)
      {
         List<FactoryData> list = new List<FactoryData>();

         TreeNode node = ((TreeView)sender).SelectedNode;

         if (node != null)
         {
            ChildNode tn = node.Tag as ChildNode;

            if (tn != null)
               list.AddRange(tn.factory);
         }

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         grid.DataSource = list;
      }

      class FactoryData
      {
         public Factory fact;
         public Matrix mtx = null;
         public object parent = null;

         public string Name { get { return fact.name; } }
         public string Mtx { get { return mtx == null ? string.Empty : mtx.name; } }
      }

      internal void FirmFilter(ICollection<string> ids)
      {
         firmFilter.Clear();
         firmFilter.AddRange(ids);
      }

      private void FmOrgMatrix_Shown(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = grid.CurrentRow;

         if (row != null)
         {
            FactoryData fd = row.DataBoundItem as FactoryData;

            if (fd != null)
            {
               if (firmMtx.ContainsKey(fd.fact.id))
               {
                  Matrix m = FmSelMtx.SelMtx(firmMtx[fd.fact.id]);

                  if (m != null)
                  {
                     fd.mtx = m;
                     grid.Refresh();
                     ChangeFactoryMatrix(fd, fd.parent);
                     btnSave.Enabled = true;
                  }
               }
            }
         }
      }
      
      private void btnRem_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = grid.CurrentRow;

         if (row != null)
         {
            FactoryData fd = row.DataBoundItem as FactoryData;

            if (fd != null)
            {
               RemoveFactoryMatrix(fd, fd.parent);
               fd.mtx = null;
               grid.Refresh();
               btnSave.Enabled = true;
            }
         }
      }

      void UpdateObjMatrix(OrgNode on, FactoryData fd, DataSet<string, ObjectMatrix> ds)
      {
         string key = on.ID + fd.fact.id;
         ObjectMatrix am = new ObjectMatrix();
         am.id = on.ID;
         am.firm = fd.fact.id;
         am.mtx = fd.Mtx;
         am.objectType = on.ObjectType;
         ds[key] = am;
      }

      void UpdateAdrMatrix(ChildNode an, FactoryData fd, DataSet<string, IDMTX> ds)
      {
         string key = an.ID + fd.fact.id;
         IDMTX am = new IDMTX();
         am.id = an.ID;
         am.firm = fd.fact.id;
         am.mtx = fd.Mtx;
         ds[key] = am;
      }

      void ChangeFactoryMatrix(FactoryData fd, object parent)
      {
         OrgNode on = parent as OrgNode;
         if(on != null)
            UpdateObjMatrix(on, fd, chOrgMatrix);
         else
         {
            ChildNode an = parent as ChildNode;
            UpdateAdrMatrix(an, fd, chAddrMatrix);
         }
      }

      void RemoveFactoryMatrix(FactoryData fd, object parent)
      {
         ParentNode on = parent as ParentNode;
         if (on != null)
            UpdateObjMatrix(on, fd, remOrgMatrix);
         else
         {
            ChildNode an = parent as ChildNode;
            UpdateAdrMatrix(an, fd, remAddrMatrix);
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         List<IDataSet> rem = new List<IDataSet>();

         if (chAddrMatrix.Count > 0)
            upd.Add(chAddrMatrix);

         if (chOrgMatrix.Count > 0)
            upd.Add(chOrgMatrix);

         if (remAddrMatrix.Count > 0)
            rem.Add(remAddrMatrix);

         if (remOrgMatrix.Count > 0)
            rem.Add(remOrgMatrix);

         if (!DataModule.UpdateDataSet(upd, rem, null, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
         else
         {
            chAddrMatrix.Clear();
            chOrgMatrix.Clear();
            remAddrMatrix.Clear();
            remOrgMatrix.Clear();
            btnSave.Enabled = false;
         }
      }

      private void tbSearch_TextChanged(object sender, EventArgs e)
      {
         if (textWait != null)
            textWait.Dispose();
         textWait = new System.Threading.Timer(new TimerCallback(TimePassed), ((ToolStripTextBox)sender).Text, 500, 0);
      }

      private void DoSearch(string filter)
      {
         treeView.BeginUpdate();
         treeView.Nodes.Clear();

         filter = filter.ToUpper();
         orgTree.BuildNodes(treeView.Nodes, filter);
         treeView.EndUpdate();
      }

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMOrgMatrixMutex");
            if (m.WaitOne(0))
               treeView.Invoke(new InvokeParamHandler(delegate(object param){DoSearch((string)param);}), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception){}
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbSearch.Text = string.Empty;
      }

      private void FmOrgMatrix_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled)
            btnSave.PerformClick();
      }
   }
}
