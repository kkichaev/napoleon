using GRSoft.NapoleonManager.Properties;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmMatrixDesignerEx : FmMatrixDesigner
   {
      public DataSet<string, Factory> dsFactory;
      public ToolStripComboBox cbFirms;
      private List<Matrix> mtxcache = new List<Matrix>();
      private List<String> removed = new List<String>();
      private Dictionary<string, List<string>> itemscahce = new Dictionary<string, List<string>>();
      public SimpleDataSet<PlanNew> dsPlanNew = new SimpleDataSet<PlanNew>(PlanNew.OBJECT_NAME, false);
      private DataSet<string, Price> dsPriceCpy = new DataSet<string, Price>(Price.OBJECT_NAME, false);

      public FmMatrixDesignerEx()
      {
         cbFirms = new ToolStripComboBox();
         cbFirms.Size = new Size(200, 25);
         cbFirms.SelectedIndexChanged += cbFirms_SelectedIndexChanged;
         toolStrip1.Items.Insert(0, cbFirms);
         tbMatrixName.Visible = false;

         ToolStripButton btn = new ToolStripButton();
         btn.Click += OrgMatrixClick;
         btn.Image = Resources.group_edit;
         btn.ToolTipText = "Привязать матрицу";
         toolStrip1.Items.Add(btn);

         dsFactory = (DataSet<string, Factory>)DataModule.Get(Factory.OBJECT_NAME) ?? new DataSet<string, Factory>(Factory.OBJECT_NAME);

         tsbFind.Visible = false;
         tsbFindBack.Visible = false;
         tvMatrix.CheckBoxes = true;
         tvMatrix.AfterCheck += tvMatrix_AfterCheck;
      }

      void tvMatrix_AfterCheck(object sender, TreeViewEventArgs e)
      {
         tsbSave.Enabled = true;
         Matrix m = e.Node.Tag as Matrix;
         if (m != null)
         {
            foreach (TreeNode tn in e.Node.Nodes)
               tn.Checked = e.Node.Checked;
         }
         else
         {
            MatrixItem mi = (MatrixItem)e.Node.Tag;
            if (mi != null)
               mi.mustBe = e.Node.Checked ? 1 : 0;
         }
      }

      protected override void FillMatrixEnded()
      {
         foreach(TreeNode mnode in tvMatrix.Nodes)
         {
            foreach(TreeNode tn in mnode.Nodes)
            {
               MatrixItem mi = (MatrixItem)tn.Tag;
               tn.Checked = mi.mustBe != 0;
            }
         }
         tsbSave.Enabled = false;
      }

      protected override string MatrixObjectName  {  get { return Matrix.ORG_MATRIX; } }

      protected override string GetMatrixPriceName(MatrixItem item)
      {
         return PriceName(item.price);
      }

      string PriceName(Price p)
      {
         return p.name + " " + p.thermalState + "/" + p.packName;
      }

      protected override void MakePriceTree(TreeView tv, DataSet<string, ManagerFolder> folders, DataSet<string, Price> price)
      {
         ArticlesTreeConstructor t = new ArticlesTreeConstructor(tv, folders, price);
         t.GetPriceName = PriceName;
         t.MakeArticlesTree(0, 1);
         PostTreeConstruct(t);
      }

      protected void OrgMatrixClick(object o, EventArgs e)
      {
         FmOrgMatrix fm = new FmOrgMatrix();
         fm.FirmFilter(itemscahce.Keys);
         fm.Show();
      }

      protected override bool IsMatrixInSet(string matrixName)
      {
         bool result = false;

         foreach(Matrix m in mtxcache)
            if (m.name.Equals(matrixName))
            {
               result = true;
               break;
            }

         return result;
      }

      void cbFirms_SelectedIndexChanged(object sender, EventArgs e)
      {
         FillMatrix();
         FilterPrice(sender);
         FillPrice();
      }

      private void FilterPrice(object sender)
      {
         List<string> items = new List<string>();

         Factory f = ((ToolStripComboBox)sender).SelectedItem as Factory;
         if (f != null && itemscahce.ContainsKey(f.id))
            items = itemscahce[f.id];

         DataSet<string, Price> cp = new DataSet<string, Price>(Price.OBJECT_NAME);
         foreach (KeyValuePair<string, Price> kv in dsPriceCpy)
         {
            if (items.Contains(kv.Key))
               cp.Add(kv.Key, kv.Value);
         }

         dsPrice.Clear();

         foreach (KeyValuePair<string, Price> kv in cp)
            dsPrice.Add(kv.Key, kv.Value);
      }

      protected override void PostTreeConstruct(Utils.ArticlesTreeConstructor atc)
      {
         atc.RemoveEmptyNodes();
      }

      protected override void PullRefreshList(List<IDataSet> list)
      {
         base.PullRefreshList(list);

         dsMatrix.Filter = string.Empty;

         list.Add(dsFactory);
         dsPlanNew.Filter = String.Format("\"date\">=ToDate('{0}')", DateTime.Now.Date.AddMonths(-1));
         list.Add(dsPlanNew);
      }

      protected override void ControlsFillAfterLoaded()
      {
         mtxcache.Clear();

         foreach(Matrix m in dsMatrix.Data)
            mtxcache.Add(m);

         itemscahce.Clear();
         foreach (PlanNew p in dsPlanNew.Values)
            if (p.firm != null)
            {
               if (!itemscahce.ContainsKey(p.firm))
                  itemscahce[p.firm] = new List<string>();

               List<string> pi = itemscahce[p.firm];
               foreach (PlanNew.Item i in p.items)
                  if (!pi.Contains(i.id))
                     pi.Add(i.id);
            }

         dsPriceCpy.Clear();

         foreach (KeyValuePair<string, Price> kv in dsPrice)
            dsPriceCpy.Add(kv.Key, kv.Value);

         cbFirms.BeginUpdate();
         cbFirms.Items.Clear();

         foreach (Factory f in dsFactory.Data)
            if(f != null && itemscahce.ContainsKey(f.id))
               cbFirms.Items.Add(f);

         cbFirms.EndUpdate();

         if (cbFirms.Items.Count > 0)
            cbFirms.SelectedIndex = 0;
      }

      protected override string EditMatrixName(string val)
      {
         MatrixNameDlg dlg = new MatrixNameDlg();
         dlg.textBox.Text = val;

         if (dlg.ShowDialog() == DialogResult.OK)
            return dlg.textBox.Text;

         return string.Empty;
      }

      protected override void EmptyNameMantrixHandler() { }

      protected override void InitNewMtx(Matrix mtx)
      {
         base.InitNewMtx(mtx);

         Factory f = cbFirms.SelectedItem as Factory;

         if (f != null)
            mtx.firm = f.id;

         mtxcache.Add(mtx);
      }

      protected override bool CheckMatrix(Matrix mtx)
      {
         bool result = false;

         Factory f = cbFirms.SelectedItem as Factory;

         if(f != null)
            result = mtx.firm.Equals(f.id);

         return result; 
      }

      public void MatrixRemoveDupItems(Matrix m)
      {
         Dictionary<string, bool> used = new Dictionary<string, bool>();
         List<MatrixItem> rmv = new List<MatrixItem>();
         foreach(MatrixItem mi in m.items)
         {
            if(used.ContainsKey(mi.id))
            {
               rmv.Add(mi);
               continue;
            }
            used[mi.id] = true;
         }

         rmv.ForEach(x => m.items.Remove(x));
      }

      public override IDataSet GetMatrixDataSet()
      {
         SimpleDataSet<Matrix> ds = new SimpleDataSet<Matrix>(Matrix.ORG_MATRIX, false);

         foreach (Matrix m in mtxcache)
         {
            MatrixRemoveDupItems(m);
            ds.Add(m);
         }

         return ds;
      }

      protected override void OnItemRemove(TreeNode node)
      {
         MatrixItem mi = node.Tag as MatrixItem;
         if( mi != null )
         {
            TreeNode mp = node.Parent;
            if( mp != null )
            {
               Matrix m = mp.Tag as Matrix;
               if (m != null)
                  m.items.Remove(mi);
            }
         }
      }

      protected override void RemoveMatrix(Matrix mtx)
      {
         if (mtx != null && mtx.name != null && mtxcache.Contains(mtx))
         {
            mtxcache.Remove(mtx);
            removed.Add(mtx.name);
         }
      }

      protected override void MoveMatrix(int pos, int newpos, Matrix m)
      {
         if (m != null)
         {
            Matrix o = mtxcache[pos];
            mtxcache.Remove(o);
            mtxcache.Insert(newpos, o);
         }
      }

      protected override bool SaveData()
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> remSet = new List<IDataSet>();
         wrSet.Add(GetMatrixDataSet());

         if (removed.Count > 0)
         {
            SimpleDataSet<Matrix> ds = new SimpleDataSet<Matrix>(Matrix.ORG_MATRIX, false);
            foreach (String m in removed)
            {
               Matrix mtx = new Matrix();
               mtx.name = m;
               ds.Add(mtx);
            }

            remSet.Add(ds);
         }

         bool res =  DataModule.UpdateDataSet(wrSet, remSet, null, Config.GetConfig().GetConnection());

         if (res)
            removed.Clear();

         return res;
      }

      protected override void RenameMatrix(Matrix m, string name)
      {
         removed.Add(m.name);
         m.name = name;
      }   }
}

